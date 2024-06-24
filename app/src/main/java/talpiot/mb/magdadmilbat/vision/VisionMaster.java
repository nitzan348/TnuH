package talpiot.mb.magdadmilbat.vision;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.MagdadMilbat.R;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions;

import java.util.function.Supplier;

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

    private Context context;

    /**
     * Width and height parameters for the CameraInput object.
     * I'm not sure what they do, my best guess is they define the frame dimensions returned
     * by the camera object
     */
    public static final int WIDTH = 960, HEIGHT = 720;

    private DecomposedFace currentFace;
    private Exercise currentExr;
    private Calib currentCalib;

    /**
     * flags to define current face state during practice.
     * counter to define how many rehearsals user made.
     */
    private boolean restingFace = false;

    public enum Calib {

        SMILE(() -> VisionMaster.getInstance().currentFace.getMouth().getSmileScore(),
                0.42, 0.315, 0.035,
                R.string.smile),
        BIG_MOUTH(() -> VisionMaster.getInstance().currentFace.getMouth().getBigMouthScore(),
                0.35, 0.1, 0.035, R.string.open_mouth),
        KISS(() -> VisionMaster.getInstance().currentFace.getMouth().getKissScore(),
                100, 40, 0.035, R.string.kiss);

        private final Supplier<Double> valSup;
        private double restingMaximumScore, actingMinimumScore, maximumSymmetry;
        private int prettyName;

        Calib(Supplier<Double> valueSupplier, double actingMin, double restingMax, double maxSym,
              int name) {
            this.valSup = valueSupplier;
            setActingMinimumScore(actingMin);
            setRestingMaximumScore(restingMax);
            setMaximumSymmetry(maxSym);
            prettyName = name;
        }

        public String get_name() {
            return VisionMaster.getInstance().context.getResources().getString(prettyName);
        }

        public double getActualActingMin() {

            double difficulty =
                    Integer.parseInt(VisionMaster.getInstance().context
                            .getSharedPreferences(name(), 0)
                            .getString("diff", "1")) / 100.0;

            double min = restingMaximumScore + (actingMinimumScore - restingMaximumScore) * 0.1; // Arbitrary 10%

            Log.i(VisionMaster.TAG, "min diff = " + min + " ret diff = "
                    + (min + difficulty * ((actingMinimumScore - min) / 0.8)));

            return min + difficulty * ((actingMinimumScore - min) / 0.8); // 0.8 to have regular human values at 80%
        }

        public double getActualRestingMax() {
            return restingMaximumScore;
        }

        public double getActualMaxSym() {
            double difficulty =
                    Integer.parseInt(
                            VisionMaster.getInstance().context
                                    .getSharedPreferences(name(), 0)
                                    .getString("sym_diff", "1")) / 100.0;

            double max = maximumSymmetry * 5; // Arbitrary 500%

            return max - difficulty * (max - 0.8 * maximumSymmetry); // Again arbitrary
        }

        public double getMaximumSymmetry() {
            return maximumSymmetry;
        }

        public void setMaximumSymmetry(double maximumSymmetry) {
            this.maximumSymmetry = maximumSymmetry;
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
    public enum Exercise {

        SMILE(() -> VisionMaster.getInstance().currentFace.getMouth().getSmileScore(),
                0.42, 0.315, 0.035,
                R.string.smile),
        BIG_MOUTH(() -> VisionMaster.getInstance().currentFace.getMouth().getBigMouthScore(),
                0.35, 0.1, 0.035, R.string.open_mouth),
        KISS(() -> VisionMaster.getInstance().currentFace.getMouth().getKissScore(),
                100, 40, 0.035, R.string.kiss);

        private final Supplier<Double> valSup;
        private double restingMaximumScore, actingMinimumScore, maximumSymmetry;
        private int prettyName;

        Exercise(Supplier<Double> valueSupplier, double actingMin, double restingMax, double maxSym,
                 int name) {
            this.valSup = valueSupplier;
            setActingMinimumScore(actingMin);
            setRestingMaximumScore(restingMax);
            setMaximumSymmetry(maxSym);
            prettyName = name;
        }

        public String get_name() {
            return VisionMaster.getInstance().context.getResources().getString(prettyName);
        }

        public double getActualActingMin() {

            double difficulty =
                    Integer.parseInt(VisionMaster.getInstance().context
                            .getSharedPreferences(name(), 0)
                            .getString("diff", "1")) / 100.0;

            double min = restingMaximumScore + (actingMinimumScore - restingMaximumScore) * 0.1; // Arbitrary 10%

            Log.i(VisionMaster.TAG, "min diff = " + min + " ret diff = "
                    + (min + difficulty * ((actingMinimumScore - min) / 0.8)));

            return min + difficulty * ((actingMinimumScore - min) / 0.8); // 0.8 to have regular human values at 80%
        }

        public double getActualRestingMax() {
            return restingMaximumScore;
        }

        public double getActualMaxSym() {
            double difficulty =
                    Integer.parseInt(
                            VisionMaster.getInstance().context
                                    .getSharedPreferences(name(), 0)
                                    .getString("sym_diff", "1")) / 100.0;

            double max = maximumSymmetry * 5; // Arbitrary 500%

            return max - difficulty * (max - 0.8 * maximumSymmetry); // Again arbitrary
        }

        public double getMaximumSymmetry() {
            return maximumSymmetry;
        }

        public void setMaximumSymmetry(double maximumSymmetry) {
            this.maximumSymmetry = maximumSymmetry;
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

    public static VisionMaster getInstance() {
        if (instance == null) {
            instance = new VisionMaster();
        }
        return instance;
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

    public double getSymmetryScore() {
        return currentFace.getMouth().getSymmetryCoef();
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
        this.context = context;
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
    public Exercise getCurrentExr() {
        return this.currentExr;
    }

    /**
     * function deals with all score system -> three flags as class flags
     * that define what state the user is at currently, deals with score comparing
     * and checks when user's face is resting.
     */
    public boolean didCompleteRep() {

        if (this.getScore() >= currentExr.getActualActingMin() && this.restingFace
                && currentExr.getActualMaxSym() > getSymmetryScore()) {
            this.restingFace = false;
            return true;
        }
        if (this.getScore() <= currentExr.getActualRestingMax()) {
            this.restingFace = true;
        }
        return false;
    }

    public boolean isRestingFace() {
        return this.restingFace;
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
                    }
                });
    }
    public static double []  recSyms(IMouth [] ar,String motion) {
        double [] recSyms = new double [numberLevels];
        double average;
        int sum=0;
        for (int i=0;i<ar.length;i++){
            sum+=ar[i].getSymmetryCoef();
        }
        average=sum/ar.length;
        recSyms[3]=average;
        recSyms[0]=average-3*diff_syms;
        recSyms[1]=average-2*diff_syms;
        recSyms[2]=average-diff_syms;
        recSyms[4]=average+diff_syms;
        recSyms[5]=average+diff_syms;
        return recSyms;
    }
    public static double [] recPows(IMouth [] ar,String motion) {
        double [] recPows = new double [numberLevels];
        double average;
        int sum=0;
        for (int i=0;i<ar.length;i++){
            if(motion=="smile"){
                sum+=ar[i].getSmileScore();
            }
            if(motion=="kiss"){
                sum+=ar[i].getKissScore();
            }
            if(motion=="open"){
                sum+=ar[i].getBigMouthScore();
            }
        }
        average=sum/ar.length;
        recPows[0]=average-diff_pows;
        recPows[1]=average;
        recPows[2]=average+diff_pows;
        return recPows;
    }
    static final int numberLevels = 3;
    static final int diff_syms=10;
    static final int diff_pows=10;
}