package com.astetyne.expirium.server.api;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Saveable {

    void readData(DataInputStream in) throws IOException;

    void writeData(DataOutputStream out) throws IOException;

}
