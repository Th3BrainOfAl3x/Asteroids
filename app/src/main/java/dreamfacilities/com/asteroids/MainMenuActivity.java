package dreamfacilities.com.asteroids;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    private Button bAbout, bExit;

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

        bExit = (Button) findViewById(R.id.bExit);
        bExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit(null);
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

    public void exit(View view) {
        finish();
    }
}
