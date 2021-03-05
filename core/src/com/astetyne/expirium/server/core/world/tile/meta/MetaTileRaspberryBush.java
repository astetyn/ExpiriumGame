package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.tile.Material;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.Tile;

public class MetaTileRaspberryBush extends MetaTile {

    private final static int minGrowSeconds = 30;
    private final static int maxGrowSeconds = 240;

    public MetaTileRaspberryBush(World world, Tile owner) {
        super(world, owner);
        if(owner.getMaterial() == Material.RASPBERRY_BUSH) {
            scheduleAfter(this::onGrow, Consts.SERVER_TPS*(int)(Math.random()*(maxGrowSeconds-minGrowSeconds)) + minGrowSeconds);
        }
    }

    public void onGrow() {
        world.changeMaterial(owner, Material.RASPBERRY_BUSH_GROWN, false, Source.NATURAL);
    }

    @Override
    public void dropItems() {
        dropItem(Item.RASPBERRY_BUSH);
        if(owner.getMaterial() == Material.RASPBERRY_BUSH_GROWN) dropItem(Item.RASPBERRY);
    }

    @Override
    public boolean onInteract(Player p, InteractType type) {
        if(owner.getMaterial() == Material.RASPBERRY_BUSH_GROWN) {
            world.changeMaterial(owner, Material.RASPBERRY_BUSH, false, Source.NATURAL);
            for(Player ep : world.getServer().getPlayers()) {
                ep.getNetManager().putHandPunchPacket(p);
            }
            int raspNumber = (int)(Math.random() * 2) + 1; // 1-2
            for(int i = 0; i < raspNumber; i++) {
                dropItem(Item.RASPBERRY);
            }
        }
        return false;
    }

    @Override
    public boolean onMaterialChange(Material to) {
        return to == Material.RASPBERRY_BUSH_GROWN;
    }

}
