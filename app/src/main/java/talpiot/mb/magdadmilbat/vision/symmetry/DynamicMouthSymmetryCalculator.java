package talpiot.mb.magdadmilbat.vision.symmetry;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import talpiot.mb.magdadmilbat.vision.Point;

public class DynamicMouthSymmetryCalculator {

    protected List<PointPair> pointPairList;

    public DynamicMouthSymmetryCalculator(LandmarkProto.NormalizedLandmarkList face) {
        pointPairList = new ArrayList<>();


        pointPairList.add(new PointPair(face, 27, 267));
        pointPairList.add(new PointPair(face, 22, 269));
        pointPairList.add(new PointPair(face, 40, 270));
        pointPairList.add(new PointPair(face, 41, 271));

        pointPairList.add(new PointPair(face, 61, 291));

        for (int i = 0; i < 4; i++) {
            pointPairList.add(new PointPair(face, 91 - i, 321 - i));
            pointPairList.add(new PointPair(face, 181 - i, 405 - i));
            pointPairList.add(new PointPair(face, 84 + i, 314 + i));
        }
    }

    private Stream<Double> transformToSymmetries(Point axisOrigin, Point axisVector) {
        return pointPairList.stream().
                map(a -> a.distanceMovedRequiredForSymmetry(axisOrigin, axisVector));
    }

    public double averageDistanceToMakeSymmetric(Point axisOrigin, Point axisVector) {
        return transformToSymmetries(axisOrigin, axisVector).reduce(0.0, Double::sum)
                / pointPairList.size();
    }

    public double maximumDistanceToMakeSymmetric(Point axisOrigin, Point axisVector) {
        return transformToSymmetries(axisOrigin, axisVector).reduce(0.0, Double::max);
    }

}
