package com.astetyne.expirium.server.api.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.Saveable;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class LivingEntity extends ExpiEntity implements Saveable, Collidable {

    private final static float MAX_HEALTH_LEVEL = 100;
    private final static float MAX_FOOD_LEVEL = 100;

    protected float healthLevel, foodLevel, temperatureLevel;

    protected boolean onGround;
    private int collisions;
    protected Fixture groundSensor;

    public LivingEntity(EntityType type, float width, float height) {
        super(type, width, height);
        healthLevel = 100;
        foodLevel = 100;
        temperatureLevel = 22;
        onGround = false;
        collisions = 0;
    }

    public LivingEntity(EntityType type, float width, float height, DataInputStream in) throws IOException {
        super(type, width, height);
        healthLevel = in.readFloat();
        foodLevel = in.readFloat();
        temperatureLevel = in.readFloat();
        onGround = false;
        collisions = 0;
    }

    @Override
    public void destroy() {
        super.destroy();
        ExpiServer.get().getWorld().getCL().unregisterListener(this);
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

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeFloat(healthLevel);
        out.writeFloat(foodLevel);
        out.writeFloat(temperatureLevel);
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
