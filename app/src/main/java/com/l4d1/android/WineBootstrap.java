package com.l4d1.android;

import android.content.Context;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class WineBootstrap {

    public static String testVersion(Context context) {
        File nativeDir = new File(
            context.getApplicationInfo().nativeLibraryDir
        );

        File wine = new File(nativeDir, "libwine_bootstrap.so");

        if (!wine.isFile()) {
            return "WINE BINARY NOT FOUND:\n" + wine;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                wine.getAbsolutePath(), "--version"
            );

            pb.redirectErrorStream(true);
            pb.environment().put(
                "WINEDLLPATH", nativeDir.getAbsolutePath()
            );
            pb.environment().put(
                "LD_LIBRARY_PATH", nativeDir.getAbsolutePath()
            );

            Process process = pb.start();

            boolean finished = process.waitFor(
                10, TimeUnit.SECONDS
            );

            if (!finished) {
                process.destroyForcibly();
                return "WINE TIMEOUT (10 seconds)";
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            output.append("Exit code: ")
                  .append(process.exitValue());

            return output.toString();

        } catch (Exception e) {
            return "WINE EXECUTION FAILED:\n" + e;
        }
    }
}
