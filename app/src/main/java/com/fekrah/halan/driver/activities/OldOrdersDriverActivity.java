package com.fekrah.halan.driver.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fekrah.halan.R;
import com.fekrah.halan.driver.adapters.OldOrdersAdapter;
import com.fekrah.halan.models.OldOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OldOrdersDriverActivity extends AppCompatActivity {

    @BindView(R.id.old_orders_recycler_ciew)
    RecyclerView oldOrdersRecyclerView;

    List<OldOrder> orders;
    OldOrdersAdapter adapter;
    LinearLayoutManager oldOrdersLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_orders);
        ButterKnife.bind(this);
        orders = new ArrayList<>();
        oldOrdersLinearLayoutManager = new LinearLayoutManager(this);
        adapter = new OldOrdersAdapter(this, orders);
        oldOrdersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        oldOrdersRecyclerView.setLayoutManager(oldOrdersLinearLayoutManager);
        oldOrdersRecyclerView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("drivers_old_order")
                .child(FirebaseAuth.getInstance().getUid())
                .orderByChild("time")
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    OldOrder order = dataSnapshot.getValue(OldOrder.class);
                    if (adapter.indexOf(order)==-1)
                    adapter.add(order);
               //     oldOrdersRecyclerView.setVisibility(View.VISIBLE);
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
