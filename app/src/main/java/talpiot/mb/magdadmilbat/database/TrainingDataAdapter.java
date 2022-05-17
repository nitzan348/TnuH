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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elia
 */
public class TrainingDataAdapter extends ArrayAdapter<TrainingData> {
    private Context context;
    private List<TrainingData> data;

    public TrainingDataAdapter(Context context, ArrayList<TrainingData> details) {
        super(context,
                0,
                0,
                details);
        this.context = context;
        this.data = details;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.activity_exercise_details, parent, false);

        TextView tvExercise = (TextView) view.findViewById(R.id.tvExercise);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvDuration = (TextView) view.findViewById(R.id.tvDuration);
        TextView tvRepetition = (TextView) view.findViewById(R.id.tvRepetition);

        TrainingData temp = data.get(position);
        tvExercise.setText(temp.getExerciseDescription());
        tvDate.setText(temp.getDate());
        tvTime.setText(temp.getTime());
        tvDuration.setText(temp.getDuration());
        tvRepetition.setText(temp.getRepetition()); // this isn't actually functional because the DB needs to be restarted

        return view;
    }
}