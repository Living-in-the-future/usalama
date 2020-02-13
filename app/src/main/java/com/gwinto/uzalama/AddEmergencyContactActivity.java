package com.gwinto.uzalama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddEmergencyContactActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText fname_txt;
    private EditText onames_txt;
    private EditText phone_txt;
    private EditText email_txt;
    private Button btn_cmd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emergency_contact);


        fname_txt = findViewById(R.id.contact_fname);
        onames_txt = findViewById(R.id.contact_onames);
        phone_txt = findViewById(R.id.contact_phone);
        email_txt = findViewById(R.id.contact_email);
        btn_cmd = findViewById(R.id.add_contact_list_item);

        btn_cmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = fname_txt.getText().toString();
                String onames = onames_txt.getText().toString();
                String email = email_txt.getText().toString();
                String phone = phone_txt.getText().toString();

                if (!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(onames) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone)){
                    Map<String,String> contactItem = new HashMap<>();

                    contactItem.put("user_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    contactItem.put("fname",fname);
                    contactItem.put("onames",onames);
                    contactItem.put("email",email);
                    contactItem.put("phone",phone);
                    contactItem.put("timestamp", FieldValue.serverTimestamp().toString());

                    FirebaseFirestore.getInstance().collection("contacts").add(contactItem).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AddEmergencyContactActivity.this, "Contact added successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddEmergencyContactActivity.this,DashboardActivity.class));
                                finish();
                            }else{
                                Toast.makeText(AddEmergencyContactActivity.this, "Failed to add contact!"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(AddEmergencyContactActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Contact");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_item_update_profile:
                        startActivity(new Intent(AddEmergencyContactActivity.this, UpdateProfileActivity.class));
                        return true;
                    case R.id.menu_item_logout:
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null)
                            FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(AddEmergencyContactActivity.this, MainActivity.class));
                        finish();
                        return true;
                    case R.id.home_menu:
                        return true;
                    default:
                        return true;
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            startActivity(new Intent(AddEmergencyContactActivity.this, MainActivity.class));
            finish();
        }
    }
}
