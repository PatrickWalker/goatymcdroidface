package com.mcgoatface.goat;

/**
 * Created by user on 07/01/2018.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RateGoatActivity extends AppCompatActivity {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    // Reference to an image file in Cloud Storage
    private StorageReference storageReference;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rategoats);
        storageReference = storage.getReference("goats");
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        StorageReference first =  storageReference.child("Auth.png");
        updateImage(first);

    }

    private void updateImage(StorageReference sr) {
        // ImageView in your Activity
        ImageView imageView = (ImageView) findViewById(R.id.goatImg);
        Glide.with(this).load(sr).into(imageView);
    }
}
