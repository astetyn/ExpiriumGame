package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.player.ExpiPlayer;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.inventory.Inventory;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileChest extends MetaTile {

    private final long placeTick;
    private final Inventory inventory;

    public MetaTileChest(ExpiWorld world, ExpiTile owner) {
        super(world, owner);
        placeTick = world.getTick();
        inventory = new Inventory(3, 3, 30);
    }

    public MetaTileChest(ExpiWorld world, ExpiTile owner, DataInputStream in) throws IOException {
        super(world, owner);
        placeTick = world.getTick();
        inventory = new Inventory(3, 3, 30, in);
    }

    @Override
    public void onInteract(ExpiPlayer p, InteractType type) {
        if(placeTick + 10 > world.getTick()) return;
        p.setSecondInv(inventory);
        p.getNetManager().putOpenDoubleInvPacket();
    }

    @Override
    public boolean onMaterialChange(Material to) {
        dropInvItems(inventory);
        return false;
    }

    @Override
    public void writeData(WorldBuffer out) {
        inventory.writeData(out);
    }
}
