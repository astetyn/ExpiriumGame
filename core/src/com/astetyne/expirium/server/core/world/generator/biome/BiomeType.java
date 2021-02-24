package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.server.core.world.tile.Material;

import java.lang.reflect.InvocationTargetException;

public enum BiomeType {

    TROPICAL_FOREST(TropicalForestGen.class),
    HILLS(HillsGen.class),
    BOREAL_FOREST(BorealForestGen.class),
    DESERT(DesertGen.class),
    ;

    private final Class<? extends BiomeGenerator> clazz;

    BiomeType(Class<? extends BiomeGenerator> clazz) {
        this.clazz = clazz;
    }

    public BiomeGenerator initGenerator(Material[][] terrain, int[] surface, int w, int h, long seed) {
        try {
            return clazz.getConstructor(Material[][].class, int[].class, int.class, int.class, long.class)
                    .newInstance(terrain, surface, w, h, seed);
        }catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return new DesertGen(terrain, surface, w, h, seed);
    }

    public static BiomeType get(int i) {
        return values()[i];
    }

}
