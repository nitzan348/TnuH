package talpiot.mb.magdadmilbat;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.media.MediaPlayer;
import android.graphics.drawable.AnimationDrawable;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MagdadMilbat.R;
import com.google.mediapipe.components.PermissionHelper;
import com.plattysoft.leonids.ParticleSystem;

import java.io.IOException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import talpiot.mb.magdadmilbat.database.HistoryDatabaseManager;
import talpiot.mb.magdadmilbat.database.TrainingData;
import talpiot.mb.magdadmilbat.vision.VisionMaster;
import talpiot.mb.magdadmilbat.vision.detectors.IMouth;

public class ExercisePage extends AppCompatActivity implements View.OnClickListener {

    /**
     * Tag for logging
     */
    private static final String TAG = "EXR";
    private static final int CAMERA_ID = 1;
    private VisionMaster vision;
    private boolean stopThread;

    private Button btnBack;
    private int reps;


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

    private String exerciseName;
    private SharedPreferences sp;
    private HistoryDatabaseManager dbManager;

    private DateTimeFormatter dtf;
    private LocalDateTime now;
    private Instant start, end;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_page);

        start = Instant.now();

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        exerciseName = getIntent().getStringExtra("exercise");
        sp = getSharedPreferences(getIntent().getStringExtra("exercise sp"), 0);
        dbManager = new HistoryDatabaseManager(this);

        // I'm pretty sure only one of those lines is needed, but better safe than sorry
        PermissionHelper.checkAndRequestCameraPermissions(this);
        requestPermissions(new String[]{"android.permission.CAMERA"}, CAMERA_ID);

        vision = VisionMaster.getInstance();
        vision.attachToContext(this);
        vision.attachFrame(findViewById(R.id.preview_display_layout));

        this.reps = 0;

        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        now = LocalDateTime.now();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view == btnBack)
        {
            end = Instant.now();

            dbManager.addTraining(getCurrentTraining());
            Intent intent = new Intent(this, ExrChoiceScreen.class);
            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        end = Instant.now();

        dbManager.addTraining(getCurrentTraining());
        Intent intent = new Intent(this, ExrChoiceScreen.class);
        startActivity(intent);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TrainingData getCurrentTraining()
    {
        long millis = Duration.between(start, end).toMillis();
        String[] datetime = dtf.format(now).split(" ", 2);
        return new TrainingData(datetime[0], datetime[1], exerciseName, convertDurationTime(millis), 1);
        // repetition isn't functional yet so the default is 1
    }

    @SuppressLint("DefaultLocale")
    public String convertDurationTime(long millis)
    {
        long seconds = millis / 1000;
        if (seconds % 60 < 10)
            return String.format("%d:0%d", seconds / 60, seconds % 60);
        return String.format("%d:%d", seconds / 60, seconds % 60);
    }
}