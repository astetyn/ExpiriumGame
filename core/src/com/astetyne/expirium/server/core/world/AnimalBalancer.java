package com.astetyne.expirium.server.core.world;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.server.core.entity.ExpiEntity;
import com.astetyne.expirium.server.core.world.generator.biome.BiomeType;
import com.astetyne.expirium.server.core.world.tile.TileFix;
import com.badlogic.gdx.math.Vector2;

public class AnimalBalancer {

    private final ExpiWorld world;
    private final int squirrelLimit;

    public AnimalBalancer(ExpiWorld world) {
        this.world = world;
        int squirrelBiomesFound = 0;
        for(BiomeType biome : world.getBiomes()) {
            if(biome == BiomeType.BOREAL_FOREST) squirrelBiomesFound++;
        }
        squirrelLimit = squirrelBiomesFound*6;
    }

    public void onTick() {

        EntityType sqType = EntityType.SQUIRREL;

        int squirrelCount = 0;
        for(ExpiEntity ee : world.getServer().getEntities()) {
            if(ee.getType() == sqType) squirrelCount++;
        }
        if(squirrelCount < squirrelLimit) world.spawnEntity(sqType, getRandSpawnLocOnGround(sqType, BiomeType.BOREAL_FOREST));
    }

    private Vector2 getRandSpawnLocOnGround(EntityType type, BiomeType reqBiome) {

        int reqW = (int) type.getWidth() + 1;
        int reqH = (int) type.getHeight() + 1;

        float offsetX = (reqW - type.getWidth())/2;
        float offsetY = (reqH - type.getHeight())/2;

        int x;
        int y;

        int iterations = 0;

        outer:
        while(true) {

            iterations++;
            if(iterations > world.getTerrainWidth()) return null;

            x = (int) (Math.random() * (world.getTerrainWidth() - reqW));
            y = world.getTerrainHeight() - reqH - 1;

            if(world.getBiomeAt(x) != reqBiome) {
                iterations--;
                continue;
            }

            yLoop:
            while(true) {

                y--;
                if(y == 0) continue outer;

                if(world.getTerrain()[y-1][x].getMaterial().getFix() == TileFix.SOFT) continue;

                // check if rectangle is free
                for(int i = 0; i < reqW; i++) {
                    for(int j = 0; j < reqH; j++) {
                        if(world.getTerrain()[y+reqH][x+reqW].getMaterial().getFix() != TileFix.SOFT) continue yLoop;
                    }
                }
                break outer;
            }
        }
        return new Vector2(x + offsetX, y + offsetY);
    }

}