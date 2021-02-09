package com.astetyne.expirium.server.core.world;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemCat;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.Saveable;
import com.astetyne.expirium.server.core.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.core.entity.ExpiEntity;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.event.TileChangeEvent;
import com.astetyne.expirium.server.core.world.calculator.BackWallCalculator;
import com.astetyne.expirium.server.core.world.calculator.FixtureCalculator;
import com.astetyne.expirium.server.core.world.calculator.StabilityCalculator;
import com.astetyne.expirium.server.core.world.generator.CreateWorldPreferences;
import com.astetyne.expirium.server.core.world.generator.WorldGenerator;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.TickTask;
import com.astetyne.expirium.server.core.world.tile.TileFix;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;

public class ExpiWorld implements Saveable, Disposable {

    private final ExpiServer server;
    private final ExpiTile[][] terrain;
    private int partHeight;
    private World b2dWorld;
    private Body terrainBody;
    private StabilityCalculator stabilityCalc;
    private FixtureCalculator fixtureCalc;
    private BackWallCalculator backWallCalculator;
    private WeatherType weatherType;
    private ExpiContactListener contactListener;
    private long tick; // total ticks since world was generated
    private int time; // from midnight = 0 ticks
    private long day; // completed days from server creation
    private final int width, height;
    private final long seed;
    private PriorityQueue<TickTask> scheduledTickTasks;

    public ExpiWorld(CreateWorldPreferences preferences, ExpiServer server) {

        this.server = server;

        width = preferences.width;
        height = preferences.height;
        seed = preferences.seed;

        terrain = new ExpiTile[preferences.height][preferences.width];
        new WorldGenerator(this, terrain, seed).generateWorld();

        time = 11200; // 7:00
        day = 0;
        weatherType = WeatherType.SUNNY;

        initAfterCreation();

    }

    public ExpiWorld(DataInputStream in, ExpiServer server) throws IOException {

        this.server = server;

        tick = in.readLong();
        day = tick / Consts.DAY_TICKS;
        time = (int) (tick % Consts.DAY_TICKS);
        System.out.println("tick: "+tick+" "+" day: "+day+" time: "+time);

        width = in.readInt();
        height = in.readInt();
        seed = in.readLong();

        terrain = new ExpiTile[height][width];

        for(int h = 0; h < height; h++) {
            for(int w = 0; w < width; w++) {
                terrain[h][w] = new ExpiTile(this, in, w, h);
            }
        }

        //todo: len docasne zatial
        weatherType = WeatherType.SUNNY;

        initAfterCreation();

    }

    private void initAfterCreation() {

        scheduledTickTasks = new PriorityQueue<>();

        partHeight = 2; // todo: this should be calculated from BUFFER_SIZE and terrainWidth

        b2dWorld = new World(new Vector2(0, -17f), false);

        contactListener = new ExpiContactListener();
        b2dWorld.setContactListener(contactListener);

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        stabilityCalc = new StabilityCalculator(server, this);
        fixtureCalc = new FixtureCalculator(this, terrainBody);
        backWallCalculator = new BackWallCalculator(server, this);

        System.out.println("Creating fixtures...");
        fixtureCalc.generateWorldFixtures();
        System.out.println("Recalculating stability...");
        stabilityCalc.generateStability();
        System.out.println("Generating world done!");

        for(int h = 0; h < height; h++) {
            for(int w = 0; w < width; w++) {
                terrain[h][w].getMeta().postInit();
            }
        }

    }

    public void dispose() {
        b2dWorld.dispose();
        fixtureCalc.dispose();
    }

    public void onTick() {

        tick++;
        time += 100;
        if(time >= Consts.DAY_TICKS) { // midnight
            time = 0;
            day++;
        }

        // time step should be 1/32 but then items are kinda buggy, so it is doubled
        for(int i = 0; i < 2; i++) {
            for(ExpiPlayer pp : server.getPlayers()) {
                pp.applyPhysics();
            }
            b2dWorld.step(1f/64, 6, 2);
        }

        while(!scheduledTickTasks.isEmpty()) {
            TickTask task = scheduledTickTasks.peek();
            if(task.tick <= tick) {
                task.runnable.run();
                scheduledTickTasks.remove();
            }else {
                break;
            }
        }

        for(int i = server.getEntities().size() - 1; i >= 0; i--) {
            server.getEntities().get(i).onTick();
        }

        for(ExpiPlayer pp : server.getPlayers()) {
            for(ExpiEntity ee : server.getEntities()) {
                pp.getNetManager().putEntityMovePacket(ee);
            }
            pp.getNetManager().putEnviroPacket();
        }
    }

    public void onInteract(ExpiPlayer p, PacketInputStream in) {

        float x = in.getFloat();
        float y = in.getFloat();
        Vector2 loc = new Vector2(x, y);
        InteractType type = InteractType.getType(in.getInt());
        ExpiTile t = server.getWorld().getTileAt(x, y);

        if(p.isInInteractRadius(loc)) {
            t.onInteract(p, type);
        }

        // following code is only for tile placing
        if(!p.isInInteractRadius(loc)) return;

        Item item = p.getInv().getItemInHand().getItem();

        if(item.getCategory() != ItemCat.MATERIAL || t.getMaterial() != Material.AIR) return;

        if(item.getBuildMaterial() == null) return;

        if(!isTileFree(t, item)) return;

        if(!stabilityCalc.canBeChanged(t, item.getBuildMaterial())) return;

        // confirmed from here
        p.getInv().removeItem(new ItemStack(item, 1));
        p.getNetManager().putInvFeedPacket();

        for(ExpiPlayer ep : server.getPlayers()) {
            ep.getNetManager().putHandPunchPacket(p);
        }

        changeMaterial(t, item.getBuildMaterial(), true, Source.PLAYER);
    }

