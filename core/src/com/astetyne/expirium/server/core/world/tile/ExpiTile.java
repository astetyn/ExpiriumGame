package com.astetyne.expirium.server.core.world.tile;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpiTile implements WorldSaveable {

    private final ExpiWorld world;
    private Material material;
    private MetaTile metaTile;
    private final List<Fixture> fixtures;
    private final int x, y;
    private int stability;
    private boolean backWall;

    public ExpiTile(ExpiWorld world, DataInputStream in, int x, int y) throws IOException {
        this.world = world;
        material = Material.getMaterial(in.readInt());
        metaTile = material.init(this.world, this, in);
        fixtures = new ArrayList<>();
        this.x = x;
        this.y = y;
        stability = 0;
        backWall = in.readBoolean();
    }

    /** This is unsafe. Do not call this if you have no big reason.*/
    public void setMaterial(Material material) {
        this.material = material;
    }

    /** This is unsafe. Call changeTile() if you want to change the material. */
    public void changeMaterial(Material material) {
        this.material = material;
        if(!metaTile.onMaterialChange(material)) {
            metaTile = material.init(world, this);
            metaTile.postInit();
        }
    }

    public void onInteract(ExpiPlayer p, InteractType type) {
        metaTile.onInteract(p, type);
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
    public void writeData(WorldBuffer out) {
        out.writeMaterial(material);
        metaTile.writeData(out);
        out.writeBoolean(backWall);
    }
}