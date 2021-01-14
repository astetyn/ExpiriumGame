package com.astetyne.expirium.server.api;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Saveable {

    void writeData(DataOutputStream out) throws IOException;

}
