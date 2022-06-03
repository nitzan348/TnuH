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
    private int res;

    public TrainingDataAdapter(Context context, int res, ArrayList<TrainingData> details) {
        super(context,
                res,
                details);
        this.context = context;
        this.data = details;
        this.res = res;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(res, parent, false);

        TextView tvExercise = (TextView) convertView.findViewById(R.id.exrType);
        TextView tvDate = (TextView) convertView.findViewById(R.id.date);
        TextView tvDifficulty = (TextView) convertView.findViewById(R.id.diff);
        TextView tvRepetition = (TextView) convertView.findViewById(R.id.reps);

        TrainingData temp = getItem(position);
        tvExercise.setText(temp.getExerciseDescription());
        tvDate.setText(temp.getDate());
        tvDifficulty.setText(temp.getDifficulty());
        tvRepetition.setText(String.format("%d", temp.getRepetition())); // this isn't actually functional because the DB needs to be restarted

        return convertView;
    }
}