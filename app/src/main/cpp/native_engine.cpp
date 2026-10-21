#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_l4d1_android_NativeEngine_getStatus(
    JNIEnv* env,
    jclass clazz
) {
    return env->NewStringUTF(
        "L4D1 ARM64 Native Engine: READY"
    );
}
