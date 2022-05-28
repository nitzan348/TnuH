package talpiot.mb.magdadmilbat.vision.detectors;
import static talpiot.mb.magdadmilbat.vision.VisionMaster.HEIGHT;
import static talpiot.mb.magdadmilbat.vision.VisionMaster.WIDTH;
import static talpiot.mb.magdadmilbat.vision.VisionMaster.getInstance;

import com.google.mediapipe.solutions.facemesh.FaceMeshResult;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class OpenCVDetector {
    final static int MAX_H = 10;
    final static int MIN_H = 10;

    final static int MAX_S = 10;
    final static int MIN_S = 10;

    final static int MAX_V = 30;
    final static int MIN_V = 30;

    private static Bitmap convertMatToBitMap(Mat input){
        Bitmap bmp = null;
        Mat rgb = new Mat();
        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGB);

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgb, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }

    public Mat getCroppedPicture(Mat cropImage, SimpleMouth face) {
        Point upperLeft = new Point(face.getCornerLeft().getX() - 10, face.getTop().getY());
        Point lowerRight = new Point(face.getCornerRight().getX() + 10, face.getBot().getY());

        Mat paintedMouthArea = this.CropLips(cropImage, face);

        Rect rect = new Rect((int) upperLeft.x, (int) upperLeft.y,
                (int) Math.abs(upperLeft.x - lowerRight.x + 1), (int) Math.abs(lowerRight.y - upperLeft.y + 1));
        return paintedMouthArea.submat(rect);
    }

    public Mat CropLips(Mat cropImage, SimpleMouth face) {
        int lineType = 8;
        int shift = 0;

        MatOfPoint matPt = new MatOfPoint();
        Vector<talpiot.mb.magdadmilbat.vision.Point> summmedPointsUpper = new Vector<>();

//        summmedPointsUpper.addAll(face.getUpperLipAreaLeftToRight());
        summmedPointsUpper.add(face.getCornerRight());
        summmedPointsUpper.addAll(face.getRightCorenerToMiddleTop());
        summmedPointsUpper.add(face.getTop());
//        summmedPointsUpper.addAll(face.getLeftCorenerToMiddleTop());
//        summmedPointsUpper.add(face.getCornerLeft());

        Vector<Point> upperAsPt = new Vector<>();
        for (talpiot.mb.magdadmilbat.vision.Point p : summmedPointsUpper) {
            upperAsPt.add(new Point(p.getX()/WIDTH, p.getY()/HEIGHT));
        }

        Point[] parr = new Point[upperAsPt.size()];
        matPt.fromArray(upperAsPt.toArray(parr));

        List<MatOfPoint> ppt = new ArrayList<MatOfPoint>();
        ppt.add(matPt);
        Imgproc.fillPoly(cropImage,
                ppt,
                new Scalar( 0,0,0 ),
                lineType,
                shift,
                new Point(0,0) );

        Vector<talpiot.mb.magdadmilbat.vision.Point> summmedPointsLower = new Vector<>();

        summmedPointsLower.addAll(face.getLowerLipAreaLeftToRight());
        summmedPointsLower.add(face.getCornerRight());
        summmedPointsLower.addAll(face.getRightCorenerToMiddleBot());
        summmedPointsLower.add(face.getBot());
        summmedPointsLower.addAll(face.getLeftCorenerToMiddleBot());
        summmedPointsLower.add(face.getCornerRight());

        Vector<Point> lowerAsPt = new Vector<>();
        for (talpiot.mb.magdadmilbat.vision.Point p : summmedPointsLower) {
            lowerAsPt.add(new Point(p.getX(), p.getY()));
        }

        Point[] pparr = new Point[lowerAsPt.size()];
        matPt.fromArray(lowerAsPt.toArray(pparr));

        ppt = new ArrayList<MatOfPoint>();
//        ppt.add(matPt);
//        Imgproc.fillPoly(cropImage,
//                ppt,
//                new Scalar( 0,0,0 ),
//                lineType,
//                shift,
//                new Point(0,0) );

        return cropImage;
    }

    public Mat faceMashToCVFrame(FaceMeshResult res) {
        Bitmap bmp = res.inputBitmap();

        Matrix matrix = new Matrix();
        matrix.postScale(1, -1, bmp.getWidth() / 2.0f, bmp.getHeight() / 2.0f);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        Mat mat = new Mat();
        Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        return mat;

    }

    public Mat thresholdTongue(Mat croppedImage) {
        Mat dstHSV = new Mat();

        //cropped image rgb to hsv
        Imgproc.cvtColor(croppedImage, dstHSV, Imgproc.COLOR_RGB2HSV);
        Mat dst = new Mat();


        SimpleMouth mt = (SimpleMouth)getInstance().getCurrentFace().getMouth();

        double[] hsv = dstHSV.get(mt.getBotX(), mt.getTopY() - (mt.getTopY() - mt.getBotY() / 3));
        double H = hsv[0] * 100; //hue
        double S = hsv[1] * 100; //saturation
        double V = hsv[2] * 100; //value
        if((H == 0 || (H > 0 && H < 20)) && (S == 0 || (S > 0 && S < 20)) && V > 85 && V < 105)
        {
            //If it's white - teeth in mouth
            Core.inRange(dstHSV, new Scalar(0 , 0, 85), new Scalar(20, 0, 105), dst);
        }


        hsv = dstHSV.get(mt.getLeftX() - Math.abs((mt.getRightX() - mt.getLeftX()) / 3), mt.getRightY());
        H = hsv[0] * 100; //hue
        S = hsv[1] * 100; //saturation
        V = hsv[2] * 100; //value
        if(H == 0)
        {
            //If it's black - dark space in mouth - teeth
            Core.inRange(dstHSV, new Scalar(MIN_H , MIN_S, MIN_V), new Scalar(MAX_H, MAX_S, MAX_V), dst);
        }


//        Mat ret = new Mat();
        //cropped image hsvto rgb
//        Imgproc.cvtColor(dst, ret, Imgproc.COLOR_HSV2RGB);

        return dst;
    }

}

