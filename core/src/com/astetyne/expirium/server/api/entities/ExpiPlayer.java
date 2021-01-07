package com.astetyne.expirium.server.api.entities;

import com.astetyne.expirium.main.entity.EntityBodyFactory;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.inventory.ExpiInventory;
import com.astetyne.expirium.server.api.world.inventory.ExpiPlayerInventory;
import com.astetyne.expirium.server.backend.ExpiTileBreaker;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.astetyne.expirium.server.backend.PacketOutputStream;
import com.astetyne.expirium.server.backend.ServerPlayerGateway;
import com.badlogic.gdx.math.Vector2;

public class ExpiPlayer extends ExpiEntity {

    private final ServerPlayerGateway gateway;
    private String name;
    private final ExpiPlayerInventory mainInv;
    private ExpiInventory secondInv;
    private final ExpiTileBreaker tileBreaker;
    private float ts1H, ts1V, ts2H, ts2V;

    public ExpiPlayer(Vector2 location, ServerPlayerGateway gateway, String name) {
        super(EntityType.PLAYER, 0.9f, 1.25f);
        body = EntityBodyFactory.createPlayerBody(location);
        GameServer.get().getWorld().getCL().registerListener(EntityBodyFactory.createSensor(body), this);
        this.gateway = gateway;
        this.name = name;
        secondInv = new ExpiInventory(1, 1, 1);
        tileBreaker = new ExpiTileBreaker(this);
        GameServer.get().getPlayers().add(this);
        mainInv = new ExpiPlayerInventory(this, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_MAX_WEIGHT);
        ts1H = ts1V = ts2H = ts2V = 0;
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

        System.out.println("fromMain: "+fromMain+" pos1: "+pos1+" toMain: "+toMain+" pos2: "+pos2);

        if(fromMain) {
            ItemStack origIs = mainInv.getGrid()[pos1.y][pos1.x];
            if(origIs == null) return;
            ItemStack is = new ItemStack(origIs);

            if(pos2.x == -1) {
                mainInv.removeItem(is);
                throwAwayItem(is);
                return;
            }

            if(toMain) {
                //todo: split / throw check
                if(!mainInv.canBeAdded(origIs, pos2.y, pos2.x)) return;
                mainInv.cleanGridFrom(origIs);
                origIs.getGridPos().set(pos2);
                mainInv.insertToGrid(origIs);
            }else {
                if(!secondInv.canBeAdded(is.getItem(), is.getAmount())) return;
                secondInv.addItem(is, true);
                mainInv.removeItem(is);
            }
        }else {
            ItemStack origIs = secondInv.getGrid()[pos1.y][pos1.x];
            if(origIs == null) return;
            ItemStack is = new ItemStack(origIs);

            if(pos2.x == -1) {
                secondInv.removeItem(is);
                throwAwayItem(is);
                return;
            }

            if(toMain) {
                //todo: split / throw check
                if(!mainInv.canBeAdded(is.getItem(), is.getAmount())) return;
                mainInv.addItem(is, true);
                secondInv.removeItem(is);
            }else {
                if(!secondInv.canBeAdded(origIs, pos2.y, pos2.x)) return;
                secondInv.cleanGridFrom(origIs);
                origIs.getGridPos().set(pos2);
                secondInv.insertToGrid(origIs);
            }
        }
        gateway.getManager().putInvFeedPacket(this);
    }

    public void onInvSplit(PacketInputStream in) {
        boolean inMain = in.getBoolean();
        IntVector2 pos = in.getIntVector();
        if(inMain) {
            ItemStack is = mainInv.getGrid()[pos.y][pos.x];
            if(is == null || !mainInv.canBeAdded(is.getItem(), 0)) return;
            is.decreaseAmount(1);
            mainInv.addItem(new ItemStack(is.getItem()), false);
        }else {
            ItemStack is = secondInv.getGrid()[pos.y][pos.x];
            if(is == null || !secondInv.canBeAdded(is.getItem(), 0)) return;
            is.decreaseAmount(1);
            secondInv.addItem(new ItemStack(is.getItem()), false);
        }
    }

    private void throwAwayItem(ItemStack is) {
        for(ExpiPlayer pp : GameServer.get().getPlayers()) {
            for(int i = 0; i < is.getAmount(); i++) {
                //todo: vytvorit spravnu lokaciu itemu, podla otocenia hraca? podla okolitych blokov?
                ExpiDroppedItem edi = new ExpiDroppedItem(getCenter(), is.getItem(), Consts.SERVER_DEFAULT_TPS);
                pp.getGateway().getManager().putEntitySpawnPacket(edi);
            }
        }
        gateway.getManager().putInvFeedPacket(this);
    }

    public void move() {

        tileBreaker.update(ts2H, ts2V);

        Vector2 center = body.getWorldCenter();
        float jump = 0;
        if(onGround || Consts.DEBUG) {
            if(body.getLinearVelocity().y < 5 && ts1V >= 0.6f) {
                jump = 1;
            }
        }
        if((body.getLinearVelocity().x >= 3 && ts1H > 0) || (body.getLinearVelocity().x <= -3 && ts1H < 0)) {
            ts1H = 0;
        }
        body.applyLinearImpulse(0, Math.min((3200.0f/Consts.SERVER_DEFAULT_TPS), 200)*jump, center.x, center.y, true);
        body.applyForceToCenter((40000.0f/Consts.SERVER_DEFAULT_TPS) * ts1H, 0, true);
    }

    public void wantsToMakeItem(ItemRecipe recipe) {
        for(ItemStack is : recipe.getRequiredItems()) {
            if(!mainInv.contains(is)) return;
        }
        for(ItemStack is : recipe.getRequiredItems()) {
            mainInv.removeItem(is);
        }
        mainInv.addItem(recipe.getProduct(), true);
        gateway.getManager().putInvFeedPacket(this);
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

    public void destroy() {
        super.destroy();
        GameServer.get().getPlayers().remove(this);
    }

    public void destroySafe() {
        super.destroy();
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
