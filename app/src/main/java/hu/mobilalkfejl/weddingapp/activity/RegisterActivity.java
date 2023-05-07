package hu.mobilalkfejl.weddingapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.mobilalkfejl.weddingapp.R;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();

    EditText emailInput;
    EditText passwordInput;
    EditText passwordAgainInput;

    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        passwordAgainInput = findViewById(R.id.input_password_again);

        preferences = getSharedPreferences(Objects.requireNonNull(RegisterActivity.class.getPackage()).toString(), MODE_PRIVATE);
        emailInput.setText(preferences.getString("email", ""));

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void register(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String passwordAgain = passwordAgainInput.getText().toString();

        if ("".equals(email) || "".equals(password) || "".equals(passwordAgain)) {
            Toast.makeText(RegisterActivity.this, "Minden meő kitöltése kötelező!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkValidEmail(email)) {
            Toast.makeText(RegisterActivity.this, "A megadott e-mail cím formátima nem megfelelő!", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "A megadott jelszónak legalább 6 karakter hosszúnak kell lennie!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordAgain)) {
            Toast.makeText(RegisterActivity.this, "A megadott jelszavak nem egyeznek meg!", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Sikeres regisztráció!");
                    finish();
                })
                .addOnFailureListener(fail -> {
                    Log.d(LOG_TAG, "Sikertelen regisztráció!");
                    Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció, kérlek póbáld meg újra!", Toast.LENGTH_LONG).show();
                });
    }

    public boolean checkValidEmail(String email) {
        Pattern p = Pattern.compile("^[0-9a-z.-]+@([0-9a-z-]+\\.)+[a-z]{2,4}$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", emailInput.getText().toString());
        editor.apply();
    }
}