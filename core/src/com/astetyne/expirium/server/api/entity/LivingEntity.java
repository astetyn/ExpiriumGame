package com.astetyne.expirium.server.api.entity;

import com.astetyne.expirium.main.entity.EntityType;

public abstract class LivingEntity extends ExpiEntity {

    private final static float MAX_HEALTH_LEVEL = 100;
    private final static float MAX_FOOD_LEVEL = 100;

    protected float healthLevel, foodLevel, temperatureLevel;

    public LivingEntity(EntityType type, float width, float height) {
        super(type, width, height);
        healthLevel = 100;
        foodLevel = 100;
        temperatureLevel = 22;
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
}
