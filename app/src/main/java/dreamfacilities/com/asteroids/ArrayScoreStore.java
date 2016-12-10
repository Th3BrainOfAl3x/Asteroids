package dreamfacilities.com.asteroids;

import java.util.Vector;

/**
 * Created by alex on 18/10/16.
 */

public class ArrayScoreStore implements ScoreStore {
    private Vector<String> scores;

    public ArrayScoreStore() {

        scores = new Vector<String>();

        scores.add("123000 Pepito Domingez");
        scores.add("111000 Pedro Martinez");
        scores.add("011000 Paco Pérez");
    }

    @Override
    public void saveScores(int points, String name, long date) {
        scores.add(0, points + " " + name);
    }

    @Override
    public Vector<String> scoresList(int amount) { return scores; }
}
