package com.astetyne.expirium.server.api.entity;

import com.astetyne.expirium.main.entity.EntityBodyFactory;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.event.TickListener;
import com.astetyne.expirium.server.api.world.inventory.ExpiInventory;
import com.astetyne.expirium.server.api.world.inventory.ExpiPlayerInventory;
import com.astetyne.expirium.server.backend.*;
import com.badlogic.gdx.math.Vector2;

public class ExpiPlayer extends LivingEntity implements TickListener {

    private final ServerPlayerGateway gateway;
    private String name;
    private final ExpiPlayerInventory mainInv;
    private ExpiInventory secondInv;
    private final ExpiTileBreaker tileBreaker;
    private float ts1H, ts1V, ts2H, ts2V;
    private long lastJump;

    public ExpiPlayer(Vector2 location, ServerPlayerGateway gateway, String name) {
        super(EntityType.PLAYER, 0.9f, 1.25f);
        body = EntityBodyFactory.createPlayerBody(location);
        GameServer.get().getWorld().getCL().registerListener(EntityBodyFactory.createSensor(body), this);
        this.gateway = gateway;
        gateway.setOwner(this);
        this.name = name;
        secondInv = new ExpiInventory(1, 1, 1, false);
        tileBreaker = new ExpiTileBreaker(this);
        GameServer.get().getPlayers().add(this);
        mainInv = new ExpiPlayerInventory(this, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_MAX_WEIGHT);
        ts1H = ts1V = ts2H = ts2V = 0;
        TickLooper.getListeners().add(this);
        lastJump = 0;
    }

    public void updateThumbSticks(PacketInputStream in) {
        ts1H = in.getFloat();
        ts1V = in.getFloat();
        ts2H = in.getFloat();
        ts2V = in.getFloat();
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
            ItemStack is = mainInv.getGrid()[row1][column1];
            if(is == null) return;

            if(column2 == -1) {
                mainInv.removeItemStack(is);
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
                    mainInv.removeItemStack(is);
                    throwAwayItem(is);
                    return;
                }
                ItemStack destIS = mainInv.getGrid()[row2][column2];
                if(destIS != null && destIS != is && destIS.getItem() == is.getItem()) {
                    destIS.increaseAmount(is.getAmount());
                    mainInv.increaseWeight(is.getItem().getWeight() * is.getAmount());
                    mainInv.removeItemStack(is);
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
                mainInv.removeItemStack(is);
            }
        }else {
            ItemStack is = secondInv.getGrid()[row1][column1];
            if(is == null) return;

            if(column2 == -1) {
                secondInv.removeItemStack(is);
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
                    secondInv.removeItemStack(is);
                    throwAwayItem(is);
                    return;
                }
                if(!mainInv.canBeAdded(is.getItem(), is.getAmount())) return;
                mainInv.addItem(is, true);
                secondInv.removeItemStack(is);
            }else {
                ItemStack destIS = secondInv.getGrid()[row2][column2];
                if(destIS != null && destIS != is && destIS.getItem() == is.getItem()) {
                    destIS.increaseAmount(is.getAmount());
                    secondInv.increaseWeight(is.getItem().getWeight() * is.getAmount());
                    secondInv.removeItemStack(is);
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
        for(ExpiPlayer pp : GameServer.get().getPlayers()) {
            for(int i = 0; i < is.getAmount(); i++) {
                //todo: vytvorit spravnu lokaciu itemu, podla otocenia hraca? podla okolitych blokov?
                ExpiDroppedItem edi = new ExpiDroppedItem(getCenter(), is.getItem(), Consts.ITEM_COOLDOWN_DROP);
                pp.getNetManager().putEntitySpawnPacket(edi);
            }
        }
        getNetManager().putInvFeedPacket();
    }

    public void applyPhysics() {

        if(onGround) {
            Vector2 center = body.getWorldCenter();
            if(ts1V >= 0.6f && lastJump + Consts.JUMP_DELAY < System.currentTimeMillis()) {
                body.applyLinearImpulse(0, 200, center.x, center.y, true);
                lastJump = System.currentTimeMillis();
            }
        }
        float vY = body.getLinearVelocity().y;
        if((body.getLinearVelocity().x >= 3 && ts1H > 0)) {
            ts1H = 0;
            //body.setLinearVelocity(3, vY);
        }else if(body.getLinearVelocity().x <= -3 && ts1H < 0) {
            ts1H = 0;
            //body.setLinearVelocity(-3, vY);
        }else {
            body.applyForceToCenter((40000.0f/Consts.SERVER_DEFAULT_TPS) * ts1H, 0, true);
        }
    }

    @Override
    public void onTick() {

        foodLevel -= (1f / Consts.SERVER_DEFAULT_TPS) / 10; // tenth of second = 1 for 10 seconds

        getNetManager().putLivingStatsPacket();

        if(mainInv.isInvalid() || secondInv.isInvalid()) {
            getNetManager().putInvFeedPacket();
            mainInv.setInvalid(false);
            secondInv.setInvalid(false);
        }

        tileBreaker.update(ts2H, ts2V);
    }

    public void wantsToMakeItem(ItemRecipe recipe) {
        for(ItemStack is : recipe.getRequiredItems()) {
            if(!mainInv.contains(is)) return;
        }
        if(!mainInv.canBeAdded(recipe.getProduct().getItem(), recipe.getProduct().getAmount())) return;

        for(ItemStack is : recipe.getRequiredItems()) {
            mainInv.removeItem(is);
        }
        mainInv.addItem(recipe.getProduct(), true);
        getNetManager().putInvFeedPacket();
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
        GameServer.get().getPlayers().remove(this);
    }

    public void destroySafe() {
        super.destroy();
        TickLooper.getListeners().remove(this);
    }

    @Override
    public void readMeta(PacketInputStream in) {
        name = in.getString();
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


}
