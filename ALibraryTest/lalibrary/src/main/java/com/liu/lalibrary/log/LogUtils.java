/*
 * Copyright (C) 2013 Google Inc. All Rights Reserved. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.liu.lalibrary.log;

import android.util.Log;

/**
 * Provides a simple wrapper to control logging in development vs production
 * environment. This library should only use the wrapper methods that this class
 * provides.
 */
public class LogUtils
{
	public static boolean DEBUG = true;
	private static final String LOG_PREFIX = "ccl_";
	private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
	private static final int MAX_LOG_TAG_LENGTH = 23;

	private LogUtils()
	{
	}
	
	private static String getFunctionName(Class<?> cls)
	{
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		
		if (sts == null)
		{
			return null;
		}

		for (StackTraceElement st : sts)
		{
			if (st.isNativeMethod())
			{
				continue;
			}

			if (st.getClassName().equals(Thread.class.getName()))
			{
				continue;
			}			
			if (!st.getClassName().equals(cls.getName()))
			{
				continue;
			}

			return "[" + Thread.currentThread().getId() + ": "
					+ st.getFileName() + ":" + st.getLineNumber() + "]";
		}

		return null;
	}

	public static String makeLogTag(String str)
	{
		if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH)
		{
			return LOG_PREFIX
					+ str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH
							- 1);
		}

		return LOG_PREFIX + str;
	}

	/**
	 * WARNING: Don't use this when obfuscating class names with Proguard!
	 */
	public static String makeLogTag(Class<?> cls)
	{
		return makeLogTag(cls.getSimpleName());
	}

	public static void LOGD(final Class<?> cls, String message)
	{
		if (DEBUG || Log.isLoggable(cls.getSimpleName(), Log.DEBUG))
		{
			String name = getFunctionName(cls);
			String ls = (name == null ? message.toString() : (name + " - " + message));
			Log.d(cls.getSimpleName(), ls);
		}
	}
	
	public static void LOGD(final Class<?> cls, final String tag, String message)
	{
		if (DEBUG || Log.isLoggable(tag, Log.DEBUG))
		{
			String name = getFunctionName(cls);
			String ls = (name == null ? message.toString() : (name + " - " + message));
			Log.d(tag, ls);
		}
	}

	public static void LOGD(final Class<?> cls, final String tag, String message, Throwable cause)
	{
		if (DEBUG || Log.isLoggable(tag, Log.DEBUG))
		{
			String name = getFunctionName(cls);
			String ls = (name == null ? message.toString() : (name + " - " + message));
			Log.d(tag, ls, cause);
		}
	}

	public static void LOGV(final Class<?> cls, final String tag, String message)
	{
		if (DEBUG && Log.isLoggable(tag, Log.VERBOSE))
		{
			String name = getFunctionName(cls);
			String ls = (name == null ? message.toString() : (name + " - " + message));
			Log.v(tag, ls);
		}
	}

	public static void LOGV(final Class<?> cls, final String tag, String message, Throwable cause)
	{
		if (DEBUG && Log.isLoggable(tag, Log.VERBOSE))
		{
			String name = getFunctionName(cls);
			String ls = (name == null ? message.toString() : (name + " - " + message));
			Log.v(tag, ls, cause);
		}
	}

	public static void LOGI(final Class<?> cls, String message)
	{
		String name = getFunctionName(cls);
		String ls = (name == null ? message.toString() : (name + " - " + message));
		Log.i(cls.getSimpleName(), ls);
	}
	
	public static void LOGI(final Class<?> cls, final String tag, String message)
	{
		String name = getFunctionName(cls);
		String ls = (name == null ? message.toString() : (name + " - " + message));
		Log.i(tag, ls);
	}

	public static void LOGI(final Class<?> cls, final String tag, String message, Throwable cause)
	{
		String name = getFunctionName(cls);
		String ls = (name == null ? message.toString() : (name + " - " + message));
		Log.i(tag, ls, cause);
	}

	public static void LOGW(final Class<?> cls, final String tag, String message)
	{
		String name = getFunctionName(cls);
		String ls = (name == null ? message.toString() : (name + " - " + message));
		Log.w(tag, ls);
	}

	public static void LOGW(final Class<?> cls, final String tag, String message, Throwable cause)
	{
		String name = getFunctionName(cls);
		String ls = (name == null ? message.toString() : (name + " - " + message));
		Log.w(tag, ls, cause);
	}

	public static void LOGE(final Class<?> cls, final String tag, String message)
	{
		String name = getFunctionName(cls);
		String ls = (name == null ? message.toString() : (name + " - " + message));
		Log.e(tag, ls);
	}

	public static void LOGE(final Class<?> cls, final String tag, String message, Throwable cause)
	{
		String name = getFunctionName(cls);
		String ls = (name == null ? message.toString() : (name + " - " + message));
		Log.e(tag, ls, cause);
	}
	
	public static void LOGE(final Class<?> cls, String message)
	{
		String name = getFunctionName(cls);
		String ls = (name == null ? message.toString() : (name + " - " + message));
		Log.e(cls.getSimpleName(), ls);
	}

}
