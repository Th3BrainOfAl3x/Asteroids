package dreamfacilities.com.asteroids;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Vector;

/**
 * Created by alex on 01/12/16.
 */

public class ScoreSQLiteStore extends SQLiteOpenHelper implements ScoreStore {

    public ScoreSQLiteStore(Context context) {
        super(context, "scores", null, 1);
    }


    //SQLiteOpenHelper
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE scores (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " points INTEGER," +
                    " name TEXT," +
                    " date LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveScores(int points, String name, long date) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO scores VALUES ( null, " + points + ", '" + name + "', " + date + ")");
        db.close();
    }

    public Vector<String> scoresList(int amount) {
        Vector<String> result = new Vector<String>();
        SQLiteDatabase db = getReadableDatabase();

        String[] FIELDS = {"points", "name"};
        Cursor cursor=db.query("scores", //
                                FIELDS,
                                null,
                                null,
                                null,
                                null,
                                "points DESC",
                                Integer.toString(amount));

        while (cursor.moveToNext()) {
            result.add(cursor.getInt(0) + " " + cursor.getString(1));
        }
        cursor.close();
        db.close();
        return result;
    }
}
