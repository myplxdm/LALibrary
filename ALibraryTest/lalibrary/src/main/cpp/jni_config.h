/*
 * sh_error.h
 *
 *  Created on: 2014骞�10鏈�8鏃�
 *      Author: Administrator
 */

#ifndef JNI_CONFIG_H_
#define JNI_CONFIG_H_

#include <jni.h>
#include <string>
#include <map>
#include <vector>
#include <errno.h>
#include <stdlib.h>
using namespace std;
#include <android/log.h>
#define LOGD(tag,a)  __android_log_write(ANDROID_LOG_DEBUG,tag,a)
#define LOGD2(tag,fmt,...) __android_log_print(ANDROID_LOG_DEBUG,tag,fmt,__VA_ARGS__)

#define TAG							"tearoom"
//#define WINDOWS
//
#ifdef WINDOWS
#define IP							"http://192.168.3.24:8080/api/"
#else
#define IP							"http://chajian.chalive.cn/api/"
#endif

#define VERSION						"1.0.5"
#define VERSION_URL					IP"version"
#define LOGIN_URL 					IP"login?v="
#define GET_VER_CODE_URL            IP"sms?"
#define REG_URL                     IP"reg?v="
#define GET_TEA_STUDIO_INFO         IP"shopinfo?"
#define BIND_URL                    IP"RegAuth?"

#define ERROR_NET_INIT_SOCKET		0x10
#define ERROR_NET_GET_HOST			0x11
#define ERROR_NET_SETOPT			0x12
#define ERROR_NET_CONN				0x13
#define ERROR_NET_SEND				0x14
#define ERROR_NET_RECV				0x15
#define ERROR_NET_HTTP_HEAD_END		0x16
#define ERROR_NET_HTTP_RESLUT		0x17 //
//
#define ERROR_NET_CONTENT			0x20
#define ERROR_NET_CREATE_LOG_USR	0x21
#define ERROR_NET_GET_LOGINAPI		0x22
#define ERROR_NET_PARAMS			0x23
//
#define ERROR_NO_NETWORK			0x30
#define ERROR_CALL_NUMBER			0x31

#endif /* JNI_CONFIG_H_ */
