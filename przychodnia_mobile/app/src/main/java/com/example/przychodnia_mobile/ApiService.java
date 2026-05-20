package com.example.przychodnia_mobile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register/patient")
    Call<Void> register(@Body RegisterRequest registerRequest);

    @GET("patients/me")
    Call<PatientResponse> getPatientProfile();
}