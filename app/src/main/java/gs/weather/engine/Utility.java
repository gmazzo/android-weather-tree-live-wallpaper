package gs.weather.engine;

import android.util.Log;

public class Utility {
    static boolean DEBUG = false;
    static float fpsMax = 0.0f;
    static float fpsMin = 1.0f;
    static int fpsSamples = 0;
    static float fpsSum = 0.0f;

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

    public static void adjustScreenPosForDepth(Vector3 destVector, float cameraFOV, float screenWidth, float screenHeight, float touchX, float touchY, float depth) {
        float f10 = (cameraFOV * (screenWidth / screenHeight)) * 0.01111111f;
        float f14 = (cameraFOV * 0.01111111f) * ((((1.0f - (touchY / screenHeight)) - 0.5f) * 2.0f) * depth);
        destVector.x = ((((touchX / screenWidth) - 0.5f) * 2.0f) * depth) * f10;
        destVector.y = depth;
        destVector.z = f14;
    }

    public static String baseFilenameFromPath(String filePath) {
        String result;
        int i = filePath.lastIndexOf(46);
        if (i > -1) {
            result = filePath.substring(0, i);
        } else {
            result = filePath;
        }
        int j = result.lastIndexOf(47);
        if (j > -1) {
            return result.substring(j + 1);
        }
        return result;
    }

    public static float floatFromPercentageGraph(float val, float[] graph) {
        return floatFromPercentageGraph(val, graph, false);
    }

    public static float floatFromPercentageGraph(float val, float[] graph, boolean clamp) {
        if (val < 0.0f) {
            return graph[0];
        }
        if (val > 1.0f) {
            return graph[graph.length - 1];
        }
        if (graph.length == 0) {
            return 0.0f;
        }
        if (graph.length < 2) {
            return graph[0];
        }
        float valAdjusted = val * ((float) graph.length);
        int indexLower = (int) valAdjusted;
        int indexUpper = ((int) valAdjusted) + 1;
        if (indexLower >= graph.length) {
            indexLower = 0;
        }
        if (indexUpper >= graph.length) {
            if (clamp) {
                indexUpper = graph.length - 1;
            } else {
                indexUpper = 0;
            }
        }
        float valRemainder = valAdjusted - ((float) indexLower);
        return (graph[indexUpper] * valRemainder) + (graph[indexLower] * (1.0f - valRemainder));
    }

    public static void fpsTrack(String tag, float timeDelta) {
        fpsSum += timeDelta;
        fpsSamples++;
        if (fpsMin > timeDelta) {
            fpsMin = timeDelta;
        }
        if (fpsMax < timeDelta) {
            fpsMax = timeDelta;
        }
        if (fpsSamples > 100) {
            float average = fpsSum / ((float) fpsSamples);
            StringBuilder sb = new StringBuilder("FPS: ");
            sb.append(1.0f / average).append("  Min: ");
            sb.append(fpsMin).append("  Max: ");
            Log.v(tag, sb.append(fpsMax).toString());
            fpsSamples = 0;
            fpsSum = 0.0f;
            fpsMin = 1.0f;
            fpsMax = 0.0f;
        }
    }

    public static float lerpTo(float dest, float start, float duration, float timeElapsed) {
        float ratio = timeElapsed / duration;
        return (dest * ratio) + ((1.0f - ratio) * start);
    }
}
