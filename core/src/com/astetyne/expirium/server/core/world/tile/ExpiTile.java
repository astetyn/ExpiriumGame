package com.astetyne.expirium.server.core.world.tile;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.Saveable;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpiTile implements Saveable {

    private final ExpiServer server;
    private Material material;
    private MetaTile metaTile;
    private final List<Fixture> fixtures;
    private final int x, y;
    private int stability;
    private boolean backWall;

    public ExpiTile(ExpiServer server, Material material, int x, int y) {
        this.server = server;
        this.material = material;
        metaTile = material.init(server, this);
        fixtures = new ArrayList<>();
        this.x = x;
        this.y = y;
        stability = 0;
        backWall = false;
    }

    public ExpiTile(ExpiServer server, DataInputStream in, int x, int y) throws IOException {
        this.server = server;
        material = Material.getMaterial(in.readByte());
        metaTile = material.init(server, this, in);
        fixtures = new ArrayList<>();
        this.x = x;
        this.y = y;
        stability = 0;
        backWall = in.readBoolean();
    }

    /** This is unsafe. Call changeTile() if you want to change the material. */
    public void setMaterial(Material material) {
        this.material = material;
        if(!metaTile.onMaterialChange(material)) {
            metaTile = material.init(server, this);
        }
    }

    public void onInteract(ExpiPlayer p, InteractType type) {
        metaTile.onInteract(p, type);
    }

    public void setMeta(MetaTile meta) {
        this.metaTile = meta;
    }

    public Material getMaterial() {
        return material;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }

    public int getStability() {
        return stability;
    }

    public boolean isLabile() {
        return metaTile.getSolidity().isLabile();
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasBackWall() {
        return backWall;
    }

    public void setBackWall(boolean backWall) {
        this.backWall = backWall;
    }

    public MetaTile getMeta() {
        return metaTile;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(material.getID());
        metaTile.writeData(out);
        out.writeBoolean(backWall);
    }
}
