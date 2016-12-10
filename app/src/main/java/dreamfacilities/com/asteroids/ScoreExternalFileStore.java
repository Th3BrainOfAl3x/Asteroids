package dreamfacilities.com.asteroids;

/**
 * Created by alex on 30/11/16.
 */

import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class ScoreExternalFileStore implements ScoreStore {
    private static String FILE = Environment.getExternalStorageDirectory() + "/scores.txt";
    private Context context;

    public ScoreExternalFileStore(Context context) {
        this.context = context;
    }

    public void saveScores(int points, String name, long date) {
        String stateSD = Environment.getExternalStorageState();
        if (!stateSD.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "SD Card not available",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            FileOutputStream f = new FileOutputStream(FILE, true);
            String txt = points + " " + name + "\n";
            f.write(txt.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        }
    }

    public Vector<String> scoresList(int amount) {
        Vector<String> result = new Vector<String>();
        String stateSD = Environment.getExternalStorageState();
        if (!stateSD.equals(Environment.MEDIA_MOUNTED) && !stateSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(context, "SD Card not readable",Toast.LENGTH_LONG).show();
            return result;
        }
        try {
            FileInputStream f = new FileInputStream(FILE);
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

