package com.example.magdadmilbat.vision;

import java.nio.FloatBuffer;

public interface IMouth extends ISubMesh {

    double getWidthNormalizer();
    double getHeightNormalizer();

    double getWidth();

    double getHeight();

    double getSymmetryCoef();

    default double getArea() {

        return getHeight()*getWidth();
    }

    default double enforceSymmetry(double input) {
        return 0.5*input + 0.5*getSymmetryCoef();
    }

    default double getBigMouthScore() {
        return enforceSymmetry(getArea()/(getHeightNormalizer()*getWidthNormalizer()));
    }

    default double getSmileScore() {
        return enforceSymmetry(getWidth()/getWidthNormalizer());
    }
}
