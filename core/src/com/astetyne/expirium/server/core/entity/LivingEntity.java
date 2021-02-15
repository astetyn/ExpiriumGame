package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class LivingEntity extends ExpiEntity implements Collidable {

    private final static float MAX_HEALTH_LEVEL = 100;
    private final static float MAX_FOOD_LEVEL = 100;

    private float healthLevel, foodLevel, temperatureLevel;

    protected boolean lookingRight;
    protected boolean onGround;
    private int collisions;
    protected Fixture mainBody, groundSensor;
    protected boolean alive;
    protected boolean invincible;

    public LivingEntity(ExpiServer server, EntityType type, Vector2 loc) {
        super(server, type, loc);
        healthLevel = 100;
        foodLevel = 100;
        temperatureLevel = 22;
        lookingRight = true;
        onGround = false;
        collisions = 0;
        alive = true;
        invincible = false;
        server.getWorld().getCL().registerListener(this);
    }

    public LivingEntity(ExpiServer server, EntityType type, DataInputStream in) throws IOException {
        super(server, type, in);
        healthLevel = in.readFloat();
        foodLevel = in.readFloat();
        temperatureLevel = in.readFloat();
        lookingRight = true;
        alive = in.readBoolean();
        onGround = false;
        collisions = 0;
        alive = true;
        invincible = false;
        server.getWorld().getCL().registerListener(this);
    }

    @Override
    public void createBodyFixtures() {

        body.setFixedRotation(true);

        PolygonShape polyShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.density = 30f;
        fixtureDef.restitution = 0f;
        fixtureDef.friction = 0.2f;
        fixtureDef.filter.categoryBits = Consts.DEFAULT_BIT;
        fixtureDef.shape = polyShape;

        float w = getWidth();
        float h = getHeight();
        float gw = 0.05f;
        float gh = 0.05f;

        // upper poly
        float[] verts = new float[]{0, gh, gw, 0, w-gw, 0, w, gh, w, h, 0, h};
        polyShape.set(verts);
        mainBody = body.createFixture(fixtureDef);

        // ground sensor
        polyShape.setAsBox(w/2-gw, gh/2, new Vector2(w/2, 0), 0);
        fixtureDef.density = 0f;
        fixtureDef.isSensor = true;
        groundSensor = body.createFixture(fixtureDef);

        polyShape.dispose();
    }

    @Override
    public void destroy() {
        super.destroy();
        alive = false;
        server.getWorld().getCL().unregisterListener(this);
    }

    @Override
    public void onTick() {
        super.onTick();

        decreaseFoodLevel(1f/(10*Consts.SERVER_TPS)); // 1 food per 10 seconds

        if(foodLevel <= 5) {
            injure(1f/(2*Consts.SERVER_TPS)); // 1 health per 2 seconds
        }else if(foodLevel >= 90) {
            increaseHealthLevel(1f/(10*Consts.SERVER_TPS)); // 1 health per 10 seconds
        }

        recalcLookingDir();
    }

    public void die() {
        if(!alive) return;
        alive = false;
        destroy();
    }

    public void injure(float damage) {
        if(invincible) return;
        healthLevel -= damage;
        if(healthLevel <= 0) {
            die();
        }else {
            for(ExpiPlayer pp : server.getPlayers()) {
                pp.getNetManager().putInjurePacket(getId(), damage);
            }
        }
    }

    public void decreaseFoodLevel(float amount) {
        if(invincible) return;
        foodLevel = Math.max(foodLevel - amount, 0);
    }

    public float getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(int healthLevel) {
        this.healthLevel = healthLevel;
    }

    public float getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public float getTemperatureLevel() {
        return temperatureLevel;
    }

    public void setTemperatureLevel(int temperatureLevel) {
        this.temperatureLevel = temperatureLevel;
    }

    public void increaseHealthLevel(float i) {
        healthLevel = Math.min(healthLevel + i, MAX_HEALTH_LEVEL);
    }

    public void increaseFoodLevel(float i) {
        foodLevel = Math.min(foodLevel + i, MAX_FOOD_LEVEL);
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public void recalcLookingDir() {
        Vector2 vel = body.getLinearVelocity();
        if(vel.x > 0) {
            lookingRight = true;
        }else if(vel.x < 0) {
            lookingRight = false;
        }
    }

    @Override
    public boolean isLookingRight() {
        return lookingRight;
    }

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeFloat(healthLevel);
        out.writeFloat(foodLevel);
        out.writeFloat(temperatureLevel);
        out.writeBoolean(alive);
    }

    @Override
    public void onCollisionBegin(Contact contact) {
        if(contact.getFixtureA() == groundSensor || contact.getFixtureB() == groundSensor) {
            collisions++;
            onGround = true;
        }
    }

    @Override
    public void onCollisionEnd(Contact contact) {
        if(contact.getFixtureA() == groundSensor || contact.getFixtureB() == groundSensor) {
            collisions--;
            if(collisions == 0) onGround = false;
        }
    }
}
