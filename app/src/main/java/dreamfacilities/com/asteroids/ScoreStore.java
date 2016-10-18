package dreamfacilities.com.asteroids;

import java.util.Vector;

/**
 * Created by alex on 18/10/16.
 */

public interface ScoreStore {
    public void saveScores(int puntos,String nombre,long fecha);
    public Vector<String> scoresList(int cantidad);
}