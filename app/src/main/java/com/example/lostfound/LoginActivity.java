package com.example.lostfound;

import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText nameEditText, matrixNoEditText, emailEditText, passwordEditText;
    Button loginBtn;
    TextView signUpTextViewBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //staff or student
        GlobalVariables.checkPattern = Pattern.compile("[A-Z]{2}[0-9]{10}");

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
        String matrixNo = matrixNoEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
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
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null && currentUser.isEmailVerified()){
                    GlobalVariables.currentUser = currentUser;
                    GlobalVariables.currentUserID = GlobalVariables.currentUser.getUid();
                    GlobalVariables.userDataDocRef = Utility.getDocumentReferenceUserData(GlobalVariables.currentUser);
                    GlobalVariables.userDataDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot userData, @Nullable FirebaseFirestoreException error) {
                            GlobalVariables.name = userData.getString("name");
                            GlobalVariables.matrixNo = userData.getString("matrixNo");
                            GlobalVariables.key = userData.getString("key");
                            GlobalVariables.organization = userData.getString("organization");
                            GlobalVariables.identification = userData.getString("identification");
                            //GlobalVariables.userCodeNo = userData.getString("userCodeNo");
                            GlobalVariables.credits = userData.getLong("credits").intValue();
                        }
                    });
                    GlobalVariables.firebaseStorage = FirebaseStorage.getInstance();
                    GlobalVariables.storageReference = GlobalVariables.firebaseStorage.getReference();
                    GlobalVariables.reportedItem = false;
                    GlobalVariables.complainItem = false;

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else{
                    Utility.showToast(LoginActivity.this, "Email is not verified");
                }
            }else{
                Utility.showToast(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
            }
        });
    }

    boolean correctLoginInfo(String name, String matrixNo, String email, String password){
        if(Objects.equals(name, "")){
            nameEditText.setError("Enter name");
            return false;
        }
        if(!GlobalVariables.checkPattern.matcher(matrixNo).matches()){
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