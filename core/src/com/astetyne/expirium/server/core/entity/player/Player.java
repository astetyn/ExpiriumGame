package com.astetyne.expirium.server.core.entity.player;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.resources.PlayerCharacter;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.ExpiColor;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.Entity;
import com.astetyne.expirium.server.core.entity.LivingEntity;
import com.astetyne.expirium.server.core.world.WorldLoader;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.inventory.Inventory;
import com.astetyne.expirium.server.core.world.inventory.PlayerInventory;
import com.astetyne.expirium.server.core.world.tile.Tile;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.astetyne.expirium.server.net.ServerPacketManager;
import com.astetyne.expirium.server.net.ServerPlayerGateway;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;

public class Player extends LivingEntity {

    public final static byte MAX_FOOD_LEVEL = 100;
    private final static byte MAX_HEALTH = 100;
    private final static float jumpThreshold = 0.6f;

    private final ServerPlayerGateway gateway;
    private final String name;
    private byte foodLevel;
    private final PlayerInventory mainInv;
    private Inventory secondInv;
    private ToolManager toolManager;
    private final ThumbStickData tsData1, tsData2;
    private long lastJump;
    private final Vector2 resurrectLoc;
    private boolean firstLife;
    private long lastDeathDay;
    private final WorldLoader worldLoader;
    private final HashSet<Entity> nearActiveEntities;
    private final HashSet<LivingEffect> activeLivingEffects;
    private boolean onLadder;
    private long lastWaterManipulation;
    private final PlayerCharacter character;

    public Player(ExpiServer server, ServerPlayerGateway gateway, String name, PlayerCharacter character, DataInputStream in) throws IOException {
        super(server, EntityType.PLAYER, MAX_HEALTH, in);
        this.gateway = gateway;
        gateway.setOwner(this);
        this.name = name;
        this.character = character;
        foodLevel = in.readByte();
        mainInv = new PlayerInventory(this, Consts.INV_MAX_SIZE, Consts.INV_MAX_SIZE, Consts.PLAYER_INV_MAX_WEIGHT, in);
        secondInv = new Inventory(1, 1, 1);
        tsData1 = new ThumbStickData();
        tsData2 = new ThumbStickData();
        toolManager = new TileBreakToolManager(server, this, tsData2);
        lastJump = 0;
        resurrectLoc = new Vector2(in.readFloat(), in.readFloat());
        firstLife = in.readBoolean();
        lastDeathDay = in.readLong();
        worldLoader = new WorldLoader(server, this);
        nearActiveEntities = new HashSet<>();
        activeLivingEffects = new HashSet<>();
        onLadder = false;
        lastWaterManipulation = 0;
        server.getWorld().scheduleTaskAfter(this::interval4Sec, Consts.SERVER_TPS * 4);
        server.getWorld().scheduleTaskAfter(this::interval20Sec, Consts.SERVER_TPS * 20);
        server.getWorld().scheduleTaskAfter(this::plannedRecalcNearEntities, Consts.SERVER_TPS/2);
        server.getPlayers().add(this);
        if(Consts.DEBUG) {
            // for fast item testing - append items here
            getInv().append(Item.BUCKET_WATER, 20);
            getInv().append(Item.SMALL_MEAT_RAW, 20);
            getInv().append(Item.LADDER_WALL, 20);
            getInv().append(Item.RECYCLER, 20);
            getInv().append(Item.COAL, 20);
        }
    }

    @Override
    public void createBodyFixtures() {
        super.createBodyFixtures();
        bodyFix.setFriction(0);
        Filter filter = new Filter();
        filter.categoryBits = Consts.PLAYER_BIT;
        filter.maskBits = Consts.DEFAULT_BIT;
        bodyFix.setFilterData(filter);
        body.setUserData(this);
    }

    // once per 4 seconds
    protected void interval4Sec() {
        if(!alive) return;

        if(foodLevel <= Consts.STARVATION_LEVEL) injure(1);
        else if(foodLevel >= 90) heal(1);

        server.getWorld().scheduleTaskAfter(this::interval4Sec, Consts.SERVER_TPS * 4);
    }

    // once per 20 seconds
    protected void interval20Sec() {
        if(!alive) return;
        decreaseFoodLevel(1);
        server.getWorld().scheduleTaskAfter(this::interval20Sec, Consts.SERVER_TPS * 20);
    }

    @Override
    public void die() {
        invincible = true;
        getNetManager().putDeathPacket(firstLife, server.getWorld().getDay() - lastDeathDay);
        foodLevel = 20;
        setHealthLevel(20);
        for(ItemStack is : mainInv.getItems()) {
            int dropAmount = (int) (Math.random() * is.getAmount()) + 1;
            for(int i = 0; i < dropAmount; i++) {
                server.getWorld().spawnEntity(EntityType.DROPPED_ITEM, getCenter(), is.getItem() , Consts.ITEM_COOLDOWN_DROP);
            }
        }
        mainInv.clear();
        server.getWorld().teleport(this, resurrectLoc, 20);
        tsData1.reset();
        tsData2.reset();
        body.setLinearVelocity(0, 0);
        firstLife = false;
        lastDeathDay = server.getWorld().getDay();
    }

