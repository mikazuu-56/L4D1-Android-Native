package com.l4d1.android;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.database.Cursor;
import android.provider.DocumentsContract;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int PICK_GAME = 1001;
    private static final int PICK_INSTALLER = 1002;

    private TextView gameStatus;
    private TextView runtimeStatus;
    private Button launchButton;

    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        prefs = getSharedPreferences(
            "l4d1_launcher", MODE_PRIVATE
        );

        buildInterface();
        restoreGameDirectory();
    }

    private TextView createText(
        String text, int size, int color
    ) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(12, 15, 12, 15);
        return view;
    }

    private void buildInterface() {

        LinearLayout layout =
            new LinearLayout(this);

        layout.setOrientation(
            LinearLayout.VERTICAL
        );

        layout.setPadding(30, 25, 30, 25);
        layout.setBackgroundColor(
            Color.rgb(18, 22, 27)
        );

        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);

        TextView title = createText(
            "LEFT 4 DEAD 1",
            30,
            Color.WHITE
        );

        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView version = createText(
            "ANDROID LAUNCHER v0.8",
            15,
            Color.LTGRAY
        );

        version.setGravity(Gravity.CENTER);
        layout.addView(version);


        Button installerButton = new Button(this);
        installerButton.setText("SELECT WINDOWS INSTALLER");

        layout.addView(installerButton);

        installerButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                Intent.ACTION_OPEN_DOCUMENT
            );

            intent.setType("*/*");
            intent.addCategory(
                Intent.CATEGORY_OPENABLE
            );

            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            );

            startActivityForResult(
                intent, PICK_INSTALLER
            );
        });

