package talpiot.mb.magdadmilbat.vision;

import com.google.mediapipe.formats.proto.LandmarkProto;

/**
 * Represents a simple 2D point
 * <p>
 * Copied from: <br>
 * https://github.com/GreenBlitz/MotionControl/blob/dev/motion/src/main/java/org/greenblitz/motion/base/Point.java
 *
 * @author Alexey
 */
public class Point {

    public static final Point ORIGIN = new Point(0, 0);

    protected double x;
    protected double y;

    public Point(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    /**
     * Import a landmark into a point. normalizes so x and y have the same units
     * @param landmark
     */
    public Point(LandmarkProto.NormalizedLandmark landmark) {
        this(landmark.getX() * VisionMaster.HEIGHT, landmark.getY() * VisionMaster.WIDTH);
    }

    public static Point cis(double ang, double len) {
        return new Point(len * Math.sin(ang), len * Math.cos(ang));
    }


    public Point clone() {
        return new Point(x, y);
    }


    public double[] get() {
        return new double[]{x, y};
    }


    public void set(double x, double y) {
        setX(x);
        setY(y);
    }


    public Point translate(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static Point add(Point first, Point other) {
        return first.clone().translate(other);
    }

    public Point negate() {
        return new Point(-x, -y);
    }

    public Point scale(double scale) {
        return new Point(scale * x, scale * y);
    }

    public Point rotate(double radians) {
        double cos = Math.cos(radians),
                sin = Math.sin(radians);
        Point temp = this.clone();
        temp.setX(- this.y * sin + this.x * cos);
        temp.setY(this.y * cos + this.x * sin);
        return temp;
    }

    /**
     *
     * @param target
     * @return A new point rotated by an angle that makes the target be (x, 0), i.e. angle 0.
     */
    public Point rotateToNormalize(Point target) {
        return rotate(-target.toPolarCoords()[1]);
    }

    public Point translate(Point p) {
        return translate(p.getX(), p.getY());
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public static Point subtract(Point subtractee, Point subtractor) {
        return Point.add(subtractee, subtractor.clone().negate());
    }

    public static double dotProduct(Point a, Point b) {
        return a.x * b.x + a.y * b.y;
    }

    public static double normSquared(Point point) {
        return dotProduct(point, point);
    }

    public static double norm(Point point) {
        return Math.hypot(point.x, point.y);
    }

    public double norm() {
        return norm(this);
    }

    public static double distSqared(Point a, Point b) {
        return normSquared(subtract(a, b));
    }

    public static double dist(Point a, Point b) {
        return Math.hypot(subtract(a, b).x, subtract(a, b).y);
    }

    public static boolean isFuzzyEqual(double first, double second, double epsilon) {
        return Math.abs(first - second) < epsilon;
    }

    public static boolean isFuzzyEqual(double first, double second) {
        return isFuzzyEqual( first, second, 1E-6);
    }

    public static boolean fuzzyEquals(Point fir, Point sec, double epsilon) {
        return isFuzzyEqual(fir.getX(), sec.getX(), epsilon) && isFuzzyEqual(fir.getY(), sec.getY(), epsilon);
    }

    public Point weightedAvg(Point b, double bWeight) {
        return new Point((1 - bWeight) * x + bWeight * b.x, (1 - bWeight) * y + bWeight * b.y);
    }

    public Point avg(Point b) {
        return weightedAvg(b, 0.5);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return (Point.isFuzzyEqual(this.getX(), point.getX()))
                && isFuzzyEqual(this.getY(), point.getY());
    }

    /**
     * @return first element is the length, second is the angle
     */
    public double[] toPolarCoords() {
        return new double[]{dist(Point.ORIGIN, this), Math.atan2(getY(), getX())};
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
