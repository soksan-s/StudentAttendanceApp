package com.example.studentmanagementsystem.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.studentmanagementsystem.model.User;
import com.google.gson.Gson;
import java.util.concurrent.TimeUnit;

public class Prefs {
    private static Prefs instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    // --- START: Define keys and session duration ---
    private static final String PREFS_NAME = "StudentAppPrefs";
    private static final String KEY_USER_DATA = "user_data";
    private static final String KEY_SESSION_EXPIRES_AT = "session_expires_at";

    // FIX 2: Define the missing SESSION_COOKIE key
    private static final String SESSION_COOKIE = "session_cookie";

    // Define how long a session should last. Here, it's set to 7 days.
    private static final long SESSION_DURATION_MINUTES = TimeUnit.DAYS.toMinutes(7);
    // --- END: Define keys and session duration ---

    private Prefs(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized Prefs getInstance(Context context) {
        if (instance == null) {
            instance = new Prefs(context);
        }
        return instance;
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_DATA, userJson);

        // --- START: NEW Logic to save the expiration time ---
        // Calculate the expiration timestamp from the current time.
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + TimeUnit.MINUTES.toMillis(SESSION_DURATION_MINUTES);
        editor.putLong(KEY_SESSION_EXPIRES_AT, expirationTimeMillis);
        // --- END: NEW Logic ---

        editor.apply();
    }

    // This method is called from LoginActivity
    public void saveSessionToken(String token) {
        // FIX 1: Declare the 'editor' variable
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Make sure we save it in the "sessionToken=THE_ACTUAL_TOKEN" format
        String cookieValue = "sessionToken=" + token;
        editor.putString(SESSION_COOKIE, cookieValue);
        editor.apply();
    }

    // This method is called from our AuthInterceptor
    public String getSessionToken() {
        // This should now return the full "sessionToken=..." string
        return sharedPreferences.getString(SESSION_COOKIE, null);
    }

    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER_DATA, null);
        if (userJson == null) {
            return null;
        }
        return gson.fromJson(userJson, User.class);
    }

    // --- START: NEW Method to check if the session is valid ---
    public boolean isUserLoggedIn() {
        // A user is logged in if their data exists AND the session has not expired.
        long expirationTime = sharedPreferences.getLong(KEY_SESSION_EXPIRES_AT, 0);
        long currentTime = System.currentTimeMillis();

        // Check if user data exists and if the current time is before the expiration time.
        return sharedPreferences.contains(KEY_USER_DATA) && currentTime < expirationTime;
    }
    // --- END: NEW Method ---

    public void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_DATA);
        editor.remove(KEY_SESSION_EXPIRES_AT); // Also clear the session timestamp
        editor.remove(SESSION_COOKIE); // Also clear the session cookie
        editor.apply();
    }
}


//package com.example.studentmanagementsystem.util;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import com.example.studentmanagementsystem.model.User;
//import com.google.gson.Gson;
//
//import java.util.concurrent.TimeUnit;
//
//public class Prefs {
//    private static Prefs instance;
//    private final SharedPreferences sharedPreferences;
//    private final Gson gson;
//
//    // --- START: Define keys and session duration ---
//    private static final String PREFS_NAME = "StudentAppPrefs";
//    private static final String KEY_USER_DATA = "user_data";
//    private static final String KEY_SESSION_EXPIRES_AT = "session_expires_at";
//
//    // Define how long a session should last. Here, it's set to 7 days.
//    private static final long SESSION_DURATION_MINUTES = TimeUnit.DAYS.toMinutes(7);
//    // --- END: Define keys and session duration ---
//
//    private Prefs(Context context) {
//        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        gson = new Gson();
//    }
//
//    public static synchronized Prefs getInstance(Context context) {
//        if (instance == null) {
//            instance = new Prefs(context);
//        }
//        return instance;
//    }
//
//    public void saveUser(User user) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        String userJson = gson.toJson(user);
//        editor.putString(KEY_USER_DATA, userJson);
//
//        // --- START: NEW Logic to save the expiration time ---
//        // Calculate the expiration timestamp from the current time.
//        long currentTimeMillis = System.currentTimeMillis();
//        long expirationTimeMillis = currentTimeMillis + TimeUnit.MINUTES.toMillis(SESSION_DURATION_MINUTES);
//        editor.putLong(KEY_SESSION_EXPIRES_AT, expirationTimeMillis);
//        // --- END: NEW Logic ---
//
//        editor.apply();
//    }
//
//    // ... inside your Prefs class
//
//    // This method is called from LoginActivity
//    public void saveSessionToken(String token) {
//        // Make sure we save it in the "sessionToken=THE_ACTUAL_TOKEN" format
//        String cookieValue = "sessionToken=" + token;
//        editor.putString(SESSION_COOKIE, cookieValue);
//        editor.apply();
//    }
//
//    // This method is called from our AuthInterceptor
//    public String getSessionToken() {
//        // This should now return the full "sessionToken=..." string
//        return sharedPreferences.getString(SESSION_COOKIE, null);
//    }
//
//
//    public User getUser() {
//        String userJson = sharedPreferences.getString(KEY_USER_DATA, null);
//        if (userJson == null) {
//            return null;
//        }
//        return gson.fromJson(userJson, User.class);
//    }
//
//    // --- START: NEW Method to check if the session is valid ---
//    public boolean isUserLoggedIn() {
//        // A user is logged in if their data exists AND the session has not expired.
//        long expirationTime = sharedPreferences.getLong(KEY_SESSION_EXPIRES_AT, 0);
//        long currentTime = System.currentTimeMillis();
//
//        // Check if user data exists and if the current time is before the expiration time.
//        return sharedPreferences.contains(KEY_USER_DATA) && currentTime < expirationTime;
//    }
//    // --- END: NEW Method ---
//
//    public void clearUserData() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove(KEY_USER_DATA);
//        editor.remove(KEY_SESSION_EXPIRES_AT); // Also clear the session timestamp
//        editor.apply();
//    }
//}
//
//
////com.example.studentmanagementsystem.util.Prefs.java
////package com.example.studentmanagementsystem.util;
////
////import android.content.Context;
////import android.content.SharedPreferences;
////import com.example.studentmanagementsystem.model.User;
////import com.google.gson.Gson;
////
////public class Prefs {
////    private static Prefs instance;
////    private final SharedPreferences prefs;
////    private final Gson gson = new Gson();
////
////    private Prefs(Context context) {
////        prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
////    }
////
////    public static synchronized Prefs getInstance(Context context) {
////        if (instance == null) {
////            instance = new Prefs(context.getApplicationContext());
////        }
////        return instance;
////    }
////
////    public void saveUser(User user) {
////        String userJson = gson.toJson(user);
////        prefs.edit().putString("LOGGED_IN_USER", userJson).apply();
////    }
////
////    public User getUser() {
////        String userJson = prefs.getString("LOGGED_IN_USER", null);
////        if (userJson == null) {
////            return null;
////        }
////        return gson.fromJson(userJson, User.class);
////    }
////
////    public void clear() {
////        prefs.edit().clear().apply();
////    }
////}
