package com.astetyne.expirium.server.api.entities;

import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.ExpiInventory;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.astetyne.expirium.server.backend.PacketOutputStream;
import com.astetyne.expirium.server.backend.ServerPlayerGateway;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;

public class ExpiPlayer extends ExpiEntity {

    private final ServerPlayerGateway gateway;
    private String name;
    private final HashSet<Integer> activeChunks;
    private final ExpiInventory inventory;

    public ExpiPlayer(Vector2 location, ServerPlayerGateway gateway, String name) {
        super(EntityType.PLAYER, location, 0.9f, 1.25f);
        this.gateway = gateway;
        this.name = name;
        activeChunks = new HashSet<>();
        GameServer.get().getPlayers().add(this);
        inventory = new ExpiInventory(Constants.PLAYER_INV_ROWS, Constants.PLAYER_INV_ROWS, Constants.PLAYER_INV_MAX_WEIGHT);
    }

    public void onMove(float x, float y, float v1, float v2) {
        body.setTransform(x, y, 0);
        body.setLinearVelocity(v1, v2);
    }

    public void wantsToMakeItem(ItemRecipe recipe) {
        for(ItemStack is : recipe.getRequiredItems()) {
            if(!inventory.contain(is)) return;
        }
        for(ItemStack is : recipe.getRequiredItems()) {
            inventory.removeItem(is);
        }
        inventory.addItem(recipe.getProduct());
        gateway.getManager().putInvFeedPacket(inventory);
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

    public ExpiInventory getInv() {
        return inventory;
    }
}
