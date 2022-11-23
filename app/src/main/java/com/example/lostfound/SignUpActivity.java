package com.example.lostfound;

import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText nameEditText, matrixNoEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountButton;
    TextView organizationClickableTextView, identificationClickableTextView, organizationTextView, identificationTextView, loginButtonTextView;
    ProgressBar progressBar;

    private int credits = 0;
    private boolean clicked = false;

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
        identificationClickableTextView = findViewById(R.id.identification_clickable_text_view);
        organizationTextView = findViewById(R.id.organization_text_view);
        identificationTextView = findViewById(R.id.identification_text_view);
        loginButtonTextView = findViewById(R.id.login_text_view_btn);
        progressBar = findViewById(R.id.progress_bar);

        organizationClickableTextView.setOnClickListener(v-> showDropDownMenuOrganization());
        identificationClickableTextView.setOnClickListener(v-> showDropDownMenuIdentification());
        createAccountButton.setOnClickListener(v-> createAccount());
        //createAccountButton.setOnClickListener(v-> debug());

        loginButtonTextView.setOnClickListener(v-> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    void showDropDownMenuOrganization(){
        PopupMenu popupMenu = new PopupMenu(SignUpActivity.this, organizationClickableTextView);
        popupMenu.getMenu().add("Kolej Matrikulasi Labuan");
        popupMenu.getMenu().add("Kolej Matrikulasi Kelantan");
        popupMenu.getMenu().add("Kolej Matrikulasi Johor");
        popupMenu.getMenu().add("Kolej Matrikulasi Kedah");
        popupMenu.getMenu().add("Kolej Matrikulasi Melaka");
        popupMenu.getMenu().add("Kolej Matrikulasi Negeri Sembilan");
        popupMenu.getMenu().add("Kolej Matrikulasi Pahang");
        popupMenu.getMenu().add("Kolej Matrikulasi Perak");
        popupMenu.getMenu().add("Kolej Matrikulasi Perlis");
        popupMenu.getMenu().add("Kolej Matrikulasi Pulau Pinang");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getTitle()=="Kolej Matrikulasi Labuan"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_LABUAN";
                organizationTextView.setText("Kolej Matrikulasi Labuan");
                return true;
            }
            else if(menuItem.getTitle()=="Kolej Matrikulasi Kelantan"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_KELANTAN";
                organizationTextView.setText("Kolej Matrikulasi Kelantan");
                return true;
            }
            else if(menuItem.getTitle()=="Kolej Matrikulasi Johor"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_JOHOR";
                organizationTextView.setText("Kolej Matrikulasi Johor");
                return true;
            }
            else if(menuItem.getTitle()=="Kolej Matrikulasi Kedah"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_KEDAH";
                organizationTextView.setText("Kolej Matrikulasi Kedah");
                return true;
            }
            else if(menuItem.getTitle()=="Kolej Matrikulasi Melaka"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_MELAKA";
                organizationTextView.setText("Kolej Matrikulasi Melaka");
                return true;
            }
            else if(menuItem.getTitle()=="Kolej Matrikulasi Negeri Sembilan"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_NEGERI_SEMBILAN";
                organizationTextView.setText("Kolej Matrikulasi Negeri Sembilan");
                return true;
            }
            else if(menuItem.getTitle()=="Kolej Matrikulasi Pahang"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_PAHANG";
                organizationTextView.setText("Kolej Matrikulasi Pahang");
                return true;
            }
            else if(menuItem.getTitle()=="Kolej Matrikulasi Perak"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_PERAK";
                organizationTextView.setText("Kolej Matrikulasi Perak");
                return true;
            }
            else if(menuItem.getTitle()=="Kolej Matrikulasi Perlis"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_PERLIS";
                organizationTextView.setText("Kolej Matrikulasi Perlis");
                return true;
            }
            else if(menuItem.getTitle()=="Kolej Matrikulasi Pulau Pinang"){
                GlobalVariables.organization = "KOLEJ_MATRIKULASI_PULAU_PINANG";
                organizationTextView.setText("Kolej Matrikulasi Pulau Pinang");
                return true;
            }
            return false;
        });
    }

    void showDropDownMenuIdentification(){
        PopupMenu popupMenu = new PopupMenu(SignUpActivity.this, identificationClickableTextView);
        popupMenu.getMenu().add("Staff");
        popupMenu.getMenu().add("Student");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getTitle()=="Staff"){
                GlobalVariables.identification = "STAFF";
                identificationTextView.setText("Staff");
                return true;
            }
            if(menuItem.getTitle()=="Student"){
                GlobalVariables.identification = "STUDENT";
                identificationTextView.setText("Student");
                return true;
            }
            return false;
        });
    }

    void createAccount(){
        String organization = organizationTextView.getText().toString();
        String identification = identificationTextView.getText().toString();
        String name = nameEditText.getText().toString().toUpperCase(Locale.ROOT);
        String identificationNo = matrixNoEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if(!clicked && isValidated(organization, identification, name, identificationNo, email, password, confirmPassword)){
            clicked = true;
            createAccountInFirebase(name, identificationNo, email, password);
        }else{
            clicked = false;
        }
    }

    void debug(){
        DocumentReference doc = Utility.getDocumentReferenceGeneralData("user_code_no");
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                int codeN = value.getLong("numberOfUsers").intValue();
                codeN += 1;
                GlobalVariables.userCodeNo = String.valueOf(codeN);
            }
        });
        DocumentReference temp = Utility.getDocumentReferenceGeneralData("temp");
        temp.update("tempUserCode", Integer.valueOf(GlobalVariables.userCodeNo));
    }

    void createAccountInFirebase(String name, String identificationNo, String email, String password){
        changeProgressBar(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
            changeProgressBar(false);
            if(task.isSuccessful()){
                Utility.showToast(SignUpActivity.this, "Account created successfully, verify email");
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                UserData userData = new UserData();
                userData.setOrganization(GlobalVariables.organization);
                userData.setIdentification(GlobalVariables.identification);
                userData.setUserCodeNo(GlobalVariables.userCodeNo);
                userData.setName(name);
                userData.setMatrixNo(identificationNo);
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

    boolean isValidated(String organization, String identification, String name, String identificationNo, String email, String password, String confirmPassword){
        if(Objects.equals(organization, "Organization")){
            organizationTextView.setError("Pick an organization");
            return false;
        }
        organizationTextView.setError(null);
        if(Objects.equals(identification, "Identification")){
            identificationTextView.setError("Pick an role");
            return false;
        }else{
            identificationTextView.setError(null);
            if(Objects.equals(name, "")){
                nameEditText.setError("Enter name");
                return false;
            }
            nameEditText.setError(null);

            if(Objects.equals(GlobalVariables.identification, "STUDENT")){
                //staff or student
                GlobalVariables.checkPattern = Pattern.compile("[A-Z]{2}[0-9]{10}");
                if(!GlobalVariables.checkPattern.matcher(identificationNo).matches()){
                    matrixNoEditText.setError("Enter matrix number");
                    return false;
                }
            }else if(Objects.equals(GlobalVariables.identification, "STAFF")){
                GlobalVariables.checkPattern = Pattern.compile("[0-9]{5}");
                if(!GlobalVariables.checkPattern.matcher(identificationNo).matches()) {
                    matrixNoEditText.setError("Enter identification number");
                    return false;
                }
            }
            matrixNoEditText.setError(null);
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        emailEditText.setError(null);
        if(password.length() < 6){
            passwordEditText.setError("At least 6 characters");
            return false;
        }
        passwordEditText.setError(null);
        if(!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Doesn't match password");
            return false;
        }
        confirmPasswordEditText.setError(null);
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