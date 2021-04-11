package com.example.transportdisplay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText regEmail,regPassword,regConfirmPass;
    private   Button registerbtn;
    private TextView haveaccounttext;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        regEmail = (EditText) findViewById(R.id.regEmail);
        regPassword = (EditText) findViewById(R.id.regPassword);
        regConfirmPass = (EditText) findViewById(R.id.regConfirmPass);

        haveaccounttext = (TextView) findViewById(R.id.haveaccounttext);
        registerbtn = (Button) findViewById(R.id.registerbtn);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email = regEmail.getText().toString().trim();
                String password = regPassword.getText().toString().trim();
                String confirmPassword = regConfirmPass.getText().toString().trim();

                boolean authcheck = authNewUserDetails(email, password, confirmPassword);
                if(authcheck) {
                    registerNewUser(email, password);
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    Toast.makeText(SignUpActivity.this,"successful registration ", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

    }

    public void registerNewUser(String email, String password){


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("LOG", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this,"successful registration "+user.getEmail() +" "+ user.getUid(), Toast.LENGTH_SHORT).show();

                    }  else{
                        // If sign in fails, display a message to the user.
                        Log.w("LOG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }

    public boolean authNewUserDetails(String email, String password, String confirmpassword){

        if(email.isEmpty()) {
            regEmail.setError("Fill in your Email");
            regEmail.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            regEmail.setError("Enter a valid Email");
            regEmail.requestFocus();
            return false;
        }

        if(password.isEmpty()) {
            regPassword.setError("Please enter your password");
            regPassword.requestFocus();
            return false;
        }
        if(password.length()<6) {
            regPassword.setError("Minimum length should be 6");
            regPassword.requestFocus();
            return false;
        }

        if(confirmpassword.isEmpty()){
            regConfirmPass.setError("Repeat your password");
            regConfirmPass.requestFocus();
            return false;
        }
        if(!password.equals(confirmpassword))
        {
            regConfirmPass.setError("Password does not match");
            regConfirmPass.requestFocus();
            return false;
        }

        else {
            return true;
        }

    }
}
