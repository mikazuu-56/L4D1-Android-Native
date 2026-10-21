package com.l4d1.android;

import android.app.Activity;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.graphics.*;
import android.view.*;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

    GLSurfaceView glView;
    WorldRenderer renderer;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().getDecorView().setSystemUiVisibility(
            5894 | 1024 | 512
        );

        try {
            String status = NativeEngine.getStatus();

            android.util.Log.i(
                "L4D1_NATIVE",
                status
            );

            android.widget.Toast.makeText(
                this,
                status,
                android.widget.Toast.LENGTH_LONG
            ).show();

        } catch (Throwable e) {
            android.util.Log.e(
                "L4D1_NATIVE",
                "Native engine failed",
                e
            );
        }

        renderer = new WorldRenderer();

        glView = new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(renderer);

        // Batasi pembaruan sekitar 30 FPS.
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        FrameLayout root = new FrameLayout(this);
        root.addView(glView);

        root.addView(new TouchHUD());

        setContentView(root);
    }

    class TouchHUD extends View {

        Paint p = new Paint(3);

        int joystickId = -1;
        int cameraId = -1;

        float joyX, joyY;
        float knobX, knobY;

        float lastCameraX, lastCameraY;

        long lastRender;

        TouchHUD() {
            super(MainActivity.this);
        }

        void circle(Canvas c, float x, float y,
                    float radius, int color) {
            p.setColor(color);
            c.drawCircle(x, y, radius, p);
        }

        void button(Canvas c, String text,
                    float x, float y, float radius) {

            circle(c, x, y, radius, 0x88336699);

            p.setColor(Color.WHITE);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(17);
            c.drawText(text, x, y + 6, p);
        }

        @Override
        protected void onDraw(Canvas c) {

            float w = getWidth();
            float h = getHeight();

            joyX = 120;
            joyY = h - 120;

            if (joystickId == -1) {
                knobX = joyX;
                knobY = joyY;
            }

            circle(c, joyX, joyY, 85, 0x553377AA);
            circle(c, knobX, knobY, 35, 0xAA55AAFF);

            button(c, "FIRE", w - 115, h - 130, 55);
            button(c, "JUMP", w - 45, h - 245, 38);
            button(c, "RELOAD", w - 245, h - 55, 42);
            button(c, "DUCK", w - 45, h - 45, 38);
            button(c, "USE", w - 245, h - 170, 40);

            p.setColor(Color.WHITE);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(24);

            c.drawText("+", w / 2, h / 2, p);

            p.setTextSize(15);
            c.drawText("L4D1 ANDROID v0.3 - 3D TEST",
                       w / 2, 30, p);

            long now = System.nanoTime();

            if (now - lastRender >= 33_333_333L) {
                glView.requestRender();
                lastRender = now;
            }

            postInvalidateDelayed(33);
        }

        void moveJoystick(float x, float y) {

            float dx = x - joyX;
            float dy = y - joyY;

            float length = (float)Math.hypot(dx, dy);

            if (length > 70) {
                dx = dx / length * 70;
                dy = dy / length * 70;
            }

            knobX = joyX + dx;
            knobY = joyY + dy;

            renderer.moveX = dx / 70;
            renderer.moveY = -dy / 70;
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            int type = e.getActionMasked();
            int index = e.getActionIndex();

            if (type == MotionEvent.ACTION_DOWN ||
                type == MotionEvent.ACTION_POINTER_DOWN) {

                int id = e.getPointerId(index);
                float x = e.getX(index);
                float y = e.getY(index);

                if (x < getWidth() * 0.35f &&
                    y > getHeight() * 0.45f &&
                    joystickId == -1) {

                    joystickId = id;
                    moveJoystick(x, y);

                } else if (cameraId == -1) {

                    cameraId = id;
                    lastCameraX = x;
                    lastCameraY = y;
                }

            } else if (type == MotionEvent.ACTION_MOVE) {

                for (int i = 0; i < e.getPointerCount(); i++) {

                    int id = e.getPointerId(i);

                    if (id == joystickId) {
                        moveJoystick(e.getX(i), e.getY(i));
                    }

                    if (id == cameraId) {

                        float x = e.getX(i);
                        float y = e.getY(i);

                        renderer.yaw +=
                            (x - lastCameraX) * 0.15f;

                        renderer.pitch -=
                            (y - lastCameraY) * 0.15f;

                        renderer.pitch = Math.max(
                            -85, Math.min(85, renderer.pitch)
                        );

                        lastCameraX = x;
                        lastCameraY = y;
                    }
                }

            } else if (type == MotionEvent.ACTION_UP ||
                       type == MotionEvent.ACTION_POINTER_UP) {

                int id = e.getPointerId(index);

                if (id == joystickId) {
                    joystickId = -1;
                    renderer.moveX = 0;
                    renderer.moveY = 0;
                }

                if (id == cameraId) {
                    cameraId = -1;
                }

            } else if (type == MotionEvent.ACTION_CANCEL) {

                joystickId = -1;
                cameraId = -1;
                renderer.moveX = 0;
                renderer.moveY = 0;
            }

            invalidate();
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glView != null) glView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glView != null) glView.onResume();
    }
}
