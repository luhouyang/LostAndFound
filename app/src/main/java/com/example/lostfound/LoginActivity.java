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

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText nameEditText, matrixNoEditText, emailEditText, passwordEditText;
    Button loginBtn;
    TextView signUpTextViewBtn;
    ProgressBar progressBar;

    private Pattern p = Pattern.compile("MS[0-9]{10}");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEditText = findViewById(R.id.login_name_edit_text);
        matrixNoEditText = findViewById(R.id.login_matrix_no_edit_text);
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
        String name  = nameEditText.getText().toString();
        String matrixNo = matrixNoEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!correctLoginInfo(name, matrixNo, email, password)){
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

    boolean correctLoginInfo(String name, String matrixNo, String email, String password){
        if(Objects.equals(name, "")){
            nameEditText.setError("Enter name");
            return false;
        }
        if(!p.matcher(matrixNo).matches()){
            matrixNoEditText.setError("Enter matrix number");
            return false;
        }
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