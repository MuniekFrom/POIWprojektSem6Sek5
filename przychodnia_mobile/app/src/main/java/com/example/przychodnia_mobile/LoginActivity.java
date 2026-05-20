package com.example.przychodnia_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = new TokenManager(this);
        apiService = ApiClient.getClient(tokenManager).create(ApiService.class);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Proszę uzupełnić wszystkie pola!", Toast.LENGTH_SHORT).show();
                } else {
                    handleLogin(username, password);
                }
            }
        });
    }

    private void handleLogin(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String jwtToken = response.body().getToken();
                    tokenManager.saveToken(jwtToken);

                    Toast.makeText(LoginActivity.this, "Zalogowano pomyślnie!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Token zapisany: " + jwtToken);

                } else {
                    Log.e(TAG, "Błąd logowania. Kod: " + response.code());
                    Toast.makeText(LoginActivity.this, "Niepoprawny login lub hasło", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Błąd sieci: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Błąd połączenia z serwerem!", Toast.LENGTH_LONG).show();
            }
        });
    }
}