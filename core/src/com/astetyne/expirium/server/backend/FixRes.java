package com.astetyne.expirium.server.backend;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class FixRes {

    public static EdgesData CAMPFIRE;
    public static EdgesData GRASS_SLOPE_R, GRASS_SLOPE_L;

    public static void load() {

        CAMPFIRE = new EdgesData();
        CAMPFIRE.add(0.2f, 0, 0.8f, 0);
        CAMPFIRE.add(0.8f, 0, 0.8f, 0.3f);
        CAMPFIRE.add(0.8f, 0.3f, 0.2f, 0.3f);
        CAMPFIRE.add(0.2f, 0.3f, 0.2f, 0);

        GRASS_SLOPE_R = new EdgesData();

        GRASS_SLOPE_L = new EdgesData();

    }

    public static class EdgesData {

        public List<Vector2> l1, l2;

        public EdgesData() {
            l1 = new ArrayList<>();
            l2 = new ArrayList<>();
        }

        public void add(float x1, float y1, float x2, float y2) {
            l1.add(new Vector2(x1, y1));
            l2.add(new Vector2(x2, y2));
        }

    }

}
