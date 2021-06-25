package com.project22.medikit.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project22.medikit.HomeActivity.HomeActivity;
import com.project22.medikit.databinding.ActivityLoginBinding;

import javax.security.auth.login.LoginException;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    //view binding to bind all the views
    private ActivityLoginBinding loginBinding;

    //firebase initialize
    private FirebaseAuth firebaseAuth;

    //Userinfo
    private String email, password;

    //firestore document id set as email
    public static String userID_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        //hide actionbar for login and signup
        getSupportActionBar().hide();

        //firebase initialize
        firebaseAuth = FirebaseAuth.getInstance();

        //if already logged in
        checkUser();

        //onclick listener set
        loginBinding.gosignupTV.setOnClickListener(this);
        loginBinding.loginbtn.setOnClickListener(this);


    }

    private void checkUser() {

        if(firebaseAuth.getCurrentUser() != null){
            userID_email = firebaseAuth.getCurrentUser().getEmail();
            //Toast.makeText(this, "ID: " +userID_email, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LogInActivity.this, HomeActivity.class));
            finish();
        } else {}
    }

    //onclick listener method
    @Override
    public void onClick(View view) {
        if(loginBinding.gosignupTV.equals(view)){
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);

        } else if(loginBinding.loginbtn.equals(view)){
            email = loginBinding.loginemailET.getText().toString().trim();
            password = loginBinding.loginpasswordET.getText().toString().trim();

            if(validate()){login();}

        } else{}

    }

    private boolean validate() {

        if(email.isEmpty()){
            loginBinding.loginemailET.setError("email id is required");
            loginBinding.loginemailET.requestFocus();
            return false;

        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginBinding.loginemailET.setError("Valid email id is required");
            loginBinding.loginemailET.requestFocus();
            return false;

        } else if(password.isEmpty()){
            loginBinding.loginpasswordET.setError("password is required");
            loginBinding.loginpasswordET.requestFocus();
            return false;

        } else{ return true;}

    }

    private void login() {
        loginBinding.loginProgressbar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        loginBinding.loginProgressbar.setVisibility(View.GONE);
                        //userID_email = email;
                        Toast.makeText(LogInActivity.this, "Logged in successfully!" + "\n" + userID_email, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LogInActivity.this, HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        loginBinding.loginProgressbar.setVisibility(View.GONE);
                        Toast.makeText(LogInActivity.this, "Login failed! "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}