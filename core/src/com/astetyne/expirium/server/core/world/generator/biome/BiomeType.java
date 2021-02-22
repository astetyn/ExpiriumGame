package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.tiles.Material;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

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

    int id;
    private static final HashMap<Integer, BiomeType> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(BiomeType it : BiomeType.values()) {
            it.id = i;
            map.put(it.id, it);
            i++;
        }
    }

    public static BiomeType get(int id) {
        return map.get(id);
    }

    public int getId() {
        return id;
    }

}
