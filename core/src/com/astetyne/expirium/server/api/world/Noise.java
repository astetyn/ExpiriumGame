package com.astetyne.expirium.server.api.world;

import java.util.Random;

/**
 * <p>
 * Adapted from Riven's Implementation of Perlin noise. Modified it to be more
 * OOP rather than C like.
 * </p>
 *
 * @author Matthew A. Johnston (WarmWaffles)
 *
 */
public class Noise {

    static final protected int SINCOS_LENGTH = (int) (360f / 0.5f);

    static final int PERLIN_YWRAPB = 4;
    static final int PERLIN_YWRAP = 1<<PERLIN_YWRAPB;
    static final int PERLIN_ZWRAPB = 8;
    static final int PERLIN_ZWRAP = 1<<PERLIN_ZWRAPB;
    static final int PERLIN_SIZE = 4095;

    static int perlin_octaves = 4; // default to medium smooth
    static float perlin_amp_falloff = 0.5f; // 50% reduction/octave

    // [toxi 031112]
    // new vars needed due to recent change of cos table in PGraphics
    static int perlin_TWOPI, perlin_PI;
    static float[] perlin_cosTable;
    static float[] perlin;

    static Random perlinRandom;

    public static float noise(float x, float y, float z) {
        if (perlin == null) {
            if (perlinRandom == null) {
                perlinRandom = new Random();
            }
            perlin = new float[PERLIN_SIZE + 1];
            for (int i = 0; i < PERLIN_SIZE + 1; i++) {
                perlin[i] = perlinRandom.nextFloat(); //(float)Math.random();
            }
            // [toxi 031112]
            // noise broke due to recent change of cos table in PGraphics
            // this will take care of it
            perlin_cosTable = new float[SINCOS_LENGTH];
            for (int i = 0; i < SINCOS_LENGTH; i++) {
                perlin_cosTable[i] = (float) Math.cos(i * Math.PI/180.0f * 0.5f);
            }
            perlin_TWOPI = perlin_PI = SINCOS_LENGTH;
            perlin_PI >>= 1;
        }

        if (x<0) x=-x;
        if (y<0) y=-y;
        if (z<0) z=-z;

        int xi=(int)x, yi=(int)y, zi=(int)z;
        float xf = x - xi;
        float yf = y - yi;
        float zf = z - zi;
        float rxf, ryf;

        float r=0;
        float ampl=0.5f;

        float n1,n2,n3;

        for (int i=0; i<perlin_octaves; i++) {
            int of=xi+(yi<<PERLIN_YWRAPB)+(zi<<PERLIN_ZWRAPB);

            rxf=noise_fsc(xf);
            ryf=noise_fsc(yf);

            n1  = perlin[of&PERLIN_SIZE];
            n1 += rxf*(perlin[(of+1)&PERLIN_SIZE]-n1);
            n2  = perlin[(of+PERLIN_YWRAP)&PERLIN_SIZE];
            n2 += rxf*(perlin[(of+PERLIN_YWRAP+1)&PERLIN_SIZE]-n2);
            n1 += ryf*(n2-n1);

            of += PERLIN_ZWRAP;
            n2  = perlin[of&PERLIN_SIZE];
            n2 += rxf*(perlin[(of+1)&PERLIN_SIZE]-n2);
            n3  = perlin[(of+PERLIN_YWRAP)&PERLIN_SIZE];
            n3 += rxf*(perlin[(of+PERLIN_YWRAP+1)&PERLIN_SIZE]-n3);
            n2 += ryf*(n3-n2);

            n1 += noise_fsc(zf)*(n2-n1);

            r += n1*ampl;
            ampl *= perlin_amp_falloff;
            xi<<=1; xf*=2;
            yi<<=1; yf*=2;
            zi<<=1; zf*=2;

            if (xf>=1.0f) { xi++; xf--; }
            if (yf>=1.0f) { yi++; yf--; }
            if (zf>=1.0f) { zi++; zf--; }
        }
        return r;
    }

    private static float noise_fsc(float i) {
        // using bagel's cosine table instead
        return 0.5f*(1.0f-perlin_cosTable[(int)(i*perlin_PI)%perlin_TWOPI]);
    }

}