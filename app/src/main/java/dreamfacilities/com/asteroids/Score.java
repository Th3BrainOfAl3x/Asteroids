package dreamfacilities.com.asteroids;

/**
 * Created by alex on 01/12/16.
 */

public class Score {

    private int points;
    private String name;
    private long date;

    public Score(int points, String name, long date) {

        this.points = points;
        this.name = name;
        this.date = date;

    }


    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() { return name;  }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
