package talpiot.mb.magdadmilbat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MagdadMilbat.R;

import talpiot.mb.magdadmilbat.vision.VisionMaster;

public class SettingsChoiceScreen extends AppCompatActivity implements View.OnClickListener {
    Button btnBack, btnSmile, btnOpenMouth, btnKiss, btnCheeks, btnCalib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_choice_screen);

        btnBack = (Button)findViewById(R.id.btnBack);
        btnSmile = (Button)findViewById(R.id.btnSmile);
        btnOpenMouth = (Button)findViewById(R.id.btnOpenMouth);
        btnKiss = (Button)findViewById(R.id.btnKiss);
        btnCheeks = (Button)findViewById(R.id.btnCheeks);
        btnCalib = (Button)findViewById(R.id.btnCalib);

        btnBack.setOnClickListener(this);
        btnSmile.setOnClickListener(this);
        btnOpenMouth.setOnClickListener(this);
        btnKiss.setOnClickListener(this);
        btnCheeks.setOnClickListener(this);
        btnCalib.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (view == btnSmile)
        {
            Intent intent = new Intent(this, SettingsScalePage.class);
            intent.putExtra("exercise", "הגדרות חיוך");
            intent.putExtra("exercise sp", VisionMaster.Exercise.SMILE.name());
            startActivity(intent);
        }
        else if (view == btnOpenMouth)
        {
            Intent intent = new Intent(this, SettingsScalePage.class);
            intent.putExtra("exercise", "הגדרות פה גדול");
            intent.putExtra("exercise sp", VisionMaster.Exercise.BIG_MOUTH.name());
            startActivity(intent);
        }
        else if (view == btnKiss)
        {
            Intent intent = new Intent(this, SettingsScalePage.class);
            intent.putExtra("exercise", "הגדרות נשיקה");
            intent.putExtra("exercise sp", VisionMaster.Exercise.KISS.name());
            startActivity(intent);
        }
        else if (view == btnCheeks)
        {
            Intent intent = new Intent(this, SettingsScalePage.class);
            intent.putExtra("exercise", "הגדרות ניפוח לחיים");
            intent.putExtra("exercise sp", "settings cheeks");
            startActivity(intent);
        }
        else if (view == btnCalib)
        {
            Intent intent = new Intent(this, ExrCalibChoiceScreen.class);
            startActivity(intent);
        }
    }
}
