package com.mcgoatface.goat;

/**
 * Created by user on 07/01/2018.
 */


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RateGoatActivity extends AppCompatActivity implements
        View.OnClickListener {
    private DatabaseReference mDatabase;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;
    // Reference to an image file in Cloud Storage
    private StorageReference storageReference;
    private TextView mTitleTextView;
    private TextView mUploaderTextView;
    private TextView mBannerTextView;
    private RatingBar ratingBar;
    private static final String RATE_BANNER = "rate_banner";
    private String GoatID;
    private FirebaseUser currentUser;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rategoats);
        mTitleTextView = findViewById(R.id.Title);;
        mUploaderTextView = findViewById(R.id.Uploader);
        mBannerTextView = findViewById(R.id.banner);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        storageReference = storage.getReference("goats");
        findViewById(R.id.more_goats).setOnClickListener(this);
        getGoat();
        addListenerOnRatingBar();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                // This is to allow for the cache to be cleared quicker so bear this in mind with production apps don't be switching pool membership too fast
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        //Feel like this shouldn't be done in here as it should be an app wide thing. For now it's alright but feels like a code smell
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        //if rating value is changed,
        //display the current rating value in the result (textview) automatically

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                //This is where we should save the value like we do in web client but being honest it's like 11:30pm and the talk is tomorrow so this is future patricks problem
                Bundle params = new Bundle();
                params.putString("goat_id", GoatID);
                params.putString("rater_id", mAuth.getCurrentUser().getUid());
                params.putFloat("rating", rating);
                mFirebaseAnalytics.logEvent("goat_rated", params);
                //Disable so we know we've called this I guess
                ratingBar.setIsIndicator(true);

            }
        });
    }

    private void getGoat() {
        db.getReference("goats").orderByChild("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long index = nextLong(new Random(), dataSnapshot.getChildrenCount());
                int i = 0;
                for (DataSnapshot goatSnapshot: dataSnapshot.getChildren()) {
                    if (i == index) {
                        GoatID = goatSnapshot.getKey();
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
        currentUser = mAuth.getCurrentUser();
    }

    private void updateActivity(GoatPicture gp, String key){
        StorageReference picture = storageReference.child(key);
        updateImage(picture);

        //do some remote Config goodness
        doRemoteConfigGoodness();

        //Set TextViews now
        mTitleTextView.setText(gp.Title);
        mUploaderTextView.setText(gp.Uploader);
    }

    private void doRemoteConfigGoodness() {
        mBannerTextView.setText(mFirebaseRemoteConfig.getString(RATE_BANNER));

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        }
                        mBannerTextView.setText(mFirebaseRemoteConfig.getString(RATE_BANNER));
                    }
                });
    }



    private void updateImage(StorageReference sr) {
        // ImageView in your Activity
        ImageView imageView = (ImageView) findViewById(R.id.goatImg);
       //Debugging layout problems so switching Image off
       // imageView.setVisibility(0);
        GlideApp.with(this).load(sr).into(imageView);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.more_goats) {
            getGoat();
        }
    }
}
