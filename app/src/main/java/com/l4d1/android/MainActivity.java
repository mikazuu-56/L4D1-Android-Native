package com.l4d1.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;

public class MainActivity extends Activity {

    GameView game;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        game = new GameView();
        setContentView(game);
    }

    class GameView extends View {

        Paint p = new Paint(3);

        float joyX, joyY;
        float knobX, knobY;
        float moveX, moveY;

        float cameraX, cameraY;

        int joystickId = -1;
        int cameraId = -1;

        String action = "READY";

        GameView() {
            super(MainActivity.this);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        void circle(Canvas c, float x, float y,
                    float radius, int color) {
            p.setColor(color);
            c.drawCircle(x, y, radius, p);
        }

        void button(Canvas c, String name,
                    float x, float y, float radius) {

            circle(c, x, y, radius, 0x99305577);

            p.setColor(Color.WHITE);
            p.setTextSize(25);
            p.setTextAlign(Paint.Align.CENTER);

            c.drawText(name, x, y + 8, p);
        }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);

            float w = getWidth();
            float h = getHeight();

            c.drawColor(0xFF101820);

            joyX = 130;
            joyY = h - 135;

            if (joystickId == -1) {
                knobX = joyX;
                knobY = joyY;
            }

            p.setColor(Color.WHITE);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(32);

            c.drawText("L4D1 ANDROID v0.2",
                       w / 2, 60, p);

            p.setTextSize(20);

            c.drawText("ACTION: " + action,
                       w / 2, 100, p);

            c.drawText(
                String.format(
                    java.util.Locale.US,
                    "MOVE %.2f / %.2f",
                    moveX, moveY
                ),
                w / 2, 135, p
            );

            c.drawText(
                String.format(
                    java.util.Locale.US,
                    "CAMERA %.0f / %.0f",
                    cameraX, cameraY
                ),
                w / 2, 165, p
            );

            circle(c, joyX, joyY, 95, 0x663377AA);
            circle(c, knobX, knobY, 40, 0xCC55AAFF);

            button(c, "FIRE", w - 110, h - 145, 65);
            button(c, "JUMP", w - 40, h - 270, 40);
            button(c, "RELOAD", w - 245, h - 65, 43);
            button(c, "DUCK", w - 55, h - 55, 43);
            button(c, "USE", w - 245, h - 185, 43);
        }

        void updateJoystick(float x, float y) {

            float dx = x - joyX;
            float dy = y - joyY;

            float distance =
                (float)Math.sqrt(dx * dx + dy * dy);

            float radius = 80;

            if (distance > radius) {
                dx = dx / distance * radius;
                dy = dy / distance * radius;
            }

            knobX = joyX + dx;
            knobY = joyY + dy;

            moveX = dx / radius;
            moveY = dy / radius;
        }

        String getAction(float x, float y) {

            float w = getWidth();
            float h = getHeight();

            if (Math.hypot(x-(w-110),y-(h-145)) < 65)
                return "FIRE";

            if (Math.hypot(x-(w-40),y-(h-270)) < 40)
                return "JUMP";

            if (Math.hypot(x-(w-245),y-(h-65)) < 43)
                return "RELOAD";

            if (Math.hypot(x-(w-55),y-(h-55)) < 43)
                return "CROUCH";

            if (Math.hypot(x-(w-245),y-(h-185)) < 43)
                return "USE";

            return "";
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
                    updateJoystick(x, y);

                } else {

                    String a = getAction(x, y);

                    if (!a.isEmpty()) {
                        action = a;
                    } else if (cameraId == -1) {
                        cameraId = id;
                        cameraX = x;
                        cameraY = y;
                    }
                }

            } else if (type == MotionEvent.ACTION_MOVE) {

                for (int i = 0; i < e.getPointerCount(); i++) {

                    int id = e.getPointerId(i);

                    if (id == joystickId) {
                        updateJoystick(e.getX(i), e.getY(i));
                    }

                    if (id == cameraId) {
                        cameraX = e.getX(i);
                        cameraY = e.getY(i);
                    }
                }

            } else if (type == MotionEvent.ACTION_UP ||
                       type == MotionEvent.ACTION_POINTER_UP) {

                int id = e.getPointerId(index);

                if (id == joystickId) {
                    joystickId = -1;
                    moveX = 0;
                    moveY = 0;
                }

                if (id == cameraId) {
                    cameraId = -1;
                }

                action = "READY";

            } else if (type == MotionEvent.ACTION_CANCEL) {

                joystickId = -1;
                cameraId = -1;
                moveX = 0;
                moveY = 0;
                action = "READY";
            }

            invalidate();
            return true;
        }
    }
}
