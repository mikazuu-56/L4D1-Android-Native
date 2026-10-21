package com.l4d1.android;

import android.content.Context;
import java.io.File;

public class RuntimeProbe {

    public static String inspect(Context context) {
        File nativeDir = new File(
            context.getApplicationInfo().nativeLibraryDir
        );

        File runtimeDir = new File(
            context.getFilesDir(), "runtime"
        );

        StringBuilder result = new StringBuilder();

        result.append("NATIVE RUNTIME PROBE\n\n");

        result.append("Native library directory:\n")
              .append(nativeDir.getAbsolutePath())
              .append("\n\n");

        result.append("Native directory exists: ")
              .append(nativeDir.isDirectory())
              .append("\n");

        result.append("Runtime directory exists: ")
              .append(runtimeDir.isDirectory())
              .append("\n");

        result.append("Wine execution: NOT TESTED");

        return result.toString();
    }
}
