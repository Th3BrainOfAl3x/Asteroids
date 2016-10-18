package dreamfacilities.com.asteroids;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

    private Button bAbout, bScores;

    public static ScoreStore store = new ArrayScoreStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        bAbout = (Button) findViewById(R.id.bAbout);
        bAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireAbout(null);
            }
        });

        bScores = (Button) findViewById(R.id.bScores);
        bScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireScores(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            firePreferences(null);
            return true;
        }

        if (id == R.id.about) {
            fireAbout(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void fireAbout(View view) {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    public void firePreferences(View view){
        Intent i = new Intent(this, Preferences.class);
        startActivity(i);
    }

    public void showPreferences(View view){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String s = "music: " + pref.getBoolean("music",true)
                 + ", graphics: " + pref.getString("graphics","?")
                 + ", fragments: " + pref.getString("fragments","0")
                 + ", multiplayer activated: " + pref.getBoolean("activate_multipalyer",false)
                 + ", conexion_type: " + pref.getString("conexion_type","?")
                 + ", max_players: " + pref.getString("max_players", "0");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void fireScores(View view) {
        Intent i = new Intent(this, Scores.class);
        startActivity(i);
    }
}
