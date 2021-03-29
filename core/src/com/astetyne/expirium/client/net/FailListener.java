package com.astetyne.expirium.client.net;

public interface FailListener {

    /**
     * This is called when client gateway is corrupted and unable to continue.
     * Note that this is called from gateway thread and you must sync it for yourself.
     */
    void onFail(String msg);

}
