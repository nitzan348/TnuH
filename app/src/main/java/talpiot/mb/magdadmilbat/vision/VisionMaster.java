package talpiot.mb.magdadmilbat.vision;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions;

import talpiot.mb.magdadmilbat.vision.detectors.IMouth;
import talpiot.mb.magdadmilbat.vision.detectors.SimpleMouth;

/**
 * This class handles all of the vision for the project. See {@link talpiot.mb.magdadmilbat.ExercisePage}
 * for usage example.
 */
public class VisionMaster {
    /**
     * Tag for logging
     */
    private static final String TAG = "VISION";

    private static VisionMaster instance;

    /**
     * Object responsible for retreiving camera frames
     */
    private CameraInput cameraInput;
    /**
     * Mediapipe object that handles face recognition and mesh generation
     */
    private FaceMesh faceMesh;
    private FaceMeshResultImageView imageView;

    /**
     * Width and height parameters for the CameraInput object.
     * I'm not sure what they do, my best guess is they define the frame dimensions returned
     * by the camera object
     */
    private static final int WIDTH = 960, HEIGHT = 720;

    private DecomposedFace currentFace;

    private VisionMaster() {
    }

    public static VisionMaster getInstance() {
        if (instance == null) {
            instance = new VisionMaster();
        }
        return instance;
    }

    public DecomposedFace getCurrentFace() {
        return currentFace;
    }

    public static class DecomposedFace {
        private final IMouth mouth;

        private DecomposedFace(IMouth mouth) {
            this.mouth = mouth;
        }

        public IMouth getMouth() {
            return mouth;
        }
    }

    public void attachToContext(Context context) {
        setupMeshRecognizer(context);
        imageView = new FaceMeshResultImageView(context);
    }

    public void attachFrame(@NonNull FrameLayout frame) {
        frame.removeAllViewsInLayout();
        imageView.setImageDrawable(null);
        frame.addView(imageView);
        imageView.setVisibility(View.VISIBLE);
    }

    public void attachCamera(Activity activity) {
        if (cameraInput != null) cameraInput.close();

        assert faceMesh != null;

        // Create new camera
        cameraInput = new CameraInput(activity);
        cameraInput.setNewFrameListener(faceMesh::send);
        // Start the camera
        cameraInput.start(activity,
                faceMesh.getGlContext(),
                CameraInput.CameraFacing.FRONT,
                WIDTH, HEIGHT);
    }

    /**
     * Set's up the FaceMesh object for use
     */
    public void setupMeshRecognizer(Context context) {
        FaceMeshOptions faceMeshOptions =
                FaceMeshOptions.builder()
                        .setStaticImageMode(false)  // Not static because we directly stream form the camera
                        .setRefineLandmarks(true)   // Not sure what this does, copied from tutorial
                        .setMaxNumFaces(1)          // Only one face should be in frame
                        .setRunOnGpu(false).build();// Run on GPU crashes, not sure why. Just use CPU.

        faceMesh = new FaceMesh(context, faceMeshOptions);
        faceMesh.setErrorListener(
                (message, e) -> Log.e(TAG, "MediaPipe Face Mesh error:" + message));

        // For showing the vision data on screen, not actually required
        Handler uiUpdater = new Handler();

        // Everytime a face mesh is detected the given function is called
        faceMesh.setResultListener(
                faceMeshResult -> {
                    if (faceMeshResult.multiFaceLandmarks().size() > 0) {

                        // The next line gets a "Face" object from the face list
                        LandmarkProto.NormalizedLandmarkList face =
                                faceMeshResult.multiFaceLandmarks().get(0);

                        // Generate a mouth from the face object
                        IMouth mouth = new SimpleMouth();
                        mouth.updateMouthData(face);

                        // Update face object
                        currentFace = new DecomposedFace(mouth);

                        // Draw image
                        imageView.setFaceMeshResult(faceMeshResult);
                        uiUpdater.post(() -> imageView.update());
                    }
                });
    }

}
