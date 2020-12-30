package com.astetyne.expirium.server.backend;

import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;
import java.util.List;

public class FixturePack {

    public List<Fixture> addedFixtures;
    public List<Integer> removedFixtures;

    public FixturePack() {
        this.addedFixtures = new ArrayList<>();
        this.removedFixtures = new ArrayList<>();
    }
}