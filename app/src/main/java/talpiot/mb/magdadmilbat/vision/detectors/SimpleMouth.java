package talpiot.mb.magdadmilbat.vision.detectors;

import com.google.mediapipe.formats.proto.LandmarkProto;

import talpiot.mb.magdadmilbat.vision.Point;
import talpiot.mb.magdadmilbat.vision.symmetry.DynamicMouthSymmetryCalculator;


/**
 * Simplest implemetation of the IMouth interface. See IMouth for documentation
 */
public class SimpleMouth implements IMouth {

    //Important specific points of the face
    private Point cornerRight, cornerLeft, LipsMiddleTop, LipsMiddleBot, faceTop, faceBot;
    private DynamicMouthSymmetryCalculator dynamicMouthSymmetryCalculator;

    @Override
    public void updateMouthData(LandmarkProto.NormalizedLandmarkList face) {

        dynamicMouthSymmetryCalculator = new DynamicMouthSymmetryCalculator(face);

        cornerLeft = new Point(face.getLandmark(61));
        cornerRight = new Point(face.getLandmark(291));
        LipsMiddleTop = new Point(face.getLandmark(0));
        LipsMiddleBot = new Point(face.getLandmark(17));
        faceTop = new Point(face.getLandmark(10));
        faceBot = new Point(face.getLandmark(152));
    }

    @Override
    public double getWidthNormalizer() {
        return Point.dist(faceTop, faceBot);
    }

    @Override
    public double getHeightNormalizer() {
        return getWidthNormalizer();
    }

    @Override
    public double getWidth() {
        return Math.abs(Point.subtract(cornerRight, cornerLeft)
                .rotateToNormalize(Point.subtract(faceTop, faceBot))
                .getY());
    }

    @Override
    public double getHeight() {
        return Math.abs(Point.subtract(LipsMiddleTop, LipsMiddleBot)
                .rotateToNormalize(Point.subtract(faceTop, faceBot))
                .getX());
    }

    @Override
    public double getSymmetryCoef() {
        Point normalizationVector = Point.subtract(LipsMiddleTop, LipsMiddleBot);

        return dynamicMouthSymmetryCalculator.maximumDistanceToMakeSymmetric(
                LipsMiddleBot, normalizationVector
        ) / getWidthNormalizer();
    }

    @Override
    public String toString() {
        return "SimpleMouth{" +
                "width=" + getWidth() +
                ", height=" + getHeight() +
                ", symm=" + getSymmetryCoef() +
                ", smile=" + getSmileScore() +
                ", openMouth=" + getBigMouthScore() +
                ", area=" + getArea() +
                ", kiss=" + getHeightWidthRatio() +
                '}';
    }
}
