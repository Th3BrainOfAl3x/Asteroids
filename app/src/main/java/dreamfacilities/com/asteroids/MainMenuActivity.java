package dreamfacilities.com.asteroids;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 0;

    private Button bPlay, bAbout, bScores, bConfig;
    private TextView title;

    private View view;
    private MediaPlayer mp;

    public static ScoreStore scoresStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(). permitNetwork().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        view = findViewById(R.id.main_menu);

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

        bPlay = (Button) findViewById(R.id.bPlay);

        bConfig = (Button) findViewById(R.id.bConfiguration);

        mp = MediaPlayer.create(this, R.raw.audio);
        mp.start();

        setTypeStorage();
    }

    private void setTypeStorage() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("Storage type", pref.getString("storage", "0"));
        switch (pref.getString("storage", "0")) {
            case "0":
                scoresStorage = new ScorePreferencesStore(this);
                break;
            case "1":
                scoresStorage = new ScoreInternalFileStore(this);
                break;

            case "2":
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    scoresStorage = new ScoreExternalFileStore(this);
                } else {
                    requestPermission();
                }
                break;

            case "3":
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    scoresStorage = new ScoreExternalFolderFileStore(this);
                } else {
                    requestPermission();
                }
                break;
            case "4":
                scoresStorage = new ScoreRawStore(this);
                break;
            case "5":
                scoresStorage = new ScoreAssetsStore(this);
                break;
            case "6":
                scoresStorage = new ScoreXMLSAXStore(this);
                break;
            case "7":
                scoresStorage = new ScoreXMLDOM(this);
                break;
            case "8":
                scoresStorage = new ScoreGSONStore(this);
                break;
            case "9":
                scoresStorage = new ScoreSQLiteStore(this);
                break;
            case "10":
                scoresStorage = new ScoreSocketStore(this);
                break;
            case "11":
                scoresStorage = new ScoreSW_PHPStore(this);
                break;
            case "12":
                scoresStorage = new ScoreHostingSW_PHPStore(this);
                break;
        }
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

    public void firePreferences(View view) {
        Intent i = new Intent(this, Preferences.class);
        startActivityForResult(i, 999);
    }

    public void showPreferences(View view) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String s = "music: " + pref.getBoolean("music", true)
                + ", graphics: " + pref.getString("graphics", "?")
                + ", fragments: " + pref.getString("fragments", "0")
                + ", multiplayer activated: " + pref.getBoolean("activate_multipalyer", false)
                + ", conexion_type: " + pref.getString("conexion_type", "?")
                + ", max_players: " + pref.getString("max_players", "0");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void fireScores(View view) {
        Intent i = new Intent(this, Scores.class);
        startActivity(i);
    }

    public void fireGame(View view) {
        Intent i = new Intent(this, Game.class);
        startActivityForResult(i, 666);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 666 && resultCode == RESULT_OK && data != null) {

            int score = data.getExtras().getInt("score");
            String name = "Alex";

            scoresStorage.saveScores(score, name, System.currentTimeMillis());
            fireScores(null);

        } else if (requestCode == 999) {
            setTypeStorage();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(view,
                    "Without the external storage permission I can't save your score",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainMenuActivity.this,
                                    new String[]{
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    },
                                    REQUEST_STORAGE_PERMISSION);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setTypeStorage();
            } else {
                Snackbar.make(view, "Without the external storage permission I can't save your score",
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }
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
        if (mp != null) savedState.putInt("position", mp.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle restoreState) {
        super.onRestoreInstanceState(restoreState);
        if (restoreState != null && mp != null) mp.seekTo(restoreState.getInt("position"));
    }
}
