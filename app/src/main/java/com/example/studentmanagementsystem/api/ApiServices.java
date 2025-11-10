package com.example.studentmanagementsystem.api;

import com.example.studentmanagementsystem.model.LoginResponse;
import com.example.studentmanagementsystem.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiServices {
    @FormUrlEncoded
    @POST("api/user") // <-- change to your actual API endpoint
    Call<LoginResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );
    @GET("api/user")
    Call<List<User>> getAllUsers();
}
