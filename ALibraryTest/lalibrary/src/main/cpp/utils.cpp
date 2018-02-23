/*
 * utils.cpp
 *
 *  Created on: 2015��4��18��
 *      Author: Administrator
 */
#include "utils.h"
#include <sys/time.h>

int splitString(string str, const char * pattern, vector<string> & vr)
{
	string::size_type pos;
	str += pattern;
	int size = str.size();
	int psize = strlen(pattern);
	for (int i = 0; i < size; i++)
	{
		pos = str.find(pattern, i);
		if (pos < size)
		{
			vr.push_back(str.substr(i, pos - i));
			i = pos + psize - 1;
		}
	}
	return vr.size();
}

string jstringToChar(JNIEnv* env, jstring jstr)
{
	char * rtn = NULL;
	string str;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0)
	{
		rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
		str = rtn;
		free(rtn);
	}
	env->ReleaseByteArrayElements(barr, ba, 0);
	env->DeleteLocalRef(strencode);
	env->DeleteLocalRef(clsstring);
	return str;
}

jstring ctoJstring(JNIEnv* env, const char* pat)
{
	jclass strClass = env->FindClass("java/lang/String");
	jmethodID ctorID = env->GetMethodID(strClass, "<init>",
			"([BLjava/lang/String;)V");
	jbyteArray bytes = env->NewByteArray(strlen(pat));
	env->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*) pat);
	jstring encoding = env->NewStringUTF("utf-8");
	jstring str = (jstring) env->NewObject(strClass, ctorID, bytes, encoding);
	//
	env->DeleteLocalRef(strClass);
	env->DeleteLocalRef(encoding);
	env->ReleaseByteArrayElements(bytes, (jbyte*) pat, JNI_COMMIT);
	return str;
}

unsigned long getCurrentTime()
{
   struct timeval tv;
   gettimeofday(&tv,NULL);
   return tv.tv_sec /1000 + tv.tv_usec / 1000 / 1000;
}

int analyseKV(string value, map<string,string> & values)
{
	vector<string> list;
	int len = splitString(value, "&", list);
	int pos;
	for (int i = 0; i < len; i++)
	{
		pos = list[i].find('=');
		if (pos != -1)
		{
			values[list[i].substr(0, pos)] = list[i].substr(pos + 1);
		}
	}
	return values.size();
}

int isdigit(string phone)
{
	int size = phone.length();
	for(int i=0;i < size;i++)
	{
		if(!(phone[i] >= '0' && phone[i]<='9'))
		{
			return 0;
		}
	}
	return 1;
}

int isPhone(string phones)
{
	int size = phones.length();
	for(int i=0;i < size;i++)
	{
		if(!((phones[i] >= '0' && phones[i]<='9') || phones[i] == '_'))
		{
			return 0;
		}
	}
	return 1;
}
