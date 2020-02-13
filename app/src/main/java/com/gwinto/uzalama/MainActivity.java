package com.gwinto.uzalama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText username_txt;
    private EditText pass_txt;
    FirebaseAuth myAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username_txt = findViewById(R.id.username);
        pass_txt = findViewById(R.id.password);
        myAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }
    }

    public void login(View view) {
        String username = username_txt.getText().toString();
        String pass = pass_txt.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!username.isEmpty() && !pass.isEmpty()) {
            myAuth.signInWithEmailAndPassword(username,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this,"Error: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(this,"All fields are required",Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }
}
