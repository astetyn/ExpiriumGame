package com.astetyne.expirium.server.core.world;

import com.astetyne.expirium.server.core.entity.Collidable;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.ArrayList;
import java.util.List;

public class ExpiContactListener implements ContactListener {

    private final List<Collidable> listeners;

    public ExpiContactListener() {
        listeners = new ArrayList<>();
    }

    @Override
    public void beginContact(Contact contact) {
        for(Collidable listener : listeners) {
            listener.onCollisionBegin(contact);
        }
    }

    @Override
    public void endContact(Contact contact) {
        for(Collidable listener : listeners) {
            listener.onCollisionEnd(contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public void registerListener(Collidable collidable) {
        listeners.add(collidable);
    }

    public void unregisterListener(Collidable collidable) {
        listeners.remove(collidable);
    }
}
