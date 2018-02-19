#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_lan_robonet_ampru_remote_MainActivity_stringFromJNI(JNIEnv *env, jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
