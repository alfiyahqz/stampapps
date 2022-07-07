package com.alfiyah.stamp

import android.content.Context
import android.content.SharedPreferences

class Pref(context: Context){
    private val PREF_FILENAME = "com.alfiyah.stamp"

    private val USERNAME ="USER_USERNAME"
    private val EMAIL ="USER_EMAIL"
    private val PASSWORD ="USER_PASSWORD"
    private val IMAGE = "USER_IMAGE"
    private val DOC_ID="DOC_ID"

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_FILENAME, 0)

    var userUsername: String get() = pref.getString(USERNAME, "").toString()
        set(value) = pref.edit().putString(USERNAME, value).apply()

    var userEmail: String get() = pref.getString(EMAIL, "").toString()
        set(value) = pref.edit().putString(EMAIL, value).apply()

    var userPassword: String get() = pref.getString(PASSWORD, "").toString()
        set(value) = pref.edit().putString(PASSWORD, value).apply()

    var userImage: String get() = pref.getString(IMAGE, "").toString()
        set(value) = pref.edit().putString(IMAGE, value).apply()

    var docId:String get() = pref.getString(DOC_ID,"").toString()
    set(value) = pref.edit().putString(DOC_ID,value).apply()

}