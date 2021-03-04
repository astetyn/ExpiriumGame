package com.astetyne.expirium.client.data;

import com.astetyne.expirium.client.utils.IntVector2;

public class ExtraCell {

    public final IntVector2 pos;
    public final ExtraCellTexture tex;

    public ExtraCell(int x, int y, ExtraCellTexture tex) {
        this.pos = new IntVector2(x, y);
        this.tex = tex;
    }
}
