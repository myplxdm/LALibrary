/*
 * utils.h
 *
 *  Created on: 2015��4��18��
 *      Author: Administrator
 */

#ifndef UTILS_H_
#define UTILS_H_

#include "jni_config.h"

int splitString(string str, const char * pattern, vector<string> & vr);
string jstringToChar(JNIEnv* env, jstring jstr);
jstring ctoJstring(JNIEnv* env, const char* pat);
unsigned long getCurrentTime();
int analyseKV(string value, map<string,string> & values);
int isdigit(string phone);
int isPhone(string phones);
#endif /* UTILS_H_ */