Button selectButton = new Button(this);
        selectButton.setText("SELECT GAME FOLDER");

        layout.addView(selectButton);

        selectButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                Intent.ACTION_OPEN_DOCUMENT_TREE
            );

            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            );

            startActivityForResult(
                intent, PICK_GAME
            );
        });

        gameStatus = createText(
            "GAME: NOT SELECTED",
            16,
            Color.YELLOW
        );

        layout.addView(gameStatus);

        runtimeStatus = createText(
            "RUNTIME: NOT INSTALLED",
            16,
            Color.RED
        );

        layout.addView(runtimeStatus);


        Button gpuButton = new Button(this);
        gpuButton.setText("CHECK VULKAN GPU");

        layout.addView(gpuButton);

        gpuButton.setOnClickListener(v -> {
            try {
                String result = NativeEngine.getStatus();

                new android.app.AlertDialog.Builder(this)
                    .setTitle("Vulkan GPU Information")
                    .setMessage(result)
                    .setPositiveButton("OK", null)
                    .show();

            } catch (Throwable e) {
                new android.app.AlertDialog.Builder(this)
                    .setTitle("Vulkan Error")
                    .setMessage(e.toString())
                    .setPositiveButton("OK", null)
                    .show();
            }
        });


        Button runtimeButton = new Button(this);
        runtimeButton.setText("CHECK RUNTIME");

        layout.addView(runtimeButton);

        runtimeButton.setOnClickListener(v -> {

            RuntimeManager manager =
                new RuntimeManager(this);

            String result = manager.getStatus();

            runtimeStatus.setText(
                manager.isReady()
                    ? "STANDALONE FILES: FOUND (UNVERIFIED)"
                    : "STANDALONE RUNTIME: NOT INSTALLED"
            );

            new android.app.AlertDialog.Builder(this)
                .setTitle("Runtime Diagnostics")
                .setMessage(result)
                .setPositiveButton("OK", null)
                .show();
        });


        Button testDx9Button = new Button(this);
        testDx9Button.setText("TEST DIRECTX 9");
        layout.addView(testDx9Button);
        testDx9Button.setOnClickListener(v -> runDx9Test());

        launchButton = new Button(this);
        launchButton.setText("LAUNCH GAME");
        launchButton.setEnabled(false);

        layout.addView(launchButton);
        launchButton.setOnClickListener(v -> runL4D1());

        TextView information = createText(
            "Launcher foundation ready.\n" +
            "Windows compatibility runtime " +
            "has not been integrated.",
            14,
            Color.LTGRAY
        );

        layout.addView(information);

        setContentView(scroll);
    }


    private void runDx9Test() {
        final String permission =
            "com.termux.permission.RUN_COMMAND";

        if (android.os.Build.VERSION.SDK_INT >= 23 &&
            checkSelfPermission(permission) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                new String[]{permission}, 2001
            );
            return;
        }

        try {
            Intent intent = new Intent();
            intent.setClassName(
                "com.termux",
                "com.termux.app.RunCommandService"
            );

            intent.setAction(
                "com.termux.RUN_COMMAND"
            );

            intent.putExtra(
                "com.termux.RUN_COMMAND_PATH",
                "/data/data/com.termux/files/home/" +
                "L4D1-Android/runtime-bridge/test-dx9.sh"
            );

            intent.putExtra(
                "com.termux.RUN_COMMAND_WORKDIR",
                "/data/data/com.termux/files/home"
            );

            intent.putExtra(
                "com.termux.RUN_COMMAND_BACKGROUND",
                true
            );

            startService(intent);

            new android.app.AlertDialog.Builder(this)
                .setTitle("DX9 Test")
                .setMessage(
                    "Launch request submitted to Termux.\\n" +
                    "Check Termux:X11 for the DX9 window."
                )
                .setPositiveButton("OK", null)
                .show();

        } catch (Exception e) {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Termux Bridge Error")
                .setMessage(e.toString())
                .setPositiveButton("OK", null)
                .show();
        }
    }


    private void runL4D1() {
        final String permission =
            "com.termux.permission.RUN_COMMAND";

        if (android.os.Build.VERSION.SDK_INT >= 23 &&
            checkSelfPermission(permission) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                new String[]{permission}, 2001
            );
            return;
        }

        try {
            Intent intent = new Intent();
            intent.setClassName(
                "com.termux",
                "com.termux.app.RunCommandService"
            );

            intent.setAction("com.termux.RUN_COMMAND");

            intent.putExtra(
                "com.termux.RUN_COMMAND_PATH",
                "/data/data/com.termux/files/home/" +
                "L4D1-Android/runtime-bridge/launch-l4d1.sh"
            );

            intent.putExtra(
                "com.termux.RUN_COMMAND_WORKDIR",
                "/data/data/com.termux/files/home"
            );

            intent.putExtra(
                "com.termux.RUN_COMMAND_BACKGROUND",
                true
            );

            startService(intent);

            new android.app.AlertDialog.Builder(this)
                .setTitle("L4D1 Launch")
                .setMessage(
                    "Launch request submitted to Termux.\n" +
                    "Check Termux:X11 and the runtime log."
                )
                .setPositiveButton("OK", null)
                .show();

        } catch (Exception e) {
            new android.app.AlertDialog.Builder(this)
                .setTitle("L4D1 Launch Error")
                .setMessage(e.toString())
                .setPositiveButton("OK", null)
                .show();
        }
    }

    private boolean hasChild(
        Uri treeUri, String name, String mime
    ) {
        try {
            String treeId =
                DocumentsContract.getTreeDocumentId(
                    treeUri
                );

            Uri children =
                DocumentsContract.buildChildDocumentsUriUsingTree(
                    treeUri, treeId
                );

            try (Cursor cursor =
                getContentResolver().query(
                    children,
                    new String[] {
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE
                    },
                    null, null, null
                )) {

                if (cursor == null) return false;

                while (cursor.moveToNext()) {

                    String fileName =
                        cursor.getString(0);

                    String fileMime =
                        cursor.getString(1);

                    if (name.equalsIgnoreCase(fileName) &&
                        (mime == null || mime.equals(fileMime))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e(
                "L4D1_LAUNCHER",
                "Folder validation failed",
                e
            );
        }

        return false;
    }

    private void validateGame(Uri uri) {

        boolean exe = hasChild(
            uri, "left4dead.exe", null
        );

        boolean data = hasChild(
            uri,
            "left4dead",
            DocumentsContract.Document.MIME_TYPE_DIR
        );

        if (exe && data) {
            gameStatus.setText(
                "GAME: BASIC FILE CHECK PASSED"
            );
            gameStatus.setTextColor(Color.GREEN);
        } else {
            gameStatus.setText(
                "GAME: INCOMPLETE\n" +
                "left4dead.exe: " + exe + "\n" +
                "left4dead folder: " + data
            );
            gameStatus.setTextColor(Color.YELLOW);
        }

        launchButton.setEnabled(exe && data);
    }

    private void restoreGameDirectory() {

        String saved = prefs.getString(
            "game_uri", null
        );

        if (saved != null) {
            Uri uri = Uri.parse(saved);

            boolean permitted = false;

            for (android.content.UriPermission permission :
                getContentResolver()
                    .getPersistedUriPermissions()) {

                if (permission.getUri().equals(uri) &&
                    permission.isReadPermission()) {
                    permitted = true;
                    break;
                }
            }

            if (permitted) {
                validateGame(uri);
            } else {
                gameStatus.setText(
                    "GAME: FOLDER ACCESS EXPIRED"
                );
            }
        }
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        super.onActivityResult(
            requestCode, resultCode, data
        );


        if (requestCode == PICK_INSTALLER &&
            resultCode == RESULT_OK &&
            data != null) {

            Uri installerUri = data.getData();

            if (installerUri != null) {
                try {
                    getContentResolver()
                        .takePersistableUriPermission(
                            installerUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                    prefs.edit()
                        .putString(
                            "installer_uri",
                            installerUri.toString()
                        )
                        .apply();

                    new android.app.AlertDialog.Builder(this)
                        .setTitle("Installer Selected")
                        .setMessage(
                            "Installer saved successfully.\n\n" +
                            "Windows execution runtime is " +
                            "not installed yet."
                        )
                        .setPositiveButton("OK", null)
                        .show();

                } catch (Exception e) {
                    new android.app.AlertDialog.Builder(this)
                        .setTitle("Installer Error")
                        .setMessage(e.toString())
                        .setPositiveButton("OK", null)
                        .show();
                }
            }
        }

if (requestCode == PICK_GAME &&
            resultCode == RESULT_OK &&
            data != null) {

            Uri uri = data.getData();

            if (uri == null) return;

            try {
                getContentResolver()
                    .takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

                prefs.edit()
                    .putString(
                        "game_uri",
                        uri.toString()
                    )
                    .apply();

                validateGame(uri);

            } catch (Exception e) {
                gameStatus.setText(
                    "GAME: ACCESS ERROR"
                );

                android.util.Log.e(
                    "L4D1_LAUNCHER",
                    "Cannot access game directory",
                    e
                );
            }
        }
    }
}
