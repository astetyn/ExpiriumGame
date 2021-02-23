package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.ExpiColor;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class LivingEntity extends Entity implements Collidable {

    private final static byte MAX_FOOD_LEVEL = 100;

    private final byte maxHealth;
    private byte healthLevel, foodLevel;

    protected boolean lookingRight;
    protected boolean onGround;
    private int collisions;
    protected Fixture bodyFix, groundSensor;
    protected boolean alive;
    protected boolean invincible;
    protected float lastFallVelocity;

    public LivingEntity(ExpiServer server, EntityType type, Vector2 loc, int maxHealth) {
        super(server, type, loc);
        this.maxHealth = (byte) maxHealth;
        healthLevel = this.maxHealth;
        foodLevel = 100;
        lookingRight = true;
        onGround = false;
        collisions = 0;
        alive = true;
        invincible = false;
        lastFallVelocity = 0;
        server.getWorld().scheduleTaskAfter(this::plannedStarve, Consts.SERVER_TPS * 10);
        server.getWorld().getCL().registerListener(this);
    }

    public LivingEntity(ExpiServer server, EntityType type, int maxHealth, DataInputStream in) throws IOException {
        super(server, type, in);
        this.maxHealth = (byte) maxHealth;
        healthLevel = in.readByte();
        foodLevel = in.readByte();
        lookingRight = true;
        alive = in.readBoolean();
        onGround = false;
        collisions = 0;
        alive = true;
        invincible = false;
        lastFallVelocity = 0;
        server.getWorld().scheduleTaskAfter(this::plannedStarve, Consts.SERVER_TPS * 10);
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
        bodyFix = body.createFixture(fixtureDef);

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

        if(foodLevel <= 5 && server.getWorld().getTick() % 64 == 0) {
            injure(1); // 1 health per 2 seconds
        }

        recalcFallDamage();
        recalcLookingDir();

        lastFallVelocity = 0;
    }

    // once per 10 seconds
    protected void plannedStarve() {
        decreaseFoodLevel(1);
        if(foodLevel >= 90) heal(1);
        server.getWorld().scheduleTaskAfter(this::plannedStarve, Consts.SERVER_TPS * 10);
    }

    protected void recalcFallDamage() {
        if(lastFallVelocity < -12 && getVelocity().y - lastFallVelocity > 11) {
            injure((int) (lastFallVelocity*(-0.5f)));
        }
    }

    public void die() {
        if(!alive) return;
        alive = false;
        destroy();
        String text = "Kill";
        for(Player pp : server.getPlayers()) {
            pp.getNetManager().putPlayTextAnim(getCenter().add(0, getHeight()/2), text, ExpiColor.RED);
        }
    }

    public void injure(int amount) {
        if(invincible) return;
        healthLevel -= amount;
        if(healthLevel <= 0) {
            die();
        }else {
            String text = "-"+amount;
            for(Player pp : server.getPlayers()) {
                pp.getNetManager().putPlayTextAnim(getCenter().add(0, getHeight()/2), text, ExpiColor.RED);
            }
        }
    }

    public void heal(int amount) {
        if(healthLevel == maxHealth) return;
        healthLevel = (byte) Math.min(healthLevel + amount, maxHealth);
        String text = "+"+amount;
        for(Player pp : server.getPlayers()) {
            pp.getNetManager().putPlayTextAnim(getCenter().add(0, getHeight()/2), text, ExpiColor.GREEN);
        }
    }

    public byte getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(int healthLevel) {
        this.healthLevel = (byte) healthLevel;
    }

    public byte getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = (byte) foodLevel;
    }

    public void increaseFoodLevel(int i) {
        foodLevel = (byte) Math.min(foodLevel + i, MAX_FOOD_LEVEL);
    }

    public void decreaseFoodLevel(int amount) {
        if(invincible) return;
        foodLevel = (byte) Math.max(foodLevel - amount, 0);
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
        out.writeByte(healthLevel);
        out.writeByte(foodLevel);
        out.writeBoolean(alive);
    }


    @Override
    public void onCollisionBegin(Contact contact) {
        if((contact.getFixtureA() == bodyFix && !contact.getFixtureB().isSensor())
                || (contact.getFixtureB() == bodyFix && !contact.getFixtureA().isSensor())) {
            lastFallVelocity = Math.min(lastFallVelocity, getVelocity().y);
        }

        if(contact.getFixtureA() == groundSensor && !(contact.getFixtureB().getBody().getUserData() instanceof Player)
                || (contact.getFixtureB() == groundSensor && !(contact.getFixtureA().getBody().getUserData() instanceof Player))) {
            collisions++;
            onGround = true;
        }
    }

    @Override
    public void onCollisionEnd(Contact contact) {
        if(contact.getFixtureA() == groundSensor && !(contact.getFixtureB().getBody().getUserData() instanceof Player)
                || (contact.getFixtureB() == groundSensor && !(contact.getFixtureA().getBody().getUserData() instanceof Player))) {
            collisions--;
            if(collisions == 0) onGround = false;
        }
    }

}
