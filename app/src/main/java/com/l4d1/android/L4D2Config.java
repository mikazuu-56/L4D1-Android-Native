package com.l4d1.android;

public class L4D2Config {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int TARGET_FPS = 30;

    public static final String EXECUTABLE =
        "left4dead2.exe";

    public static final String GAME_DIRECTORY =
        "left4dead2";

    public static String[] getLaunchArguments() {
        return new String[] {
            "-windowed",
            "-w", String.valueOf(WIDTH),
            "-h", String.valueOf(HEIGHT),
            "-dxlevel", "90",
            "-novid",
            "+fps_max", String.valueOf(TARGET_FPS)
        };
    }

    public static String getStatus() {
        return "LEFT 4 DEAD 2 CONFIGURATION\n"
            + "Resolution: " + WIDTH + "x" + HEIGHT + "\n"
            + "Target FPS: " + TARGET_FPS + "\n"
            + "DirectX level: 90\n"
            + "Executable: " + EXECUTABLE;
    }
}
