package com.example.przychodnia_mobile;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register/patient")
    Call<Void> register(@Body RegisterRequest registerRequest);

    @GET("patients/me")
    Call<PatientResponse> getPatientProfile();

    @GET("doctors")
    Call<List<DoctorResponse>> getDoctors();

    // Endpoints from AppointmentController
    @GET("appointments/me")
    Call<List<AppointmentResponse>> getMyAppointments();

    @GET("appointments/available")
    Call<List<AppointmentSlotResponse>> getAvailableSlots(@Query("doctorId") Long doctorId);

    @POST("appointments/book")
    Call<AppointmentResponse> bookAppointment(@Body AppointmentRequest request);

    @DELETE("appointments/{appointmentId}")
    Call<Void> cancelAppointment(@Path("appointmentId") Long appointmentId);

    // Endpoint for global calendar summary
    @GET("appointments/calendar")
    Call<List<CalendarDayResponse>> getCalendar();
}
