package talpiot.mb.magdadmilbat.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 *
 * @author Elia
 */
public class HistoryDatabaseManager extends SQLiteOpenHelper {

    public HistoryDatabaseManager(Context context) {
        super(context, "Training", null, 1); // 1 = version.
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTrainingTable(sqLiteDatabase);
    }

    //The function updates the table
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table Training"); //Deletes the table
        onCreate(sqLiteDatabase); // call to "onCreate" function,that will recreate the table
    }

    private void createTrainingTable(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table Training (id integer primary key autoincrement, exerciseDescription text not null," +
                " date text not null, time text not null, duration text not null, repetition integer not null)";
        sqLiteDatabase.execSQL(sql);
    }

    /**
     * This function adds the value of the training it receives to the training table
     **/
    public void addTraining(TrainingData training) {
        String sql = "insert into Training(exerciseDescription, date, time, duration, repetition) values ('" +
                training.getExerciseDescription() + "',  '" + training.getDate() + "', '" + training.getTime()
                + "', '" + training.getDifficulty() + "', " + training.getRepetition() + ")";
        SQLiteDatabase sqLiteDatabase = getWritableDatabase(); // Open connection.
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close(); // Close connection.
    }

    /**
     * This method gets a cursor that holds a record (row) from the training table
     * and creates for us a training object according to that record.
     **/
    private static TrainingData getTraining(Cursor cursor) {
        int dateI = cursor.getColumnIndex("date");
        int durationI = cursor.getColumnIndex("duration");
        int exerciseDescriptionI = cursor.getColumnIndex("exerciseDescription");
        int repetition = cursor.getColumnIndex("repetition");
        String duration = cursor.getString(durationI);
        String exerciseDescription = cursor.getString(exerciseDescriptionI);
        String date = cursor.getString(dateI);
        int rep_real = cursor.getInt(repetition);

        return new TrainingData(date, exerciseDescription, duration, rep_real);

    }

    /**
     * This function receives from the table all the training performed,
     * and adds it to the array of training that we return at the end.
     **/
    public ArrayList<TrainingData> getAllTraining() {
        ArrayList<TrainingData> trainingArrayList = new ArrayList<>(); //array of training

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Training", null);

        while (cursor.moveToNext()) { //Goes through all the records and adds them to the array
            trainingArrayList.add(getTraining(cursor));
        }
        cursor.close();
        sqLiteDatabase.close(); // Close connection.
        return trainingArrayList;
    }


}
