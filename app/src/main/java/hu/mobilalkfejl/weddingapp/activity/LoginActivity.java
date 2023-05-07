package hu.mobilalkfejl.weddingapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import hu.mobilalkfejl.weddingapp.R;

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getName();

    EditText emailInput;
    EditText passwordInput;

    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);

        preferences = getSharedPreferences(Objects.requireNonNull(LoginActivity.class.getPackage()).toString(), MODE_PRIVATE);
        emailInput.setText(preferences.getString("email_value", ""));
        passwordInput.setText(preferences.getString("password_value", ""));

        firebaseAuth = FirebaseAuth.getInstance();

        requestPermissions(new String[]{ Manifest.permission.POST_NOTIFICATIONS }, 0);
    }

    private void launchWeddings() {
        startActivity(new Intent(this, WeddingsActivity.class));
    }

    public void login(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(LoginActivity.this, "Minden meő kitöltése kötelező!", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(success ->{
                    Log.d(LOG_TAG, "Sikeres bejelentkezés! (e-mail és jelszó)");
                    launchWeddings();
                })
                .addOnFailureListener(fail -> {
                    Log.d(LOG_TAG, "Sikertelen bejelentkezés! (e-mail és jelszó)");
                    Toast.makeText(LoginActivity.this, "Sikertelen bejelentkezés, kérlek próbáld meg újra!", Toast.LENGTH_LONG).show();
                });
    }

    public void anonymusLogin(View view) {
        firebaseAuth.signInAnonymously()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Sikeres bejelentkezés! (vendég)");
                    launchWeddings();
                })
                .addOnFailureListener(fail -> {
                    Log.d(LOG_TAG, "Sikertelen bejelentkezés! (vendég)");
                    Toast.makeText(LoginActivity.this, "Sikertelen bejelentkezés, kérlek próbáld meg újra!", Toast.LENGTH_LONG).show();
                });
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email_value", emailInput.getText().toString());
        editor.putString("password_value", passwordInput.getText().toString());
        editor.apply();
    }
}