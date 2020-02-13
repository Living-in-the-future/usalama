package com.gwinto.uzalama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth myAuth;
    private EditText email_txt;
    private EditText pass_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myAuth = FirebaseAuth.getInstance();
        email_txt = findViewById(R.id.email);
        pass_txt = findViewById(R.id.pass);
    }
    public void register(View view) {
        String email = email_txt.getText().toString();
        String pass = pass_txt.getText().toString();
        if (!email.isEmpty() && !pass.isEmpty()) {
            myAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){


                    Toast.makeText(RegisterActivity.this,"Registration successful, proceed to login",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this,"Error!" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
                }
            });

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(RegisterActivity.this, "Error! All fields are required.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(myAuth.getCurrentUser() != null){
            startActivity(new Intent(RegisterActivity.this,DashboardActivity.class));
        }
    }

    public void gotoLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}
