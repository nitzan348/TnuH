package talpiot.mb.magdadmilbat.vision.detectors;

import com.google.mediapipe.formats.proto.LandmarkProto;

/**
 * Generic mouth object represeting a mouth
 *
 */
public interface IMouth {

    /**
     * data from:
     * https://raw.githubusercontent.com/google/mediapipe/a908d668c730da128dfa8d9f6bd25d519d006692/mediapipe/modules/face_geometry/data/canonical_face_model_uv_visualization.png
     * <p>
     * This takes the face and updates the inside data to match the given face
     *
     * @param face
     */
    void updateMouthData(LandmarkProto.NormalizedLandmarkList face);

    /**
     *
     * @return Get a value to normalize the x axis. e.g. the width of the face
     */
    double getWidthNormalizer();

    /**
     *
     * @return Get a value to normalize the y axis. e.g. the height of the face
     */
    double getHeightNormalizer();

    /**
     *
     * @return Width of the lips
     */
    double getWidth();

    /**
     *
     * @return Get the height of the lips
     */
    double getHeight();

    /**
     * @return number representing the symmetry of the lips
     */
    double getSymmetryCoef();


    /**
     *
     * @return Get the area of the lips
     */
    default double getArea() {

        return getHeight() * getWidth() * Math.PI; // Area of ellipse
    }

    /**
     *
     * @return Get the score for the "open mouth wide" exercise
     */
    default double getBigMouthScore() {
        return getArea() / (getHeightNormalizer() * getWidthNormalizer());
    }

    /**
     * @return ration of height by width of mouth - In score units
     */
    default double getHeightWidthRatio() {
        return (getHeight() / getWidth()) * 100; //Success rates for a kiss
    }

    /**
     *
     * @return number representing the success of a kiss
     */
    default double getKissScore() {
        return getHeightWidthRatio();
    }

    /**
     *
     * @return Get the score for the "smile wide exercise
     */
    default double getSmileScore() {
        return getWidth() / getWidthNormalizer();
    }
}
