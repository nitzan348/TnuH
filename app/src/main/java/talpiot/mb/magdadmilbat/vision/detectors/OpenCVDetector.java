package talpiot.mb.magdadmilbat.vision.detectors;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.TextureFrame;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import org.opencv.core.Mat;


public class OpenCVDetector {


    public Mat getCroppedPicture(Mat cropImage, SimpleMouth face) {
        Point upperLeft = new Point(face.getCornerLeft().getX() - 10, face.getTop().getY());
        Point lowerRight = new Point(face.getCornerRight().getX() + 10, face.getBot().getY());

        Rect rect = new Rect((int) upperLeft.x, (int) upperLeft.y,
                (int) (upperLeft.x - lowerRight.x + 1), (int) (lowerRight.y - upperLeft.y + 1));
        return cropImage.submat(rect);
    }

    public void /* openCV frame */ mediapipeToCVFrame(TextureFrame frame) {

    }


}

