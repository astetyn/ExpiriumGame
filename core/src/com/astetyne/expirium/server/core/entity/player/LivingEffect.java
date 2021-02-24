package com.astetyne.expirium.server.core.entity.player;

public enum LivingEffect {
    
    DROWNING,
    ;

    public static LivingEffect get(int i) {
        return values()[i];
    }
    
}
