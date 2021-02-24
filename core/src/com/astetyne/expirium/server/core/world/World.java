package com.astetyne.expirium.server.core.world;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.Entity;
import com.astetyne.expirium.server.core.entity.LivingEntity;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.event.TileChangeEvent;
import com.astetyne.expirium.server.core.world.calculator.BackWallCalculator;
import com.astetyne.expirium.server.core.world.calculator.FixtureCalculator;
import com.astetyne.expirium.server.core.world.calculator.StabilityCalculator;
import com.astetyne.expirium.server.core.world.calculator.WaterEngine;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.generator.biome.BiomeType;
import com.astetyne.expirium.server.core.world.tile.*;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Disposable;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class World implements WorldSaveable, Disposable {

    private final ExpiServer server;
    private final Tile[][] terrain;
    private final com.badlogic.gdx.physics.box2d.World b2dWorld;
    private final Body terrainBody;
    private final StabilityCalculator stabilityCalc;
    private final FixtureCalculator fixtureCalc;
    private final BackWallCalculator backWallCalculator;
    private final WaterEngine waterEngine;
    private final WeatherType weatherType;
    private final ExpiContactListener contactListener;
    private long tick; // total ticks since world was generated
    private int time; // from midnight = 0 ticks
    private long day; // completed days from server creation
    private final int width, height;
    private final long seed;
    private final PriorityQueue<TickTask> scheduledTickTasks;
    private final AnimalBalancer animalBalancer;
    private final BiomeType[] biomes;

    public World(DataInputStream in, long tick, ExpiServer server) throws IOException {

        this.server = server;
        this.tick = tick;

        day = tick / Consts.TICKS_IN_DAY;
        time = (int) (tick % Consts.TICKS_IN_DAY);

        scheduledTickTasks = new PriorityQueue<>();

        width = in.readInt();
        height = in.readInt();
        seed = in.readLong();

        boolean createMeta = in.readBoolean();
        terrain = new Tile[width][height];
        long startT = System.nanoTime();
        for(int w = 0; w < width; w++) {
            for(int h = 0; h < height; h++) {
                terrain[w][h] = new Tile(this, w, h, in, createMeta);
            }
        }
        System.out.println("Loading tiles time (ms): "+(System.nanoTime() - startT)/1000000);

        biomes = new BiomeType[width/Consts.WORLD_BIOME_WIDTH];
        for(int i = 0; i < biomes.length; i++) {
            biomes[i] = BiomeType.get(in.readInt());
        }

        //todo: len docasne zatial
        weatherType = WeatherType.SUNNY;

        b2dWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, -17f), false);

        contactListener = new ExpiContactListener();
        b2dWorld.setContactListener(contactListener);

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        stabilityCalc = new StabilityCalculator(server, terrain, width, height);
        fixtureCalc = new FixtureCalculator(terrain, width, height, terrainBody);
        backWallCalculator = new BackWallCalculator(server, terrain, width, height);
        waterEngine = new WaterEngine(server, terrain, width, height);

        fixtureCalc.generateWorldFixtures();
        stabilityCalc.generateStability();

        animalBalancer = new AnimalBalancer(this);
    }

    public void dispose() {
        b2dWorld.dispose();
        fixtureCalc.dispose();
    }

    public void onTick() {

        tick++;
        time++;
        if(Consts.DEBUG) time += 99;
        if(time >= Consts.TICKS_IN_DAY) { // midnight
            time = 0;
            day++;
        }

        waterEngine.onTick(); // should be called before physics step

        // time step should be 1/32 but then items are kinda buggy, so it is doubled
        for(int i = 0; i < 2; i++) {
            for(Player pp : server.getPlayers()) {
                pp.applyPhysics();
            }
            waterEngine.applyPhysics();
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

        animalBalancer.onTick();

        List<Entity> copy = new ArrayList<>(server.getEntities());
        for(Entity ee : copy) {
            ee.onTick();
        }

        for(Player pp : server.getPlayers()) {
            for(Entity ee : server.getEntities()) {
                pp.getNetManager().putEntityMovePacket(ee);
            }
            pp.getNetManager().putEnviroPacket();
        }
    }

    public void onInteract(Player p, PacketInputStream in) {

        float x = in.getFloat();
        float y = in.getFloat();
        Vector2 loc = new Vector2(x, y);
        InteractType type = InteractType.getType(in.getInt());
        Tile t = server.getWorld().getTileAt(x, y);

        if(p.isInInteractRadius(loc)) {
            t.onInteract(p, type);
        }

        //DEBUG------
        waterEngine.createWater(t);
        //DEBUG------

        // following code is only for tile placing
        if(!p.isInInteractRadius(loc)) return;

        Item item = p.getInv().getItemInHand().getItem();;

        if(t.getMaterial() != Material.AIR) return;

        if(item.getBuildMaterial() == null) return;

        if(!isTileFree(t, item)) return;

        if(!stabilityCalc.canBeChanged(t, item.getBuildMaterial())) return;

        // confirmed from here
        p.getInv().remove(item, 1);

        for(Player ep : server.getPlayers()) {
            ep.getNetManager().putHandPunchPacket(p);
        }

        changeMaterial(t, item.getBuildMaterial(), true, Source.PLAYER);
    }

    public void changeMaterial(Tile t, Material to, boolean withDrops, Source source) {

        Material fromMat = t.getMaterial();
        MetaTile fromMeta = t.getMeta();

        if(to == Material.AIR && withDrops) t.getMeta().dropItems();

        t.changeMaterial(to);
        for(Player ep : server.getPlayers()) {
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

        waterEngine.updateWater(t);
        waterEngine.updateWater(getTileAt(t.getLoc(new IntVector2(0, 0)).add(0, 1)));
    }

    private boolean isTileFree(Tile t, Item toPlace) {

        int x = t.getX();
        int y = t.getY();

        if(toPlace.getBuildMaterial().getFix() == TileFix.SOFT) return true;

        for(Entity p : server.getEntities()) {

            float px = p.getLocation().x;
            float py = p.getLocation().y;
            float pxe = px + p.getWidth();
            float pye = py + p.getHeight();

            //todo: what if entity is rotated?
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
        while(terrain[x][y].getMaterial().getFix() == TileFix.SOFT) {
            if(y == 0) {
                x = (int) (Math.random() * width);
                y = height-2;
            }else {
                y--;
            }
        }
        return new Vector2(x, y+2);
    }

    public void teleport(Entity e, float x, float y) {

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

    private boolean isPlaceSafe(Entity e, float x, float y) {

        float ew = e.getType().getWidth();
        float eh = e.getType().getHeight();

        if(x <= 0 || x + ew >= width || y <= 0 || y + eh >= height) return false;

        //todo: what if entity is bigger than 2 tiles?
        if(getTileAt(x, y).getMaterial().getFix() != TileFix.SOFT) return false;
        if(getTileAt(x + ew, y).getMaterial().getFix() != TileFix.SOFT) return false;
        if(getTileAt(x + ew, y + eh).getMaterial().getFix() != TileFix.SOFT) return false;
        if(getTileAt(x, y + eh).getMaterial().getFix() != TileFix.SOFT) return false;

        return true;
    }

    public Tile getTileAt(float x, float y) {
        return terrain[(int)x][(int)y];
    }

    public Tile getTileAt(Vector2 vec) {
        return terrain[(int)vec.x][(int)vec.y];
    }

    public Tile getTileAt(int x, int y) {
        return terrain[x][y];
    }

    public Tile getTileAt(IntVector2 loc) {
        return terrain[loc.x][loc.y];
    }

    public boolean isInWorld(Vector2 v) {
        return v.x >= 0 && v.x < width && v.y >= 0 && v.y < height;
    }

    public Body getTerrainBody() {
        return terrainBody;
    }

    public com.badlogic.gdx.physics.box2d.World getB2dWorld() {
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

    public Tile[][] getTerrain() {
        return terrain;
    }

    public int getTerrainWidth() {
        return width;
    }

    public int getTerrainHeight() {
        return height;
    }

    public long getDay() {
        return day;
    }

    public long getTick() {
        return tick;
    }

    public BiomeType[] getBiomes() {
        return biomes;
    }

    public BiomeType getBiomeAt(float x) {
        int i = (int) (x / Consts.WORLD_BIOME_WIDTH);
        return biomes[i];
    }

    public long scheduleTaskAfter(Runnable runnable, long afterTicks) {
        TickTask task = new TickTask(runnable, tick + afterTicks);
        scheduleTask(task);
        return task.tick;
    }

    public long scheduleTask(Runnable runnable, long tick) {
        TickTask task = new TickTask(runnable, tick);
        scheduleTask(task);
        return task.tick;
    }

    public void scheduleTask(TickTask task) {
        scheduledTickTasks.add(task);
    }

    public ExpiServer getServer() {
        return server;
    }

    public Entity spawnEntity(EntityType type, Vector2 loc, Object... args) {
        Class<?>[] argClasses = new Class[args.length+2];
        Object[] objects = new Object[args.length+2];
        for(int i = 0; i < args.length; i++) {
            argClasses[i+2] = args[i].getClass();
            objects[i+2] = args[i];
        }

        argClasses[0] = ExpiServer.class;
        argClasses[1] = Vector2.class;

        objects[0] = server;
        objects[1] = loc;

        try {
            Entity e = type.getEntityClass().getConstructor(argClasses).newInstance(objects);
            e.createBodyFixtures();
            for(Player ep : server.getPlayers()) {
                if(e == ep) continue;
                ep.getNetManager().putEntitySpawnPacket(e);
            }
            for(Player p : server.getPlayers()) {
                p.recalcNearEntities();
            }
            return e;
        }catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entity spawnEntity(EntityType type, DataInputStream in) {
        try {
            Entity e = type.getEntityClass().getConstructor(ExpiServer.class, DataInputStream.class).newInstance(server, in);
            e.createBodyFixtures();
            for(Player ep : server.getPlayers()) {
                if(e == ep) continue;
                ep.getNetManager().putEntitySpawnPacket(e);
            }
            return e;
        }catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void hitEntity(Entity attacker, LivingEntity victim, int damage) {
        //System.out.println("Attacker "+attacker.getType()+" hit "+victim.getType()+" with "+damage+" damage");
        victim.injure(damage);
        Vector2 pushVec = new Vector2(victim.getCenter().sub(attacker.getCenter()));
        pushVec.nor();
        pushVec.scl(200);
        victim.getBody().applyLinearImpulse(pushVec, victim.getCenter(), true);
    }

    @Override
    public void writeData(WorldBuffer out) {

        out.writeInt(width);
        out.writeInt(height);
        out.writeLong(seed);

        out.writeBoolean(false);

        for(int w = 0; w < width; w++) {
            for(int h = 0; h < height; h++) {
                terrain[w][h].writeData(out);
            }
        }
        for(BiomeType biome : biomes) {
            out.writeInt(biome.ordinal());
        }
    }
}
