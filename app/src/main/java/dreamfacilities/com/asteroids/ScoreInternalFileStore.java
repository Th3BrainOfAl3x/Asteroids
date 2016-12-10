package dreamfacilities.com.asteroids;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Created by alex on 29/11/16.
 */

public class ScoreInternalFileStore implements ScoreStore {
    private static String FILE = "scores.txt";
    private Context context;

    public ScoreInternalFileStore(Context context) {
        this.context = context;
    }

    public void saveScores(int puntos, String nombre, long fecha) {
        try {
            FileOutputStream f = context.openFileOutput(FILE, Context.MODE_APPEND);
            String texto = puntos + " " + nombre + "\n";
            f.write(texto.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        }
    }

    public Vector<String> scoresList(int cantidad) {
        Vector<String> result = new Vector<String>();
        try {
            FileInputStream f = context.openFileInput(FILE);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(f));
            int n = 0;
            String linea;
            do {
                linea = entrada.readLine();
                if (linea != null) {
                    result.add(linea);
                    n++;
                }
            } while (n < cantidad && linea != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        }
        return result;
    }
}
