package com.example.studentmanagementsystem.api;


import android.content.Context;

import com.example.studentmanagementsystem.util.Prefs;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://api/";

    public static Retrofit getClient(Context ctx, String baseUrl) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Interceptor to add token automatically
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .header("Accept", "application/json");

                    String token = Prefs.getToken(ctx);
                    if (token != null) {
                        builder.header("Cookie", "sessionToken=" + token);
                    }

                    Request request = builder.method(original.method(), original.body()).build();
                    return chain.proceed(request);
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
