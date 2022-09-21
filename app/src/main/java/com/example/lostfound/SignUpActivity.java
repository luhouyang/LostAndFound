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

import io.grpc.okhttp.internal.Util;

public class SignUpActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountButton;
    TextView loginButtonTextView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.sign_up_email_edit_text);
        passwordEditText = findViewById(R.id.sign_up_password_edit_text);
        confirmPasswordEditText = findViewById(R.id.sign_up_confirm_password_edit_text);
        createAccountButton = findViewById(R.id.create_account_btn);
        loginButtonTextView = findViewById(R.id.login_text_view_btn);
        progressBar = findViewById(R.id.progress_bar);

        createAccountButton.setOnClickListener(v-> createAccount());
        loginButtonTextView.setOnClickListener(v-> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    void createAccount(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if(isValidated(email, password, confirmPassword)){
            createAccountInFirebase(email, password);
        }else{
            return;
        }
    }

    void createAccountInFirebase(String email, String password){
        changeProgressBar(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
           changeProgressBar(false);
            if(task.isSuccessful()){
               Utility.showToast(SignUpActivity.this, "Account created successfully, verify email");
               firebaseAuth.getCurrentUser().sendEmailVerification();
               firebaseAuth.signOut();
               finish();
           }else{
               Utility.showToast(SignUpActivity.this, "Failed");
           }
        });
    }

    boolean isValidated(String email, String password, String confirmPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(password.length() < 6){
            passwordEditText.setError("At least 6 characters");
            return false;
        }
        if(!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Doesn't match password");
            return false;
        }
        return true;
    }

    void changeProgressBar(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            createAccountButton.setVisibility(View.VISIBLE);
        }
    }
}