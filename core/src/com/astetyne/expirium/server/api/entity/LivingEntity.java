package com.astetyne.expirium.server.api.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.Saveable;
import com.astetyne.expirium.server.api.event.TickListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class LivingEntity extends ExpiEntity implements Saveable, Collidable, TickListener {

    private final static float MAX_HEALTH_LEVEL = 100;
    private final static float MAX_FOOD_LEVEL = 100;

    private float healthLevel, foodLevel, temperatureLevel;

    protected boolean onGround;
    private int collisions;
    protected Fixture groundSensor;

    protected boolean alive;

    protected boolean invincible;

    public LivingEntity(ExpiServer server, EntityType type, Vector2 loc) {
        super(server, type, loc);
        healthLevel = 100;
        foodLevel = 100;
        temperatureLevel = 22;
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
        alive = in.readBoolean();
        onGround = false;
        collisions = 0;
        alive = true;
        invincible = false;
        server.getWorld().getCL().registerListener(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        alive = false;
        server.getWorld().getCL().unregisterListener(this);
    }

    @Override
    public void onTick(float delta) {

        decreaseFoodLevel(delta);

        if(foodLevel <= 5) {
            injure(delta);
        }

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

    @Override
    public void writeData(DataOutputStream out) throws IOException {
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