    public void changeMaterial(ExpiTile t, Material to, boolean withDrops, Source source) {

        Material fromMat = t.getMaterial();
        MetaTile fromMeta = t.getMeta();

        if(to == Material.AIR && withDrops) t.getMeta().dropItems();

        t.changeMaterial(to);
        for(ExpiPlayer ep : server.getPlayers()) {
            ep.getNetManager().putMaterialChangePacket(t);
        }

        TileChangeEvent e = new TileChangeEvent(t, fromMeta, fromMat, source);

        fixtureCalc.updateTileFixturesAndNearbyTiles(t);
        stabilityCalc.onTileChange(e); // not sure if this should be before or after modules

        // modules
        /*List<TileChangeListener> list = server.getEventManager().getTileChangeListeners();
        for(int i = list.size() - 1; i >= 0; i--) {
            list.get(i).onTileChange(e);
        }*/

        backWallCalculator.onTileChange(e);
    }

    private boolean isTileFree(ExpiTile t, Item toPlace) {

        int x = t.getX();
        int y = t.getY();

        if(toPlace.getBuildMaterial().getFix() == TileFix.SOFT) return true;

        for(ExpiEntity p : server.getEntities()) {

            float px = p.getLocation().x;
            float py = p.getLocation().y;
            float pxe = px + p.getWidth();
            float pye = py + p.getHeight();

            if(((px > x && px < x+1) || (px < x && x < pxe) || (pxe > x && pxe < x+1)) && //check if x collide
                    ((py > y && py < y+1) || (py < y && y < pye) || (pye > y && pye < y+1))) { //check if y collide
                return false;
            }
        }
        return true;
    }

    public Vector2 getSpawnLocation() {
        int x = (int) (width/2 -10 + Math.random()*11);
        int y = height-2;
        while(terrain[y][x].getMaterial().getFix() == TileFix.SOFT) {
            if(y == 0) {
                x = (int) (Math.random() * width);
                y = height-2;
            }else {
                y--;
            }
        }
        return new Vector2(x, y+2);
    }

    public void teleport(ExpiEntity e, float x, float y) {

        float precision = 0.5f;

        float rightX = x;
        float leftX = x;

        while(true) {
            if(isPlaceSafe(e, rightX, y)) {
                e.teleport(rightX, y);
                break;
            }
            if(isPlaceSafe(e, leftX, y)) {
                e.teleport(leftX, y);
                break;
            }
            rightX += precision;
            leftX -= precision;
            if(leftX <= 0 || rightX + e.getType().getWidth() >= width) {
                leftX = x;
                rightX = x;
                y++;
            }
            if(y + e.getType().getHeight() >= height) {
                System.out.println("Can not find safe teleport loc for: "+e+". Teleport canceled.");
                break;
            }
        }

    }

    private boolean isPlaceSafe(ExpiEntity e, float x, float y) {

        float ew = e.getType().getWidth();
        float eh = e.getType().getHeight();

        if(x <= 0 || x + ew >= width || y <= 0 || y + eh >= height) return false;

        if(getTileAt(x, y).getMaterial().getFix() != TileFix.SOFT) return false;
        if(getTileAt(x + ew, y).getMaterial().getFix() != TileFix.SOFT) return false;
        if(getTileAt(x + ew, y + eh).getMaterial().getFix() != TileFix.SOFT) return false;
        if(getTileAt(x, y + eh).getMaterial().getFix() != TileFix.SOFT) return false;

        return true;
    }

    public ExpiTile getTileAt(float x, float y) {
        return terrain[(int)y][(int)x];
    }

    public ExpiTile getTileAt(Vector2 vec) {
        return terrain[(int)vec.y][(int)vec.x];
    }

    public Body getTerrainBody() {
        return terrainBody;
    }

    public World getB2dWorld() {
        return b2dWorld;
    }

    public WeatherType getWeather() {
        return weatherType;
    }

    public ExpiContactListener getCL() {
        return contactListener;
    }

    public int getTime() {
        return time;
    }

    public ExpiTile[][] getTerrain() {
        return terrain;
    }

    public int getTerrainWidth() {
        return width;
    }

    public int getTerrainHeight() {
        return height;
    }

    public int getPartHeight() {
        return partHeight;
    }

    public long getDay() {
        return day;
    }

    public void spawnDroppedItem(Item item, Vector2 loc, int ticksCooldown) {
        new ExpiDroppedItem(server, loc, item, ticksCooldown);
    }

    public long getTick() {
        return tick;
    }

    public void scheduleTask(Runnable runnable, long afterTicks) {
        TickTask task = new TickTask(runnable, tick + afterTicks);
        scheduleTask(task);
    }

    public void scheduleTask(TickTask task) {
        scheduledTickTasks.add(task);
    }

    public ExpiServer getServer() {
        return server;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

        out.writeLong(tick);

        out.writeInt(width);
        out.writeInt(height);
        out.writeLong(seed);

        for(int h = 0; h < height; h++) {
            for(int w = 0; w < width; w++) {
                terrain[h][w].writeData(out);
            }
        }

    }
}
