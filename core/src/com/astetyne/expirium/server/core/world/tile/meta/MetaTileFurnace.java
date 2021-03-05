package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.inventory.FuelCookingInventory;
import com.astetyne.expirium.server.core.world.inventory.Inventory;
import com.astetyne.expirium.server.core.world.tile.Material;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.Tile;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileFurnace extends MetaTile {

    private final FuelCookingInventory inventory;
    private final long placeTick;

    public MetaTileFurnace(World world, Tile owner) {
        super(world, owner);
        inventory = new FuelCookingInventory(world, 1, 4, 5);
        placeTick = world.getTick();
        scheduleAfter(this::onInvUpdate, Consts.SERVER_TPS/2);
    }

    public MetaTileFurnace(World world, Tile owner, DataInputStream in) throws IOException {
        super(world, owner);
        inventory = new FuelCookingInventory(world, 1, 4, 5, in);
        placeTick = world.getTick();
        scheduleAfter(this::onInvUpdate, Consts.SERVER_TPS/2);
    }

    @Override
    public void writeData(WorldBuffer out) {
        inventory.writeData(out);
    }

    @Override
    public boolean onInteract(Player p, InteractType type) {
        if(placeTick + 10 > world.getTick()) return false;
        p.setSecondInv(inventory);
        p.getNetManager().putOpenDoubleInvPacket();
        return true;
    }

    private void onInvUpdate() {
        inventory.onCookingUpdate();
        if(inventory.getFuel() == 0 && owner.getMaterial() != Material.FURNACE_OFF) {
            world.changeMaterial(owner, Material.FURNACE_OFF, false, Source.NATURAL);
        }else if(inventory.getFuel() > 0 && owner.getMaterial() != Material.FURNACE_ON) {
            world.changeMaterial(owner, Material.FURNACE_ON, false, Source.NATURAL);
        }
        scheduleAfter(this::onInvUpdate, Consts.SERVER_TPS/2);
    }

    @Override
    public boolean onMaterialChange(Material to) {
        if(to == Material.FURNACE_OFF || to == Material.FURNACE_ON) return true;
        return super.onMaterialChange(to);
    }

    @Override
    protected Inventory getBoundInventory() {
        return inventory;
    }
}
