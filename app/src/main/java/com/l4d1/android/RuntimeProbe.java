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

    public static String execute(Context context) {
        File binary = new File(
            context.getApplicationInfo().nativeLibraryDir,
            "libwineprobe.so"
        );

        if (!binary.isFile()) {
            return "PROBE NOT FOUND: " + binary;
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(
                binary.getAbsolutePath()
            );

            builder.redirectErrorStream(true);

            Process process = builder.start();

            java.io.BufferedReader reader =
                new java.io.BufferedReader(
                    new java.io.InputStreamReader(
                        process.getInputStream()
                    )
                );

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            output.append("Exit code: ")
                  .append(exitCode);

            return output.toString();

        } catch (Exception e) {
            return "PROBE EXECUTION FAILED:\n" + e;
        }
    }

}
