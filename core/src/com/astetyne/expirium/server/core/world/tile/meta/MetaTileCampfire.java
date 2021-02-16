package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.inventory.CookingInventory;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.net.SimpleServerPacket;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileCampfire extends MetaTile {

    private final long placeTick;
    private final CookingInventory inventory;

    public MetaTileCampfire(ExpiWorld world, ExpiTile owner) {
        super(world, owner);
        placeTick = world.getTick();
        inventory = new CookingInventory(world, 1, 2, 5);
        scheduleAfter(this::onReduce, Consts.SERVER_TPS*100);
        scheduleAfter(this::onEnd, Consts.SERVER_TPS*120);
        scheduleAfter(this::onInvTick, Consts.SERVER_TPS/2);
    }

    public MetaTileCampfire(ExpiWorld world, ExpiTile owner, DataInputStream in) throws IOException {
        super(world, owner);
        placeTick = in.readLong();
        inventory = new CookingInventory(world, 1, 2, 5, in);
        long tickPassed = world.getTick() - placeTick;
        scheduleAfter(this::onReduce, Consts.SERVER_TPS*100 - tickPassed);
        scheduleAfter(this::onEnd, Consts.SERVER_TPS*120 - tickPassed);
        scheduleAfter(this::onInvTick, Consts.SERVER_TPS/2);
    }

    private void onInvTick() {
        inventory.onCookingUpdate();
        scheduleAfter(this::onInvTick, Consts.SERVER_TPS/2);
    }

    private void onReduce() {
        world.changeMaterial(owner, Material.CAMPFIRE_SMALL, false, Source.NATURAL);
    }

    private void onEnd() {
        world.changeMaterial(owner, Material.AIR, false, Source.NATURAL);
        for(ExpiPlayer ep : world.getServer().getPlayers()) {
            if(ep.getSecondInv() == inventory) {
                ep.getNetManager().putSimpleServerPacket(SimpleServerPacket.CLOSE_DOUBLE_INV);
            }
        }
        dropInvItems(inventory);
    }

    @Override
    public void onInteract(ExpiPlayer p, InteractType type) {
        if(placeTick + 10 > world.getTick()) return;
        p.setSecondInv(inventory);
        p.getNetManager().putOpenDoubleInvPacket();
    }

    @Override
    public boolean onMaterialChange(Material to) {
        if(to == Material.CAMPFIRE_SMALL) {
            return true;
        }
        dropInvItems(inventory);
        return false;
    }

    @Override
    public void dropItems() {}

    @Override
    public void writeData(WorldBuffer out) {
        out.writeLong(placeTick);
        inventory.writeData(out);
    }

}
