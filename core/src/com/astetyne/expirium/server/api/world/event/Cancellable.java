package com.astetyne.expirium.server.api.world.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
