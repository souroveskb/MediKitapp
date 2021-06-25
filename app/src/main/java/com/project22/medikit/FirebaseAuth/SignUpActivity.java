package com.project22.medikit.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project22.medikit.DataModels.User;
import com.project22.medikit.databinding.ActivitySignUpBinding;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    //view binding ussed to bind the views
    private ActivitySignUpBinding signUpBinding;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    
    //userData
    private String name, age, email, password;
    public static String info = "userinfo";
    public static String collectionName = "Users";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        //hide actionbar for login and signup
        getSupportActionBar().hide();

        //firebase initiate
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        signUpBinding.gologinTV.setOnClickListener(this);
        signUpBinding.signupbtn.setOnClickListener(this);
        signUpBinding.signuptitleTV.setOnClickListener(this);

    }

    //onclick listener method
    @Override
    public void onClick(View view) {

        if(signUpBinding.signuptitleTV.equals(view)){
            Toast.makeText(this, "Medikit App", Toast.LENGTH_SHORT).show();
        } else if(signUpBinding.gologinTV.equals(view)){
            Tologinactivity();
        
        } else if(signUpBinding.signupbtn.equals(view)){
            name = signUpBinding.signupnameET.getText().toString().trim();
            age = signUpBinding.signupageET.getText().toString().trim();
            email = signUpBinding.signupemailET.getText().toString().trim();
            password = signUpBinding.signuppasswordET.getText().toString().trim();
            
            if(datavalidate()){
                signup();
            }
        }

    }

    private void Tologinactivity() {
        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
        finish();
    }

    private boolean datavalidate() {

        if(name.isEmpty()){
            signUpBinding.signupnameET.setError("name is required");
            signUpBinding.signupnameET.requestFocus();
            return false;

        } else if(age.isEmpty()){
            signUpBinding.signupageET.setError("age is required");
            signUpBinding.signupageET.requestFocus();
            return false;

        } else if(email.isEmpty()){
            signUpBinding.signupemailET.setError("email id is required");
            signUpBinding.signupemailET.requestFocus();
            return false;

        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpBinding.signupemailET.setError("valid email id is required");
            signUpBinding.signupemailET.requestFocus();
            return false;

        } else if(password.isEmpty()){
            signUpBinding.signuppasswordET.setError("password is required");
            signUpBinding.signuppasswordET.requestFocus();
            return false;

        } else if(password.length()< 6) {
            signUpBinding.signuppasswordET.setError("password must be more than 6 char");
            signUpBinding.signuppasswordET.requestFocus();
            return false;

        } else{ return true;}
    }



    private void signup() {
        signUpBinding.signupProgressbar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user = new User(name, age, email);
                        Map<String, Object> usermap = new HashMap<>();
                        usermap.put("name", name);
                        usermap.put("age", age);
                        usermap.put("email",email);
                        //usermap.put(info, user);
                        firestore.collection(collectionName).document(email).set(usermap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        signUpBinding.signupProgressbar.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this, "User is registered!", Toast.LENGTH_SHORT).show();
                                        Tologinactivity();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        signUpBinding.signupProgressbar.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this, "Error!"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signUpBinding.signupProgressbar.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this, "Error!"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
