package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.inventory.Inventory;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.Tile;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileChest extends MetaTile {

    private final long placeTick;
    private final Inventory inventory;

    public MetaTileChest(World world, Tile owner) {
        super(world, owner);
        placeTick = world.getTick();
        inventory = new Inventory(3, 3, 30);
    }

    public MetaTileChest(World world, Tile owner, DataInputStream in) throws IOException {
        super(world, owner);
        placeTick = world.getTick();
        inventory = new Inventory(3, 3, 30, in);
    }

    @Override
    public boolean onInteract(Player p, InteractType type) {
        if(placeTick + 10 > world.getTick()) return false;
        p.setSecondInv(inventory);
        p.getNetManager().putOpenDoubleInvPacket();
        return true;
    }

    @Override
    public void writeData(WorldBuffer out) {
        inventory.writeData(out);
    }

    @Override
    protected Inventory getBoundInventory() {
        return inventory;
    }
}
