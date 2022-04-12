package talpiot.mb.magdadmilbat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MagdadMilbat.R;

import java.util.ArrayList;


import talpiot.mb.magdadmilbat.database.HistoryDatabaseManager;
import talpiot.mb.magdadmilbat.database.TrainingData;
import talpiot.mb.magdadmilbat.database.TrainingDataAdapter;

public class HistoryPage extends AppCompatActivity implements View.OnClickListener {
    Button btnBack;
    ListView lvHistory;

    TrainingDataAdapter trainingDataAdapter;
    HistoryDatabaseManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);

        historyManager = new HistoryDatabaseManager(this);
        btnBack = (Button)findViewById(R.id.btnBack);

        trainingDataAdapter = new TrainingDataAdapter(this, historyManager.getAllTraining());
        lvHistory = (ListView)findViewById(R.id.lvHistory);

        lvHistory.setAdapter(trainingDataAdapter);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}