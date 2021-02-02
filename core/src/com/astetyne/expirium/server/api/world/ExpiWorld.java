package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemCategory;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.tiles.ItemDropper;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.Saveable;
import com.astetyne.expirium.server.api.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entity.ExpiEntity;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.*;
import com.astetyne.expirium.server.api.world.generator.CreateWorldPreferences;
import com.astetyne.expirium.server.api.world.generator.WorldGenerator;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;
import com.astetyne.expirium.server.resources.TileFix;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ExpiWorld implements Saveable, Disposable, PlayerInteractListener {

    private final ExpiServer server;
    private final ExpiTile[][] terrain;
    private int partHeight;
    private World b2dWorld;
    private Body terrainBody;
    private StabilityCalculator stabilityCalc;
    private FixtureCalculator fixtureCalc;
    private WeatherType weatherType;
    private ExpiContactListener contactListener;
    private float stepsTimeAccumulator;
    private float dayTime; // in seconds from 6:00 local time
    private int day; // completed days from server creation
    private final int width, height;
    private final long seed;

    public ExpiWorld(CreateWorldPreferences preferences, ExpiServer server) {

        this.server = server;

        width = preferences.width;
        height = preferences.height;
        seed = preferences.seed;

        terrain = new ExpiTile[preferences.height][preferences.width];
        new WorldGenerator(terrain).generateWorld();

        dayTime = 50; // 7:00
        day = 0;
        weatherType = WeatherType.SUNNY;

        initAfterCreation();

    }

    public ExpiWorld(DataInputStream in, ExpiServer server) throws IOException {

        this.server = server;

        width = in.readInt();
        height = in.readInt();
        seed = in.readLong();

        terrain = new ExpiTile[height][width];

        for(int h = 0; h < height; h++) {
            for(int w = 0; w < width; w++) {
                terrain[h][w] = new ExpiTile(TileType.getType(in.readByte()), TileType.getType(in.readByte()), w, h);
            }
        }

        dayTime = in.readFloat();
        day = in.readInt();

        //todo: len docasne zatial
        weatherType = WeatherType.SUNNY;

        initAfterCreation();

    }

    private void initAfterCreation() {

        stepsTimeAccumulator = 0;

        partHeight = 2; // todo: this should be calculated from BUFFER_SIZE and terrainWidth

        b2dWorld = new World(new Vector2(0, -12f), false);

        contactListener = new ExpiContactListener();
        b2dWorld.setContactListener(contactListener);

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        stabilityCalc = new StabilityCalculator(terrain);
        fixtureCalc = new FixtureCalculator(this, terrainBody);

        System.out.println("Creating fixtures...");
        fixtureCalc.generateWorldFixtures();
        System.out.println("Recalculating stability...");
        stabilityCalc.generateStability();
        System.out.println("Generating world done!");

        server.getEventManager().getPlayerInteractListeners().add(this);

    }

    public void dispose() {
        b2dWorld.dispose();
        fixtureCalc.dispose();
    }

    public void onTick(float delta) {

        dayTime += delta;
        if(dayTime >= Consts.DAY_TIME_SEC) {
            dayTime = 0;
            day++;
        }

        for(ExpiPlayer pp : server.getPlayers()) {
            pp.applyPhysics();
        }

        stepsTimeAccumulator += delta;
        while(stepsTimeAccumulator >= 1/60f) {
            b2dWorld.step(1/60f, 6, 2);
            stepsTimeAccumulator -= 1/60f;
        }
        //b2dWorld.step(1f/Consts.SERVER_DEFAULT_TPS, 6, 2);

        for(ExpiPlayer pp : server.getPlayers()) {
            for(ExpiEntity ee : server.getEntities()) {
                pp.getNetManager().putEntityMovePacket(ee);
            }
            pp.getNetManager().putEnviroPacket();
        }
    }

    public void onInteract(PlayerInteractEvent event) {

        // this is only for tile placing

        ExpiPlayer p = event.getPlayer();
        if(!p.isInInteractRadius(event.getLoc())) return;

        Item item = p.getInv().getItemInHand().getItem();
        ExpiTile t = event.getTile();

        if(item.getCategory() != ItemCategory.MATERIAL || event.getTile().getType() != TileType.AIR) return;

        if(item.getBuildTile() == null) return;

        if(!isTileFree(t, item)) return;

        if(!stabilityCalc.canBeChanged(t, item.getBuildTile())) return;

        // confirmed from here
        p.getInv().removeItem(new ItemStack(item, 1));
        p.getNetManager().putInvFeedPacket();

        for(ExpiPlayer ep : server.getPlayers()) {
            ep.getNetManager().putHandPunchPacket(p);
        }

        changeTile(t, item.getBuildTile(), true, p, Source.PLAYER);
    }

    public void changeTile(ExpiTile t, TileType to, boolean withDrops, ExpiPlayer p, Source source) {

        TileType from = t.getType();

        if(to == TileType.AIR && withDrops) {
            createDroppedItem(t);
        }

        t.setTypeFront(to);
        fixtureCalc.updateTileFixturesAndNearbyTiles(t);

        List<ExpiTile> changedTiles = new ArrayList<>();
        changedTiles.add(t);

        HashSet<ExpiTile> affectedTiles = stabilityCalc.updateStability(t);
        int size = affectedTiles.size();
        Iterator<ExpiTile> it = affectedTiles.iterator();
        while(it.hasNext()) {
            ExpiTile t2 = it.next();
            if(t2.getStability() != 0) continue;
            if(Math.random() < 2.0 / size && withDrops && t2.getType() != TileType.AIR) { //todo: ta posledna kontrola je zbytocna, ale funguje
                createDroppedItem(t2);
            }
            t2.setTypeFront(TileType.AIR);
            fixtureCalc.updateTileFixturesAndNearbyTiles(t2);
            changedTiles.add(t2);
            it.remove();
        }

        affectedTiles.add(t);

        for(ExpiPlayer pp : server.getPlayers()) {
            for(ExpiTile t2 : changedTiles) {
                pp.getNetManager().putTileChangePacket(t2);
            }
            pp.getNetManager().putStabilityPacket(affectedTiles);
        }

        TileChangeEvent e = new TileChangeEvent(t, from, p, source);
        List<TileChangeListener> list = server.getEventManager().getTileChangeListeners();
        for(int i = list.size() - 1; i >= 0; i--) {
            list.get(i).onTileChange(e);
        }
    }

    /**
     * Call right before tile is changed.
     */
    private void createDroppedItem(ExpiTile t) {
        if(t == null || t.getType() == TileType.AIR) return;
        float off = (1 - EntityType.DROPPED_ITEM.getWidth())/2;
        Vector2 loc = new Vector2(t.getX() + off, t.getY() + off);
        for(Item item : ItemDropper.chooseItems(t.getType().getItemDropper())) {
            if(item == Item.EMPTY) return;
            spawnDroppedItem(item, loc, Consts.ITEM_COOLDOWN_BREAK);
        }
    }

    private boolean isTileFree(ExpiTile t, Item toPlace) {

        int x = t.getX();
        int y = t.getY();

        if(toPlace.getBuildTile().getFix() == TileFix.SOFT) return true;

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
        while(terrain[y][x].getType().getFix() == TileFix.SOFT) {
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

        System.out.println("teleport req to: "+x+" "+y);

        while(true) {
            if(isPlaceSafe(e, rightX, y)) {
                e.teleport(rightX, y);
                System.out.println("succes right: "+rightX+" "+y);
                break;
            }
            if(isPlaceSafe(e, leftX, y)) {
                e.teleport(leftX, y);
                System.out.println("succes left: "+leftX+" "+y);
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
            System.out.println("not success");
        }

    }

    private boolean isPlaceSafe(ExpiEntity e, float x, float y) {

        float ew = e.getType().getWidth();
        float eh = e.getType().getHeight();

        if(x <= 0 || x + ew >= width || y <= 0 || y + eh >= height) return false;

        if(getTileAt(x, y).getType().getFix() != TileFix.SOFT) return false;
        if(getTileAt(x + ew, y).getType().getFix() != TileFix.SOFT) return false;
        if(getTileAt(x + ew, y + eh).getType().getFix() != TileFix.SOFT) return false;
        if(getTileAt(x, y + eh).getType().getFix() != TileFix.SOFT) return false;

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

    public float getTime() {
        return dayTime;
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

    public int getDay() {
        return day;
    }

    public void spawnDroppedItem(Item item, Vector2 loc, float cooldown) {
        ExpiDroppedItem edi = new ExpiDroppedItem(server, loc, item, cooldown);
        for(ExpiPlayer pp : server.getPlayers()) {
            pp.getNetManager().putEntitySpawnPacket(edi);
        }
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

        out.writeInt(width);
        out.writeInt(height);
        out.writeLong(seed);

        for(int h = 0; h < height; h++) {
            for(int w = 0; w < width; w++) {
                out.writeByte(terrain[h][w].getType().getID());
                out.writeByte(terrain[h][w].getTypeBack().getID());
            }
        }

        out.writeFloat(dayTime);
        out.writeInt(day);

    }
}
