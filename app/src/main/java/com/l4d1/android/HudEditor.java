package com.l4d1.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class HudEditor extends Activity {

    private FrameLayout canvas;
    private SharedPreferences prefs;
    private View selected;
    private String selectedName;

    private final String[] names = {
        "JOYSTICK", "FIRE", "AIM", "RELOAD",
        "JUMP", "CROUCH", "USE", "SWAP"
    };

    private final float[][] defaults = {
        {0.15f, 0.73f}, {0.88f, 0.48f},
        {0.76f, 0.38f}, {0.87f, 0.70f},
        {0.75f, 0.77f}, {0.88f, 0.85f},
        {0.63f, 0.84f}, {0.63f, 0.65f}
    };

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        prefs = getSharedPreferences("l4d2_hud", MODE_PRIVATE);

        canvas = new FrameLayout(this);
        canvas.setBackgroundColor(Color.rgb(25, 35, 40));
        setContentView(canvas);

        canvas.post(this::buildHud);
    }

    private GradientDrawable background() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.rgb(35, 110, 125));
        d.setCornerRadius(100);
        d.setStroke(2, Color.WHITE);
        return d;
    }

    private void buildHud() {
        canvas.removeAllViews();

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        for (int i = 0; i < names.length; i++) {
            final String name = names[i];

            TextView button = new TextView(this);
            button.setText(name);
            button.setTextColor(Color.WHITE);
            button.setTextSize(11);
            button.setGravity(Gravity.CENTER);
            button.setBackground(background());

            float size = prefs.getFloat(name + "_size", 1f);
            float opacity = prefs.getFloat(name + "_alpha", 0.7f);

            int base = name.equals("JOYSTICK") ? 110 : 75;
            int pixels = (int)(base * size * getResources()
                .getDisplayMetrics().density);

            FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(pixels, pixels);

            float x = prefs.getFloat(name + "_x", defaults[i][0]);
            float y = prefs.getFloat(name + "_y", defaults[i][1]);

            lp.leftMargin = Math.max(0, Math.min(w - pixels,
                (int)(x * w - pixels / 2f)));
            lp.topMargin = Math.max(0, Math.min(h - pixels,
                (int)(y * h - pixels / 2f)));

            button.setAlpha(opacity);
            canvas.addView(button, lp);

            button.setOnTouchListener(new View.OnTouchListener() {
                float startX, startY;
                int originalX, originalY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    FrameLayout.LayoutParams p =
                        (FrameLayout.LayoutParams)v.getLayoutParams();

                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            selected = v;
                            selectedName = name;
                            startX = event.getRawX();
                            startY = event.getRawY();
                            originalX = p.leftMargin;
                            originalY = p.topMargin;
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            p.leftMargin = Math.max(0,
                                Math.min(canvas.getWidth() - v.getWidth(),
                                originalX + (int)(event.getRawX() - startX)));

                            p.topMargin = Math.max(0,
                                Math.min(canvas.getHeight() - v.getHeight(),
                                originalY + (int)(event.getRawY() - startY)));

                            v.setLayoutParams(p);
                            return true;

                        case MotionEvent.ACTION_UP:
                            float nx = (p.leftMargin + v.getWidth()/2f)
                                / canvas.getWidth();
                            float ny = (p.topMargin + v.getHeight()/2f)
                                / canvas.getHeight();

                            prefs.edit()
                                .putFloat(name + "_x", nx)
                                .putFloat(name + "_y", ny)
                                .apply();
                            return true;
                    }
                    return true;
                }
            });
        }

        createMenu();
    }

    private void createMenu() {
        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.HORIZONTAL);
        menu.setBackgroundColor(Color.argb(220, 0, 0, 0));

        Button edit = new Button(this);
        edit.setText("EDIT");
        menu.addView(edit);

        Button reset = new Button(this);
        reset.setText("RESET");
        menu.addView(reset);

        Button save = new Button(this);
        save.setText("SAVE");
        menu.addView(save);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP | Gravity.CENTER_HORIZONTAL
        );

        canvas.addView(menu, lp);

        edit.setOnClickListener(v -> showEditor());

        reset.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            buildHud();
        });

        save.setOnClickListener(v -> {
            prefs.edit().apply();
            finish();
        });
    }

    private void showEditor() {
        if (selected == null || selectedName == null) {
            new AlertDialog.Builder(this)
                .setMessage("Pilih tombol HUD terlebih dahulu.")
                .setPositiveButton("OK", null)
                .show();
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 20, 35, 20);

        TextView sizeLabel = new TextView(this);
        sizeLabel.setText("SIZE: 50–200%");
        layout.addView(sizeLabel);

        SeekBar sizeBar = new SeekBar(this);
        sizeBar.setMax(150);
        sizeBar.setProgress((int)(
            prefs.getFloat(selectedName + "_size", 1f) * 100 - 50
        ));
        layout.addView(sizeBar);

        TextView alphaLabel = new TextView(this);
        alphaLabel.setText("OPACITY: 10–100%");
        layout.addView(alphaLabel);

        SeekBar alphaBar = new SeekBar(this);
        alphaBar.setMax(90);
        alphaBar.setProgress((int)(
            prefs.getFloat(selectedName + "_alpha", 0.7f) * 100 - 10
        ));
        layout.addView(alphaBar);

        new AlertDialog.Builder(this)
            .setTitle("EDIT " + selectedName)
            .setView(layout)
            .setPositiveButton("APPLY", (dialog, which) -> {
                prefs.edit()
                    .putFloat(selectedName + "_size",
                        (sizeBar.getProgress() + 50) / 100f)
                    .putFloat(selectedName + "_alpha",
                        (alphaBar.getProgress() + 10) / 100f)
                    .apply();

                buildHud();
            })
            .setNegativeButton("CANCEL", null)
            .show();
    }
}
