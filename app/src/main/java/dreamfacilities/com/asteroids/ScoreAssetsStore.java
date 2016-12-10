package dreamfacilities.com.asteroids;

/**
 * Created by alex on 30/11/16.
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class ScoreAssetsStore implements ScoreStore {
    private Context context;

    public ScoreAssetsStore(Context context) {
        this.context = context;
    }

    public void saveScores(int puntos, String name, long date) {

    }

    public Vector<String> scoresList(int amount) {
        Vector<String> result = new Vector<String>();

        try {
            InputStream f = context.getAssets().open("folder/scores.txt");
            BufferedReader input = new BufferedReader(new InputStreamReader(f));
            int n = 0;
            String line;
            do {
                line = input.readLine();
                if (line != null) {
                    result.add(line);
                    n++;
                }
            } while (n < amount && line != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        }
        return result;
    }
}