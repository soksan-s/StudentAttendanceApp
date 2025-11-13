package com.example.studentmanagementsystem.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://student-attendance-system-f7ly.onrender.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // --- REMOVE THE HARDCODED TOKEN ---
            // String sessionToken = "ADMIN_SESSION_FOREVER"; // DELETE THIS LINE

            // Create the AuthInterceptor by passing the application context
            AuthInterceptor authInterceptor = new AuthInterceptor(context.getApplicationContext());

            // Build OkHttpClient and add BOTH interceptors
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor) // First, add the auth header from Prefs
                    .addInterceptor(logging)          // Then, log the request
                    .build();

            // Build Retrofit with the new client
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
