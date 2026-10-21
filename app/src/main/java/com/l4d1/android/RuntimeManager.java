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

    private File runtimeFile(String path) {
        return new File(runtimeDir(), path);
    }

    private boolean exists(String path) {
        return runtimeFile(path).isFile();
    }

    public String getStatus() {

        StringBuilder result = new StringBuilder();

        result.append("L4D1 ANDROID RUNTIME v0.7\n\n");

        result.append("Android: ")
              .append(Build.VERSION.RELEASE)
              .append("\n");

        result.append("CPU ABI: ")
              .append(Build.SUPPORTED_ABIS[0])
              .append("\n\n");

        result.append("Runtime files:\n");

        result.append("Hangover Wine: ")
              .append(exists("bin/wine") ? "FOUND" : "MISSING")
              .append("\n");

        result.append("WowBox64: ")
              .append(exists("lib/wine/aarch64-windows/wowbox64.dll")
                      ? "FOUND" : "MISSING")
              .append("\n");

        result.append("WineD3D x86: ")
              .append(exists("lib/wine/i386-windows/wined3d.dll")
                      ? "FOUND" : "MISSING")
              .append("\n");

        result.append("DirectX 9 x86: ")
              .append(exists("lib/wine/i386-windows/d3d9.dll")
                      ? "FOUND" : "MISSING")
              .append("\n\n");

        result.append("Runtime execution: NOT VERIFIED\n");

        result.append("Android integration: NOT COMPLETE\n");

        result.append("\nRuntime directory:\n")
              .append(runtimeDir().getAbsolutePath());

        return result.toString();
    }

    public boolean isReady() {
        // File existence is not proof of executable compatibility.
        return false;
    }
}
