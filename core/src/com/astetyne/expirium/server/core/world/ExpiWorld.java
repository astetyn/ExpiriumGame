package com.astetyne.expirium.server.core.world;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemCat;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.tiles.ItemDropper;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.Saveable;
import com.astetyne.expirium.server.core.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.core.entity.ExpiEntity;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.*;
import com.astetyne.expirium.server.core.world.calculator.BackWallCalculator;
import com.astetyne.expirium.server.core.world.calculator.FixtureCalculator;
import com.astetyne.expirium.server.core.world.calculator.StabilityCalculator;
import com.astetyne.expirium.server.core.world.generator.CreateWorldPreferences;
import com.astetyne.expirium.server.core.world.generator.WorldGenerator;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.TileFix;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ExpiWorld implements Saveable, Disposable, PlayerInteractListener {

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
    private int time; // from midnight = 0 ticks
    private int day; // completed days from server creation
    private final int width, height;
    private final long seed;

    public ExpiWorld(CreateWorldPreferences preferences, ExpiServer server) {

        this.server = server;

        width = preferences.width;
        height = preferences.height;
        seed = preferences.seed;

        terrain = new ExpiTile[preferences.height][preferences.width];
        new WorldGenerator(terrain, seed).generateWorld();

        time = 11200; // 7:00
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
                terrain[h][w] = new ExpiTile(in, w, h);
            }
        }

        time = in.readInt();
        day = in.readInt();

        //todo: len docasne zatial
        weatherType = WeatherType.SUNNY;

        initAfterCreation();

    }

    private void initAfterCreation() {

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

        server.getEventManager().getPlayerInteractListeners().add(this);

    }

    public void dispose() {
        b2dWorld.dispose();
        fixtureCalc.dispose();
    }

    public void onTick() {

        time += 100;
        if(time == Consts.DAY_TICKS) { // midnight
            time = 0;
            day++;
        }

        for(ExpiPlayer pp : server.getPlayers()) {
            pp.applyPhysics();
        }

        b2dWorld.step(1f/Consts.SERVER_TPS, 6, 2);

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

        if(item.getCategory() != ItemCat.MATERIAL || event.getTile().getType() != TileType.AIR) return;

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

        if(to == TileType.AIR && withDrops) createDroppedItem(t);

        t.setType(to);
        fixtureCalc.updateTileFixturesAndNearbyTiles(t);

        for(ExpiPlayer pp : server.getPlayers()) {
            pp.getNetManager().putTileChangePacket(t);
        }

        TileChangeEvent e = new TileChangeEvent(t, from, p, source);

        stabilityCalc.onTileChange(e); // not sure if this should be before or after modules

        // modules
        List<TileChangeListener> list = server.getEventManager().getTileChangeListeners();
        for(int i = list.size() - 1; i >= 0; i--) {
            list.get(i).onTileChange(e);
        }

        backWallCalculator.onTileChange(e);
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
                terrain[h][w].writeData(out);
            }
        }

        out.writeInt(time);
        out.writeInt(day);

    }
}
