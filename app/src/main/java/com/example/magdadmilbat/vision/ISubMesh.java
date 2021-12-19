package com.example.magdadmilbat.vision;

import java.nio.FloatBuffer;

public interface ISubMesh {

    default float[] getVectorInMesh(int index, FloatBuffer data) {
        return new float[]{data.get(3*index), data.get(3*index+1), data.get(3*index+2)};
    }

}
