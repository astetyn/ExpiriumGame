package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.WorldLoader;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.inventory.Inventory;
import com.astetyne.expirium.server.core.world.inventory.PlayerInventory;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.astetyne.expirium.server.net.ServerPacketManager;
import com.astetyne.expirium.server.net.ServerPlayerGateway;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;

import java.io.DataInputStream;
import java.io.IOException;

public class ExpiPlayer extends LivingEntity {

    private final ServerPlayerGateway gateway;
    private final String name;
    private final PlayerInventory mainInv;
    private Inventory secondInv;
    private final ExpiTileBreaker tileBreaker;
    private final ThumbStickData tsData1, tsData2;
    private long lastJump;
    private final Vector2 resurrectLoc;
    private boolean wasAlreadyDead;
    private long lastDeathDay;
    private final WorldLoader worldLoader;

    public ExpiPlayer(ExpiServer server, Vector2 location, ServerPlayerGateway gateway, String name) {
        super(server, EntityType.PLAYER, location);
        this.gateway = gateway;
        gateway.setOwner(this);
        this.name = name;
        mainInv = new PlayerInventory(this, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_MAX_WEIGHT);
        secondInv = new Inventory(1, 1, 1);
        tileBreaker = new ExpiTileBreaker(server, this);
        tsData1 = new ThumbStickData();
        tsData2 = new ThumbStickData();
        lastJump = 0;
        resurrectLoc = new Vector2(location);
        wasAlreadyDead = false;
        lastDeathDay = 0;
        worldLoader = new WorldLoader(server, this);
        server.getPlayers().add(this);
    }

    public ExpiPlayer(ExpiServer server, ServerPlayerGateway gateway, String name, DataInputStream in) throws IOException {
        super(server, EntityType.PLAYER, in);
        // order is important - must be same as in writeData()
        this.gateway = gateway;
        gateway.setOwner(this);
        this.name = name;
        mainInv = new PlayerInventory(this, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_MAX_WEIGHT, in);
        secondInv = new Inventory(1, 1, 1);
        tileBreaker = new ExpiTileBreaker(server, this);
        tsData1 = new ThumbStickData();
        tsData2 = new ThumbStickData();
        lastJump = 0;
        resurrectLoc = new Vector2(in.readFloat(), in.readFloat());
        wasAlreadyDead = in.readBoolean();
        lastDeathDay = in.readLong();
        worldLoader = new WorldLoader(server, this);
        server.getPlayers().add(this);
    }

    @Override
    public void createBodyFixtures() {
        super.createBodyFixtures();
        bodyFix.setFriction(0);
        Filter filter = new Filter();
        filter.categoryBits = Consts.PLAYER_BIT;
        filter.maskBits = Consts.DEFAULT_BIT;
        bodyFix.setFilterData(filter);
    }

    @Override
    public void die() {
        invincible = true;
        getNetManager().putDeathPacket(!wasAlreadyDead, server.getWorld().getDay() - lastDeathDay);
        setFoodLevel(20);
        setHealthLevel(20);
        for(ItemStack is : mainInv.getItems()) {
            int dropAmount = (int) (Math.random() * is.getAmount()) + 1;
            for(int i = 0; i < dropAmount; i++) {
                server.getWorld().spawnEntity(EntityType.DROPPED_ITEM, getCenter(), is.getItem() , Consts.ITEM_COOLDOWN_DROP);
            }
        }
        mainInv.clear();
        server.getWorld().teleport(this, resurrectLoc.x, resurrectLoc.y);
        tsData1.reset();
        tsData2.reset();
        body.setLinearVelocity(0, 0);
        wasAlreadyDead = true;
        lastDeathDay = server.getWorld().getDay();
    }

    public void updateThumbSticks(PacketInputStream in) {
        tsData1.horz = in.getFloat();
        tsData1.vert = in.getFloat();
        tsData2.horz = in.getFloat();
        tsData2.vert = in.getFloat();
    }

    public void applyPhysics() {

        if(invincible && tsData1.horz != 0 || tsData1.vert != 0 || tsData2.horz != 0 || tsData2.vert != 0) {
            invincible = false;
        }

        float vY = body.getLinearVelocity().y;

        if(onGround && tsData1.vert >= 0.6f && lastJump + Consts.JUMP_DELAY < System.currentTimeMillis()) {
            Vector2 center = body.getWorldCenter();
            body.applyLinearImpulse(0, 320, center.x, center.y, true);
            lastJump = System.currentTimeMillis();
        }

        if((body.getLinearVelocity().x >= 3 && tsData1.horz > 0)) {
            tsData1.horz = 0;
            //body.setLinearVelocity(3, vY);
        }else if(body.getLinearVelocity().x <= -3 && tsData1.horz < 0) {
            tsData1.horz = 0;
            //body.setLinearVelocity(-3, vY);
        }else {
            body.applyForceToCenter((50000.0f/Consts.SERVER_TPS) * tsData1.horz, 0, true);
        }
        //System.out.println("loc: "+getLocation());
        Vector2 vel = body.getLinearVelocity();
        if(vel.x > 0 && onGround) {
            vel.x -= Math.min(0.1f, vel.x);
        }else if(onGround){
            vel.x += Math.min(0.1f, -vel.x);
        }
        body.setLinearVelocity(vel);
    }

    @Override
    public void onTick() {
        super.onTick();

        if(!worldLoader.isCompleted()) {
            worldLoader.onTick();
        }

        getNetManager().putLivingStatsPacket();

        if(mainInv.needsUpdate(this) || secondInv.needsUpdate(this)) {
            getNetManager().putInvFeedPacket();
            mainInv.updateHotSlots();
            mainInv.wasUpdated(this);
            secondInv.wasUpdated(this);
        }

        tileBreaker.onTick(tsData2);
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

    public void wantsToMakeItem(ItemRecipe recipe) {
        System.out.println("Wants to make item");
        if(Consts.DEBUG) {
            System.out.println("Overriding item check (debug mode)");
            mainInv.append(recipe.getProduct());
            return;
        }

        if(!mainInv.contains(recipe.getRequiredItems())) return;

        System.out.println("Has req items");
        if(!mainInv.canAppend(recipe.getProduct())) return;
        System.out.println("Can be added");

        for(ItemStack is : recipe.getRequiredItems()) {
            mainInv.remove(is);
        }
        mainInv.append(recipe.getProduct());
        System.out.println("Added");
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

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        mainInv.writeData(out);
        out.writeFloat(resurrectLoc.x);
        out.writeFloat(resurrectLoc.y);
        out.writeBoolean(wasAlreadyDead);
        out.writeLong(lastDeathDay);
    }
}
