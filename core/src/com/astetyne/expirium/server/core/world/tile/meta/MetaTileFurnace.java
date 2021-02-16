package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.inventory.FuelCookingInventory;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileFurnace extends MetaTile {

    private final FuelCookingInventory inventory;
    private final long placeTick;

    public MetaTileFurnace(ExpiWorld world, ExpiTile owner) {
        super(world, owner);
        inventory = new FuelCookingInventory(world, 2, 2, 5);
        placeTick = world.getTick();
        scheduleAfter(this::onInvTick, Consts.SERVER_TPS);
    }

    public MetaTileFurnace(ExpiWorld world, ExpiTile owner, DataInputStream in) throws IOException {
        super(world, owner);
        inventory = new FuelCookingInventory(world, 2, 2, 5, in);
        placeTick = world.getTick();
        scheduleAfter(this::onInvTick, Consts.SERVER_TPS);
    }

    @Override
    public void writeData(WorldBuffer out) {
        inventory.writeData(out);
    }

    @Override
    public void onInteract(ExpiPlayer p, InteractType type) {
        if(placeTick + 10 > world.getTick()) return;
        p.setSecondInv(inventory);
        p.getNetManager().putOpenDoubleInvPacket();
    }

    private void onInvTick() {
        inventory.onCookingUpdate();
        if(inventory.getFuel() == 0 && owner.getMaterial() != Material.FURNACE_OFF) {
            world.changeMaterial(owner, Material.FURNACE_OFF, false, Source.NATURAL);
        }else if(inventory.getFuel() > 0 && owner.getMaterial() != Material.FURNACE_ON) {
            world.changeMaterial(owner, Material.FURNACE_ON, false, Source.NATURAL);
        }
        scheduleAfter(this::onInvTick, Consts.SERVER_TPS);
    }

    @Override
    public boolean onMaterialChange(Material to) {
        if(to == Material.FURNACE_OFF || to == Material.FURNACE_ON) return true;
        dropInvItems(inventory);
        return false;
    }
}
