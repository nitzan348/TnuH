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
import com.google.common.collect.ImmutableSet;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;
import java.util.List;

/** An ImageView implementation for displaying {@link FaceMeshResult}. */
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
     *     {@link Bitmap}.
     */
    public void setFaceMeshResult(FaceMeshResult result) {
        if (result == null) {
            return;
        }
        Bitmap bmInput = result.inputBitmap();

        Matrix matrix = new Matrix();
        matrix.postScale(1, -1, bmInput.getWidth()/2.0f, bmInput.getHeight()/2.0f);
        bmInput = Bitmap.createBitmap(bmInput, 0, 0, bmInput.getWidth(), bmInput.getHeight(), matrix, true);

        int width = bmInput.getWidth();
        int height = bmInput.getHeight();
        latest = Bitmap.createBitmap(width, height, bmInput.getConfig());

        Canvas canvas = new Canvas(latest);
        Size imageSize = new Size(width, height);
        canvas.drawBitmap(bmInput, new Matrix(), null);

        int numFaces = result.multiFaceLandmarks().size();
        for (int i = 0; i < numFaces; ++i) {
            drawLandmarksOnCanvas(
                    canvas,
                    result.multiFaceLandmarks().get(i).getLandmarkList(),
                    FaceMeshConnections.FACEMESH_LIPS,
                    imageSize,
                    LIPS_COLOR,
                    LIPS_THICKNESS);
        }
    }

    /** Updates the image view with the latest {@link FaceMeshResult}. */
    public void update() {
        postInvalidate();
        if (latest != null) {
            setImageBitmap(latest);
        }
    }

    private void drawLandmarksOnCanvas(
            Canvas canvas,
            List<NormalizedLandmark> faceLandmarkList,
            ImmutableSet<FaceMeshConnections.Connection> connections,
            Size imageSize,
            int color,
            int thickness) {
        // Draw connections.
        for (FaceMeshConnections.Connection c : connections) {
            Paint connectionPaint = new Paint();
            connectionPaint.setColor(color);
            connectionPaint.setStrokeWidth(thickness);
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
