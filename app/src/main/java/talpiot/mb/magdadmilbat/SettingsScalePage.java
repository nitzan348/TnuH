// comment
package talpiot.mb.magdadmilbat;

import android.content.Context;
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

public class SettingsScalePage extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    Button btnBack, btnSave;
    SeekBar sbDiffLevel, sbSymmLevel;
    TextView tvExercise;
    SharedPreferences sp, spGet;
    int level, repetition;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_levels_settings);

        tvExercise = (TextView) findViewById(R.id.tvExercise);
        tvExercise.setText(getIntent().getStringExtra("exercise"));

        btnBack = (Button)findViewById(R.id.btnBack);
        btnSave = (Button)findViewById(R.id.btnSave);

        sbDiffLevel = (SeekBar)findViewById(R.id.sbDiffLevel);
        sbSymmLevel = (SeekBar)findViewById(R.id.sbSymmLevel);

        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        sbDiffLevel.setOnSeekBarChangeListener(this);
        sbSymmLevel.setOnSeekBarChangeListener(this);
        sbDiffLevel.setMin(0);
        sbSymmLevel.setMin(0);
        sbDiffLevel.setMax(2);
        sbSymmLevel.setMax(2);

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
        {
            saveSP(sp);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override

    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == sbDiffLevel)
        {
            level = i;
        }
        else if (seekBar == sbSymmLevel)
        {
            repetition = i;
        }
    }

    public void loadSP(SharedPreferences sp)
    {
        sbDiffLevel.setProgress(Integer.parseInt(sp.getString("diff", "0")));
        sbSymmLevel.setProgress(Integer.parseInt(sp.getString("sym_diff", "0")));
    }

    public void saveSP(SharedPreferences sp)
    {
        //spGet = getSharedPreferences(getIntent().getStringExtra("exercise sp"), 0);
        /**
        double [] diff_arr = new double[4];
        double [] symm_arr = new double[4];
        SharedPreferences spGet = getSharedPreferences("calibInfo", MODE_PRIVATE);
        //spGet = this.getSharedPreferences("calibInfo", Context.MODE_PRIVATE);
        for(int i=0; i<4; i++){
            diff_arr[i] = (shared.getdouble("calibInfo","diff" + i));
            diff_arr[i] = (shared.getdouble("calibInfo","sym_diff" + i));
        }
        String channel = (shared.getString(keyChannel, ""));
        */
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("diff", String.valueOf(level));
        editor.putString("sym_diff", String.valueOf(repetition));
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


