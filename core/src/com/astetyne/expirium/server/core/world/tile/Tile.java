package com.astetyne.expirium.server.core.world.tile;

import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tile implements WorldSaveable {

    private final World world;
    private Material material;
    private MetaTile metaTile;
    private final List<Fixture> fixtures;
    private final int x, y;
    private byte stability;
    private byte waterLevel;
    private boolean backWall;

    public Tile(World world, int x, int y, DataInputStream in, boolean createMeta) throws IOException {
        this.world = world;
        material = Material.get(in.readInt());
        if(createMeta) {
            metaTile = material.init(world, this);
        }else {
            metaTile = material.init(world, this, in);
        }
        fixtures = new ArrayList<>();
        this.x = x;
        this.y = y;
        stability = 0;
        waterLevel = in.readByte();
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
        }
    }

    public boolean onInteract(Player p, InteractType type) {
        return metaTile.onInteract(p, type);
    }

    public Material getMaterial() {
        return material;
    }

    public void setStability(int stability) {
        this.stability = (byte) stability;
    }

    public byte getStability() {
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

    public IntVector2 getLoc(IntVector2 vec) {
        vec.set(x, y);
        return vec;
    }

    public byte getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = (byte) waterLevel;
    }

    public void increaseWaterLevel(int i) {
        waterLevel += i;
    }

    @Override
    public void writeData(WorldBuffer out) {
        out.writeMaterial(material);
        metaTile.writeData(out);
        out.writeByte(waterLevel);
        out.writeBoolean(backWall);
    }
}
