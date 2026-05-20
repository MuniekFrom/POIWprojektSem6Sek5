package com.example.przychodnia_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText etFirstName, etLastName, etEmail, etPassword, etPesel, etPhone;
    private Button btnRegister;
    private TextView tvLogin;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = ApiClient.getClient(new TokenManager(this)).create(ApiService.class);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPesel = findViewById(R.id.etPesel);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to login
            }
        });
    }

    private void handleRegister() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String pesel = etPesel.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || pesel.isEmpty()) {
            Toast.makeText(this, "Proszę uzupełnić wymagane pola!", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password, pesel, phone);

        apiService.register(registerRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Rejestracja pomyślna! Możesz się zalogować.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.e(TAG, "Błąd rejestracji. Kod: " + response.code());
                    Toast.makeText(RegisterActivity.this, "Błąd rejestracji. Spróbuj ponownie.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Błąd sieci: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Błąd połączenia z serwerem!", Toast.LENGTH_LONG).show();
            }
        });
    }
}