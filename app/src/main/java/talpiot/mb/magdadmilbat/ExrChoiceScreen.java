package talpiot.mb.magdadmilbat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MagdadMilbat.R;

import talpiot.mb.magdadmilbat.vision.VisionMaster;

public class ExrChoiceScreen extends AppCompatActivity implements View.OnClickListener {
    Button btnBack, btnCheeks, btnKiss, btnOpenMouth, btnSmile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_exr_choice_screen);

        btnBack = (Button)findViewById(R.id.btnBack);
        btnSmile = (Button)findViewById(R.id.btnSmile);
        btnOpenMouth = (Button)findViewById(R.id.btnOpenMouth);
        btnKiss = (Button)findViewById(R.id.btnKiss);
        btnCheeks = (Button)findViewById(R.id.btnCheeks);

        btnBack.setOnClickListener(this);
        btnSmile.setOnClickListener(this);
        btnOpenMouth.setOnClickListener(this);
        btnKiss.setOnClickListener(this);
        btnCheeks.setOnClickListener(this);

    }

    public void onClick(View view) {
        if (view == btnBack)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (view == btnSmile)
        {
            Intent intent = new Intent(this, ExercisePage.class);
            intent.putExtra("exercise", "חיוך");
            intent.putExtra("exercise sp", "settings smile");
            VisionMaster.getInstance().setCurrentExr(VisionMaster.Exercise.SMILE);
            startActivity(intent);
        }
        else if (view == btnOpenMouth)
        {
            Intent intent = new Intent(this, ExercisePage.class);
            intent.putExtra("exercise", "פה גדול");
            intent.putExtra("exercise sp", "settings open mouth");
            VisionMaster.getInstance().setCurrentExr(VisionMaster.Exercise.BIG_MOUTH);
            startActivity(intent);
        }
        else if (view == btnKiss)
        {
            Intent intent = new Intent(this, ExercisePage.class);
            intent.putExtra("exercise", "נשיקה");
            intent.putExtra("exercise sp", "settings kiss");
            VisionMaster.getInstance().setCurrentExr(VisionMaster.Exercise.KISS);
            startActivity(intent);
        }
        else if (view == btnCheeks)
        {
            Intent intent = new Intent(this, ExercisePage.class);
            intent.putExtra("exercise", "ניפוח לחיים");
            intent.putExtra("exercise sp", "settings cheeks");
            startActivity(intent);
        }
    }
}
