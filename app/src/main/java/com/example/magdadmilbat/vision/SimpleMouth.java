package com.example.magdadmilbat.vision;

import java.nio.FloatBuffer;

public class SimpleMouth implements IMouth {

    private Vector3D cornerRight, cornerLeft, top, bot;

    public void updateMouthData(FloatBuffer buffer) {
        cornerLeft = new Vector3D(getVectorInMesh(76, buffer));
        cornerRight = new Vector3D(getVectorInMesh(306, buffer));
        top = new Vector3D(getVectorInMesh(12, buffer));
        bot = new Vector3D(getVectorInMesh(15, buffer));
    }

    @Override
    public double getWidthNormalizer() {
        return 1;
    }

    @Override
    public double getHeightNormalizer() {
        return 1;
    }

    @Override
    public double getWidth() {
        return Math.abs(cornerRight.x - cornerLeft.x);
    }

    @Override
    public double getHeight() {
        return Math.abs(top.y - bot.y);
    }

    @Override
    public double getSymmetryCoef() {
        return Math.abs(cornerRight.y - cornerLeft.y)/getHeightNormalizer()
                + Math.abs(top.x - bot.x)/getWidthNormalizer();
    }
}
