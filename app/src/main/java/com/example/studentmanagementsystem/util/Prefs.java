// in com.example.studentmanagementsystem.util.Prefs.java
package com.example.studentmanagementsystem.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.studentmanagementsystem.model.User;
import com.google.gson.Gson;

public class Prefs {
    private static Prefs instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    private Prefs(Context context) {
        prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
    }

    public static synchronized Prefs getInstance(Context context) {
        if (instance == null) {
            instance = new Prefs(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        prefs.edit().putString("LOGGED_IN_USER", userJson).apply();
    }

    public User getUser() {
        String userJson = prefs.getString("LOGGED_IN_USER", null);
        if (userJson == null) {
            return null;
        }
        return gson.fromJson(userJson, User.class);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