    public void updateThumbSticks(PacketInputStream in) {
        tsData1.horz = in.getFloat();
        tsData1.vert = in.getFloat();
        tsData2.horz = in.getFloat();
        tsData2.vert = in.getFloat();
    }

    public void applyPhysics() {
        super.applyPhysics();

        Vector2 vel = body.getLinearVelocity();

        if(tsData1.vert >= jumpThreshold) {
            tsData1.horz = Math.abs(tsData1.horz) > 0.5 ? tsData1.horz : 0;
        }

        float horzFactor;
        float maxHorzVel;

        if(inWater) {
            horzFactor = 500;
            maxHorzVel = 2;
            if(tsData1.vert >= jumpThreshold) {
                body.applyForceToCenter(0, 400, true);

                if(!underWater && onGround && lastJump + Consts.JUMP_DELAY < System.currentTimeMillis()) {
                    Vector2 center = body.getWorldCenter();
                    body.applyLinearImpulse(0, 200, center.x, center.y, true);
                    lastJump = System.currentTimeMillis();
                }
            }
        }else if(onLadder) {

            horzFactor = 800;
            maxHorzVel = 2f;

            float area = type.getWidth() * type.getHeight();
            tempVec.set(0, area * BODY_DENSITY * body.getWorld().getGravity().y * -0.7f);
            body.applyForceToCenter(tempVec, true);
            if(vel.y <= -2) {
                body.setLinearVelocity(vel.x, -2);
            }
            if(tsData1.vert >= jumpThreshold && vel.y <= 5) {
                body.applyForceToCenter(0, tsData1.vert * 500, true);
            }
        }else {
            horzFactor = 1250;
            maxHorzVel = 3;
            if(tsData1.vert >= jumpThreshold && onGround && lastJump + Consts.JUMP_DELAY < System.currentTimeMillis()) {
                Vector2 center = body.getWorldCenter();
                body.applyLinearImpulse(0, 350f, center.x, center.y, true);
                lastJump = System.currentTimeMillis();
            }
        }

        if(!(vel.x >= maxHorzVel && tsData1.horz > 0) && !(vel.x <= -maxHorzVel && tsData1.horz < 0)) {
            body.applyForceToCenter(horzFactor * tsData1.horz, 0, true);
        }

        vel = body.getLinearVelocity();

        // ground friction
        if(onGround) {
            if(vel.x > 0) {
                vel.x -= Math.min(0.15f, vel.x);
            }else {
                vel.x += Math.min(0.15f, -vel.x);
            }
        }else {
            if(vel.x > 0) {
                vel.x -= Math.min(0.01f, vel.x);
            }else {
                vel.x += Math.min(0.01f, -vel.x);
            }
        }
        body.setLinearVelocity(vel);
    }

    @Override
    public void onTick() {
        super.onTick();

        if(invincible && (tsData1.horz != 0 || tsData1.vert != 0 || tsData2.horz != 0 || tsData2.vert != 0)) {
            invincible = false;
        }

        if(!worldLoader.isCompleted()) {
            worldLoader.onTick();
        }

        recalcLadder();
        getNetManager().putLivingStatsPacket();

        if(mainInv.needsUpdate(this) || secondInv.needsUpdate(this)) {
            getNetManager().putInvFeedPacket();
            mainInv.updateHotSlots();
            mainInv.wasUpdated(this);
            secondInv.wasUpdated(this);
        }

        Item itemInHand = mainInv.getItemInHand().getItem();

        if((itemInHand.isTileBreaker() || itemInHand == Item.EMPTY) && !(toolManager instanceof TileBreakToolManager)) {
            toolManager.end();
            toolManager = new TileBreakToolManager(server, this, tsData2);
        }else if(itemInHand.isWeapon() && !(toolManager instanceof CombatToolManager)) {
            toolManager.end();
            toolManager = new CombatToolManager(server, this, tsData2);
        }
        toolManager.onTick();

        checkEffects();
    }

    private void checkEffects() {
        if(underWater) {
            if(ticksUnderWater >= Consts.DROWNING_TICKS) {
                activeLivingEffects.remove(LivingEffect.DROWNING);
                activeLivingEffects.add(LivingEffect.SERIOUS_DROWNING);
            }else {
                activeLivingEffects.add(LivingEffect.DROWNING);
            }
        }else {
            activeLivingEffects.remove(LivingEffect.DROWNING);
            activeLivingEffects.remove(LivingEffect.SERIOUS_DROWNING);
        }
        if(foodLevel <= Consts.STARVATION_LEVEL) {
            activeLivingEffects.add(LivingEffect.STARVATION);
        }else {
            activeLivingEffects.remove(LivingEffect.STARVATION);
        }
    }

    @Override
    public void recalcLookingDir() {
        super.recalcLookingDir();
        if(tsData2.horz > 0) {
            lookingRight = true;
        }else if(tsData2.horz < 0) {
            lookingRight = false;
        }
    }

