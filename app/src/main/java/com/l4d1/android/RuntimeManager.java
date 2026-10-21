package com.l4d1.android;

import android.content.Context;
import android.os.Build;
import java.io.File;

public class RuntimeManager {

    private final Context context;

    public RuntimeManager(Context context) {
        this.context = context;
    }

    public String getStatus() {

        File runtime = new File(
            context.getFilesDir(), "runtime"
        );

        File wine = new File(runtime, "bin/wine");
        File box86 = new File(runtime, "bin/box86");
        File box64 = new File(runtime, "bin/box64");

        StringBuilder s = new StringBuilder();

        s.append("L4D1 RUNTIME DIAGNOSTICS\n\n");

        s.append("Android: ")
         .append(Build.VERSION.RELEASE)
         .append("\n");

        s.append("CPU ABI: ")
         .append(Build.SUPPORTED_ABIS[0])
         .append("\n\n");

        s.append("Wine: ")
         .append(wine.isFile() ? "FOUND" : "MISSING")
         .append("\n");

        s.append("Box86: ")
         .append(box86.isFile() ? "FOUND" : "MISSING")
         .append("\n");

        s.append("Box64: ")
         .append(box64.isFile() ? "FOUND" : "MISSING")
         .append("\n\n");

        s.append("Runtime execution: NOT VERIFIED\n");

        s.append("\nRuntime directory:\n")
         .append(runtime.getAbsolutePath());

        return s.toString();
    }

    public boolean isReady() {
        return false;
    }
}
