package com.astetyne.expirium.client.items;

import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;

import java.io.DataInputStream;
import java.io.IOException;

public class GridItemStack extends ItemStack implements WorldSaveable {

    private final IntVector2 gridPos;

    public GridItemStack(ItemStack is) {
        super(is);
        gridPos = new IntVector2(0, 0);
    }

    public GridItemStack(GridItemStack is) {
        super(is);
        gridPos = new IntVector2(is.getGridPos());
    }

    public GridItemStack(Item item, int amount, IntVector2 pos) {
        super(item, amount);
        gridPos = pos;
    }

    public GridItemStack(DataInputStream in) throws IOException {
        super(Item.getType(in.readInt()), in.readInt());
        gridPos = new IntVector2(in.readInt(), in.readInt());
    }

    public IntVector2 getGridPos() {
        return gridPos;
    }

    @Override
    public void writeData(WorldBuffer out) {
        out.writeInt(item.getId());
        out.writeInt(amount);
        out.writeInt(gridPos.x);
        out.writeInt(gridPos.y);
    }
}
