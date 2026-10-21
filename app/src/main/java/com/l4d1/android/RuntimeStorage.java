package com.l4d1.android;

import android.content.Context;
import java.io.File;

public class RuntimeStorage {

    private final Context context;

    public RuntimeStorage(Context context) {
        this.context = context;
    }

    private File ensureDirectory(String name) {
        File dir = new File(context.getFilesDir(), name);

        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException(
                "Cannot create directory: " + dir
            );
        }

        return dir;
    }

    public File getRuntimeDirectory() {
        return ensureDirectory("runtime");
    }

    public File getConfigDirectory() {
        return ensureDirectory("config");
    }

    public File getLogDirectory() {
        return ensureDirectory("logs");
    }

    public String getStatus() {
        StringBuilder result = new StringBuilder();

        result.append("APK INTERNAL STORAGE\n\n");

        result.append("Runtime:\n")
              .append(getRuntimeDirectory())
              .append("\n\n");

        result.append("Config:\n")
              .append(getConfigDirectory())
              .append("\n\n");

        result.append("Logs:\n")
              .append(getLogDirectory());

        return result.toString();
    }
}
