package dreamfacilities.com.asteroids;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by alex on 18/10/16.
 */
public class Preferences extends PreferenceActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences); }
}
