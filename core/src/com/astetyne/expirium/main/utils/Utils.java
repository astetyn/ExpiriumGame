package com.astetyne.expirium.main.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Utils {

    public static int sizeof(Object obj) {

        try {

            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.close();


            return byteOutputStream.toByteArray().length;
        }catch(IOException ignored) {
            ignored.printStackTrace();
        }
        return -1;
    }

}
