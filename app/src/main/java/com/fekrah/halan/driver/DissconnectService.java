package com.fekrah.halan.driver;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DissconnectService extends IntentService {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;


    public DissconnectService() {
        super("DissconnectService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null) {

            mFirebaseDatabase.getReference().child("drivers_location")
                    .child(mCurrentUser.getUid()).onDisconnect().removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFirebaseDatabase.getReference().child("drivers_current_order")
                                        .child(mCurrentUser.getUid()).onDisconnect()
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }
                        }
                    });


        }
    }

    public void onTaskRemoved(Intent rootIntent) {

        if (mCurrentUser != null) {

            mFirebaseDatabase.getReference().child("drivers_location")
                    .child(mCurrentUser.getUid()).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFirebaseDatabase.getReference().child("drivers_current_order")
                                        .child(mCurrentUser.getUid())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }
                        }
                    });

        }

        stopSelf();
    }
}
