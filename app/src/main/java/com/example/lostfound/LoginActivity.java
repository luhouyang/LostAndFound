package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginBtn;
    TextView signUpTextViewBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.login_email_edit_text);
        passwordEditText = findViewById(R.id.login_password_edit_text);
        loginBtn = findViewById(R.id.login_btn);
        signUpTextViewBtn = findViewById(R.id.sign_up_text_view_btn);
        progressBar = findViewById(R.id.progress_bar);

        loginBtn.setOnClickListener(v-> loginUser());
        signUpTextViewBtn.setOnClickListener(v-> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });
    }

    void loginUser(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!correctLoginInfo(email, password)){
            return;
        }

        loginAccountInFirebase(email, password);
    }

    void loginAccountInFirebase(String email, String password){
        changeProgressBar(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task->{
            changeProgressBar(false);
            if(task.isSuccessful()){
                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }else{
                    Utility.showToast(LoginActivity.this, "Email is not verified");
                }
            }else{
                Utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
            }
        });
    }

    boolean correctLoginInfo(String email, String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(password.length() < 6){
            passwordEditText.setError("At least 6 characters");
            return false;
        }
        return true;
    }

    void changeProgressBar(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
}