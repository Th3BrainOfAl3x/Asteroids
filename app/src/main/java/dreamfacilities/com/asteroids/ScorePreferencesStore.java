package dreamfacilities.com.asteroids;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Vector;

/**
 * Created by alex on 29/11/16.
 */

public class ScorePreferencesStore implements ScoreStore {
    private static String PREFERENCES = "scores";
    private Context context;

    public ScorePreferencesStore(Context context) {
        this.context = context;
    }

    public void saveScores(int points, String name, long date) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (int n = 9; n >= 1; n--) {
            editor.putString("score" + n, preferences.getString("score" + (n - 1), ""));
        }
        editor.putString("score0", points + " " + name);
        editor.apply();
    }

    public Vector<String> scoresList(int cantidad) {

        Vector<String> result = new Vector<String>();
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        for (int n = 0; n <= 9; n++) {
            String s = preferences.getString("score" + n, "");
            if (!s.isEmpty()) {
                result.add(s);
            }
        }

        return result;
    }
}
