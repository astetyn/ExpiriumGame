package com.astetyne.expirium.client.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static final GlyphLayout glyphLayout = new GlyphLayout();

    private static final String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Map<Character, Integer> symbolIndex = new HashMap<>(symbols.length() * 4 / 3 + 1);
    private static final BigInteger radix = BigInteger.valueOf(symbols.length());

    static {
        for (int i = 0; i < symbols.length(); i++) {
            symbolIndex.put(symbols.charAt(i), i);
        }
    }

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

    public static int getDirSize(FileHandle dir) {
        int size = 0;
        for(FileHandle fh : dir.list()) {
            if(fh.isDirectory()) {
                size += getDirSize(fh);
            }else {
                size += fh.length();
            }
        }
        return size;
    }

    public static void deleteDir(FileHandle dir) {
        for(FileHandle fh : dir.list()) {
            if(fh.isDirectory()) {
                deleteDir(fh);
            }else {
                fh.delete();
            }
        }
        dir.delete();
    }

    public static float getTextWidth(String text, BitmapFont font) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }

    public static float getTextHeight(String text, BitmapFont font) {
        glyphLayout.setText(font, text);
        return glyphLayout.height;
    }

    public static float fromCMToPercW(float cm) {
        return (cm * Gdx.graphics.getDensity() * 100) / Gdx.graphics.getWidth() * 1000;
    }

    public static float percFromW(float val) {
        return Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight() * val / 2;
    }

    public static float percFromH(float val) {
        return Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth() * val * 2;
    }

    // main only for testing
    public static void main(String[] args) throws UnknownHostException {
        System.out.println("-- running main for testing --");
        System.out.println(getCodeFromAddress((Inet4Address) Inet4Address.getByName("192.168.137.244")));
        System.out.println(getAddressFromCode("CUMOEx"));
    }

    public static Inet4Address getAddressFromCode(String code) throws UnknownHostException {
        BigInteger value = BigInteger.ZERO;
        for (int i = 0; i < code.length(); i++) {
            Integer index = symbolIndex.get(code.charAt(i));
            value = value.multiply(radix).add(BigInteger.valueOf(index));
        }
        return (Inet4Address) Inet4Address.getByAddress(ByteBuffer.allocate(4).putInt(value.intValue()).array());
    }

    public static String getCodeFromAddress(Inet4Address address) {
        BigInteger bi = BigInteger.valueOf(Integer.toUnsignedLong(ByteBuffer.wrap(address.getAddress()).getInt()));
        StringBuilder buf = new StringBuilder();
        for (BigInteger v = bi; v.signum() != 0; v = v.divide(radix)) {
            buf.append(symbols.charAt(v.mod(radix).intValue()));
        }
        return buf.reverse().toString();
    }

    public static String getGameCode() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            return getCodeFromAddress((Inet4Address) inetAddress);
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "error";
    }

}
