package talpiot.mb.magdadmilbat.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.MagdadMilbat.R;
import java.util.List;

public class TrainingDataAdapter extends ArrayAdapter<TrainingData> {
    private Context context;
    private List<TrainingData> data;
    private DatabaseManager manager;

    public TrainingDataAdapter(Context context) {
        super(context,
                0,
                0,
                new DatabaseManager(context).getAllTraining());
        this.context = context;
        this.manager = new DatabaseManager(context);
        this.data = manager.getAllTraining();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.activity_exercise_details, parent, false);

        TextView tvExercise = (TextView) view.findViewById(R.id.tvExercise);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);

        TrainingData temp = data.get(position);
        tvExercise.setText(temp.toString());
        tvDate.setText(temp.getDate());
        tvTime.setText(temp.getTime());

        return view;
    }
}