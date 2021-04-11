package com.example.transportdisplay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText logemail, logpass;
    SharedPreferences sharedPreferences;
    String MyPreference = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         logemail = findViewById(R.id.logemail);
         logpass = (EditText) findViewById(R.id.logpass);
        final Button registerbtn  = (Button) findViewById(R.id.registerbtn);
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regbtnClicked(v);
            }
        });

        final Button loginbtn = (Button) findViewById(R.id.loginbtn);

        sharedPreferences = getSharedPreferences(MyPreference, Context.MODE_PRIVATE);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });
    }

    public void loginUser(){
        final String  email = logemail.getText().toString().trim();
        final String  password = logpass.getText().toString().trim();

        if(email.isEmpty()) {
            logemail.setError("Fill in your Email");
            logemail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            logemail.setError("Enter a valid Email");
            logemail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            logpass.setError("Please enter your password");
            logpass.requestFocus();
            return;
        }

        else {

            // CHECK WITH FIREBASE DATABASE TO CONFIRM LOGIN-USER
//            loginToFirebase();

            // Authenticate with Firebase and subscribe to updates
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
//                    subscribeToUpdates();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.commit();
                        Toast.makeText(LoginActivity.this, "Login Info successfully saved", Toast.LENGTH_SHORT).show();


                        startActivity(new Intent(LoginActivity.this,MapsActivity.class));
                        Log.d("LOG", "firebase auth success");
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Unsuccessfully", Toast.LENGTH_SHORT).show();

                        Log.d("LOG", "firebase auth failed");
                    }
                }
            });
        }
    }

    public void regbtnClicked(View view){
        startActivity(new Intent(this, SignUpActivity.class));
    }

}

