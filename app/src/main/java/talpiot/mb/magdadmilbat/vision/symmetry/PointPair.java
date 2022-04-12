package talpiot.mb.magdadmilbat.vision.symmetry;

import com.google.mediapipe.formats.proto.LandmarkProto;

import talpiot.mb.magdadmilbat.vision.Point;

public class PointPair {

    protected Point first, second;

    public PointPair(Point first, Point second) {
        this.first = first;
        this.second = second;
    }

    public PointPair(LandmarkProto.NormalizedLandmarkList face, int first, int second) {
        this(new Point(face.getLandmark(first)), new Point(face.getLandmark(second)));
    }

    private static Point reflectAroundAxis(Point target, Point axisOrigin, Point axisVector) {
        double rot = -axisVector.toPolarCoords()[1];
        Point inNewCoordSystem = Point.subtract(target, axisOrigin).rotate(rot);
        Point reflected = new Point(inNewCoordSystem.getX(), -inNewCoordSystem.getY());
        return Point.add(reflected.rotate(-rot), axisOrigin);
    }

    public double distanceMovedRequiredForSymmetry(Point axisOrigin, Point axisVector) {
        return Point.dist(first, reflectAroundAxis(second, axisOrigin, axisVector));
    }

    public Point getFirst() {
        return first;
    }

    public Point getSecond() {
        return second;
    }


    @Override
    public String toString() {
        return "PointPair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
