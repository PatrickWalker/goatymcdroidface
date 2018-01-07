package com.mcgoatface.goat;

/**
 * Created by user on 07/01/2018.
 */


import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RateGoatActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    // Reference to an image file in Cloud Storage
    StorageReference storageReference = storage.getReference("goats");

    // ImageView in your Activity
    ImageView imageView = (ImageView) findViewById(R.id.GoatImageView);;


}