    private void recalcLadder() {

        float w = getWidth();
        float h = getHeight();
        float wh = w/2;
        float hh = h/2;
        Vector2 center = getCenter();

        int leftX = (int) (center.x - wh);
        int bottomY = (int) (center.y - hh);

        for(int x = leftX; x <= center.x + wh; x++) {
            for(int y = bottomY; y <= center.y + hh; y++) {
                Tile t = server.getWorld().getTileAt(x, y);
                if(t.getMaterial().isClimbable()) {
                    onLadder = true;
                    return;
                }
            }
        }
        onLadder = false;

    }

    private void plannedRecalcNearEntities() {
        recalcNearEntities();
        server.getWorld().scheduleTaskAfter(this::plannedRecalcNearEntities, Consts.SERVER_TPS/2);
    }

    public void recalcNearEntities() {
        nearActiveEntities.clear();
        for(Entity ee : server.getEntities()) {
            if(ee == this) continue;
            if(ee.getCenter().dst(getCenter()) <= Consts.ACTIVE_ENTITIES_RADIUS) nearActiveEntities.add(ee);
        }
    }

    public void wantsToMakeItem(ItemRecipe recipe) {
        if(Consts.DEBUG) {
            mainInv.append(recipe.getProduct());
            return;
        }
        if(!mainInv.contains(recipe.getRequiredItems())) {
            getNetManager().putWarningPacket("Not enough resources.", 500, ExpiColor.RED);
            return;
        }
        if(!mainInv.canAppend(recipe.getProduct())) {
            getNetManager().putWarningPacket("Not enough space.", 500, ExpiColor.RED);
            return;
        }

        for(ItemStack is : recipe.getRequiredItems()) {
            mainInv.remove(is);
        }
        mainInv.append(recipe.getProduct());
    }

    public void writeLivingStats(PacketOutputStream out) {
        out.putByte(getHealthLevel());
        out.putByte(foodLevel);
        out.putByte(getActiveLivingEffects().size());
        for(LivingEffect effect : getActiveLivingEffects()) {
            out.putByte(effect.ordinal());
        }
    }

    public ServerPacketManager getNetManager() {
        return gateway.getManager();
    }

    public ServerPlayerGateway getGateway() {
        return gateway;
    }

    public String getName() {
        return name;
    }

    public void increaseFoodLevel(int i) {
        foodLevel = (byte) Math.min(foodLevel + i, MAX_FOOD_LEVEL);
    }

    public void decreaseFoodLevel(int amount) {
        if(invincible) return;
        foodLevel = (byte) Math.max(foodLevel - amount, 0);
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public void destroy() {
        super.destroy();
        server.getPlayers().remove(this);
    }

    public void destroySafe() {
        super.destroy();
    }

    @Override
    public void writeInitClientMeta(PacketOutputStream out) {
        out.putByte(character.ordinal());
        out.putString(name);
    }

    public PlayerInventory getInv() {
        return mainInv;
    }

    public Inventory getSecondInv() {
        return secondInv;
    }

    public void setSecondInv(Inventory secondInv) {
        this.secondInv = secondInv;
        secondInv.willNeedUpdate();
    }

    public Vector2 getResurrectLoc() {
        return resurrectLoc;
    }

    public void setResurrectLoc(float x, float y) {
        resurrectLoc.x = x;
        resurrectLoc.y = y;
    }

    public boolean isInInteractRadius(Vector2 loc) {
        return isInInteractRadius(loc.x, loc.y);
    }

    public boolean isInInteractRadius(float x, float y) {
        return getCenter().dst(x, y) <= Consts.INTERACT_RADIUS;
    }

    public HashSet<Entity> getNearActiveEntities() {
        return nearActiveEntities;
    }

    public boolean isHoldingTS() {
        return tsData1.horz != 0 || tsData1.vert != 0 || tsData2.vert != 0 || tsData2.horz != 0;
    }

    public HashSet<LivingEffect> getActiveLivingEffects() {
        return activeLivingEffects;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    public boolean isAbleManipulateWater() {
        return lastWaterManipulation + Consts.WATER_MANIPULATE_COOLDOWN < System.currentTimeMillis();
    }

    public void waterManipulate() {
        lastWaterManipulation = System.currentTimeMillis();
    }

    public byte getFoodLevel() {
        return foodLevel;
    }

    public boolean isLivingFirstLife() {
        return firstLife;
    }

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeByte(foodLevel);
        mainInv.writeData(out);
        out.writeFloat(resurrectLoc.x);
        out.writeFloat(resurrectLoc.y);
        out.writeBoolean(firstLife);
        out.writeLong(lastDeathDay);
    }

    public static void writeDefaultData(WorldBuffer out, Vector2 spawnLoc) {
        LivingEntity.writeDefaultData(out, spawnLoc, MAX_HEALTH);
        out.writeByte(MAX_FOOD_LEVEL);
        PlayerInventory.writeDefaultData(out);
        out.writeFloat(spawnLoc.x);
        out.writeFloat(spawnLoc.y);
        out.writeBoolean(true);
        out.writeLong(0);
    }
}
