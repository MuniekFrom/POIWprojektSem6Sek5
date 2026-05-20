package com.example.przychodnia_mobile;

import androidx.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class JwtInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public JwtInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = tokenManager.getToken();

        if (token != null && !token.isEmpty()) {
            Request authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authenticatedRequest);
        }

        return chain.proceed(originalRequest);
    }
}
