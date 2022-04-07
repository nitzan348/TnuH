package talpiot.mb.magdadmilbat.vision.detectors;
import android.graphics.Bitmap;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import com.google.mediapipe.framework.TextureFrame;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;

public class OpenCVDetector {


    public Mat faceMashToCVFrame(FaceMeshResult res) {
        Bitmap bmp = res.inputBitmap();
        Mat mat = new Mat();
        Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        return mat;

    }


}
