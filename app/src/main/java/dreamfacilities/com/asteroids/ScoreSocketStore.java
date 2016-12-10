package dreamfacilities.com.asteroids;

import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by alex on 02/12/16.
 */

public class ScoreSocketStore implements ScoreStore {

    public ScoreSocketStore(Context context) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    public void saveScores(int points, String name, long date) {
        try {
            Socket sk = new Socket("158.42.146.127", 1234);

            BufferedReader input = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(sk.getOutputStream()), true);

            output.println(points + " " + name);
            String answer = input.readLine();
            if (!answer.equals("OK")) {
                Log.e("Asteroids", "Error: server answer incorrect");
            }
            sk.close();
        } catch (Exception e) {
            Log.e("Asteroids", e.toString(), e);
        }
    }

    public Vector<String> scoresList(int amount) {
        Vector<String> result = new Vector<String>();
        try {
            Socket sk = new Socket("158.42.146.127", 1234);
            BufferedReader input = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(sk.getOutputStream()), true);
            output.println("PUNTUACIONES");
            int n = 0;
            String answer;
            do {
                answer = input.readLine();
                if (answer != null) {
                    result.add(answer);
                    n++;
                }
            } while (n < amount && answer != null);
            sk.close();
        } catch (Exception e) {
            Log.e("Asteroids", e.toString(), e);
        }

        return result;
    }
}
