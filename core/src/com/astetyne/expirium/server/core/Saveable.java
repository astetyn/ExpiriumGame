package com.astetyne.expirium.server.core;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Saveable {

    void writeData(DataOutputStream out) throws IOException;

}
