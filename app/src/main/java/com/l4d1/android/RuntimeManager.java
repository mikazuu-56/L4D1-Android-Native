package com.l4d1.android;

import android.content.Context;
import android.os.Build;
import java.io.File;

public class RuntimeManager {

    private final Context context;

    public RuntimeManager(Context context) {
        this.context = context;
    }

    private File runtimeDir() {
        return new File(context.getFilesDir(), "runtime");
    }

    private boolean hasWineFiles() {
        File wine = new File(
            runtimeDir(), "bin/wine"
        );

        File wowbox = new File(
            runtimeDir(),
            "lib/wine/aarch64-windows/wowbox64.dll"
        );

        return wine.isFile() && wowbox.isFile();
    }

    public boolean isReady() {
        // File presence does not prove executability.
        // Standalone execution has not been validated.
        return false;
    }

    public String getStatus() {
        StringBuilder s = new StringBuilder();

        s.append("LEFT 4 DEAD 2 RUNTIME\n\n");

        s.append("Android: ")
         .append(Build.VERSION.RELEASE)
         .append("\n");

        s.append("CPU ABI: ")
         .append(Build.SUPPORTED_ABIS[0])
         .append("\n\n");

        s.append("Wine files: ")
         .append(
             hasWineFiles() ? "FOUND" : "MISSING"
         )
         .append("\n");

        s.append("Wine execution: NOT VERIFIED\n");

        s.append("WowBox64 execution: NOT VERIFIED\n");

        s.append("Graphics integration: INCOMPLETE\n");

        s.append("Standalone runtime: NOT READY\n\n");

        s.append("Runtime directory:\n")
         .append(runtimeDir().getAbsolutePath());

        return s.toString();
    }
}
