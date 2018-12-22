package com.fekrah.halan;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yayandroid.locationmanager.LocationManager;

public class App extends Application {

//
//    private FirebaseDatabase mFirebaseDatabase;
//    private DatabaseReference mUserReference;
//    private FirebaseAuth mAuth;
//    private FirebaseUser mCurrentUser;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager.enableLog(true);
        Fresco.initialize(this);

//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        mCurrentUser = mAuth.getCurrentUser();
//        if (mCurrentUser != null) {
//            mFirebaseDatabase.getReference().child("Customer_current_order")
//                    .child(mCurrentUser.getUid()).onDisconnect()
//                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//
//                }
//            });
//
//        }

    }
}
