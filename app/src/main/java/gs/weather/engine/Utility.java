package gs.weather.engine;

import android.util.Log;

public class Utility {
    static boolean DEBUG = false;

    public static class Logger {
        public static void v(String tag, String info) {
            if (Utility.DEBUG) {
                Log.v(tag, info);
            }
        }

        public static void d(String tag, String info) {
            if (Utility.DEBUG) {
                Log.d(tag, info);
            }
        }

        public static void i(String tag, String info) {
            if (Utility.DEBUG) {
                Log.i(tag, info);
            }
        }

        public static void w(String tag, String info) {
            if (Utility.DEBUG) {
                Log.w(tag, info);
            }
        }

        public static void e(String tag, String info) {
            if (Utility.DEBUG) {
                Log.e(tag, info);
            }
        }
    }

    public static void adjustScreenPosForDepth(Vector destVector, float cameraFOV, float screenWidth, float screenHeight, float touchX, float touchY, float depth) {
        float f10 = (cameraFOV * (screenWidth / screenHeight)) * 0.01111111f;
        float f14 = (cameraFOV * 0.01111111f) * ((((1.0f - (touchY / screenHeight)) - 0.5f) * 2.0f) * depth);
        destVector.setX(((((touchX / screenWidth) - 0.5f) * 2.0f) * depth) * f10);
        destVector.setY(depth);
        destVector.setZ(f14);
    }

}
