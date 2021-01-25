package com.astetyne.expirium.server;

public interface ServerFailListener {

    /**
     * This is called when server is corrupted and unable to continue.
     * Note that this is called from server thread and you must sync it for yourself.
     */
    void onServerFail(String msg);

}
