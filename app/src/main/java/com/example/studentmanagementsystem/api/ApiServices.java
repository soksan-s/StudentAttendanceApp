package com.example.studentmanagementsystem.api;

import com.example.studentmanagementsystem.model.LoginRequest;
// We will create this new LoginResponseWrapper class
import com.example.studentmanagementsystem.model.LoginResponseWrapper;
import com.example.studentmanagementsystem.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiServices {

    /**
     * Step 1: Login with email and password.
     * The server will respond with a success message and set a session cookie.
     * We change the response type to `Void` because we only care about the success/failure and the cookie.
     */
    @POST("api/login") // Use your actual login endpoint
    Call<Void> login(@Body LoginRequest loginRequest);

    /**
     * Step 2: Get the current user's data using the session cookie.
     * Retrofit will automatically handle sending the cookie that was set in Step 1.
     * The response from `/api/me` is nested, so we need a wrapper class.
     */
    @GET("api/me")
    Call<LoginResponseWrapper> getMe();
}
