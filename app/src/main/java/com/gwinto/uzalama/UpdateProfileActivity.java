package com.gwinto.uzalama;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.UpdateAppearance;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private FirebaseFirestore firebaseFirestore;
    private CircleImageView profileImage;
    private Uri profileImageURI = null;
    private boolean isChanged = false;
    String user_id = null;
    private EditText fname_txt, onames_txt,phone_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.update_profile);

        firebaseFirestore = FirebaseFirestore.getInstance();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_item_update_profile:
                        startActivity(new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class));
                        return true;
                    case R.id.menu_item_logout:
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null)
                            FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(UpdateProfileActivity.this, MainActivity.class));
                        finish();
                        return true;
                    case R.id.home_menu:
                        return true;
                    default:
                        return true;
                }
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();
        fname_txt = findViewById(R.id.update_profile_fname);
        onames_txt = findViewById(R.id.update_profile_onames);
        phone_txt = findViewById(R.id.update_profile_phone);
        profileImage = findViewById(R.id.update_profile_image);


        firebaseFirestore.collection("profile").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String fname = task.getResult().getString("fname");
                        String onames = task.getResult().getString("onames");
                        String phone = task.getResult().getString("phone");
                        String profile = task.getResult().getString("profile");

                        fname_txt.setText(fname);
                        onames_txt.setText(onames);
                        phone_txt.setText(phone);

                        RequestOptions options = new RequestOptions();
                        options.placeholder(R.drawable.profile);

                        profileImageURI = Uri.parse(profile);

                        Glide.with(UpdateProfileActivity.this).setDefaultRequestOptions(options).load(profile).into(profileImage);
                    }
                }else{
                    Toast.makeText(UpdateProfileActivity.this, "Failed to get user profile from database", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(UpdateProfileActivity.this, "I'm Clicked!", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(UpdateProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }else{
                        if(ContextCompat.checkSelfPermission(UpdateProfileActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }else{
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setAspectRatio(1,1)
                                    .start(UpdateProfileActivity.this);
                        }
                    }
                }else{
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(UpdateProfileActivity.this);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            startActivity(new Intent(UpdateProfileActivity.this, MainActivity.class));
            finish();
        }else {
            user_id = user.getUid();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                profileImageURI = result.getUri();
                profileImage.setImageURI(profileImageURI);
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(UpdateProfileActivity.this, "Error!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateUserProfile(View view) {
        final String fname = fname_txt.getText().toString();
        final String onames = onames_txt.getText().toString();
        final String phone = phone_txt.getText().toString();

        if (!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(onames) && !TextUtils.isEmpty(phone) && profileImageURI != null && user_id != null) {

            if (isChanged) {
                final StorageReference imagePath = mStorageRef.child("profile_images").child(user_id + ".jpg");

                imagePath.putFile(profileImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Toast.makeText(UpdateProfileActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        return imagePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Map<String, String> profileData = new HashMap<>();
                            profileData.put("fname", fname);
                            profileData.put("onames", onames);
                            profileData.put("phone", phone);
                            profileData.put("profile", downloadUri.toString());

                            firebaseFirestore.collection("profile").document(user_id).set(profileData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(UpdateProfileActivity.this, DashboardActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(UpdateProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(UpdateProfileActivity.this, "Failed to upload image!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Uri downloadUri = profileImageURI;
                Map<String, String> profileData = new HashMap<>();
                profileData.put("fname", fname);
                profileData.put("onames", onames);
                profileData.put("phone", phone);
                profileData.put("profile", downloadUri.toString());

                firebaseFirestore.collection("profile").document(user_id).set(profileData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UpdateProfileActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(UpdateProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else{
            Toast.makeText(this, "Some fields are empty!", Toast.LENGTH_SHORT).show();
        }
    }
}
