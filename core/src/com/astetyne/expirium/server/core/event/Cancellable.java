package com.astetyne.expirium.server.core.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
