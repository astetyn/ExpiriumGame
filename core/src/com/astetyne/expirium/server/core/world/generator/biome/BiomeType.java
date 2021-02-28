package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.server.core.world.generator.WorldGenerator;

import java.lang.reflect.InvocationTargetException;

public enum BiomeType {

    TROPICAL_FOREST(TropicalForestGen.class),
    HILLS(HillsGen.class),
    BOREAL_FOREST(BorealForestGen.class),
    DESERT(DesertGen.class),
    WATER_BIOME(WaterBiome.class),
    ;

    private final Class<? extends BiomeGenerator> clazz;

    BiomeType(Class<? extends BiomeGenerator> clazz) {
        this.clazz = clazz;
    }

    public BiomeGenerator initGenerator(WorldGenerator gen) {
        try {
            return clazz.getConstructor(WorldGenerator.class).newInstance(gen);
        }catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return new DesertGen(gen);
    }

    public static BiomeType get(int i) {
        return values()[i];
    }

}
