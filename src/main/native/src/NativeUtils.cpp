#include "NativeUtils.h"

JNIEXPORT jstring JNICALL Java_www_mys_com_sbjni_utils_NativeUtils_getJniStr
        (JNIEnv *env, jclass type) {
    return env->NewStringUTF("This is jniStr.");
}
