package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.inventory.Inventory;
import com.astetyne.expirium.server.core.world.inventory.RecyclerInventory;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.Tile;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileRecycler extends MetaTile {

    private final long placeTick;
    private final RecyclerInventory inventory;

    public MetaTileRecycler(World world, Tile owner) {
        super(world, owner);
        placeTick = world.getTick();
        inventory = new RecyclerInventory(world, 1, 1, 3);
        scheduleAfter(this::onInvUpdate, Consts.SERVER_TPS/2);
    }

    public MetaTileRecycler(World world, Tile owner, DataInputStream in) throws IOException {
        super(world, owner);
        placeTick = world.getTick();
        inventory = new RecyclerInventory(world, 1, 1, 3, in);
        scheduleAfter(this::onInvUpdate, Consts.SERVER_TPS/2);
    }

    private void onInvUpdate() {
        inventory.update();
        scheduleAfter(this::onInvUpdate, Consts.SERVER_TPS/2);
    }

    @Override
    public boolean onInteract(Player p, InteractType type) {
        if(placeTick + 10 > world.getTick()) return false;
        p.setSecondInv(inventory);
        p.getNetManager().putOpenDoubleInvPacket();
        return true;
    }

    @Override
    protected Inventory getBoundInventory() {
        return inventory;
    }
}
