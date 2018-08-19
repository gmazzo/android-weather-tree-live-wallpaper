package com.hm.weather.engine;

import android.graphics.Color;
import java.io.Serializable;

public class Vector4 implements Serializable {
    private static final long serialVersionUID = 2;
    public float a;
    public float x;
    public float y;
    public float z;

    public Vector4() {
        this.a = 1.0f;
        this.z = 1.0f;
        this.y = 1.0f;
        this.x = 1.0f;
    }

    public Vector4(float X, float Y, float Z, float A) {
        this.x = X;
        this.y = Y;
        this.z = Z;
        this.a = A;
    }

    public Vector4(Vector4 vector4) {
        this.x = vector4.x;
        this.y = vector4.y;
        this.z = vector4.z;
        this.a = vector4.a;
    }

    public static int getWebColorFromGlColor(float r, float g, float b, float a) {
        return Color.argb((int) (a * 255.0f), (int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f));
    }

    public static void mix(Vector4 dest, Vector4 main, Vector4 blend, float amount) {
        float oneMinusAmount = 1.0f - amount;
        dest.set((main.x * oneMinusAmount) + (blend.x * amount), (main.y * oneMinusAmount) + (blend.y * amount), (main.z * oneMinusAmount) + (blend.z * amount), (main.a * oneMinusAmount) + (blend.a * amount));
    }

    public void add(float X, float Y, float Z, float A) {
        this.x += X;
        this.y += Y;
        this.z += Z;
        this.a += A;
    }

    public void add(Vector4 other) {
        add(other.x, other.y, other.z, other.a);
    }

    public void divide(float divisor) {
        multiply(1.0f / divisor);
    }

    public boolean equals(float X, float Y, float Z, float A) {
        if (this.x == X && this.y == Y && this.z == Z && this.a == A) {
            return true;
        }
        return false;
    }

    public boolean equals(Vector4 vector4) {
        return equals(vector4.x, vector4.y, vector4.z, vector4.a);
    }

    public int getWebColor() {
        return getWebColorFromGlColor(this.x, this.y, this.z, this.a);
    }

    public void multiply(float scale) {
        multiply(scale, scale, scale, scale);
    }

    public void multiply(float aX, float aY, float aZ, float aA) {
        this.x *= aX;
        this.y *= aY;
        this.z *= aZ;
        this.a *= aA;
    }

    public void normalizeXYZ() {
        float length_reciprocal = 1.0f / Vector3.magnitude(this.x, this.y, this.z);
        this.x *= length_reciprocal;
        this.y *= length_reciprocal;
        this.z *= length_reciprocal;
    }

    public void set(float X, float Y, float Z, float A) {
        this.x = X;
        this.y = Y;
        this.z = Z;
        this.a = A;
    }

    public void set(int webColor) {
        this.x = ((float) Color.red(webColor)) / 255.0f;
        this.y = ((float) Color.green(webColor)) / 255.0f;
        this.z = ((float) Color.blue(webColor)) / 255.0f;
        this.a = ((float) Color.alpha(webColor)) / 255.0f;
    }

    public void set(Vector3 vector3, float A) {
        this.x = vector3.x;
        this.y = vector3.y;
        this.z = vector3.z;
        this.a = A;
    }

    public void set(Vector4 vector4) {
        this.x = vector4.x;
        this.y = vector4.y;
        this.z = vector4.z;
        this.a = vector4.a;
    }

    public void set(String prefColor, float min, float range) {
        String[] as = prefColor.split(" ");
        if (as.length >= 3) {
            this.x = (Float.parseFloat(as[0]) * range) + min;
            this.y = (Float.parseFloat(as[1]) * range) + min;
            this.z = (Float.parseFloat(as[2]) * range) + min;
        }
        if (as.length == 4) {
            this.a = (Float.parseFloat(as[3]) * range) + min;
        }
    }

    public void setToArray(float[] ret) {
        if (ret != null && ret.length == 4) {
            ret[0] = this.x;
            ret[1] = this.y;
            ret[2] = this.z;
            ret[3] = this.a;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(this.x).append(", ");
        sb.append(this.y).append(", ");
        sb.append(this.z).append(", ");
        return sb.append(this.a).append(")").toString();
    }
}
