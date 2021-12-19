package com.example.magdadmilbat.vision;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Vector3D {

    public float x, y, z;

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(float[] arrData) {
        this(arrData[0], arrData[1], arrData[2]);
    }

    public Vector3D(Vector3D other) {
        this(other.x, other.y, other.z);
    }

    @NonNull
    @Override
    public String toString() {
        return "Vector3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3D vector3D = (Vector3D) o;
        return Float.compare(vector3D.x, x) == 0 && Float.compare(vector3D.y, y) == 0 && Float.compare(vector3D.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
