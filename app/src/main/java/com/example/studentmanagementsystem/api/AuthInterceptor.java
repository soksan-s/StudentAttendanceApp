package com.example.studentmanagementsystem.api;

import android.content.Context; // Import Context
import androidx.annotation.NonNull;
import com.example.studentmanagementsystem.util.Prefs; // Import Prefs
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    // The constructor now takes a Context
    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // Get the original request from the chain.
        Request originalRequest = chain.request();

        // --- DYNAMICALLY GET THE TOKEN FROM PREFS ---
        String sessionToken = Prefs.getInstance(context).getSessionToken();

        // If the sessionToken is null or empty, proceed without adding the header.
        if (sessionToken == null || sessionToken.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        // Build a new request, adding the "Cookie" header with the saved session token.
        Request newRequest = originalRequest.newBuilder()
                .header("Cookie", sessionToken) // Prefs already stores it as "sessionToken=..."
                .build();

        // Proceed with the new, modified request.
        return chain.proceed(newRequest);
    }
}
