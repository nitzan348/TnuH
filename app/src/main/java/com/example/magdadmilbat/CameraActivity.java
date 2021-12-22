package com.example.magdadmilbat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.example.magdadmilbat.vision.FaceMeshResultGlRenderer;
import com.example.magdadmilbat.vision.IMouth;
import com.example.magdadmilbat.vision.SimpleMouth;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.glutil.EglManager;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;

import java.util.List;


public class CameraActivity extends AppCompatActivity {


    private static final String TAG = "VISION";

    private CameraInput cameraInput;
    private FaceMesh faceMesh;

    private SurfaceTexture previewFrameTexture;
    private SurfaceView previewDisplayView;
    private EglManager eglManager;
    private ExternalTextureConverter converter;

    private static final int WIDTH = 1024, HEIGHT = 768;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();
        eglManager = new EglManager(null);

        PermissionHelper.checkAndRequestCameraPermissions(this);
        requestPermissions(new String[]{"android.permission.CAMERA"}, 1);

        setupMeshRecognizer();
        startCamera();
    }

    private void setupPreviewDisplayView() {
        previewDisplayView.setVisibility(View.GONE);
        ViewGroup viewGroup = findViewById(R.id.preview_display_layout);
        viewGroup.addView(previewDisplayView);

        previewDisplayView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {}

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                // (Re-)Compute the ideal size of the camera-preview display (the area that the
                                // camera-preview frames get rendered onto, potentially with scaling and rotation)
                                // based on the size of the SurfaceView that contains the display.
//                                Size viewSize = new Size(width, height);
////                                Size displaySize = cameraInput.computeDisplaySizeFromViewSize(viewSize);
//                                Size displaySize = viewSize;

                                // Connect the converter to the camera-preview frames as its input (via
                                // previewFrameTexture), and configure the output width and height as the computed
                                // display size.
                                converter.setSurfaceTextureAndAttachToGLContext(
                                        previewFrameTexture, WIDTH, HEIGHT);
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {}
                        });
    }

    public void setupMeshRecognizer() {
        FaceMeshOptions faceMeshOptions =
                FaceMeshOptions.builder()
                        .setStaticImageMode(false)
                        .setRefineLandmarks(true)
                        .setMaxNumFaces(1)
                        .setRunOnGpu(false).build();

        faceMesh = new FaceMesh(this, faceMeshOptions);
        faceMesh.setErrorListener(
                (message, e) -> Log.e(TAG, "MediaPipe Face Mesh error:" + message));

        faceMesh.setResultListener(
                faceMeshResult -> {


                    try {
                        LandmarkProto.NormalizedLandmarkList face =
                                faceMeshResult.multiFaceLandmarks().get(0);
                        IMouth mouth = new SimpleMouth();
                        mouth.updateMouthData(face);
                        Log.i(
                                TAG,
                                String.format(
                                        "Mouth data: area=%f, symmetry=%f",
                                        mouth.getArea(), mouth.getSymmetryCoef()));
                    } catch (IndexOutOfBoundsException e) {
                        Log.i(TAG, "NO FACEEEE");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        converter = new ExternalTextureConverter(eglManager.getContext());
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraInput.close();
        converter.close();
    }

    public void startCamera() {
        if (cameraInput != null) {
            cameraInput.close();
        }
        cameraInput = new CameraInput(this);
        cameraInput.setOnCameraStartedListener(surfaceTexture -> {
            previewFrameTexture = surfaceTexture;
            previewDisplayView.setVisibility(View.VISIBLE);
        });
        cameraInput.setNewFrameListener(frame -> {
            faceMesh.send(frame);
        });
        cameraInput.start(this, faceMesh.getGlContext(), CameraInput.CameraFacing.FRONT,
                WIDTH, HEIGHT);
    }


}

