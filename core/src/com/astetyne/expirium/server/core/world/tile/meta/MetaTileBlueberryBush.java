package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;

public class MetaTileBlueberryBush extends MetaTile {

    public MetaTileBlueberryBush(ExpiWorld world, ExpiTile owner) {
        super(world, owner);
    }

    @Override
    public void postInit() {
        if(owner.getMaterial() == Material.BLUEBERRY_BUSH) {
            world.scheduleTask(this::onGrow, Consts.SERVER_TPS*(int)(Math.random()*180+10));
        }
    }

    public void onGrow() {
        if(owner.getMeta() != this) return;
        world.changeMaterial(owner, Material.BLUEBERRY_BUSH_GROWN, false, Source.NATURAL);
    }

    @Override
    public void dropItems() {
        dropItem(Item.BLUEBERRY_BUSH);
        if(owner.getMaterial() == Material.BLUEBERRY_BUSH_GROWN) dropItem(Item.BLUEBERRY);
    }

    @Override
    public void onInteract(ExpiPlayer p, InteractType type) {
        if(owner.getMaterial() == Material.BLUEBERRY_BUSH_GROWN) {
            world.changeMaterial(owner, Material.BLUEBERRY_BUSH, false, Source.NATURAL);
            for(ExpiPlayer ep : world.getServer().getPlayers()) {
                ep.getNetManager().putHandPunchPacket(p);
            }
            int blueNumber = (int)(Math.random() * 2) + 1; // 1-2
            for(int i = 0; i < blueNumber; i++) {
                dropItem(Item.BLUEBERRY);
            }
        }
    }

    @Override
    public boolean onMaterialChange(Material to) {
        return to == Material.BLUEBERRY_BUSH_GROWN;
    }

}
