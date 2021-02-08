package com.astetyne.expirium.server.core.world.tile;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.Saveable;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.badlogic.gdx.math.Vector2;

import java.io.DataOutputStream;
import java.io.IOException;

public class MetaTile implements Saveable {

    protected final ExpiWorld world;
    protected final ExpiTile owner;

    /** Only this constructor will be called. Do not change parameters in your superclass.*/
    public MetaTile(ExpiWorld world, ExpiTile owner) {
        this.world = world;
        this.owner = owner;
    }

    public void postInit() {}

    /** Returns true if meta should be kept. False if meta should be changed.*/
    public boolean onMaterialChange(Material to) {
        return false;
    }

    public void onInteract(ExpiPlayer p, InteractType type) {}

    public void dropItems() {
        if(owner.getMaterial().getDefaultDropItem() == null) return;
        dropItem(owner.getMaterial().getDefaultDropItem());
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {}

    public void dropItem(Item item) {
        float off = (1 - EntityType.DROPPED_ITEM.getWidth())/2;
        Vector2 loc = new Vector2(owner.getX() + off, owner.getY() + off);
        world.spawnDroppedItem(item, loc, Consts.ITEM_COOLDOWN_BREAK);
    }

}
