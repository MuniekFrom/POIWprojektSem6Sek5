package com.example.przychodnia_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientPanelActivity extends AppCompatActivity {

    private static final String TAG = "PatientPanelActivity";

    private Button btnLogout;
    private TextView tvPatientId, tvPatientFirstName, tvPatientLastName, tvPatientPesel, tvPatientPhone;
    
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_panel);

        tokenManager = new TokenManager(this);
        apiService = ApiClient.getClient(tokenManager).create(ApiService.class);

        btnLogout = findViewById(R.id.btnLogout);
        tvPatientId = findViewById(R.id.tvPatientId);
        tvPatientFirstName = findViewById(R.id.tvPatientFirstName);
        tvPatientLastName = findViewById(R.id.tvPatientLastName);
        tvPatientPesel = findViewById(R.id.tvPatientPesel);
        tvPatientPhone = findViewById(R.id.tvPatientPhone);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tokenManager.clearToken();
                Intent intent = new Intent(PatientPanelActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        fetchPatientData();
    }

    private void fetchPatientData() {
        apiService.getPatientProfile().enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PatientResponse patient = response.body();
                    updateUI(patient);
                } else {
                    Log.e(TAG, "Błąd pobierania danych: " + response.code());
                    Toast.makeText(PatientPanelActivity.this, getString(R.string.error_fetching_data), Toast.LENGTH_SHORT).show();
                    
                    if (response.code() == 401) {
                        // Token expired or invalid
                        tokenManager.clearToken();
                        startActivity(new Intent(PatientPanelActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                Log.e(TAG, "Błąd sieci: " + t.getMessage());
                Toast.makeText(PatientPanelActivity.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI(PatientResponse patient) {
        tvPatientId.setText(getString(R.string.patient_id, patient.getId()));
        tvPatientFirstName.setText(getString(R.string.patient_first_name, patient.getFirstName()));
        tvPatientLastName.setText(getString(R.string.patient_last_name, patient.getLastName()));
        tvPatientPesel.setText(getString(R.string.patient_pesel, patient.getPesel()));
        
        String phone = patient.getPhone() != null ? patient.getPhone() : getString(R.string.no_phone);
        tvPatientPhone.setText(getString(R.string.patient_phone, phone));
    }
}