package com.example.przychodnia_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientPanelActivity extends AppCompatActivity {

    private static final String TAG = "PatientPanelActivity";

    private TextView tvPatientId, tvPatientFirstName, tvPatientLastName, tvPatientPesel, tvPatientPhone;
    private RecyclerView rvMyVisits, rvDoctors, rvTimeSlots;
    private TextView tvNoVisits, tvNoDoctors, tvSlotsPlaceholder;
    private ApiService apiService;
    private TokenManager tokenManager;
    private Button btnLogout;
    
    private DoctorResponse selectedDoctor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force light mode to fix dark background issues and match design
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_panel);

        tokenManager = new TokenManager(this);
        apiService = ApiClient.getClient(tokenManager).create(ApiService.class);

        // 1. Initialize UI components
        tvPatientId = findViewById(R.id.tvPatientId);
        tvPatientFirstName = findViewById(R.id.tvPatientFirstName);
        tvPatientLastName = findViewById(R.id.tvPatientLastName);
        tvPatientPesel = findViewById(R.id.tvPatientPesel);
        tvPatientPhone = findViewById(R.id.tvPatientPhone);
        btnLogout = findViewById(R.id.btnLogout);

        rvMyVisits = findViewById(R.id.rvMyVisits);
        tvNoVisits = findViewById(R.id.tvNoVisits);
        rvDoctors = findViewById(R.id.rvDoctors);
        tvNoDoctors = findViewById(R.id.tvNoDoctors);
        rvTimeSlots = findViewById(R.id.rvTimeSlots);
        tvSlotsPlaceholder = findViewById(R.id.tvSlotsPlaceholder);

        btnLogout.setOnClickListener(v -> {
            tokenManager.clearToken();
            Intent intent = new Intent(PatientPanelActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 2. Load data from database
        loadPatientProfile();
        loadMyVisits();
        loadDoctors();
    }

    private void loadPatientProfile() {
        apiService.getPatientProfile().enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PatientResponse patient = response.body();
                    tvPatientId.setText(getString(R.string.patient_id, patient.getId()));
                    tvPatientFirstName.setText(getString(R.string.patient_first_name, patient.getFirstName()));
                    tvPatientLastName.setText(getString(R.string.patient_last_name, patient.getLastName()));
                    tvPatientPesel.setText(getString(R.string.patient_pesel, patient.getPesel()));
                    String phone = patient.getPhone() != null ? patient.getPhone() : getString(R.string.no_phone);
                    tvPatientPhone.setText(getString(R.string.patient_phone, phone));
                } else if (response.code() == 401) {
                    handleUnauthorized();
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                Toast.makeText(PatientPanelActivity.this, getString(R.string.error_fetching_data), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMyVisits() {
        apiService.getMyAppointments().enqueue(new Callback<List<AppointmentResponse>>() {
            @Override
            public void onResponse(Call<List<AppointmentResponse>> call, Response<List<AppointmentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AppointmentResponse> appointments = response.body();
                    if (appointments.isEmpty()) {
                        tvNoVisits.setVisibility(View.VISIBLE);
                        rvMyVisits.setVisibility(View.GONE);
                    } else {
                        tvNoVisits.setVisibility(View.GONE);
                        rvMyVisits.setVisibility(View.VISIBLE);
                        rvMyVisits.setAdapter(new VisitsAdapter(appointments, appointmentId -> cancelVisit(appointmentId)));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentResponse>> call, Throwable t) {
                Log.e(TAG, "Error loading visits", t);
            }
        });
    }

    private void cancelVisit(Long appointmentId) {
        apiService.cancelAppointment(appointmentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PatientPanelActivity.this, getString(R.string.visit_cancelled), Toast.LENGTH_SHORT).show();
                    loadMyVisits();
                    if (selectedDoctor != null) loadAvailableSlots(selectedDoctor);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PatientPanelActivity.this, getString(R.string.visit_cancel_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDoctors() {
        apiService.getDoctors().enqueue(new Callback<List<DoctorResponse>>() {
            @Override
            public void onResponse(Call<List<DoctorResponse>> call, Response<List<DoctorResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DoctorResponse> doctors = response.body();
                    if (doctors.isEmpty()) {
                        tvNoDoctors.setVisibility(View.VISIBLE);
                        rvDoctors.setVisibility(View.GONE);
                    } else {
                        tvNoDoctors.setVisibility(View.GONE);
                        rvDoctors.setVisibility(View.VISIBLE);
                        rvDoctors.setAdapter(new DoctorsAdapter(doctors, doctor -> {
                            selectedDoctor = doctor;
                            loadAvailableSlots(selectedDoctor);
                        }));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
                Log.e(TAG, "Error loading doctors", t);
            }
        });
    }

    private void loadAvailableSlots(DoctorResponse doctor) {
        tvSlotsPlaceholder.setVisibility(View.GONE);
        rvTimeSlots.setVisibility(View.VISIBLE);
        apiService.getAvailableSlots(doctor.getId()).enqueue(new Callback<List<AppointmentSlotResponse>>() {
            @Override
            public void onResponse(Call<List<AppointmentSlotResponse>> call, Response<List<AppointmentSlotResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AppointmentSlotResponse> slots = response.body();
                    // Inject doctor info into slot objects for the card display
                    for (AppointmentSlotResponse slot : slots) {
                        slot.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
                        slot.setDoctorSpecialization(doctor.getSpecialization());
                    }
                    rvTimeSlots.setAdapter(new SlotsAdapter(slots, (slot, reason) -> bookVisit(slot.getId(), reason)));
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentSlotResponse>> call, Throwable t) {
                Toast.makeText(PatientPanelActivity.this, getString(R.string.error_loading_slots), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookVisit(Long slotId, String reason) {
        AppointmentRequest request = new AppointmentRequest(slotId, reason);
        apiService.bookAppointment(request).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PatientPanelActivity.this, getString(R.string.visit_booked_success), Toast.LENGTH_SHORT).show();
                    loadMyVisits();
                    if (selectedDoctor != null) loadAvailableSlots(selectedDoctor);
                } else {
                    Toast.makeText(PatientPanelActivity.this, getString(R.string.visit_book_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                Toast.makeText(PatientPanelActivity.this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUnauthorized() {
        tokenManager.clearToken();
        Intent intent = new Intent(PatientPanelActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
