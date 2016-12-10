package dreamfacilities.com.asteroids;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by alex on 18/10/16.
 */

public class Graphic {
    private Drawable drawable; //Imagen que dibujaremos
    private int cenX, cenY; //Posición del centro del gráfico
    private int width, height; //Dimensiones de la imagen
    private double incX, incY; //Velocidad desplazamiento
    private double angle, rotation; //Ángulo y velocidad rotación
    private int radioColission; //Para determinar colisión
    private int xPrevious, yPrevious; // Posición anterior
    private int radioInval; // Radio usado en invalidate()
    private View view; // Usada en view.invalidate()


    public Graphic(View view, Drawable drawable) {
        this.view = view;
        this.drawable = drawable;
        width = drawable.getIntrinsicWidth();
        height = drawable.getIntrinsicHeight();
        radioColission = (height + width) / 4;
        radioInval = (int) Math.hypot(width / 2, height / 2);
    }

    public void drawGraphic(Canvas canvas) {
        int x = cenX - width / 2;
        int y = cenY - height / 2;
        drawable.setBounds(x, y, x + width, y + height);
        canvas.save();
        canvas.rotate((float) angle, cenX, cenY);
        drawable.draw(canvas);
        canvas.restore();
        view.invalidate(cenX - radioInval, cenY - radioInval,
                cenX + radioInval, cenY + radioInval);
        view.invalidate(xPrevious - radioInval, yPrevious - radioInval,
                xPrevious + radioInval, yPrevious + radioInval);
        xPrevious = cenX;
        yPrevious = cenY;
    }

    public void incrementPos(double factor) {
        cenX += incX * factor;
        cenY += incY * factor;
        angle += rotation * factor;

        if (cenX < 0) cenX = view.getWidth();
        if (cenX > view.getWidth()) cenX = 0;
        if (cenY < 0) cenY = view.getHeight();
        if (cenY > view.getHeight()) cenY = 0;
    }

    public double distance(Graphic g) {
        return Math.hypot(cenX - g.cenX, cenY - g.cenY);
    }

    public boolean verifyCollision(Graphic g) {
        return (distance(g) < (radioColission + g.radioColission));
    }

    public int getCenX() {
        return cenX;
    }

    public void setCenX(int cenX) {
        this.cenX = cenX;
    }

    public int getCenY() {
        return cenY;
    }

    public void setCenY(int cenY) {
        this.cenY = cenY;
    }

    public int getAncho() {
        return width;
    }

    public void setAncho(int width) {
        this.width = width;
    }

    public int getAlto() {
        return height;
    }

    public void setAlto(int height) {
        this.height = height;
    }

    public double getIncX() {
        return incX;
    }

    public void setIncX(double incX) {
        this.incX = incX;
    }

    public double getIncY() {
        return incY;
    }

    public void setIncY(double incY) {
        this.incY = incY;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public int getRadioColision() {
        return radioColission;
    }

    public void setRadioColision(int radioColission) {
        this.radioColission = radioColission;
    }

    public int getxPrevious() {
        return xPrevious;
    }

    public void setxPrevious(int xPrevious) {
        this.xPrevious = xPrevious;
    }

    public int getyPrevious() {
        return yPrevious;
    }

    public void setyPrevious(int yPrevious) {
        this.yPrevious = yPrevious;
    }

    public int getRadioInval() {
        return radioInval;
    }

    public void setRadioInval(int radioInval) {
        this.radioInval = radioInval;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}