package com.example.magdadmilbat.vision;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.nio.FloatBuffer;

public class SimpleMouth implements IMouth {

    private LandmarkProto.NormalizedLandmark cornerRight, cornerLeft, top, bot;

    @Override
    public void updateMouthData(LandmarkProto.NormalizedLandmarkList face) {
        cornerLeft = face.getLandmark(61);
        cornerRight = face.getLandmark(291);
        top = face.getLandmark(0);
        bot = face.getLandmark(17);
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
        return Math.abs(cornerRight.getX() - cornerLeft.getX());
    }

    @Override
    public double getHeight() {
        return Math.abs(top.getY() - bot.getY());
    }

    @Override
    public double getSymmetryCoef() {
        return Math.abs(cornerRight.getY() - cornerLeft.getY())/getHeightNormalizer()
                + Math.abs(top.getX() - bot.getX())/getWidthNormalizer();
    }
}
