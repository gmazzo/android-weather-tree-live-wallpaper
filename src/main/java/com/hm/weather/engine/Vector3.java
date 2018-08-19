package com.hm.weather.engine;

import java.io.Serializable;

public class Vector3 implements Serializable {
    public static final float DEGREES_IN_A_RADIAN = 57.29578f;
    public static final float PI = 3.141593f;
    public static final float PI_TIMES_TWO = 6.283185f;
    private static final long serialVersionUID = 2;
    public float x;
    public float y;
    public float z;

    public Vector3() {
        this.z = 0.0f;
        this.y = 0.0f;
        this.x = 0.0f;
    }

    public Vector3(float X, float Y, float Z) {
        this.x = X;
        this.y = Y;
        this.z = Z;
    }

    public Vector3(Vector3 vector3) {
        this.x = vector3.x;
        this.y = vector3.y;
        this.z = vector3.z;
    }

    public static float angleFrom2dVector(float X, float Y) {
        return ((float) Math.atan2((double) X, (double) Y)) * 57.29578f;
    }

    public static void crossProduct(Vector3 dest, Vector3 first, Vector3 second) {
        dest.x = (first.y * second.z) - (first.z * second.y);
        dest.y = (first.z * second.x) - (first.x * second.z);
        dest.z = (first.x * second.y) - (first.y * second.x);
    }

    public static float distanceBetween(float x1, float y1, float z1, float x2, float y2, float z2) {
        return magnitude(x1 - x2, y1 - y2, z1 - z2);
    }

    public static float distanceBetween(Vector3 first, Vector3 second) {
        return distanceBetween(first.x, first.y, first.z, second.x, second.y, second.z);
    }

    public static float dotProduct(float x1, float y1, float z1, float x2, float y2, float z2) {
        return ((x1 * x2) + (y1 * y2)) + (z1 * z2);
    }

    public static float magnitude(float X, float Y, float Z) {
        return (float) Math.sqrt((double) (((X * X) + (Y * Y)) + (Z * Z)));
    }

    public void add(float X, float Y, float Z) {
        this.x += X;
        this.y += Y;
        this.z += Z;
    }

    public void add(Vector3 other) {
        add(other.x, other.y, other.z);
    }

    public float angleX() {
        return angleFrom2dVector(this.y, this.z);
    }

    public float angleY() {
        return angleFrom2dVector(this.x, this.z);
    }

    public float angleZ() {
        return angleFrom2dVector(this.x, this.y);
    }

    public float distanceTo(float X, float Y, float Z) {
        return magnitude(this.x - X, this.y - Y, this.z - Z);
    }

    public float distanceTo(Vector3 other) {
        return distanceTo(other.x, other.y, other.z);
    }

    public float dotProduct(float X, float Y, float Z) {
        return dotProduct(this.x, this.y, this.z, X, Y, Z);
    }

    public float dotProduct(Vector3 other) {
        return dotProduct(this.x, this.y, this.z, other.x, other.y, other.z);
    }

    public boolean equals(float X, float Y, float Z) {
        if (this.x == X && this.y == Y && this.z == Z) {
            return true;
        }
        return false;
    }

    public boolean equals(Vector3 vector3) {
        return equals(vector3.x, vector3.y, vector3.z);
    }

    public float magnitude() {
        return magnitude(this.x, this.y, this.z);
    }

    public void multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
    }

    public void multiply(float aX, float aY, float aZ) {
        this.x *= aX;
        this.y *= aY;
        this.z *= aZ;
    }

    public void normalize() {
        float length = magnitude(this.x, this.y, this.z);
        if (length != 0.0f) {
            float length_reciprocal = 1.0f / length;
            this.x *= length_reciprocal;
            this.y *= length_reciprocal;
            this.z *= length_reciprocal;
        }
    }

    public void rotateAroundX(float degrees) {
        double radians = (double) (degrees / 57.29578f);
        float radSin = (float) Math.sin(radians);
        float radCos = (float) Math.cos(radians);
        this.y = (this.y * radCos) - (this.z * radSin);
        this.z = (this.y * radSin) + (this.z * radCos);
    }

    public void rotateAroundY(float degrees) {
        double radians = (double) (degrees / 57.29578f);
        float radSin = (float) Math.sin(radians);
        float radCos = (float) Math.cos(radians);
        this.x = (this.x * radCos) - (this.z * radSin);
        this.z = (this.x * radSin) + (this.z * radCos);
    }

    public void rotateAroundZ(float degrees) {
        double radians = (double) (degrees / 57.29578f);
        float radSin = (float) Math.sin(radians);
        float radCos = (float) Math.cos(radians);
        this.x = (this.x * radCos) - (this.y * radSin);
        this.y = (this.x * radSin) + (this.y * radCos);
    }

    public void set(float xyz) {
        set(xyz, xyz, xyz);
    }

    public void set(float X, float Y, float Z) {
        this.x = X;
        this.y = Y;
        this.z = Z;
    }

    public void set(Vector3 vector3) {
        this.x = vector3.x;
        this.y = vector3.y;
        this.z = vector3.z;
    }

    public void subtract(float X, float Y, float Z) {
        this.x -= X;
        this.y -= Y;
        this.z -= Z;
    }

    public void subtract(Vector3 other) {
        subtract(other.x, other.y, other.z);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(this.x).append(", ");
        sb.append(this.y).append(", ");
        return sb.append(this.z).append(")").toString();
    }
}
