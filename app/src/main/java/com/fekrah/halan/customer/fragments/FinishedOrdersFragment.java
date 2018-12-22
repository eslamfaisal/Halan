package com.fekrah.halan.customer.fragments;




import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class FinishedOrdersFragment extends Fragment {

    @BindView(R.id.old_orders_recycler_ciew)
    RecyclerView oldOrdersRecyclerView;

    List<OldOrder> orders;
    OldOrdersAdapter adapter;
    LinearLayoutManager oldOrdersLinearLayoutManager;

    public FinishedOrdersFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finished_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        orders = new ArrayList<>();
        oldOrdersLinearLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new OldOrdersAdapter(getActivity(), orders);
        oldOrdersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        oldOrdersRecyclerView.setLayoutManager(oldOrdersLinearLayoutManager);
        oldOrdersRecyclerView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("customer_finished_order")
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
