package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.Locale;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    EditText nameEditText, matrixNoEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountButton;
    TextView organizationClickableTextView, organizationTextView, loginButtonTextView;
    ProgressBar progressBar;

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
        organizationClickableTextView = findViewById(R.id.organization_clickable_text_view);
        organizationTextView = findViewById(R.id.organization_text_view);
        loginButtonTextView = findViewById(R.id.login_text_view_btn);
        progressBar = findViewById(R.id.progress_bar);

        organizationClickableTextView.setOnClickListener(v-> showDropDownMenu());
        createAccountButton.setOnClickListener(v-> createAccount());
        loginButtonTextView.setOnClickListener(v-> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    void showDropDownMenu(){
        PopupMenu popupMenu = new PopupMenu(SignUpActivity.this, organizationClickableTextView);
        popupMenu.getMenu().add("Kolej Matrikulasi Labuan");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getTitle()=="Kolej Matrikulasi Labuan"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_LABUAN";
                organizationTextView.setText("Kolej Matrikulasi Labuan");
                return true;
            }
            return false;
        });
    }

    void createAccount(){
        String name = nameEditText.getText().toString().toUpperCase(Locale.ROOT);
        String matrixNo = matrixNoEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if(isValidated(name, matrixNo, email, password, confirmPassword)){
            createAccountInFirebase(name, matrixNo, email, password);
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
                userData.setOrganization(GlobalVariables.organization);
                userData.setName(name);
                userData.setMatrixNo(matrixNo);
                userData.setCredits(credits);
                DocumentReference documentReference = Utility.getDocumentReferenceUserData(currentUser);
                documentReference.set(userData).addOnCompleteListener(SignUpActivity.this, v-> {
                    Utility.showToast(SignUpActivity.this, "Successfully added");
                    currentUser.sendEmailVerification();
                    firebaseAuth.signOut();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                });
           }else{
                Utility.showToast(SignUpActivity.this, "Failed");
           }
        });
    }

    boolean isValidated(String name, String matrixNo, String email, String password, String confirmPassword){
        if(Objects.equals(GlobalVariables.organization, "")){
            organizationTextView.setError("Pick an organization");
            return false;
        }
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