package dreamfacilities.com.asteroids;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

    private Button bPlay, bAbout, bScores, bConfig;
    private TextView title;

    private MediaPlayer mp;

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

        //Animation animationTitle = AnimationUtils.loadAnimation(this, R.anim.turn_with_zoom);
        //bAbout.startAnimation(animationTitle);

        bPlay = (Button) findViewById(R.id.bPlay);
        //Animation animationBPlay = AnimationUtils.loadAnimation(this, R.anim.appear);
        //bPlay.startAnimation(animationBPlay);

        bConfig = (Button) findViewById(R.id.bConfiguration);
        //Animation animationBConfig = AnimationUtils.loadAnimation(this, R.anim.move_right);
        //bConfig.startAnimation(animationBConfig);

        mp = MediaPlayer.create(this, R.raw.audio);
        mp.start();
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

    public void fireGame(View view){
        Intent i = new Intent(this, Game.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mp.pause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mp.start();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mp.stop();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        mp.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        if(mp != null) savedState.putInt("position", mp.getCurrentPosition());
    }
    @Override
    protected void onRestoreInstanceState(Bundle restoreState) {
        super.onRestoreInstanceState(restoreState);
        if(restoreState != null && mp != null) mp.seekTo(restoreState.getInt("position"));
    }
}
