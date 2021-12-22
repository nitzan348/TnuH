package com.example.magdadmilbat.vision;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.nio.FloatBuffer;

public interface IMouth {

    /**
     * data from:
     * https://raw.githubusercontent.com/google/mediapipe/a908d668c730da128dfa8d9f6bd25d519d006692/mediapipe/modules/face_geometry/data/canonical_face_model_uv_visualization.png
     * @param face
     */
    void updateMouthData(LandmarkProto.NormalizedLandmarkList face);

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
