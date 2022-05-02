package talpiot.mb.magdadmilbat.vision.detectors;
import static talpiot.mb.magdadmilbat.vision.VisionMaster.getInstance;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.TextureFrame;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import org.opencv.core.Mat;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import com.google.mediapipe.framework.TextureFrame;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;
import org.opencv.core.Core;
import org.opencv.core.CvType;


public class OpenCVDetector {
    final static int MAX_H = 10;
    final static int MIN_H = 10;

    final static int MAX_S = 10;
    final static int MIN_S = 10;

    final static int MAX_V = 30;
    final static int MIN_V = 30;


    public Mat getCroppedPicture(Mat cropImage, SimpleMouth face) {
        Point upperLeft = new Point(face.getCornerLeft().getX() - 10, face.getTop().getY());
        Point lowerRight = new Point(face.getCornerRight().getX() + 10, face.getBot().getY());

        Rect rect = new Rect((int) upperLeft.x, (int) upperLeft.y,
                (int) Math.abs(upperLeft.x - lowerRight.x + 1), (int) Math.abs(lowerRight.y - upperLeft.y + 1));
        return cropImage.submat(rect);
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

