package com.gmail.ya.anjaryuliana.tugasmultitouch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnTouchListener {
    private Matrix matriks = new Matrix();
    private Matrix simpanMatriks = new Matrix();
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF mulai = new PointF();
    private PointF tengah = new PointF();
    private float jarakAwal = 1f;
    private float jarak = 0f;
    private float rotasiAwal = 0f;
    private float[] evenAkhir = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView tampilGambar = (ImageView) findViewById(R.id.imageView);
        tampilGambar.setOnTouchListener(this);
    }
    public boolean onTouch(View view, MotionEvent event) {
        ImageView gambar = (ImageView) view;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                simpanMatriks.set(matriks);
                mulai.set(event.getX(), event.getY());
                mode = DRAG;
                evenAkhir = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                jarakAwal = jarak2(event);
                if (jarakAwal > 10f) {
                    simpanMatriks.set(matriks);
                    nilaiTengah(tengah, event);
                    mode = ZOOM;
                }
                evenAkhir = new float[4];
                evenAkhir[0] = event.getX(0);
                evenAkhir[1] = event.getX(1);
                evenAkhir[2] = event.getY(0);
                evenAkhir[3] = event.getY(1);
                jarak = rotasi(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                evenAkhir = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matriks.set(simpanMatriks);
                    float dx = event.getX() - mulai.x;
                    float dy = event.getY() - mulai.y;
                    matriks.postTranslate(dx, dy);
                } else if (mode == ZOOM) {
                    float jarakBaru = jarak2(event);
                    if (jarakBaru > 10f) {
                        matriks.set(simpanMatriks);
                        float scale = (jarakBaru / jarakAwal);
                        matriks.postScale(scale, scale, tengah.x, tengah.y);
                    }
                    if (evenAkhir != null && event.getPointerCount() == 3) {
                        rotasiAwal = rotasi(event);
                        float r = rotasiAwal - jarak;
                        float[] values = new float[9];
                        matriks.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (gambar.getWidth() / 2) * sx;
                        float yc = (gambar.getHeight() / 2) * sx;
                        matriks.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }
        gambar.setImageMatrix(matriks);
        return true;
    }
    private float jarak2(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }
    private void nilaiTengah(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    private float rotasi(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}
