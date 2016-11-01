package dreamfacilities.com.asteroids;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import java.util.List;
import java.util.Vector;

/**
 * Created by alex on 18/10/16.
 */

public class GameView extends View implements SensorEventListener {
    // //// ASTEROIDS //////
    private Vector<Graphic> asteroids;// Vector con los Asteroides
    private int numAsteroids = 5; // Número inicial de asteroids
    private int numFragments = 3; // Fragmentos en que se divide

    // //// STARSHIP //////
    private Graphic starship;
    private int rotationStarship;
    private double accelerationSpaceship;
    private static final int MAX_SPEED_STARSHIP = 20;
    private static final int STEP_ROTATION_STARSHIP = 5;
    private static final float STEP_ACCELERATION_STARSHIP = 0.5f;

    // //// THREAD Y TIMES //////
    // Thread to proccess main game
    private GameThread thread = new GameThread();
    // How long we want to listen for changes (ms)
    private static int PROCESS_PERIOD = 50;
    // When was create last process
    private long lastProcess = 0;

    // /// BULLET ////
    private Graphic bullet;
    private static int BULLET_SPEED_STEP = 12;
    private boolean activeBullet = false;
    private int bulletTimer;

    private SharedPreferences pref;

    //MEDIA
    private SoundPool soundPool;

    int idShoot, idExplosion;

    public GameView(Context context, AttributeSet attrs) {

        super(context, attrs);
        Drawable drawableStarship, drawableAsteroid, drawableBullet;

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (pref.getString("graphics", "1").equals("0")) {

            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            Path pathAsteroid = new Path();
            pathAsteroid.moveTo((float) 0.3, (float) 0.0);
            pathAsteroid.lineTo((float) 0.6, (float) 0.0);
            pathAsteroid.lineTo((float) 0.6, (float) 0.3);
            pathAsteroid.lineTo((float) 0.8, (float) 0.2);
            pathAsteroid.lineTo((float) 1.0, (float) 0.4);
            pathAsteroid.lineTo((float) 0.8, (float) 0.6);
            pathAsteroid.lineTo((float) 0.9, (float) 0.9);
            pathAsteroid.lineTo((float) 0.8, (float) 1.0);
            pathAsteroid.lineTo((float) 0.4, (float) 1.0);
            pathAsteroid.lineTo((float) 0.0, (float) 0.6);
            pathAsteroid.lineTo((float) 0.0, (float) 0.2);
            pathAsteroid.lineTo((float) 0.3, (float) 0.0);

            ShapeDrawable dAsteroid = new ShapeDrawable(new PathShape(pathAsteroid, 1, 1));
            dAsteroid.getPaint().setColor(Color.WHITE);
            dAsteroid.getPaint().setStyle(Paint.Style.STROKE);
            dAsteroid.setIntrinsicWidth(50);
            dAsteroid.setIntrinsicHeight(50);

            drawableAsteroid = dAsteroid;

            Path pathStarship = new Path();
            pathStarship.lineTo((float) 1, (float) 0.5);
            pathStarship.lineTo((float) 0, (float) 1);
            pathStarship.lineTo((float) 0, (float) 0);

            ShapeDrawable dStarship = new ShapeDrawable(new PathShape(pathStarship, 1, 1));
            dStarship.getPaint().setColor(Color.WHITE);
            dStarship.getPaint().setStyle(Paint.Style.STROKE);
            dStarship.setIntrinsicWidth(20);
            dStarship.setIntrinsicHeight(15);

            drawableStarship = dStarship;

            ShapeDrawable dBullet = new ShapeDrawable(new RectShape());
            dBullet.getPaint().setColor(Color.WHITE);
            dBullet.getPaint().setStyle(Paint.Style.STROKE);
            dBullet.setIntrinsicWidth(15);
            dBullet.setIntrinsicHeight(3);
            drawableBullet = dBullet;

            setBackgroundColor(Color.BLACK);

        } else {

            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            drawableAsteroid = context.getResources().getDrawable(R.drawable.asteroide1);
            drawableStarship = context.getResources().getDrawable(R.drawable.nave);
            drawableBullet = context.getResources().getDrawable(R.drawable.misil1);

        }

        starship = new Graphic(this, drawableStarship);
        starship.setIncX(0);
        starship.setIncY(0);

        bullet = new Graphic(this, drawableBullet);
        bullet.setIncX(0);
        bullet.setIncY(0);

        asteroids = new Vector<Graphic>();
        for (int i = 0; i < numAsteroids; i++) {
            Graphic asteroid = new Graphic(this, drawableAsteroid);
            asteroid.setIncY(Math.random() * 4 - 2);
            asteroid.setIncX(Math.random() * 4 - 2);
            asteroid.setAngle((int) (Math.random() * 360));
            asteroid.setRotation((int) (Math.random() * 8 - 4));
            asteroids.add(asteroid);
        }

        //Register the sensor and listener to our view
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (!listSensors.isEmpty()) {
            Sensor orientationSensor = listSensors.get(0);
            mSensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        idShoot = soundPool.load(context, R.raw.shoot, 0);
        idExplosion = soundPool.load(context, R.raw.explosion, 0);
    }

    protected void updatePhysics() {
        long now = System.currentTimeMillis();
        if (lastProcess + PROCESS_PERIOD > now) {
            return; // Exit if the period of the process is not ended.
        }
        // To execute in real time calculate delay
        double delay = (now - lastProcess) / PROCESS_PERIOD;
        lastProcess = now; // Save for next time

        // Update speed and direction off the starship from
        // rotationStarship y accelerationSpaceship (according to player input)

        starship.setAngle((int) (starship.getAngle() + rotationStarship * delay));
        double nIncX = starship.getIncX() + accelerationSpaceship *
                Math.cos(Math.toRadians(starship.getAngle())) * delay;
        double nIncY = starship.getIncY() + accelerationSpaceship *
                Math.sin(Math.toRadians(starship.getAngle())) * delay;

        // Update if the module of the speed not exceed max
        if (Math.hypot(nIncX, nIncY) <= MAX_SPEED_STARSHIP) {
            starship.setIncX(nIncX);
            starship.setIncY(nIncY);
        }
        starship.incrementPos(delay); // Update poition

        for (Graphic asteroid : asteroids) {
            asteroid.incrementPos(delay);
        }

        if (activeBullet) {
            bullet.incrementPos(delay);
            bulletTimer -= delay;
            if (bulletTimer < 0) {
                activeBullet = false;
            } else {
                for (int i = 0; i < asteroids.size(); i++) {
                    if (bullet.verifyCollision(asteroids.elementAt(i))) {
                        destroyAsteroid(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int width_before, int height_before) {
        super.onSizeChanged(width, height, width_before, height_before);

        starship.setCenX(width / 2);
        starship.setCenY(height / 2);

        for (Graphic asteroid : asteroids) {

            do {

                asteroid.setCenX((int) (Math.random() * width));
                asteroid.setCenY((int) (Math.random() * height));

            } while (asteroid.distance(starship) < (width + height) / 5);
        }

        lastProcess = System.currentTimeMillis();
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (asteroids) {
            for (Graphic asteroid : asteroids) {
                asteroid.drawGraphic(canvas);
            }
        }
        starship.drawGraphic(canvas);

        if(activeBullet) bullet.drawGraphic(canvas);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        boolean processed = true;
        if (pref.getBoolean("keyboard", false)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    accelerationSpaceship = +STEP_ACCELERATION_STARSHIP;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    rotationStarship = -STEP_ROTATION_STARSHIP;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    rotationStarship = +STEP_ROTATION_STARSHIP;
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    activateBullet();
                    break;
                default:
                    processed = false;
                    break;
            }
        }
        return processed;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);

        boolean processed = true;

        if (pref.getBoolean("keyboard", false)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    accelerationSpaceship = 0;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    rotationStarship = 0;
                    break;
                default:
                    processed = false;
                    break;
            }
        }

        return processed;
    }

    private float mX = 0, mY = 0;
    private boolean shoot = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (pref.getBoolean("touch", false)) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    shoot = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);
                    if (dy < 6 && dx > 6) {
                        rotationStarship = Math.round((x - mX) / 2);
                        shoot = false;
                    } else if (dy > 6) {
                        accelerationSpaceship = Math.round((mY - y) / 25);
                        shoot = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    rotationStarship = 0;
                    accelerationSpaceship = 0;
                    if (shoot) {
                        activateBullet();
                    }
                    break;
            }
            mX = x;
            mY = y;
        }
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private boolean existInitialValue = false;
    private float initialValue;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (pref.getBoolean("sensor_accelerometer", false)) {
            float value = event.values[1];
            if (!existInitialValue) {
                initialValue = value;
                existInitialValue = true;
            }
            rotationStarship = (int) (value - initialValue) / 3;
        }
    }

