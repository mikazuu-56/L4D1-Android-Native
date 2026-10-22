package com.l4d1.android;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class WineBootstrap {

    private static void copyFile(File source, File destination)
            throws Exception {
        try (InputStream in = new FileInputStream(source);
             FileOutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
        }
        destination.setReadable(true, true);
        destination.setExecutable(true, true);
    }

    public static String testVersion(Context context) {
        try {
            File nativeDir = new File(
                context.getApplicationInfo().nativeLibraryDir
            );

            File wine = new File(nativeDir, "libwine_bootstrap.so");
            File ntdll = new File(nativeDir, "libntdll_bootstrap.so");
            File spawn = new File(nativeDir, "libandroid-spawn.so");

            if (!wine.isFile() || !ntdll.isFile() || !spawn.isFile()) {
                return "WINE FILE MISSING\n"
                    + "wine: " + wine.isFile() + "\n"
                    + "ntdll: " + ntdll.isFile() + "\n"
                    + "android-spawn: " + spawn.isFile();
            }

            File root = new File(context.getFilesDir(), "wine-runtime");
            File binDir = new File(root, "bin");
            File libDir = new File(root, "lib/wine/aarch64-unix");

            if (!binDir.mkdirs() && !binDir.isDirectory()) {
                return "Cannot create bin directory:\n" + binDir;
            }
            if (!libDir.mkdirs() && !libDir.isDirectory()) {
                return "Cannot create library directory:\n" + libDir;
            }

            File runtimeWine = new File(binDir, "wine");
            copyFile(wine, runtimeWine);
            copyFile(ntdll, new File(libDir, "ntdll.so"));
            copyFile(spawn, new File(libDir, "libandroid-spawn.so"));

            ProcessBuilder pb = new ProcessBuilder(
                runtimeWine.getAbsolutePath(), "--version"
            );
            pb.directory(binDir);
            pb.redirectErrorStream(true);
            pb.environment().put("LD_LIBRARY_PATH", libDir.getAbsolutePath());

            Process process = pb.start();
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return "WINE TIMEOUT (10 seconds)\nRuntime: " + root;
            }

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            output.append("Exit code: ").append(process.exitValue());
            output.append("\nRuntime: ").append(root);
            return output.toString();

        } catch (Exception e) {
            return "WINE EXECUTION FAILED:\n" + e;
        }
    }
}
