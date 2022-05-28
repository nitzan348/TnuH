package talpiot.mb.magdadmilbat.vision.detectors;

import com.google.mediapipe.formats.proto.LandmarkProto;

import talpiot.mb.magdadmilbat.vision.Point;

import java.io.*;
import java.util.*;


/**
 * Simplest implemetation of the IMouth interface. See IMouth for documentation
 */
public class SimpleMouth implements IMouth {

    private Point cornerRight, cornerLeft, top, bot, faceTop, faceBot;

    public Vector<Point> getRightCorenerToMiddleBot() {
        return rightCorenerToMiddleBot;
    }
    private Vector<Point> upperLipAreaLeftToRight = new Vector<Point>(13);
    private Vector<Point> lowerLipAreaLeftToRight = new Vector<Point>(13);

    //Added saved points.
    private Vector<Point> rightCorenerToMiddleTop = new Vector<Point>(4);
    private Vector<Point> leftCorenerToMiddleTop = new Vector<Point>(4);

    public Vector<Point> getRightCorenerToMiddleTop() {
        return rightCorenerToMiddleTop;
    }

    public Vector<Point> getLeftCorenerToMiddleTop() {
        return leftCorenerToMiddleTop;
    }

    public Vector<Point> getLeftCorenerToMiddleBot() {
        return leftCorenerToMiddleBot;
    }

    public Vector<Point> getUpperLipAreaLeftToRight() {
        return upperLipAreaLeftToRight;
    }

    private Vector<Point> rightCorenerToMiddleBot = new Vector<Point>(4);

    public Vector<Point> getLowerLipAreaLeftToRight() {
        return lowerLipAreaLeftToRight;
    }

    private Vector<Point> leftCorenerToMiddleBot = new Vector<Point>(4);
    private static final double SCALER = 1000;

    @Override
    public void updateMouthData(LandmarkProto.NormalizedLandmarkList face) {

        //Points needed to calculate mouth symmetry,
        leftCorenerToMiddleTop.add(new Point(face.getLandmark(185)));
        leftCorenerToMiddleTop.add(new Point(face.getLandmark(40)));
        leftCorenerToMiddleTop.add(new Point(face.getLandmark(39)));
        leftCorenerToMiddleTop.add(new Point(face.getLandmark(37)));

        rightCorenerToMiddleTop.add(new Point(face.getLandmark(409)));
        rightCorenerToMiddleTop.add(new Point(face.getLandmark(270)));
        rightCorenerToMiddleTop.add(new Point(face.getLandmark(269)));
        rightCorenerToMiddleTop.add(new Point(face.getLandmark(267)));

        leftCorenerToMiddleBot.add(new Point(face.getLandmark((146))));
        leftCorenerToMiddleBot.add(new Point(face.getLandmark((91))));
        leftCorenerToMiddleBot.add(new Point(face.getLandmark((181))));
        leftCorenerToMiddleBot.add(new Point(face.getLandmark((84))));

        rightCorenerToMiddleBot.add(new Point(face.getLandmark((375))));
        rightCorenerToMiddleBot.add(new Point(face.getLandmark((321))));
        rightCorenerToMiddleBot.add(new Point(face.getLandmark((405))));
        rightCorenerToMiddleBot.add(new Point(face.getLandmark((314))));

        upperLipAreaLeftToRight.add(new Point(face.getLandmark(214)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(207)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(206)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(203)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark;
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(97)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(2)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(326)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(327)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(423)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(426)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(436)));
        upperLipAreaLeftToRight.add(new Point(face.getLandmark(434)));

        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(214)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(210)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(211)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(32)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(208)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(199)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(428)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(262)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(431)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(430)));
        lowerLipAreaLeftToRight.add(new Point(face.getLandmark(434)));



        cornerLeft = new Point(face.getLandmark(61));
        cornerRight = new Point(face.getLandmark(291));
        top = new Point(face.getLandmark(0));
        bot = new Point(face.getLandmark(17));
        faceTop = new Point(face.getLandmark(10));
        faceBot = new Point(face.getLandmark(152));
    }


    public int getTopX() { return (int)top.getX(); }
    public int getTopY() { return (int)top.getY(); }
    public int getBotX() { return (int)bot.getX(); }
    public int getBotY() { return (int)bot.getY(); }

    public int getLeftX() { return (int)cornerLeft.getX(); }
    public int getLeftY() { return (int)cornerLeft.getY(); }
    public int getRightX() { return (int)cornerRight.getX(); }
    public int getRightY() { return (int)cornerRight.getY(); }

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

        Point normalizationVector = Point.subtract(faceTop, faceBot);

        //Calculates left and right half of mouth symmetry.
        for (int i = 0; i < 4; i++) {
            firstMouthHalf += Math.abs(
                    +Point.subtract(leftCorenerToMiddleTop.get(i), leftCorenerToMiddleBot.get(i)).rotateToNormalize(normalizationVector).getX() / getWidthNormalizer());

            secondMouthHalf += Math.abs(
                    +Point.subtract(rightCorenerToMiddleTop.get(i), rightCorenerToMiddleBot.get(i)).rotateToNormalize(normalizationVector).getX() / getWidthNormalizer());
        }

        return Math.abs(Point.subtract(top, bot)
                .rotateToNormalize(normalizationVector)
                .getY()) / getHeightNormalizer()
                + Math.abs(Point.subtract(cornerRight, cornerLeft)
                .rotateToNormalize(normalizationVector)
                .getX()) / getWidthNormalizer()
//        //Added them to final result.
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
                ", kiss=" + getHeightWidthRatio() +
                '}';
    }

    public Point getCornerRight() {
        return cornerRight;
    }

    public Point getCornerLeft() {
        return cornerLeft;
    }

    public Point getTop() {
        return top;
    }

    public Point getBot() {
        return bot;
    }
}
