package com.astetyne.main.world;

import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;

public class ExpiContactListener implements ContactListener {

    private final HashMap<Fixture, Collidable> listeners;
    private final HashMap<Collidable, Fixture> convenientMap;

    public ExpiContactListener() {
        listeners = new HashMap<>();
        convenientMap = new HashMap<>();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(listeners.containsKey(fixA)) {
            listeners.get(fixA).onCollisionBegin(fixB);
        }
        if(listeners.containsKey(fixB)) {
            listeners.get(fixB).onCollisionBegin(fixA);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(listeners.containsKey(fixA)) {
            listeners.get(fixA).onCollisionEnd(fixB);
        }
        if(listeners.containsKey(fixB)) {
            listeners.get(fixB).onCollisionEnd(fixA);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public void registerListener(Fixture fix, Collidable collidable) {
        listeners.put(fix, collidable);
        convenientMap.put(collidable, fix);
    }

    public void unregisterListener(Collidable collidable) {
        Fixture key = convenientMap.get(collidable);
        listeners.remove(key);
        convenientMap.remove(collidable);
    }
}
