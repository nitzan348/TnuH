package com.example.magdadmilbat.vision;

import java.nio.FloatBuffer;

public interface ISubMesh {

    default float[] getDataInMesh(int index, FloatBuffer data) {
        return new float[]{data.get(3*index), data.get(3*index+1), data.get(3*index+2)};
    }

    default Vector3D getVectorInMesh(int index, FloatBuffer data) {
        return new Vector3D(data.get(3*index), data.get(3*index+1), data.get(3*index+2));
    }

}
