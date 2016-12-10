package dreamfacilities.com.asteroids;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by alex on 18/10/16.
 */
public class Preferences extends PreferenceActivity {

    private Activity parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
