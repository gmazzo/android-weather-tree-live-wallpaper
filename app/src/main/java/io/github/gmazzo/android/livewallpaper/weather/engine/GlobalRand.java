package io.github.gmazzo.android.livewallpaper.weather.engine;

import java.util.Random;

public class GlobalRand {
    public static Random rand = new Random();

    public static boolean flipCoin() {
        return rand.nextFloat() < 0.5f;
    }

    public static float floatRange(float min, float max) {
        return (rand.nextFloat() * (max - min)) + min;
    }

    public static int intRange(int min, int max) {
        return rand.nextInt(max - min) + min;
    }

    public static void randomNormalizedVector(EngineColor dest) {
        dest.set(new Vector(
                        floatRange(-1.0f, 1.0f),
                        floatRange(-1.0f, 1.0f),
                        floatRange(-1.0f, 1.0f))
                        .normalize(),
                dest.getA());
    }

}
