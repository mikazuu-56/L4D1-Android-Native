package com.l4d1.android;

import android.content.Context;
import android.content.pm.PackageManager;
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

    private boolean hasStandaloneRuntime() {
        File wine = new File(runtimeDir(), "bin/wine");
        File wowbox = new File(
            runtimeDir(),
            "lib/wine/aarch64-windows/wowbox64.dll"
        );

        return wine.isFile() && wowbox.isFile();
    }

    private boolean hasTermux() {
        try {
            context.getPackageManager().getPackageInfo(
                "com.termux", 0
            );
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isReady() {
        return hasStandaloneRuntime();
    }

    public String getStatus() {
        StringBuilder s = new StringBuilder();

        s.append("L4D1 ANDROID RUNTIME v0.9\n\n");

        s.append("Android: ")
         .append(Build.VERSION.RELEASE)
         .append("\n");

        s.append("CPU ABI: ")
         .append(Build.SUPPORTED_ABIS[0])
         .append("\n\n");

        s.append("Termux installed: ")
         .append(hasTermux() ? "YES" : "NO")
         .append("\n");

        s.append("Standalone runtime files: ")
         .append(hasStandaloneRuntime() ? "FOUND" : "MISSING")
         .append("\n\n");

        s.append("Runtime execution: NOT VERIFIED\n");

        s.append("Standalone integration: INCOMPLETE\n\n");

        s.append("Runtime directory:\n")
         .append(runtimeDir().getAbsolutePath());

        return s.toString();
    }
}
