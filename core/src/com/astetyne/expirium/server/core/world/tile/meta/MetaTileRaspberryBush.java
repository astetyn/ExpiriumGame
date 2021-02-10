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

public class MetaTileRaspberryBush extends MetaTile {

    public MetaTileRaspberryBush(ExpiWorld world, ExpiTile owner) {
        super(world, owner);
    }

    @Override
    public void postInit() {
        if(owner.getMaterial() == Material.RASPBERRY_BUSH_1) {
            world.scheduleTask(this::onGrow, Consts.SERVER_TPS*(int)(Math.random()*180+10));
        }
    }

    public void onGrow() {
        if(owner.getMeta() != this) return;
        world.changeMaterial(owner, Material.RASPBERRY_BUSH_2, false, Source.NATURAL);
    }

    @Override
    public void dropItems() {
        dropItem(Item.RASPBERRY_BUSH);
        if(owner.getMaterial() == Material.RASPBERRY_BUSH_2) dropItem(Item.RASPBERRY);
    }

    @Override
    public void onInteract(ExpiPlayer p, InteractType type) {
        if(owner.getMaterial() == Material.RASPBERRY_BUSH_2) {
            world.changeMaterial(owner, Material.RASPBERRY_BUSH_1, false, Source.NATURAL);
            for(ExpiPlayer ep : world.getServer().getPlayers()) {
                ep.getNetManager().putHandPunchPacket(p);
            }
            int raspNumber = (int)(Math.random() * 2) + 1; // 1-2
            for(int i = 0; i < raspNumber; i++) {
                dropItem(Item.RASPBERRY);
            }
        }
    }

    @Override
    public boolean onMaterialChange(Material to) {
        return to == Material.RASPBERRY_BUSH_2;
    }

}
