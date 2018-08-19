package com.hm.weather.engine;

import java.util.Random;

public class GlobalRand {
    public static Random rand = new Random();

    public static boolean flipCoin() {
        if (rand.nextFloat() < 0.5f) {
            return true;
        }
        return false;
    }

    public static float floatRange(float min, float max) {
        return (rand.nextFloat() * (max - min)) + min;
    }

    public static int intRange(int min, int max) {
        return rand.nextInt(max - min) + min;
    }

    public static void randomNormalizedVector(Vector3 dest) {
        float x = floatRange(-1.0f, 1.0f);
        float y = floatRange(-1.0f, 1.0f);
        float z = floatRange(-1.0f, 1.0f);
        float length_reciprocal = 1.0f / Vector3.magnitude(x, y, z);
        dest.x = x * length_reciprocal;
        dest.y = y * length_reciprocal;
        dest.z = z * length_reciprocal;
    }

    public static void randomNormalizedVector(Vector4 dest) {
        float x = floatRange(-1.0f, 1.0f);
        float y = floatRange(-1.0f, 1.0f);
        float z = floatRange(-1.0f, 1.0f);
        float length_reciprocal = 1.0f / Vector3.magnitude(x, y, z);
        dest.x = x * length_reciprocal;
        dest.y = y * length_reciprocal;
        dest.z = z * length_reciprocal;
    }
}
