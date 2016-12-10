package dreamfacilities.com.asteroids;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by alex on 01/12/16.
 */

public class ScoreGSONStore implements ScoreStore {
    private String string;
    private Gson gson = new Gson();
    private Type type = new TypeToken<Clase>() {}.getType();

    public class Clase {
        public ArrayList<Score> scores = new ArrayList<>();
        public boolean saved;
    }

    public ScoreGSONStore(Context context) {
        saveScores(45000, "Alex Catalan", System.currentTimeMillis());
        saveScores(31000, "Otro alex", System.currentTimeMillis());
    }

    @Override
    public void saveScores(int points, String name, long date) {

        //string = readString();
        Clase obj;
        if (string == null) {
            obj = new Clase();
        } else {
            obj = gson.fromJson(string, type);
        }

        obj.scores.add(new Score(points, name, date));
        string = gson.toJson(obj, type);
        //saveString(string);
    }

    @Override
    public Vector<String> scoresList(int  amount) {
        //string = leerString();
        Clase obj;
        if (string == null) {
            obj = new Clase();
        } else {
            obj = gson.fromJson(string, type);
        }

        Vector<String> output = new Vector<>();

        for (Score score : obj.scores) {
            output.add(score.getPoints()+" "+score.getName());
        }
        return output;
    }


}
