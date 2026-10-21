package com.l4d1.android;

public class NativeEngine {

    static {
        System.loadLibrary("l4d1_native");
    }

    public static native String getStatus();
}
