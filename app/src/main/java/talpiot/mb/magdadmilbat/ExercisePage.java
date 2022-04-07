package talpiot.mb.magdadmilbat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MagdadMilbat.R;
import com.google.mediapipe.components.PermissionHelper;

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
    SharedPreferences sp;
    private boolean stopThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_page);

        btnBack = (Button) findViewById(R.id.btnBack);
        tvRepetition = (TextView) findViewById(R.id.tvRepetition);
        tvExercise = (TextView) findViewById(R.id.tvExercise);
        btnBack.setOnClickListener(this);
        tvExercise.setText(getIntent().getStringExtra("exercise"));

        sp = getSharedPreferences(getIntent().getStringExtra("exercise sp"), 0);
        tvRepetition.setText(sp.getString("repetition", "1"));

        // I'm pretty sure only one of those lines is needed, but better safe than sorry
        PermissionHelper.checkAndRequestCameraPermissions(this);
        requestPermissions(new String[]{"android.permission.CAMERA"}, CAMERA_ID);

        vision = VisionMaster.getInstance();
        vision.attachToContext(this);
        vision.attachFrame(findViewById(R.id.preview_display_layout));
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack)
        {
            Intent intent = new Intent(this, ExrChoiceScreen.class);
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
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            vision.attachCamera(this);

            stopThread = false;
            new Thread(new Runnable() {
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
                                    String.format(":O %o, :) %o",
                                            (int) (1000 * mouth.getBigMouthScore()),
                                            (int) (1000 * mouth.getSmileScore()))
                            ));

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