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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.function.Supplier;

import talpiot.mb.magdadmilbat.database.DatabaseManager;
import talpiot.mb.magdadmilbat.database.TrainingData;
import talpiot.mb.magdadmilbat.vision.detectors.IMouth;
import talpiot.mb.magdadmilbat.vision.detectors.SimpleMouth;

/**
 * This class handles all of the vision for the project. See {@link talpiot.mb.magdadmilbat.ExercisePage}
 * for usage example.
 *
 * @author Alexey
 */
public class VisionMaster extends Thread {
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
    public static final int WIDTH = 960, HEIGHT = 720;

    private DecomposedFace currentFace;
    private Exercise currentExr;

    /**
    * saves all past faces to calculate score and improvment.
    * */
    private Vector<LandmarkProto.NormalizedLandmarkList> pastMovement= new Vector<LandmarkProto.NormalizedLandmarkList>();

    /**
     * flags to define current face state during practice.
     * counter to define how many rehearsals user made.
     * */
    private boolean InPractice = false;
    private boolean restingFace = false;
    static int amountOfRehearsals = 0;
    private double rehearsalScoreToBeat = 0.0;
    private double restingFaceScore = 0.0;

    public enum Exercise {

        SMILE(() -> VisionMaster.getInstance().currentFace.getMouth().getSmileScore(), 10, 100),
        BIG_MOUTH(() -> VisionMaster.getInstance().currentFace.getMouth().getBigMouthScore(), 10, 100);

        private final Supplier<Double> valSup;
        private double restingMaximumScore, actingMinimumScore;

        Exercise(Supplier<Double> valueSupplier, double minScore, double maxScore) {
            this.valSup = valueSupplier;
            setActingMinimumScore(minScore);
            setRestingMaximumScore(maxScore);
        }

        public double get() {
            return valSup.get();
        }

        public double getRestingMaximumScore() {
            return restingMaximumScore;
        }

        public void setRestingMaximumScore(double restingMaximumScore) {
            this.restingMaximumScore = restingMaximumScore;
        }

        public double getActingMinimumScore() {
            return actingMinimumScore;
        }

        public void setActingMinimumScore(double actingMinimumScore) {
            this.actingMinimumScore = actingMinimumScore;
        }
    }

    private VisionMaster() {
    }

    public void startNewSession() {
//        DatabaseManager thread = new DatabaseManager(imageView.getContext());
//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
//        String formattedDate = df.format(c);
//        TrainingData training(c, );
//        thread.addTraining(TrainingData training);
    }

    public static VisionMaster getInstance() {
        if (instance == null) {
            instance = new VisionMaster();
        }
        return instance;
    }

    public Vector<LandmarkProto.NormalizedLandmarkList> getPastFaces() {
        return pastMovement;
    }

    public DecomposedFace getCurrentFace() {
        return currentFace;
    }

    public double getScore() {
        if (currentExr != null) {
            return currentExr.get();
        }
        return 0;
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

    public void setCurrentExr(Exercise currentExr) {
        this.currentExr = currentExr;
    }

    /**
     * Set's up the FaceMesh object for use
     */

    /**
     * function deals with all score system -> three flags as class flags
     * that define what state the user is at currently, deals with score comparing
     * and checks when user's face is resting.
     * */
    public void checkForPracticeScore(LandmarkProto.NormalizedLandmarkList face) {

        //adds current movement to past faces.
        this.pastMovement.add(face);

        if (this.getScore() >= restingFaceScore && this.getScore() <= rehearsalScoreToBeat) { //checks if current face is similar to first captured.
            this.InPractice = true;

        }
        else {
            if (this.getScore() >= rehearsalScoreToBeat && this.InPractice) { //CAN BE CHANGES ACCORDING TO CLIENT REQUEST(private field). (level of difficulty).
                amountOfRehearsals++;
                this.InPractice = false;
            }
            else if (this.getScore() <= restingFaceScore){
                this.restingFace = true;
                this.InPractice = false;
            }
        }
    }

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

                        checkForPracticeScore(face);
                    }
                });
    }

}
