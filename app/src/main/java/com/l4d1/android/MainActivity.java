package com.l4d1.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    FrameLayout root;

    void addButton(String text, int gravity, int width,
                   int height, int x, int y) {

        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(0xAA163653)
        );

        FrameLayout.LayoutParams p =
            new FrameLayout.LayoutParams(width, height, gravity);

        if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            p.rightMargin = x;
        } else {
            p.leftMargin = x;
        }

        if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            p.bottomMargin = y;
        } else {
            p.topMargin = y;
        }

        root.addView(button, p);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101820);
        setContentView(root);

        TextView title = new TextView(this);
        title.setText("LEFT 4 DEAD 1\nANDROID PROTOTYPE\n30 FPS TARGET");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);

        root.addView(title, new FrameLayout.LayoutParams(
            -1, -1
        ));

        addButton("MOVE", Gravity.LEFT | Gravity.BOTTOM,
                  180, 140, 30, 30);

        addButton("FIRE", Gravity.RIGHT | Gravity.BOTTOM,
                  130, 100, 30, 100);

        addButton("RELOAD", Gravity.RIGHT | Gravity.BOTTOM,
                  110, 65, 180, 30);

        addButton("JUMP", Gravity.RIGHT | Gravity.BOTTOM,
                  100, 65, 30, 220);

        addButton("CROUCH", Gravity.RIGHT | Gravity.BOTTOM,
                  110, 65, 30, 30);

        addButton("USE", Gravity.RIGHT | Gravity.TOP,
                  100, 65, 180, 30);

        addButton("MENU", Gravity.LEFT | Gravity.TOP,
                  100, 65, 20, 20);
    }
}
