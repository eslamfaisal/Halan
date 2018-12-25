package com.fekrah.halan.customer.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fekrah.halan.R;
import com.fekrah.halan.customer.adapters.CustomerNotificationAdapter;
import com.fekrah.halan.models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerNotificationActivity extends AppCompatActivity {

    @BindView(R.id.notification_recycler_view)
    RecyclerView notificationRecyclerView;
    List<Notification> notifications;
    CustomerNotificationAdapter adapter;
    LinearLayoutManager notificationLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_notification);
        ButterKnife.bind(this);
        notifications = new ArrayList<>();
        notificationLayoutManager = new LinearLayoutManager(this);
        adapter = new CustomerNotificationAdapter(notifications, this);
        notificationRecyclerView.setLayoutManager(notificationLayoutManager);
        notificationRecyclerView.setAdapter(adapter);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            FirebaseDatabase.getInstance().getReference().child("customer_notification")
                    .child(FirebaseAuth.getInstance().getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.getValue() != null) {
                        Notification notification = dataSnapshot.getValue(Notification.class);
                        adapter.addRoom(0, notification);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }
}
