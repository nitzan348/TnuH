package talpiot.mb.magdadmilbat.vision.detectors;

import com.google.mediapipe.formats.proto.LandmarkProto;

import talpiot.mb.magdadmilbat.vision.Point;
import talpiot.mb.magdadmilbat.vision.VisionMaster;
import java.io.*;
import java.util.*;


/**
 * Simplest implemetation of the IMouth interface. See IMouth for documentation
 */
public class SimpleMouth implements IMouth {

    private Point cornerRight, cornerLeft, top, bot, faceTop, faceBot;
    //Added saved points.
    private Vector<Point> rightCorenerToMiddleTop = new Vector<Point>(4);
    private Vector<Point> leftCorenerToMiddleTop = new Vector<Point>(4);
    private Vector<Point> rightCorenerToMiddleBot = new Vector<Point>(4);
    private Vector<Point> leftCorenerToMiddleBot = new Vector<Point>(4);
    private static final double SCALER = 1000;

    @Override
    public void updateMouthData(LandmarkProto.NormalizedLandmarkList face) {

        //Points needed to calculate mouth symmetry,
        leftCorenerToMiddleTop.add(new Point(face.getLandmark(185)));
        leftCorenerToMiddleTop.add(new Point(face.getLandmark(40)));
        leftCorenerToMiddleTop.add(new Point(face.getLandmark(39)));
        leftCorenerToMiddleTop.add(new Point(face.getLandmark(37)));

        rightCorenerToMiddleTop.add(new Point (face.getLandmark(409)));
        rightCorenerToMiddleTop.add(new Point (face.getLandmark(270)));
        rightCorenerToMiddleTop.add(new Point (face.getLandmark(269)));
        rightCorenerToMiddleTop.add(new Point (face.getLandmark(267)));

        leftCorenerToMiddleBot.add(new Point(face.getLandmark((146))));
        leftCorenerToMiddleBot.add(new Point(face.getLandmark((91))));
        leftCorenerToMiddleBot.add(new Point(face.getLandmark((181))));
        leftCorenerToMiddleBot.add(new Point(face.getLandmark((84))));

        rightCorenerToMiddleBot.add(new Point(face.getLandmark((375))));
        rightCorenerToMiddleBot.add(new Point(face.getLandmark((321))));
        rightCorenerToMiddleBot.add(new Point(face.getLandmark((405))));
        rightCorenerToMiddleBot.add(new Point(face.getLandmark((314))));

        cornerLeft = new Point(face.getLandmark(61));
        cornerRight = new Point(face.getLandmark(291));
        top = new Point(face.getLandmark(0));
        bot = new Point(face.getLandmark(17));
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
        return Math.abs(Point.subtract(top, bot)
                .rotateToNormalize(Point.subtract(faceTop, faceBot))
                .getX());
    }

    @Override
    public double getSymmetryCoef() {
        double firstMouthHalf = 0.0, secondMouthHalf = 0.0;

        //Calculates left and right half of mouth symmetry.
        for(int i = 0; i < 4; i++) {
            firstMouthHalf += Math.abs(Point.subtract(leftCorenerToMiddleTop.get(i), leftCorenerToMiddleBot.get(i)).getY() / getHeightNormalizer()
                                + Point.subtract(leftCorenerToMiddleTop.get(i), leftCorenerToMiddleBot.get(i)).getX() / getWidthNormalizer());

            secondMouthHalf += Math.abs(Point.subtract(rightCorenerToMiddleTop.get(i), rightCorenerToMiddleTop.get(i)).getY() / getHeightNormalizer()
                                + Point.subtract(rightCorenerToMiddleTop.get(i), rightCorenerToMiddleBot.get(i)).getX() / getWidthNormalizer());
        }

        return Math.abs(Point.subtract(top, bot)
                .rotateToNormalize(Point.subtract(faceTop, faceBot))
                .getY()) / getHeightNormalizer()
                + Math.abs(Point.subtract(cornerRight, cornerLeft)
                .rotateToNormalize(Point.subtract(faceTop, faceBot))
                .getX()) / getWidthNormalizer()
        //Added them to final result.
                + firstMouthHalf + secondMouthHalf;
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
                '}';
    }
}
