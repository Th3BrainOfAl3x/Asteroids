package dreamfacilities.com.asteroids;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

/**
 * Created by alex on 02/12/16.
 */

public class ScoreSW_PHPStore implements ScoreStore {

    public ScoreSW_PHPStore(Context context) {

    }
    public Vector<String> scoresList(int amount) {

        Vector<String> result = new Vector<String>();
        HttpURLConnection conexion = null;
        try {

            URL url = new URL("http://158.42.146.127/puntuaciones/lista.php" + "?max=20");

            conexion = (HttpURLConnection) url.openConnection();

            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String line = reader.readLine();
                while (!line.equals("")) {
                    result.add(line);
                    line = reader.readLine();
                }
                reader.close();
            } else {
                Log.e("Asteroids", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        } finally {

            if (conexion != null) conexion.disconnect();
            return result;
        }
    }

    public void saveScores(int points, String name, long date) {
        HttpURLConnection conexion = null;
        try {
            URL url = new URL("http://158.42.146.127/puntuaciones/nueva.php?"
                    + "puntos=" + points
                    + "&nombre=" + URLEncoder.encode(name, "UTF-8")
                    + "&fecha=" + date);

            conexion = (HttpURLConnection) url.openConnection();

            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                if (!linea.equals("OK")) {
                    Log.e("Asteroids", "Error Web Service");
                }
            } else {
                Log.e("Asteroids", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        } finally {
            if (conexion != null) conexion.disconnect();
        }
    }
}
