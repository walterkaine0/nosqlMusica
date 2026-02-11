package com.example.optimusprime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity; // Обязательно импортировать

import com.google.firebase.auth.FirebaseAuth;

// 1. ОШИБКА: Нужно обязательно наследоваться от AppCompatActivity
public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEdit, passEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Убедись, что макет называется activity_register
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailEdit = findViewById(R.id.emailInput);
        passEdit = findViewById(R.id.passInput);

        // Кнопка регистрации (Белая капсула)
        findViewById(R.id.btnRegister).setOnClickListener(v -> registerUser());

        // Текст "Log in" внизу
        findViewById(R.id.txtGoToLogin).setOnClickListener(v -> {
            finish(); // Возвращает на экран логина
        });
    }

    private void registerUser() {
        String email = emailEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Password too short (min 6 chars)", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Успех: переходим на главный экран и очищаем историю переходов
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // Ошибка: выводим сообщение из Firebase
                        String error = task.getException() != null ? task.getException().getMessage() : "Error";
                        Toast.makeText(RegisterActivity.this, "Sign up failed: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
