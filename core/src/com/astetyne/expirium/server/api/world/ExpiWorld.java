package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemCategory;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.tiles.ItemDropper;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.Saveable;
import com.astetyne.expirium.server.api.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entity.ExpiEntity;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.*;
import com.astetyne.expirium.server.api.world.generator.CreateWorldPreferences;
import com.astetyne.expirium.server.api.world.generator.WorldGenerator;
import com.astetyne.expirium.server.api.world.generator.WorldLoadingException;
import com.astetyne.expirium.server.api.world.listeners.CampfireListener;
import com.astetyne.expirium.server.api.world.listeners.TreeListener;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;
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

    private final ExpiTile[][] worldTerrain;
    private int partHeight;
    private World b2dWorld;
    private Body terrainBody;
    private StabilityCalculator stabilityCalc;
    private FixtureCalculator fixtureCalc;
    private WeatherType weatherType;
    private ExpiContactListener contactListener;
    private float stepsTimeAccumulator;
    private float worldTime;
    private final int width, height;
    private final long seed;

    public ExpiWorld(CreateWorldPreferences preferences) {

        width = preferences.width;
        height = preferences.height;
        seed = preferences.seed;

        worldTerrain = new ExpiTile[preferences.height][preferences.width];
        new WorldGenerator(worldTerrain).generateWorld();

        worldTime = 0;
        weatherType = WeatherType.SUNNY;

        try {
            GameServer.get().getFileManager().saveWorld(this);
        }catch(IOException e) {
            e.printStackTrace();
        }

        initAfterCreation();

    }

    public ExpiWorld(DataInputStream in) throws WorldLoadingException {
        try {

            width = in.readInt();
            height = in.readInt();
            seed = in.readLong();

            worldTerrain = new ExpiTile[height][width];

            for(int h = 0; h < height; h++) {
                for(int w = 0; w < width; w++) {
                    worldTerrain[h][w] = new ExpiTile(TileType.getType(in.readByte()), TileType.getType(in.readByte()), w, h);
                }
            }

            //todo: len docasne zatial
            worldTime = 0;
            weatherType = WeatherType.SUNNY;

            initAfterCreation();

        }catch(IOException ignored) {
            throw new WorldLoadingException("IO Exception during world loading.");
        }
    }

    /**
     * This method is useful for world objects which require getWorld() from server in their constructor.
     */
    public void loadWorldStuff(DataInputStream in) throws WorldLoadingException {
        try {

            int entitiesSize = in.readInt();
            for(int i = 0; i < entitiesSize; i++) {
                EntityType.getType(in.readInt()).initEntity(in);
            }

        }catch(IOException ignored) {
            throw new WorldLoadingException("IO Exception during world stuff loading.");
        }
    }

    private void initAfterCreation() {

        stepsTimeAccumulator = 0;

        new CampfireListener();
        new TreeListener();

        partHeight = 2; // todo: this should be calculated from BUFFER_SIZE and terrainWidth

        b2dWorld = new World(new Vector2(0, -9.81f), false);

        contactListener = new ExpiContactListener();
        b2dWorld.setContactListener(contactListener);

        BodyDef terrainDef = new BodyDef();
        terrainDef.type = BodyDef.BodyType.StaticBody;
        terrainDef.position.set(0, 0);
        terrainBody = b2dWorld.createBody(terrainDef);

        stabilityCalc = new StabilityCalculator(worldTerrain);
        fixtureCalc = new FixtureCalculator(this, terrainBody);

        System.out.println("Creating fixtures...");
        fixtureCalc.generateWorldFixtures();
        System.out.println("Recalculating stability...");
        stabilityCalc.generateStability();
        System.out.println("Generating world done!");

        GameServer.get().getEventManager().getPlayerInteractListeners().add(this);

    }

    public void dispose() {
        b2dWorld.dispose();
    }

    public void onTick() {

        worldTime += 1f / Consts.SERVER_DEFAULT_TPS;
        if(worldTime == Consts.SERVER_DEFAULT_TPS * Consts.DAY_TIME_SEC) {
            worldTime = 0;
        }

        for(ExpiPlayer pp : GameServer.get().getPlayers()) {
            pp.applyPhysics();
        }

        stepsTimeAccumulator += 1f / Consts.SERVER_DEFAULT_TPS;
        while(stepsTimeAccumulator >= 1/60f) {
            b2dWorld.step(1/60f, 6, 2);
            stepsTimeAccumulator -= 1/60f;
        }
        //b2dWorld.step(1f/Consts.SERVER_DEFAULT_TPS, 6, 2);

        for(ExpiEntity ee : GameServer.get().getEntities()) {
            for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                pp.getNetManager().putEntityMovePacket(ee);
            }
        }
    }

    public void onInteract(PlayerInteractEvent event) {

        // this is only for tile breaking

        ExpiPlayer p = event.getPlayer();
        Item item = p.getInv().getItemInHand().getItem();
        ExpiTile t = event.getTile();

        if(item.getCategory() != ItemCategory.MATERIAL || event.getTile().getTypeFront() != TileType.AIR) return;

        if(item.getBuildTile() == null) return;

        if(!isTileFree(t, item)) return;

        if(!stabilityCalc.canBeChanged(t, item.getBuildTile())) return;

        // confirmed from here
        p.getInv().removeItem(new ItemStack(item, 1));
        p.getNetManager().putInvFeedPacket();

        changeTile(t, item.getBuildTile(), true, p, Source.PLAYER);
    }

    public void changeTile(ExpiTile t, TileType to, boolean withDrops, ExpiPlayer p, Source source) {

        TileType from = t.getTypeFront();

        if(to == TileType.AIR && withDrops) {
            createDroppedItem(t);
        }

        t.setTypeFront(to);
        fixtureCalc.recalcTileFixturesPlus(t);

        List<ExpiTile> changedTiles = new ArrayList<>();
        changedTiles.add(t);

        HashSet<ExpiTile> affectedTiles = stabilityCalc.updateStability(t);
        int size = affectedTiles.size();
        Iterator<ExpiTile> it = affectedTiles.iterator();
        while(it.hasNext()) {
            ExpiTile t2 = it.next();
            if(t2.getStability() != 0) continue;
            if(Math.random() < 2.0 / size && withDrops && t2.getTypeFront() != TileType.AIR) { //todo: ta posledna kontrola je zbytocna, ale funguje
                createDroppedItem(t2);
            }
            t2.setTypeFront(TileType.AIR);
            fixtureCalc.clearTileFixtures(t2);
            changedTiles.add(t2);
            it.remove();
        }

        affectedTiles.add(t);

        for(ExpiPlayer pp : GameServer.get().getPlayers()) {
            for(ExpiTile t2 : changedTiles) {
                pp.getNetManager().putTileChangePacket(t2);
            }
            pp.getNetManager().putStabilityPacket(affectedTiles);
        }

        TileChangeEvent e = new TileChangeEvent(t, from, p, source);
        List<TileChangeListener> list = GameServer.get().getEventManager().getTileChangeListeners();
        for(int i = list.size() - 1; i >= 0; i--) {
            list.get(i).onTileChange(e);
        }
    }

    /**
     * Call right before tile is changed.
     */
    private void createDroppedItem(ExpiTile t) {
        if(t == null || t.getTypeFront() == TileType.AIR) return;
        float off = (1 - Consts.D_I_SIZE)/2;
        Vector2 loc = new Vector2(t.getX() + off, t.getY() + off);
        for(Item item : ItemDropper.chooseItems(t.getTypeFront().getItemDropper())) {
            if(item == Item.EMPTY) return;
            ExpiDroppedItem droppedItem = new ExpiDroppedItem(loc, item, Consts.ITEM_COOLDOWN_BREAK);
            for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                pp.getNetManager().putEntitySpawnPacket(droppedItem);
            }
        }
    }

    private boolean isTileFree(ExpiTile t, Item toPlace) {

        int x = t.getX();
        int y = t.getY();

        if(toPlace.getBuildTile().getSolidity().isSoft()) return true;

        for(ExpiEntity p : GameServer.get().getEntities()) {

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

    public Vector2 getSaveLocationForSpawn() {
        int i = 0;
        while(i != height && worldTerrain[i][10].getTypeFront() != TileType.AIR) {
            i++;
        }
        return new Vector2(10, i+2);
    }

    public ExpiTile getTileAt(float x, float y) {
        return worldTerrain[(int)y][(int)x];
    }

    public ExpiTile getTileAt(Vector2 vec) {
        return worldTerrain[(int)vec.y][(int)vec.x];
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

    public float getWorldTime() {
        return worldTime;
    }

    public ExpiTile[][] getTerrain() {
        return worldTerrain;
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

    @Override
    public void writeData(DataOutputStream out) throws IOException {

        out.writeInt(width);
        out.writeInt(height);
        out.writeLong(seed);

        for(int h = 0; h < height; h++) {
            for(int w = 0; w < width; w++) {
                out.writeByte(worldTerrain[h][w].getTypeFront().getID());
                out.writeByte(worldTerrain[h][w].getTypeBack().getID());
            }
        }

        // world stuff from here
        int entitiesSize = 0;
        for(ExpiEntity e : GameServer.get().getEntities()) {
            if(e instanceof ExpiPlayer) continue;
            entitiesSize++;
        }
        out.writeInt(entitiesSize);
        for(ExpiEntity e : GameServer.get().getEntities()) {
            if(e instanceof ExpiPlayer) continue;
            out.writeInt(e.getType().getID());
            e.writeData(out);
        }

    }
}
