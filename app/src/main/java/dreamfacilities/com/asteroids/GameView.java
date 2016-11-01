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
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

import java.util.Vector;

/**
 * Created by alex on 18/10/16.
 */

public class GameView extends View {
    // //// ASTEROIDES //////
    private Vector<Graphic> asteroids;// Vector con los Asteroides
    private int numAsteroides = 5; // Número inicial de asteroids
    private int numFragmentos = 3; // Fragmentos en que se divide

    // //// NAVE //////
    private Graphic nave;
    private int turnStarship;
    private double accelerationSpaceship;
    private static final int MAX_VELOCIDAD_NAVE = 20;
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    public GameView(Context context, AttributeSet attrs) {

        super(context, attrs);
        Drawable drawableStarship, drawableAsteroid, drawableMisil;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

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

            ShapeDrawable dAsteroid = new ShapeDrawable( new PathShape(pathAsteroid, 1, 1));
            dAsteroid.getPaint().setColor(Color.WHITE);
            dAsteroid.getPaint().setStyle(Paint.Style.STROKE);
            dAsteroid.setIntrinsicWidth(50);
            dAsteroid.setIntrinsicHeight(50);

            drawableAsteroid = dAsteroid;

            Path pathStarship = new Path();
            pathStarship.lineTo((float) 1, (float) 0.5);
            pathStarship.lineTo((float) 0, (float) 1);
            pathStarship.lineTo((float) 0, (float) 0);

            ShapeDrawable dStarship = new ShapeDrawable( new PathShape(pathStarship, 1, 1));
            dStarship.getPaint().setColor(Color.WHITE);
            dStarship.getPaint().setStyle(Paint.Style.STROKE);
            dStarship.setIntrinsicWidth(20);
            dStarship.setIntrinsicHeight(15);

            drawableStarship = dStarship;

            setBackgroundColor(Color.BLACK);

        } else {

            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            drawableAsteroid = context.getResources().getDrawable(R.drawable.asteroide1);
            drawableStarship = context.getResources().getDrawable(R.drawable.nave);

        }

        nave = new Graphic(this, drawableStarship);
        nave.setIncX(0);
        nave.setIncY(0);

        asteroids = new Vector<Graphic>();
        for (int i = 0; i < numAsteroides; i++) {
            Graphic asteroid = new Graphic(this, drawableAsteroid);
            asteroid.setIncY(Math.random() * 4 - 2);
            asteroid.setIncX(Math.random() * 4 - 2);
            asteroid.setAngle((int) (Math.random() * 360));
            asteroid.setRotation((int) (Math.random() * 8 - 4));
            asteroids.add(asteroid);
        }

    }

    @Override
    protected void onSizeChanged(int width, int height, int width_before, int height_before) {
        super.onSizeChanged(width, height, width_before, height_before);

        nave.setCenX(width / 2);
        nave.setCenY(height / 2);

        for (Graphic asteroid : asteroids) {

            do {

                asteroid.setCenX((int) (Math.random() * width));
                asteroid.setCenY((int) (Math.random() * height));

            } while (asteroid.distance(nave) < (width + height) / 5);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Graphic asteroid : asteroids) {
            asteroid.drawGraphic(canvas);
        }
        nave.drawGraphic(canvas);
    }

}