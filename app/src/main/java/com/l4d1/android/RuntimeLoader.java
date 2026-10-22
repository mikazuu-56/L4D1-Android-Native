package com.l4d1.android;

import android.content.Context;
import java.io.File;

public class RuntimeLoader {

    private final Context context;

    public RuntimeLoader(Context context) {
        this.context = context;
    }

    private File runtimeRoot() {
        return new File(context.getFilesDir(), "runtime");
    }

    private boolean exists(String relativePath) {
        return new File(runtimeRoot(), relativePath).isFile();
    }

    public String inspect() {
        StringBuilder report = new StringBuilder();

        report.append("L4D2 STANDALONE RUNTIME\n\n");

        String[] components = {
            "bin/wine",
            "lib/wine/aarch64-unix/ntdll.so",
            "lib/wine/aarch64-windows/wowbox64.dll",
            "lib/wine/i386-windows/ntdll.dll",
            "lib/ld-linux-aarch64.so.1",
            "lib/libc.so.6",
            "bin/box64",
            "dxvk/d3d9.dll"
        };

        int found = 0;

        for (String component : components) {
            boolean available = exists(component);

            if (available) {
                found++;
            }

            report.append(available ? "[OK] " : "[MISSING] ")
                  .append(component)
                  .append("\n");
        }

        report.append("\nComponents: ")
              .append(found)
              .append("/")
              .append(components.length);

        report.append("\n\nRuntime execution: NOT VERIFIED");
        report.append("\nGraphics backend: NOT INTEGRATED");
        report.append("\nTouch input: NOT CONNECTED");
        report.append("\nStandalone game: NOT READY");

        return report.toString();
    }
}
