package talpiot.mb.magdadmilbat;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MagdadMilbat.R;
import talpiot.mb.magdadmilbat.vision.IMouth;
import talpiot.mb.magdadmilbat.vision.SimpleMouth;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions;

public class ExercisePage extends AppCompatActivity implements View.OnClickListener {

    /**
     * Tag for logging
     */
    private static final String TAG = "VISION";

    /**
     * Object responsible for retreiving camera frames
     */
    private CameraInput cameraInput;
    private ShowCamera cameraDisplay;
    /**
     * Mediapipe object that handles face recognition and mesh generation
     */
    private FaceMesh faceMesh;

    /**
     * Width and height parameters for the CameraInput object.
     * I'm not sure what they do, my best guess is they define the frame dimensions returned
     * by the camera object
     */
    private static final int WIDTH = 960, HEIGHT = 720;
    private static final int CAMERA_ID = 1;

    private Button btnBack;
    private TextView tvRepetition, tvExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_page);

        btnBack = (Button) findViewById(R.id.btnBack);
        tvRepetition = (TextView) findViewById(R.id.tvRepetition);
        tvExercise = (TextView) findViewById(R.id.tvExercise);
        btnBack.setOnClickListener(this);
        tvExercise.setText(getIntent().getStringExtra("exercise"));

        // I'm pretty sure only one of those lines is needed, but better safe than sorry
        PermissionHelper.checkAndRequestCameraPermissions(this);
        requestPermissions(new String[]{"android.permission.CAMERA"}, CAMERA_ID);

        setupMeshRecognizer();
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
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
        TextView debugView = findViewById(R.id.exerciseQualDisplay);
        Handler textUpdater = new Handler();

        // Everytime a face mesh is detected the given function is called
        faceMesh.setResultListener(
                faceMeshResult -> {
                    Log.i(TAG, "asdsada");
                    try {
                        // The next line gets a "Face" object from the face list
                        LandmarkProto.NormalizedLandmarkList face =
                                faceMeshResult.multiFaceLandmarks().get(0);

                        // Generate a mouth from the face object
                        IMouth mouth = new SimpleMouth();
                        mouth.updateMouthData(face);

                        // Very arabic way to update the text every ~100ms
                        if (((System.currentTimeMillis() % 1000) / 100) % 7 == 0) {
                            textUpdater.post(() ->
                                    debugView.setText(
                                            String.format("Width = %f, Height = %f. " +
                                                            "Symmetry = %f, Area = %f " +
                                                            "Wide Score = %f, Smile Score = %f",
                                                    mouth.getWidth(), mouth.getHeight(),
                                                    mouth.getSymmetryCoef(), mouth.getArea(),
                                                    mouth.getBigMouthScore(), mouth.getSmileScore())));
                        }
                    } catch (IndexOutOfBoundsException e) {
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startCamera();
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
        cameraInput = null;
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

        FrameLayout frame = findViewById(R.id.preview_display_layout);
        if (cameraDisplay == null) {
            cameraDisplay = new ShowCamera(this, Camera.open(CAMERA_ID));
            frame.addView(cameraDisplay);
        }
    }
}