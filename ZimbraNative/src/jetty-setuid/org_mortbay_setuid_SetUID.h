/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_mortbay_setuid_SetUID */

#ifndef _Included_org_mortbay_setuid_SetUID
#define _Included_org_mortbay_setuid_SetUID
#ifdef __cplusplus
extern "C" {
#endif
#undef org_mortbay_setuid_SetUID_OK
#define org_mortbay_setuid_SetUID_OK 0L
#undef org_mortbay_setuid_SetUID_ERROR
#define org_mortbay_setuid_SetUID_ERROR -1L

JNIEXPORT jint JNICALL Java_org_mortbay_setuid_SetUID_setuid
  (JNIEnv *, jclass, jint);

JNIEXPORT jint JNICALL Java_org_mortbay_setuid_SetUID_setumask
  (JNIEnv *, jclass, jint);

JNIEXPORT jint JNICALL Java_org_mortbay_setuid_SetUID_setgid
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
