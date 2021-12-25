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

/**
 * Activity responsible for the exercise, including all of the code to run the vision
 */
public class CameraActivity extends AppCompatActivity {

    /**
     * Tag for logging
     */
    private static final String TAG = "VISION";

    /**
     * Object responsible for retreiving camera frames
     */
    private CameraInput cameraInput;
    /**
     * Mediapipe object that handles face recognition and mesh generation
     */
    private FaceMesh faceMesh;

    /**
     * Width and height parameters for the CameraInput object.
     * I'm not sure what they do, my best guess is they define the frame dimensions returned
     * by the camera object
     */
    private static final int WIDTH = 1024, HEIGHT = 768;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // I'm pretty sure only one of those lines is needed, but better safe than sorry
        PermissionHelper.checkAndRequestCameraPermissions(this);
        requestPermissions(new String[]{"android.permission.CAMERA"}, 1);

        setupMeshRecognizer();
        startCamera();
    }

    /**
     * Set's up the FaceMesh object for use
     */
    public void setupMeshRecognizer() {
        FaceMeshOptions faceMeshOptions =
                FaceMeshOptions.builder()
                        .setStaticImageMode(false)  // Not static because we directly stream form the camera
                        .setRefineLandmarks(true)   // Not sure what this does, copied from tutorial
                        .setMaxNumFaces(1)          // Only one face should be in frame
                        .setRunOnGpu(false).build();// Run on GPU crashes, not sure why. Just use CPU.

        faceMesh = new FaceMesh(this, faceMeshOptions);
        faceMesh.setErrorListener(
                (message, e) -> Log.e(TAG, "MediaPipe Face Mesh error:" + message));

        // For showing the vision data on screen, not actually required
        TextView debugView = findViewById(R.id.debug_view);
        Handler textUpdater = new Handler();

        // Everytime a face mesh is detected the given function is called
        faceMesh.setResultListener(
                faceMeshResult -> {
                    try {
                        // The next line gets a "Face" object from the face list
                        LandmarkProto.NormalizedLandmarkList face =
                                faceMeshResult.multiFaceLandmarks().get(0);
                        IMouth mouth = new SimpleMouth2();

                        mouth.updateMouthData(face);

                        // Very arabic way to update the text every ~100ms
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


    /**
     * Creates and start the camera
     */
    public void startCamera() {
        // Prevent two cameras being opened
        if (cameraInput != null) {
            cameraInput.close();
        }
        // Create new camares and send it's frames to the FaceMesh object
        cameraInput = new CameraInput(this);
        cameraInput.setNewFrameListener(frame -> {
            faceMesh.send(frame);
        });
        // Start the camera
        cameraInput.start(this, faceMesh.getGlContext(), CameraInput.CameraFacing.FRONT,
                WIDTH, HEIGHT);
    }


}

