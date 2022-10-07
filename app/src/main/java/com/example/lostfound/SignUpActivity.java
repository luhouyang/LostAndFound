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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText nameEditText, matrixNoEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountButton;
    TextView loginButtonTextView;
    ProgressBar progressBar;

    private Pattern p = Pattern.compile("[A-Z]{2}[0-9]{10}");
    private int credits = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.sign_up_name_edit_text);
        matrixNoEditText = findViewById(R.id.sign_up_matrix_no_edit_text);
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
        String name = nameEditText.getText().toString().toUpperCase(Locale.ROOT);
        String matrixNo = matrixNoEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if(isValidated(name, matrixNo, email, password, confirmPassword)){
            createAccountInFirebase(name, matrixNo, email, password);
        }else{
            return;
        }
    }

    void createAccountInFirebase(String name, String matrixNo, String email, String password){
        changeProgressBar(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
            changeProgressBar(false);
            if(task.isSuccessful()){
                Utility.showToast(SignUpActivity.this, "Account created successfully, verify email");
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                UserData userData = new UserData();
                userData.setName(name);
                userData.setMatrixNo(matrixNo);
                userData.setCredits(credits);
                DocumentReference documentReference = Utility.getDocumentReferenceUserData(currentUser);
                documentReference.set(userData).addOnCompleteListener(SignUpActivity.this, v-> {
                    Utility.showToast(SignUpActivity.this, "Successfully added");
                    currentUser.sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                });
           }else{
                Utility.showToast(SignUpActivity.this, "Failed");
           }
        });
    }

    boolean isValidated(String name, String matrixNo, String email, String password, String confirmPassword){
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