package dreamfacilities.com.asteroids;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.os.Bundle;
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


    // MAIN
    private int score = 0;
    private Activity parent;

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
    private Vector<Graphic> bullets;
    private static int BULLET_SPEED_STEP = 12;
    private Vector<Integer> bulletTimers;

    private SharedPreferences pref;

    //MEDIA
    private SoundPool soundPool;

    int idShoot, idExplosion;

    private Drawable drawableStarship, drawableBullet;
    private Drawable drawableAsteroid[] = new Drawable[3];

    SensorManager mSensorManager;

    public GameView(Context context, AttributeSet attrs) {

        super(context, attrs);

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

            for (int i = 0; i < 3; i++) {
                ShapeDrawable dAsteroid = new ShapeDrawable(new PathShape(
                        pathAsteroid, 1, 1));
                dAsteroid.getPaint().setColor(Color.WHITE);
                dAsteroid.getPaint().setStyle(Paint.Style.STROKE);
                dAsteroid.setIntrinsicWidth(50 - i * 14);
                dAsteroid.setIntrinsicHeight(50 - i * 14);
                drawableAsteroid[i] = dAsteroid;
            }

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


            drawableAsteroid[0] = context.getResources().getDrawable(R.drawable.asteroide1);
            drawableAsteroid[1] = context.getResources().getDrawable(R.drawable.asteroide2);
            drawableAsteroid[2] = context.getResources().getDrawable(R.drawable.asteroide3);

            drawableStarship = context.getResources().getDrawable(R.drawable.nave);
            drawableBullet = context.getResources().getDrawable(R.drawable.misil1);

        }

        starship = new Graphic(this, drawableStarship);
        starship.setIncX(0);
        starship.setIncY(0);


        asteroids = new Vector<Graphic>();
        for (int i = 0; i < numAsteroids; i++) {
            Graphic asteroid = new Graphic(this, drawableAsteroid[0]);
            asteroid.setIncY(Math.random() * 4 - 2);
            asteroid.setIncX(Math.random() * 4 - 2);
            asteroid.setAngle((int) (Math.random() * 360));
            asteroid.setRotation((int) (Math.random() * 8 - 4));
            asteroids.add(asteroid);
        }

        bullets = new Vector<Graphic>();
        bulletTimers = new Vector<Integer>();

        //Register the sensor and listener to our view
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!listSensors.isEmpty()) {
            Sensor accelerometerSensor = listSensors.get(0);
            mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
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
            if(asteroid.verifyCollision(starship)){
                gameOver();
            }
        }
        synchronized (bullets) {


            if (!bullets.isEmpty()) {
                for (int b = 0; b < bullets.size(); b++) {

                    Graphic bullet = bullets.get(b);
                    bullet.incrementPos(delay);

                    int bulletTimer = bulletTimers.get(b).intValue();
                    bulletTimer -= delay;

                    if (bulletTimer < 0) {
                        bullets.removeElementAt(b);
                        bulletTimers.removeElementAt(b);
                    } else {
                        for (int i = 0; i < asteroids.size(); i++) {
                            if (bullet.verifyCollision(asteroids.elementAt(i))) {
                                destroyAsteroid(i);
                                bullets.removeElementAt(b);
                                bulletTimers.removeElementAt(b);
                                break;
                            }
                        }
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
            for (Graphic asteroid : asteroids) asteroid.drawGraphic(canvas);

        }
        synchronized (bullets) {
            if (!bullets.isEmpty()) for (Graphic bullet : bullets) bullet.drawGraphic(canvas);
        }
        starship.drawGraphic(canvas);
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private boolean existRotationInitialValue = false,
            existAccelerationInitialValue = false;
    private float initialRotationValue, initialAccelerationValue;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (pref.getBoolean("sensor_accelerometer", false)) {
            float rotationValue = event.values[1];
            float accelerationValue = event.values[0];

            //Log.d("Sensor 1","Acelerómetro "+1+": "+event.values[1]);
            if (!existRotationInitialValue) {
                initialRotationValue = rotationValue;
                existRotationInitialValue = true;
            }
            if (!existAccelerationInitialValue) {
                initialAccelerationValue = accelerationValue;
                existAccelerationInitialValue = true;
            }
            // ACC -180 -- 180
            rotationStarship = (int) (rotationValue - initialRotationValue);

            int accResult = (int) (accelerationValue - initialAccelerationValue) / 2;
            if (accResult < 0) accelerationSpaceship = (-accResult);
        }
    }

    private void destroyAsteroid(int i) {
        synchronized (asteroids) {
            int tam;
            if (asteroids.get(i).getDrawable() != drawableAsteroid[2]) {
                if (asteroids.get(i).getDrawable() == drawableAsteroid[1]) {
                    tam = 2;
                    score = 1000;
                } else {
                    tam = 1;
                    score = 500;
                }
                for (int n = 0; n < numFragments; n++) {
                    Graphic asteroid = new Graphic(this, drawableAsteroid[tam]);
                    asteroid.setCenX(asteroids.get(i).getCenX());
                    asteroid.setCenY(asteroids.get(i).getCenY());
                    asteroid.setIncX(Math.random() * 7 - 2 - tam);
                    asteroid.setIncY(Math.random() * 7 - 2 - tam);
                    asteroid.setAngle((int) (Math.random() * 360));
                    asteroid.setRotation((int) (Math.random() * 8 - 4));
                    asteroids.add(asteroid);
                }
            }
        }
        asteroids.removeElementAt(i);
        soundPool.play(idExplosion, 1, 1, 0, 0, 1);

        if(asteroids.isEmpty()){
            gameOver();
        }
    }

    private void activateBullet() {
        Graphic bullet = new Graphic(this, drawableBullet);
        bullet.setCenX(starship.getCenX());
        bullet.setCenY(starship.getCenY());
        bullet.setAngle(starship.getAngle());
        bullet.setIncX(Math.cos(Math.toRadians(bullet.getAngle())) * BULLET_SPEED_STEP);
        bullet.setIncY(Math.sin(Math.toRadians(bullet.getAngle())) * BULLET_SPEED_STEP);

        int indexTimer = 0;
        if (!bullets.isEmpty()) indexTimer = bullets.size();

        bullets.add(bullet);
        bulletTimers.add(indexTimer, (int) Math.min(this.getWidth() / Math.abs(bullet.getIncX()), this.getHeight() / Math.abs(bullet.getIncY())) - 2);
        soundPool.play(idShoot, 1, 1, 1, 0, 1);
    }

    public void activateSensors() {
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!listSensors.isEmpty()) {
            Sensor accelerometerSensor = listSensors.get(0);
            mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void deactivateSensors() {
        mSensorManager.unregisterListener(this);
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

    public void setParent(Activity game) {
        this.parent = game;
    }

    private void gameOver() {
        Bundle bundle = new Bundle();
        bundle.putInt("score", score);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        parent.setResult(Activity.RESULT_OK, intent);
        parent.finish();
    }

}