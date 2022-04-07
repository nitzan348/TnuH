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
    private String duration; // duration that the Training take

    public TrainingData(String date, String time, String exerciseDescription, String duration) {
        this.date = date;
        this.time = time;
        this.exerciseDescription = exerciseDescription;
        this.duration = duration;
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
