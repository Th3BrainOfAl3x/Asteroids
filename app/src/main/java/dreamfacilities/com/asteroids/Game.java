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
    }

    @Override
    protected void onPause() {
        gameView.getThread().pausar();
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        gameView.getThread().reanudar();
    }
    @Override
    protected void onDestroy() {
        gameView.getThread().detener();
        super.onDestroy();
    }
}
