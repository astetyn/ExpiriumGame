package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.GameServer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class EntityBodyFactory {

    public static Body createDroppedEntityBody(Vector2 loc) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(loc);

        Body body = GameServer.get().getWorld().getB2dWorld().createBody(bodyDef);

        PolygonShape polyShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        polyShape.setAsBox(0.25f, 0.25f);
        fixtureDef.density = 30f;
        fixtureDef.restitution = 0.2f;
        fixtureDef.friction = 0.2f;
        fixtureDef.shape = polyShape;
        fixtureDef.filter.categoryBits = Consts.DEFAULT_BIT;

        body.createFixture(fixtureDef);
        polyShape.dispose();

        return body;
    }

    public static Body createPlayerBody(Vector2 loc) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(loc);

        Body body = GameServer.get().getWorld().getB2dWorld().createBody(bodyDef);

        PolygonShape polyShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.density = 30f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = Consts.PLAYER_BIT;
        fixtureDef.filter.maskBits = Consts.DEFAULT_BIT;

        body.setFixedRotation(true);

        // upper poly
        polyShape.setAsBox(0.45f, 0.55f, new Vector2(0.45f, 0.7f), 0);
        fixtureDef.shape = polyShape;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef);

        // bottom poly
        float[] verts = new float[8];
        verts[0] = 0.05f;
        verts[1] = 0.15f;
        verts[2] = 0.2f;
        verts[3] = 0;
        verts[4] = 0.7f;
        verts[5] = 0;
        verts[6] = 0.85f;
        verts[7] = 0.15f;

        polyShape.set(verts);
        fixtureDef.shape = polyShape;
        fixtureDef.friction = 1;
        body.createFixture(fixtureDef);

        polyShape.dispose();

        return body;
    }

    public static Fixture createSensor(Body body) {

        PolygonShape polyShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        polyShape.setAsBox(0.4f, 0.3f, new Vector2(0.45f, 0.2f), 0);
        fixtureDef.density = 0f;
        fixtureDef.shape = polyShape;
        fixtureDef.isSensor = true;
        return body.createFixture(fixtureDef);

    }

}
