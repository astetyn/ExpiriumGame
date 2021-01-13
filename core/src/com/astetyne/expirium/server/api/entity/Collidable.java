package com.astetyne.expirium.server.api.entity;

import com.badlogic.gdx.physics.box2d.Fixture;

public interface Collidable {

    void onCollisionBegin(Fixture fix);

    void onCollisionEnd(Fixture fix);

}
