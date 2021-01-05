package com.astetyne.expirium.server.api.entities;

import com.astetyne.expirium.main.entity.EntityBodyFactory;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.inventory.ExpiPlayerInventory;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.astetyne.expirium.server.backend.PacketOutputStream;
import com.astetyne.expirium.server.backend.ServerPlayerGateway;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;

public class ExpiPlayer extends ExpiEntity {

    private final ServerPlayerGateway gateway;
    private String name;
    private final HashSet<Integer> activeChunks;
    private final ExpiPlayerInventory inventory;

    public ExpiPlayer(Vector2 location, ServerPlayerGateway gateway, String name) {
        super(EntityType.PLAYER, location, 0.9f, 1.25f);
        GameServer.get().getWorld().getCL().registerListener(EntityBodyFactory.createSensor(body), this);
        this.gateway = gateway;
        this.name = name;
        activeChunks = new HashSet<>();
        GameServer.get().getPlayers().add(this);
        inventory = new ExpiPlayerInventory(this, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_MAX_WEIGHT);
    }

    public void onMove(float horz, float vert) {

        Vector2 center = body.getWorldCenter();
        float jump = 0;
        if(onGround || Consts.DEBUG) {
            if(body.getLinearVelocity().y < 5 && vert >= 0.6f) {
                jump = 1;
            }
        }
        if((body.getLinearVelocity().x >= 3 && horz > 0) || (body.getLinearVelocity().x <= -3 && horz < 0)) {
            horz = 0;
        }
        body.applyLinearImpulse(0, Math.min((3200.0f/Consts.SERVER_DEFAULT_TPS), 200)*jump, center.x, center.y, true);
        body.applyForceToCenter((40000.0f/Consts.SERVER_DEFAULT_TPS) * horz, 0, true);
    }

    public void wantsToMakeItem(ItemRecipe recipe) {
        for(ItemStack is : recipe.getRequiredItems()) {
            if(!inventory.contains(is)) return;
        }
        for(ItemStack is : recipe.getRequiredItems()) {
            inventory.removeItem(is);
        }
        inventory.addItem(recipe.getProduct());
        gateway.getManager().putMainInvFeedPacket(inventory);
    }

    public ServerPlayerGateway getGateway() {
        return gateway;
    }

    public String getName() {
        return name;
    }

    public HashSet<Integer> getActiveChunks() {
        return activeChunks;
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
        return inventory;
    }
}
