#include <jni.h>
#include <string>
#include <zlib.h>


const jint PADDING_VALUE = 10;

char *ConvertJByteaArrayToChars(JNIEnv *env, jbyteArray bytearray) {
    char *chars = NULL;
    jbyte *bytes;
    bytes = env->GetByteArrayElements(bytearray, 0);
    int chars_len = env->GetArrayLength(bytearray);
    chars = new char[chars_len + 1];
    memset(chars, 0, chars_len + 1);
    memcpy(chars, bytes, chars_len);
    chars[chars_len] = 0;
    env->ReleaseByteArrayElements(bytearray, bytes, 0);
    //
    return chars;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_ranger_zip_ZipCryptor_encrypt(JNIEnv *env, jobject clazz, jbyteArray bytes) {

    //start padding
    jbyte *arr = env->GetByteArrayElements(bytes, 0);
    jsize len = env->GetArrayLength(bytes);
    for (int i = 0; i < len; ++i) {
        arr[i] += PADDING_VALUE;
    }
    //copy jbytearray send java
    jbyteArray data = env->NewByteArray(len);
    env->SetByteArrayRegion(data, 0, len, arr);
    return data;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ranger_zip_ZipCryptor_decrypt(JNIEnv *env, jobject clazz, jbyteArray array) {
    //padding
    jbyte *arr = env->GetByteArrayElements(array, 0);
    jsize len = env->GetArrayLength(array);
    for (int i = 0; i < len; ++i) {
        arr[i] -= PADDING_VALUE;
    }
    jbyteArray new_array = env->NewByteArray(len);
    env->SetByteArrayRegion(new_array, 0, env->GetArrayLength(array), arr);
    char *d = ConvertJByteaArrayToChars(env, new_array);
    env->ReleaseByteArrayElements(new_array, arr, JNI_ABORT);
    return env->NewStringUTF(d);
}

