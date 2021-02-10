package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.WorldLoader;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.inventory.ExpiInventory;
import com.astetyne.expirium.server.core.world.inventory.ExpiPlayerInventory;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.astetyne.expirium.server.net.ServerPacketManager;
import com.astetyne.expirium.server.net.ServerPlayerGateway;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.IOException;

public class ExpiPlayer extends LivingEntity {

    private final ServerPlayerGateway gateway;
    private final String name;
    private final ExpiPlayerInventory mainInv;
    private ExpiInventory secondInv;
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
        mainInv = new ExpiPlayerInventory(this, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_MAX_WEIGHT);
        secondInv = new ExpiInventory(1, 1, 1, false);
        tileBreaker = new ExpiTileBreaker(server, this);
        tsData1 = new ThumbStickData();
        tsData2 = new ThumbStickData();
        lastJump = 0;
        resurrectLoc = new Vector2(location);
        wasAlreadyDead = false;
        lastDeathDay = 0;
        worldLoader = new WorldLoader(server, this);
        postInit();
        server.getPlayers().add(this);
    }

    public ExpiPlayer(ExpiServer server, ServerPlayerGateway gateway, String name, DataInputStream in) throws IOException {
        super(server, EntityType.PLAYER, in);
        // order is important - must be same as in writeData()
        this.gateway = gateway;
        gateway.setOwner(this);
        this.name = name;
        mainInv = new ExpiPlayerInventory(this, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_MAX_WEIGHT, in);
        secondInv = new ExpiInventory(1, 1, 1, false);
        tileBreaker = new ExpiTileBreaker(server, this);
        tsData1 = new ThumbStickData();
        tsData2 = new ThumbStickData();
        lastJump = 0;
        resurrectLoc = new Vector2(in.readFloat(), in.readFloat());
        wasAlreadyDead = in.readBoolean();
        lastDeathDay = in.readLong();
        worldLoader = new WorldLoader(server, this);
        postInit();
        server.getPlayers().add(this);
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
                server.getWorld().spawnDroppedItem(is.getItem(), getCenter(), Consts.ITEM_COOLDOWN_DROP);
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

    public void onInvMove(PacketInputStream in) {

        boolean fromMain = in.getBoolean();
        IntVector2 pos1 = in.getIntVector();
        boolean toMain = in.getBoolean();
        IntVector2 pos2 = in.getIntVector();

        int row1 = pos1.y;
        int column1 = pos1.x;
        int row2 = pos2.y;
        int column2 = pos2.x;

        if(row1 == row2 && column1 == column2) return;

        //System.out.println("fromMain: "+fromMain+" pos1: "+pos1+" toMain: "+toMain+" pos2: "+pos2);

        if(fromMain) {
            GridItemStack is = mainInv.getGrid()[row1][column1];
            if(is == null) return;

            if(column2 == -1) {
                mainInv.removeGridItem(is);
                throwAwayItem(is);
                return;
            }

            if(toMain) {
                if(row2 == 0 && column2 == mainInv.getColumns() - 2 && mainInv.isPlaceFor(is.getItem())) { //split
                    if(is.getAmount() == 1) return;
                    is.decreaseAmount(1);
                    mainInv.decreaseWeight(is.getItem().getWeight());
                    mainInv.addItem(new ItemStack(is.getItem()), false);
                    getNetManager().putInvFeedPacket();
                    return;
                }else if(row2 == 0 && column2 == mainInv.getColumns() - 1) { //throw
                    mainInv.removeGridItem(is);
                    throwAwayItem(is);
                    return;
                }
                ItemStack destIS = mainInv.getGrid()[row2][column2];
                if(destIS != null && destIS != is && destIS.getItem() == is.getItem()) {
                    destIS.increaseAmount(is.getAmount());
                    mainInv.increaseWeight(is.getItem().getWeight() * is.getAmount());
                    mainInv.removeGridItem(is);
                    getNetManager().putInvFeedPacket();
                    return;
                }
                if(!mainInv.isPlaceFor(is.getItem(), row2, column2, row1, column1)) return;
                mainInv.cleanGridFrom(is);
                is.getGridPos().set(pos2);
                mainInv.insertToGrid(is);
            }else {
                if(!secondInv.canBeAdded(is.getItem(), is.getAmount())) return;
                secondInv.addItem(is, true);
                mainInv.removeGridItem(is);
            }
        }else {
            GridItemStack is = secondInv.getGrid()[row1][column1];
            if(is == null) return;

            if(column2 == -1) {
                secondInv.removeGridItem(is);
                throwAwayItem(is);
                return;
            }

            if(toMain) {
                if(row2 == 0 && column2 == mainInv.getColumns() - 2 && secondInv.isPlaceFor(is.getItem())) { //split
                    if(is.getAmount() == 1) return;
                    is.decreaseAmount(1);
                    secondInv.decreaseWeight(is.getItem().getWeight());
                    secondInv.addItem(new ItemStack(is.getItem()), false);
                    getNetManager().putInvFeedPacket();
                    return;
                }else if(row2 == 0 && column2 == mainInv.getColumns() - 1) { //throw
                    secondInv.removeGridItem(is);
                    throwAwayItem(is);
                    return;
                }
                if(!mainInv.canBeAdded(is.getItem(), is.getAmount())) return;
                mainInv.addItem(is, true);
                secondInv.removeGridItem(is);
            }else {
                ItemStack destIS = secondInv.getGrid()[row2][column2];
                if(destIS != null && destIS != is && destIS.getItem() == is.getItem()) {
                    destIS.increaseAmount(is.getAmount());
                    secondInv.increaseWeight(is.getItem().getWeight() * is.getAmount());
                    secondInv.removeGridItem(is);
                    getNetManager().putInvFeedPacket();
                    return;
                }
                if(!secondInv.isPlaceFor(is.getItem(), row2, column2, row1, column1)) return;
                secondInv.cleanGridFrom(is);
                is.getGridPos().set(pos2);
                secondInv.insertToGrid(is);
            }
        }
        getNetManager().putInvFeedPacket();
    }

    private void throwAwayItem(ItemStack is) {
        for(int i = 0; i < is.getAmount(); i++) {
            //todo: vytvorit spravnu lokaciu itemu, podla otocenia hraca? podla okolitych blokov?
            server.getWorld().spawnDroppedItem(is.getItem(), getCenter(), Consts.ITEM_COOLDOWN_DROP);
        }
        getNetManager().putInvFeedPacket();
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
            vel.x -= 0.1f;
        }else if(onGround){
            vel.x += 0.1f;
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

        if(mainInv.isInvalid() || secondInv.isInvalid()) {
            getNetManager().putInvFeedPacket();
            mainInv.setInvalid(false);
            secondInv.setInvalid(false);
        }

        tileBreaker.onTick(tsData2);
    }

    public void wantsToMakeItem(ItemRecipe recipe) {
        System.out.println("Wants to make item");
        if(Consts.DEBUG) {
            System.out.println("Overriding item check (debug mode)");
            mainInv.addItem(recipe.getProduct(), true);
            getNetManager().putInvFeedPacket();
            return;
        }
        for(ItemStack is : recipe.getRequiredItems()) {
            if(!mainInv.contains(is)) return;
        }
        System.out.println("Has req items");
        if(!mainInv.canBeAdded(recipe.getProduct().getItem(), recipe.getProduct().getAmount())) return;
        System.out.println("Can be added");

        for(ItemStack is : recipe.getRequiredItems()) {
            mainInv.removeItem(is);
        }
        mainInv.addItem(recipe.getProduct(), true);
        getNetManager().putInvFeedPacket();
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
        destroySafe();
        server.getPlayers().remove(this);
    }

    public void destroySafe() {
        super.destroy();
        server.getEventManager().getTickListeners().remove(this);
    }

    @Override
    public void writeMeta(PacketOutputStream out) {
        out.putString(name);
    }

    public ExpiPlayerInventory getInv() {
        return mainInv;
    }

    public ExpiInventory getSecondInv() {
        return secondInv;
    }

    public void setSecondInv(ExpiInventory secondInv) {
        this.secondInv = secondInv;
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
