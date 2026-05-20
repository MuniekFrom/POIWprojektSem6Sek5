package com.example.przychodnia_mobile;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(TokenManager tokenManager) {
        if (retrofit == null) {
            // HttpLoggingInterceptor pozwala podglądać zapytania w konsoli Logcat
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new JwtInterceptor(tokenManager))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create()) // Mapuje JSON ze Springa na obiekty Javy
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
