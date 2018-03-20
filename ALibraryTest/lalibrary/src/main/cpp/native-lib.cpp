#include "aes.h"
#include "utils.h"
#include <stdlib.h>

#define WEB_KEY                         "45c5-4d3e-b431-5"
//

int g_err_no = 0;
string g_str_err;
#define BUFFER_SIZE                     8 * 1024

void encodeUrl(const char * url, const char * param, const char * ver, char * result, int resultSize)
{
    char in[resultSize];
    memset(in, 0, resultSize);
    int len = strlen(param);
    len = aes_ecb_encrypt_PKCS5Padding((char *)param, len, result, (char *)WEB_KEY, 128);
    bin2Hex(result, len, in, resultSize);
    if(ver)
    {
        sprintf(result, "%sv=%s&sign=%s", url, ver, in);
    }else
    {
        sprintf(result, "%ssign=%s", url, in);
    }
}

extern "C" JNIEXPORT jstring JNICALL Java_com_liu_app_JniApi_reqEncode(JNIEnv *env, jobject instance, jstring url_,
                                                                       jstring ver_, jobjectArray keys, jobjectArray values)
{
    const char * url = env->GetStringUTFChars(url_, 0);
    int keyLen = env->GetArrayLength(keys);
    int valLen = env->GetArrayLength(values);
    if (keyLen != valLen)return false;
    string buffer;
    jstring key,value;
    for(int i = 0;i < keyLen;i++)
    {
        key = (jstring)env->GetObjectArrayElement(keys, i);
        value = (jstring)env->GetObjectArrayElement(values, i);
        buffer.append(env->GetStringUTFChars(key, NULL));
        buffer.append("=");
        buffer.append(env->GetStringUTFChars(value, NULL));
        if (i < keyLen - 1)
        {
            buffer.append("&");
        }
    }

    char result[BUFFER_SIZE];
    encodeUrl(url, buffer.c_str(), ver_ ? env->GetStringUTFChars(ver_, 0) : NULL, result, BUFFER_SIZE);

    env->ReleaseStringUTFChars(url_, url);

    return ctoJstring(env, result);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_liu_app_JniApi_decodeResult(JNIEnv *env, jobject instance, jstring str_)
{
    const char *str = env->GetStringUTFChars(str_, 0);

    char in[BUFFER_SIZE];
    char out[BUFFER_SIZE];
    memset(in, 0, BUFFER_SIZE);
    memset(out, 0, BUFFER_SIZE);
    int len = hex2bin(str, (unsigned char *) in, strlen(str));
    aes_ecb_decrypt_PKCS5Padding(in, len, out, (char *) WEB_KEY, 128);

    env->ReleaseStringUTFChars(str_, str);

    return ctoJstring(env, out);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_liu_app_JniApi_getString(JNIEnv *env, jobject instance, jstring value_)
{
    const char *value = env->GetStringUTFChars(value_, 0);

    char in[BUFFER_SIZE];
    char out[BUFFER_SIZE];
    memset(in, 0, BUFFER_SIZE);
    memset(out, 0, BUFFER_SIZE);
    int len = strlen(value);
    len = aes_ecb_encrypt_PKCS5Padding((char *) value, len, out, (char *) WEB_KEY, 128);
    bin2Hex(out, len, in, BUFFER_SIZE);

    env->ReleaseStringUTFChars(value_, value);

    return ctoJstring(env, in);
}