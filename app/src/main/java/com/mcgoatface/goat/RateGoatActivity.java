package com.mcgoatface.goat;

/**
 * Created by user on 07/01/2018.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RateGoatActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    // Reference to an image file in Cloud Storage
    private StorageReference storageReference;
    private TextView mTitleTextView;
    private TextView mUploaderTextView;
    private TextView mBannerTextView;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rategoats);
        mTitleTextView = findViewById(R.id.Title);;
        mUploaderTextView = findViewById(R.id.Uploader);
        mBannerTextView = findViewById(R.id.banner);
        storageReference = storage.getReference("goats");
        db.getReference("goats").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long index = nextLong(new Random(), dataSnapshot.getChildrenCount());
                int i = 0;
                for (DataSnapshot goatSnapshot: dataSnapshot.getChildren()) {
                    if (i == index) {
                        GoatPicture gtPic = goatSnapshot.getValue(GoatPicture.class);
                        updateActivity(gtPic, goatSnapshot.getKey());
                    }
                    i++;
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
        mAuth = FirebaseAuth.getInstance();
    }

    private long nextLong(Random rng, long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits-val+(n-1) < 0L);
        return val;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void updateActivity(GoatPicture gp, String key){
        StorageReference picture = storageReference.child(key);
        updateImage(picture);
        //Set TextViews now
        mTitleTextView.setText(gp.Title);
        mUploaderTextView.setText(gp.Uploader);
    }

    private void updateImage(StorageReference sr) {
        // ImageView in your Activity
        ImageView imageView = (ImageView) findViewById(R.id.goatImg);
       //Debugging layout problems so switching Image off
       // imageView.setVisibility(0);
        GlideApp.with(this).load(sr).into(imageView);
    }
}
