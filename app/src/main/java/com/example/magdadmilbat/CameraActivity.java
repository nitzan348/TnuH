package com.example.magdadmilbat;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.magdadmilbat.vision.IMouth;
import com.example.magdadmilbat.vision.SimpleMouth;
import com.example.magdadmilbat.vision.SimpleMouth2;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions;


public class CameraActivity extends AppCompatActivity {


    private static final String TAG = "VISION";

    private CameraInput cameraInput;
    private FaceMesh faceMesh;

    private static final int WIDTH = 1024, HEIGHT = 768;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        PermissionHelper.checkAndRequestCameraPermissions(this);
        requestPermissions(new String[]{"android.permission.CAMERA"}, 1);

        setupMeshRecognizer();
        startCamera();
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

        TextView debugView = (TextView) findViewById(R.id.debug_view);
        Handler textUpdater = new Handler();

        faceMesh.setResultListener(
                faceMeshResult -> {
                    try {
                        LandmarkProto.NormalizedLandmarkList face =
                                faceMeshResult.multiFaceLandmarks().get(0);
                        IMouth mouth = new SimpleMouth2();
                        mouth.updateMouthData(face);

                        if (((System.currentTimeMillis() % 1000) / 100) % 7 == 0) {
                            textUpdater.post(() ->
                                    debugView.setText(
                                            String.format("Width = %f, Height = %f.\n" +
                                                            "Symmetry = %f, Area = %f\n" +
                                                            "Wide Score = %f, Smile Score = %f",
                                                    mouth.getWidth(), mouth.getHeight(),
                                                    mouth.getSymmetryCoef(), mouth.getArea(),
                                                    mouth.getBigMouthScore(), mouth.getSmileScore())));
                        }

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
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        } else {
            PermissionHelper.checkAndRequestCameraPermissions(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraInput.close();
    }

    public void startCamera() {
        if (cameraInput != null) {
            cameraInput.close();
        }
        cameraInput = new CameraInput(this);
        cameraInput.setNewFrameListener(frame -> {
            faceMesh.send(frame);
        });
        cameraInput.start(this, faceMesh.getGlContext(), CameraInput.CameraFacing.FRONT,
                WIDTH, HEIGHT);
    }


}

