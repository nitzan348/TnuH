package talpiot.mb.magdadmilbat;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.media.MediaPlayer;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import talpiot.mb.magdadmilbat.database.HistoryDatabaseManager;
import talpiot.mb.magdadmilbat.database.TrainingData;
import talpiot.mb.magdadmilbat.vision.VisionMaster;
import talpiot.mb.magdadmilbat.vision.detectors.IMouth;

public class calibRelaxPage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EXR";
    private static final int CAMERA_ID = 1;
    private VisionMaster vision;
    private boolean stopThread;

    private Button btnBack;
    private Button cameraShotCalib;
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
            ting.stop();
            try {
                ting.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        ting.start();
    }

    private String calibName;
    private SharedPreferences sp;
    private HistoryDatabaseManager dbManager;

    private DateTimeFormatter dtf;
    private LocalDateTime now;
    private Instant start, end;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_camera_screen);

        start = Instant.now();

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        //exerciseName = getIntent().getStringExtra("exercise");
        //sp = getSharedPreferences(getIntent().getStringExtra("exercise sp"), 0);
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
        if (view == btnBack) {
            end = Instant.now();

            saveDate();

            Intent intent = new Intent(this, ExrChoiceScreen.class);
            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        end = Instant.now();

        saveDate();

        Intent intent = new Intent(this, ExrChoiceScreen.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveDate() {

        if (reps <= 0) {
            return;
        }

        VisionMaster.Exercise curr = VisionMaster.getInstance().getCurrentExr();
        int diff = Integer.parseInt(getSharedPreferences(curr.name(), 0)
                .getString("diff", "1"));
        int symm = Integer.parseInt(getSharedPreferences(curr.name(), 0)
                .getString("sym_diff", "1"));

        LocalDate dateObj = LocalDate.now();
        TrainingData exerciseObj = new TrainingData(
                dateObj.toString(),
                VisionMaster.getInstance().getCurrentExr().get_name(),
                String.format("%d - %d", diff, symm),
                reps);
        HistoryDatabaseManager dbObj = new HistoryDatabaseManager(this);
        dbObj.addTraining(exerciseObj);

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
                    IMouth[] ar = new IMouth[3];
                    TextView repCounter = findViewById(R.id.exerciseQualDisplay);
                    TextView commandDisplayer = findViewById(R.id.commandToDo);
                    while (!cameraShotCalib.isPressed()) {
                    }
                    ar[0] = vision.getCurrentFace().getMouth();
                    while (!cameraShotCalib.isPressed()) {
                    }
                    ar[1] = vision.getCurrentFace().getMouth();
                    while (!cameraShotCalib.isPressed()) {
                    }
                    ar[2] = vision.getCurrentFace().getMouth();
                    double[] recSyms = VisionMaster.recSyms(ar, calibName);
                    double[] recPows = VisionMaster.recPows(ar, calibName);
                }
                public boolean isPressed() {
                    return true;
                }
            })
            ;}
    }


    @SuppressLint("DefaultLocale")
    public String convertDurationTime(long millis) {
        long seconds = millis / 1000;
        if (seconds % 60 < 10)
            return String.format("%d:0%d", seconds / 60, seconds % 60);
        return String.format("%d:%d", seconds / 60, seconds % 60);
    }
}