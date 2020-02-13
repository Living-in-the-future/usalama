package com.gwinto.uzalama;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {
    Toolbar mainToolBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mainToolBar = findViewById(R.id.toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        mainToolBar.setTitle(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
        }

        FirebaseFirestore.getInstance().collection("profile").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if(task.getResult().exists()){
                        CircleImageView profileImage = findViewById(R.id.user_profile_image);
                        String image = task.getResult().getString("profile");
                        Glide.with(DashboardActivity.this).load(image).into(profileImage);
                    }else{
                        startActivity(new Intent(DashboardActivity.this,UpdateProfileActivity.class));
                    }
                }else{
                    Toast.makeText(DashboardActivity.this, "Failed to get profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch(item.getItemId()){
                    case R.id.contact_list_menu:
                        mainToolBar.setTitle(R.string.contact_list);
                        selectedFragment = new ContactsFragment();
                        break;
                    case R.id.history_menu:
                        mainToolBar.setTitle(R.string.history);
                        selectedFragment = new HistoryFragment();
                        break;
                    default:
                        mainToolBar.setTitle(R.string.app_name);
                        selectedFragment = new HomeFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                return true;
            }
        });

        mainToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_item_update_profile:
                        goToUpdateProfile();
                        return true;
                    case R.id.menu_item_logout:
                        logout();
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
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    public void addNewContact(View view) {
        startActivity(new Intent(DashboardActivity.this, AddEmergencyContactActivity.class));
    }

    public void logout() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        finish();
    }

    public void goToUpdateProfile() {
        startActivity(new Intent(DashboardActivity.this, UpdateProfileActivity.class));
    }


    public void goToHistory(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HistoryFragment()).commit();
    }

    public void gotToContacts(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ContactsFragment()).commit();
    }
}
