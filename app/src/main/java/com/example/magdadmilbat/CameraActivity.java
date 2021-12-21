package com.example.magdadmilbat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.example.magdadmilbat.vision.FaceMeshResultGlRenderer;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;


public class CameraActivity extends AppCompatActivity {


    private static final String TAG = "CameraAct";
    private CameraInput cameraInput;
    private FaceMesh faceMesh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        PermissionHelper.checkAndRequestCameraPermissions(this);
        setupMeshRecognizer();
        setupCamera();

        cameraInput.start(this, faceMesh.getGlContext(), CameraInput.CameraFacing.FRONT,
                1024, 768);
    }

    public void setupMeshRecognizer() {
        FaceMeshOptions faceMeshOptions =
                FaceMeshOptions.builder()
                        .setStaticImageMode(false)
                        .setRefineLandmarks(true)
                        .setMaxNumFaces(1)
                        .setRunOnGpu(true).build();

        faceMesh = new FaceMesh(this, faceMeshOptions);
        faceMesh.setErrorListener(
                (message, e) -> Log.e(TAG, "MediaPipe Face Mesh error:" + message));

        faceMesh.setResultListener(
                faceMeshResult -> {
                    try {
                        LandmarkProto.NormalizedLandmark noseLandmark =
                                faceMeshResult.multiFaceLandmarks().get(0).getLandmarkList().get(1);
                        Log.i(
                                TAG,
                                String.format(
                                        "MediaPipe Face Mesh nose normalized coordinates (value range: [0, 1]): x=%f, y=%f",
                                        noseLandmark.getX(), noseLandmark.getY()));
                    } catch (IndexOutOfBoundsException e) {
                        Log.i(TAG, "NO FACEEEE");
                    }
                });
    }

    private void setupCamera() {
        cameraInput = new CameraInput(this);
        cameraInput.setNewFrameListener(faceMesh::send);
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
        }
    }

    public void startCamera() {}


}

