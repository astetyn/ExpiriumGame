package com.astetyne.expirium.server.api.entity;

import com.badlogic.gdx.physics.box2d.Contact;

public interface Collidable {

    void onCollisionBegin(Contact contact);

    void onCollisionEnd(Contact contact);

}
