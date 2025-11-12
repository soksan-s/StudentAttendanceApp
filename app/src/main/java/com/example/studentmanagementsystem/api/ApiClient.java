package com.example.studentmanagementsystem.api;

import java.util.concurrent.TimeUnit;
import okhttp3.CookieJar; // Import
import okhttp3.JavaNetCookieJar; // Import
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.net.CookieManager; // Import

public class ApiClient {

    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://student-attendance-system-f7ly.onrender.com/";

    public static Retrofit getSimpleClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // --- START OF COOKIE MANAGEMENT ---
            // Create a CookieManager to handle session cookies
            CookieManager cookieManager = new CookieManager();
            CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
            // --- END OF COOKIE MANAGEMENT ---

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .cookieJar(cookieJar) // Add the cookie jar to OkHttpClient
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}





//package com.example.studentmanagementsystem.api;
//
//import okhttp3.OkHttpClient;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class ApiClient {
//
//    private static Retrofit retrofit = null;
//    private static final String BASE_URL = "https://student-attendance-system-f7ly.onrender.com/";
//
//    // Keep only the simple client, as authentication is handled on a per-call basis for login.
//    public static Retrofit getSimpleClient() {
//        // A common issue: a shared retrofit instance can cause problems.
//        // Let's ensure it's built correctly. It's better to create a single OkHttpClient.
//        if (retrofit == null) {
//            OkHttpClient client = new OkHttpClient.Builder().build(); // Basic client
//
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(client)
//                    .build();
//        }
//        return retrofit;
//    }
//}
