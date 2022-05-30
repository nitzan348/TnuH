package talpiot.mb.magdadmilbat.database;

/**
 *
 * @author Elia
 */
public class TrainingData {
    //private Date date;
    private String date;
    private String time; // Training start time
    private String exerciseDescription;
    // private String duration; // duration that the Training take
    private int repetition; // number of times the user repeat exercise

    public TrainingData(String date, String time, String exerciseDescription, String duration, int repetition) {
        this.date = date;
        this.time = time;
        this.exerciseDescription = exerciseDescription;
        // this.duration = duration;
        this.repetition = repetition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExerciseDescription() {
        return exerciseDescription;
    }

    public void setExerciseDescription(String exerciseDescription) {
        this.exerciseDescription = exerciseDescription;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    @Override
    public String toString() {
        return "Training{" +
                "Date='" + date + '\'' +
                ", Time='" + time + '\'' +
                ", Exercise Description: '" + exerciseDescription + '\'' +
                ", duration:" + duration +
                '}';
    }
}
