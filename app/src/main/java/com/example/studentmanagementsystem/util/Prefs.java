package com.example.studentmanagementsystem.util;

import android.content.Context;

public class Prefs {
    private static final String PREFS_NAME = "student_prefs";
    private static final String KEY_TOKEN = "session_token";
    private static final String KEY_USER_TYPE = "user_type";

    public static void saveToken(Context ctx, String token) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_TOKEN, token).apply();
    }

    public static String getToken(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, null);
    }

    public static void saveUserType(Context ctx, String type) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_USER_TYPE, type).apply();
    }

    public static String getUserType(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_USER_TYPE, "");
    }

    public static void clear(Context ctx) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
