// Copyright 2021 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package talpiot.mb.magdadmilbat.vision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.appcompat.widget.AppCompatImageView;

import android.util.Size;

import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import talpiot.mb.magdadmilbat.vision.detectors.OpenCVDetector;
import talpiot.mb.magdadmilbat.vision.detectors.SimpleMouth;

/**
 * An ImageView implementation for displaying {@link FaceMeshResult}.
 * <p>
 * Copied and modified from:
 * https://github.com/google/mediapipe/tree/master/mediapipe/examples/android/solutions/facemesh
 */
public class FaceMeshResultImageView extends AppCompatImageView {
    private static final String TAG = "FaceMeshResultImageView";

    private static final int LIPS_COLOR = Color.parseColor("#E0E0E0");
    private static final int LIPS_THICKNESS = 5; // Pixels
    private Bitmap latest;

    public FaceMeshResultImageView(Context context) {
        super(context);
        setScaleType(AppCompatImageView.ScaleType.FIT_CENTER);
    }

    /**
     * Sets a {@link FaceMeshResult} to render.
     *
     * @param result a {@link FaceMeshResult} object that contains the solution outputs and the input
     *               {@link Bitmap}.
     */
    public void setFaceMeshResult(FaceMeshResult result) {
        if (result == null) {
            return;
        }
        Bitmap bmInput = result.inputBitmap();

        Matrix matrix = new Matrix();
        matrix.postScale(1, -1, bmInput.getWidth() / 2.0f, bmInput.getHeight() / 2.0f);
        bmInput = Bitmap.createBitmap(bmInput, 0, 0, bmInput.getWidth(), bmInput.getHeight(), matrix, true);

        int width = bmInput.getWidth();
        int height = bmInput.getHeight();
        latest = Bitmap.createBitmap(width, height, bmInput.getConfig());

//        // test
        OpenCVDetector testing = new OpenCVDetector();
        Mat tst =
                testing.CropLips(testing.faceMashToCVFrame(result),
                        (SimpleMouth) VisionMaster.getInstance().getCurrentFace().getMouth());
        width = tst.width();
        height = tst.height();
        Bitmap bmp = null;
        Mat tmp = new Mat(height, width, CvType.CV_8U, new Scalar(4));
//        Imgproc.cvtColor(tst, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
        bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tst, bmp);
        latest = bmp;
        bmInput = bmp;
//        // --------------------

        Canvas canvas = new Canvas(latest);
        Size imageSize = new Size(width, height);
        canvas.drawBitmap(bmInput, new Matrix(), null);

        int numFaces = result.multiFaceLandmarks().size();
        for (int i = 0; i < numFaces; ++i) {
            drawLips(
                    canvas,
                    result.multiFaceLandmarks().get(i).getLandmarkList(),
                    imageSize
            );
        }
    }

    /**
     * Updates the image view with the latest {@link FaceMeshResult}.
     */
    public void update() {
        postInvalidate();
        if (latest != null) {
            setImageBitmap(latest);
        }
    }

    private void drawLips(
            Canvas canvas,
            List<NormalizedLandmark> faceLandmarkList,
            Size imageSize) {
        // Draw connections.
        for (FaceMeshConnections.Connection c : FaceMeshConnections.FACEMESH_LIPS) {
            Paint connectionPaint = new Paint();
            connectionPaint.setColor(FaceMeshResultImageView.LIPS_COLOR);
            connectionPaint.setStrokeWidth(FaceMeshResultImageView.LIPS_THICKNESS);
            NormalizedLandmark start = faceLandmarkList.get(c.start());
            NormalizedLandmark end = faceLandmarkList.get(c.end());
            canvas.drawLine(
                    start.getX() * imageSize.getWidth(),
                    start.getY() * imageSize.getHeight(),
                    end.getX() * imageSize.getWidth(),
                    end.getY() * imageSize.getHeight(),
                    connectionPaint);
        }
    }
}
