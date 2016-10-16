package dreamfacilities.com.asteroids;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    private Button bAbout;

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
    }

    public void fireAbout(View view) {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }
}
