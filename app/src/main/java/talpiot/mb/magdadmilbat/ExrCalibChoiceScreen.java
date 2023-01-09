package talpiot.mb.magdadmilbat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MagdadMilbat.R;

public class ExrCalibChoiceScreen extends AppCompatActivity implements View.OnClickListener {
    Button btnBack, btnCheeksCalibration, btnTongueCalibration, btnKissCalibration, btnOpenMouthCalibration, btnSmileCalibration, btnRelaxationCalibration;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_calibration_choice_page);

        btnBack = (Button)findViewById(R.id.btnBack);
        btnSmileCalibration = (Button)findViewById(R.id.btnSmileCalibration);
        btnOpenMouthCalibration = (Button)findViewById(R.id.btnOpenMouthCalibration);
        btnKissCalibration = (Button)findViewById(R.id.btnKissCalibration);
        btnCheeksCalibration = (Button)findViewById(R.id.btnCheeksCalibration);
        btnTongueCalibration = (Button)findViewById(R.id.btnTongueCalibration);
        btnRelaxationCalibration = (Button)findViewById(R.id.btnRelaxationCalibration);

        btnBack.setOnClickListener(this);
        btnSmileCalibration.setOnClickListener(this);
        btnOpenMouthCalibration.setOnClickListener(this);
        btnKissCalibration.setOnClickListener(this);
        btnCheeksCalibration.setOnClickListener(this);
        btnTongueCalibration.setOnClickListener(this);
        btnRelaxationCalibration.setOnClickListener(this);

    }

    public void onClick(View view) {
        if (view == btnBack)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (view == btnSmileCalibration)
        {
            Intent intent = new Intent(this, calibPage.class);
            //intent.putExtra("exercise", "כיול חיוך");
            //intent.putExtra("exercise sp", "smile calibration");
            // VisionMaster.getInstance().setCurrentExr(VisionMaster.Calib.SMILE);
            startActivity(intent);
        }
        else if (view == btnOpenMouthCalibration)
        {
            Intent intent = new Intent(this, calibPage.class);
            //intent.putExtra("exercise", "כיול פה גדול");
            //intent.putExtra("exercise sp", "open mouth calibration");
            // VisionMaster.getInstance().setCurrentExr(VisionMaster.Calib.OPENMOUTH);
            startActivity(intent);
        }
        else if (view == btnKissCalibration)
        {
            Intent intent = new Intent(this, calibPage.class);
            //intent.putExtra("exercise", "כיול נשיקה");
            //intent.putExtra("exercise sp", "kiss calibration");
            //   VisionMaster.getInstance().setCurrentExr(VisionMaster.Calib.KISS);
            startActivity(intent);
        }
        else if (view == btnCheeksCalibration)
        {
            Intent intent = new Intent(this, calibPage.class);
            //intent.putExtra("exercise", "כיול ניפוח לחיים");
            //intent.putExtra("exercise sp", "cheeks calibration");
            //   VisionMaster.getInstance().setCurrentExr(VisionMaster.CHEEKS);
            startActivity(intent);
        }
        else if (view == btnTongueCalibration)
        {
            Intent intent = new Intent(this, calibPage.class);
            //intent.putExtra("exercise", "כיול הוצאת לשון");
            //intent.putExtra("exercise sp", "tongue calibration");
            //   VisionMaster.getInstance().setCurrentExr(VisionMaster.CHEEKS);
            startActivity(intent);
        }
        else if (view == btnRelaxationCalibration)
        {
            Intent intent = new Intent(this, calibRelaxPage.class);
            //intent.putExtra("exercise", "כיול הרפיה");
            //intent.putExtra("exercise sp", "relaxation calibration");
            //   VisionMaster.getInstance().setCurrentExr(VisionMaster.CHEEKS);
            startActivity(intent);
        }
    }
}
