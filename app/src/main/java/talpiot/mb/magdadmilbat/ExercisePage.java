package talpiot.mb.magdadmilbat;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.media.MediaPlayer;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MagdadMilbat.R;
import com.google.mediapipe.components.PermissionHelper;
import com.plattysoft.leonids.ParticleSystem;

import java.io.IOException;

import talpiot.mb.magdadmilbat.vision.VisionMaster;
import talpiot.mb.magdadmilbat.vision.detectors.IMouth;

public class ExercisePage extends AppCompatActivity implements View.OnClickListener {

    /**
     * Tag for logging
     */
    private static final String TAG = "EXR";

    private static final int CAMERA_ID = 1;

    private VisionMaster vision;

    private Button btnBack;
    private TextView tvRepetition, tvExercise;
    private boolean stopThread;
    private int reps;



    AnimationDrawable starAnimation;

    private void showConffetti() {
        new ParticleSystem(this, 200, R.drawable.confeti2, 10000)
                .setSpeedModuleAndAngleRange(0.1f, 0.3f, 90, 180)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.emiter_top_right), 100, 500);
        new ParticleSystem(this, 200, R.drawable.confeti3, 10000)
                .setSpeedModuleAndAngleRange(0.1f, 0.3f, 0, 90)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.emiter_top_left), 100, 500);
    }

    private MediaPlayer ting;
    private void playTing() {
        if (ting.isPlaying()) {
            ting.stop();        try {
                ting.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        ting.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_page);

        btnBack = findViewById(R.id.btnBack);
        tvRepetition = findViewById(R.id.tvRepetition);
        tvExercise = findViewById(R.id.tvExercise);
        btnBack.setOnClickListener(this);
        tvExercise.setText(getIntent().getStringExtra("exercise"));

        // I'm pretty sure only one of those lines is needed, but better safe than sorry
        PermissionHelper.checkAndRequestCameraPermissions(this);
        requestPermissions(new String[]{"android.permission.CAMERA"}, CAMERA_ID);

        vision = VisionMaster.getInstance();
        vision.attachToContext(this);
        vision.attachFrame(findViewById(R.id.preview_display_layout));

        this.reps = 0;
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
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
        ting = MediaPlayer.create(this, R.raw.success);
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            vision.attachCamera(this);

            stopThread = false;
            new Thread(new Runnable() {
                @SuppressLint("DefaultLocale")
                @Override
                public void run() {
                    TextView txt = findViewById(R.id.exerciseQualDisplay);
                    while (!stopThread) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (vision.getCurrentFace() != null) {
                            IMouth mouth = vision.getCurrentFace().getMouth();

                            runOnUiThread(() -> txt.setText(
                                    String.format("reps done: %d",
                                            reps)
                            ));

                            if (VisionMaster.getInstance().didCompleteRep()) {
                                reps += 1;
                                runOnUiThread(() -> showConffetti());
                                playTing();
                            }

                            Log.i(TAG, mouth.toString());
                        }
                    }
                }
            }).start();
        } else {
            PermissionHelper.checkAndRequestCameraPermissions(this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopThread = true;
    }

}