    private void destroyAsteroid(int i) {
        synchronized (asteroids) {
            asteroids.removeElementAt(i);
            activeBullet = false;
            soundPool.play(idExplosion, 1, 1, 0, 0, 1);
        }
    }

    private void activateBullet() {
        bullet.setCenX(starship.getCenX());
        bullet.setCenY(starship.getCenY());
        bullet.setAngle(starship.getAngle());
        bullet.setIncX(Math.cos(Math.toRadians(bullet.getAngle())) * BULLET_SPEED_STEP);
        bullet.setIncY(Math.sin(Math.toRadians(bullet.getAngle())) * BULLET_SPEED_STEP);
        bulletTimer = (int) Math.min(this.getWidth() / Math.abs(bullet.getIncX()), this.getHeight() / Math.abs(bullet.getIncY())) - 2;
        activeBullet = true;
        soundPool.play(idShoot, 1, 1, 1, 0, 1);
    }

    private void activateSensors(){

    }

    private void deactivateSensors(){

    }

    public GameThread getThread() {
        return thread;
    }

    class GameThread extends Thread {

        private boolean paused, running;

        public synchronized void pause() {
            paused = true;
        }

        public synchronized void resumeGame() {
            paused = false;
            notify();
        }

        public void stopGame() {
            running = false;
            if (paused) resumeGame();
        }

        @Override
        public void run() {
            running = true;
            while (running) {
                updatePhysics();
                synchronized (this) {
                    while (paused) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }


}