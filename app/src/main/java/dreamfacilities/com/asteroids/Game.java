package dreamfacilities.com.asteroids;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by alex on 18/10/16.
 */

public class Game extends Activity {
    private GameView gameView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        gameView = (GameView) findViewById(R.id.GameView);
        gameView.setParent(this);

    }

    @Override
    protected void onPause() {
        gameView.getThread().pause();
        gameView.activateSensors();
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        gameView.activateSensors();
        gameView.getThread().resumeGame();
    }
    @Override
    protected void onDestroy() {
        gameView.deactivateSensors();
        gameView.getThread().stopGame();
        super.onDestroy();
    }
}
