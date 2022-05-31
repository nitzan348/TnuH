package talpiot.mb.magdadmilbat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MagdadMilbat.R;

public class SettingsPage extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    Button btnBack, btnSave;
    SeekBar sbDiff, sbSymmDiff;
    TextView tvExercise, tvLevelNumber, tvRepetitionNumber;
    EditText etDuration;
    SharedPreferences sp;
    int level, repetition;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvExercise = (TextView) findViewById(R.id.tvExercise);
        tvExercise.setText(getIntent().getStringExtra("exercise"));

        btnBack = (Button)findViewById(R.id.btnBack);
        btnSave = (Button)findViewById(R.id.btnSave);

        sbDiff = (SeekBar)findViewById(R.id.sbLevel);
        sbSymmDiff = (SeekBar)findViewById(R.id.sbSymmDiff);
        tvLevelNumber = (TextView)findViewById(R.id.tvLevelNumber);
        tvRepetitionNumber = (TextView)findViewById(R.id.tvRepetitionNumber);
        etDuration = (EditText)findViewById(R.id.etDuration);

        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        sbDiff.setOnSeekBarChangeListener(this);
        sbSymmDiff.setOnSeekBarChangeListener(this);
        sbDiff.setMin(1);
        sbSymmDiff.setMin(1);
        sbDiff.setMax(100);
        sbSymmDiff.setMax(100);

        sp = getSharedPreferences(getIntent().getStringExtra("exercise sp"), 0);
        loadSP(sp);
    }

    @Override
    public void onClick(View view)
    {
        if (view == btnBack)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (view == btnSave)
        {            saveSP(sp);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == sbDiff)
        {
            tvLevelNumber.setText(String.valueOf(i));
            level = i;
        }
        else if (seekBar == sbSymmDiff)
        {
            tvRepetitionNumber.setText(String.valueOf(i));
            repetition = i;
        }
    }

    public void loadSP(SharedPreferences sp)
    {
        sbDiff.setProgress(Integer.parseInt(sp.getString("diff", "1")));
        sbSymmDiff.setProgress(Integer.parseInt(sp.getString("sym_diff", "1")));
        etDuration.setText(sp.getString("duration", null));
    }

    public void saveSP(SharedPreferences sp)
    {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("diff", String.valueOf(level));
        editor.putString("sym_diff", String.valueOf(repetition));
        editor.putString("duration", etDuration.getText().toString());
        editor.apply();
        Toast.makeText(this, "ההגדרות נשמרו", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}