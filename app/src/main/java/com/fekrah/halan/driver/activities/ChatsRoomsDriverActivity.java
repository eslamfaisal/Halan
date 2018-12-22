package com.fekrah.halan.driver.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fekrah.halan.R;
import com.fekrah.halan.driver.adapters.ChatsAdapter;
import com.fekrah.halan.models.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsRoomsDriverActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.chats_recycler_view)
    RecyclerView mSearchRecyclerView;

    String userId;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mUsersReference;

    List<Room> users;
    ChatsAdapter usersAdapter;
    LinearLayoutManager skillsLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_rooms_driver);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference().child("rooms").child(userId);
        mUsersReference.keepSynced(true);

        users = new ArrayList<>();
        List<String> key = new ArrayList<>();
        skillsLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        usersAdapter = new ChatsAdapter(users, key, this);
        mSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // mSearchRecyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        mSearchRecyclerView.setLayoutManager(skillsLinearLayoutManager);
        mSearchRecyclerView.setAdapter(usersAdapter);

        mUsersReference.orderByChild("time").endAt(System.currentTimeMillis()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey() != null) {
                    Room room = dataSnapshot.getValue(Room.class);
                    int index = usersAdapter.indexOfRoom(room.getRoom_key());
                    if (index == -1) {
                        //    if (!room.getLast_message().equals("")) {
                            usersAdapter.addRoom(0, room);
                            usersAdapter.addKey(0, room.getRoom_key());
                        }
                    // }

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

        mUsersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Room room = dataSnapshot.getValue(Room.class);
                //if (!room.getLast_message().equals("")) {
                    int index = usersAdapter.indexOfRoom(room.getRoom_key());
                    if (index == -1) {
                        usersAdapter.addRoom(0, room);
                        usersAdapter.addKey(0, room.getRoom_key());
                    }
                // }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Room room = dataSnapshot.getValue(Room.class);
                //  if (!room.getLast_message().equals("")) {
                    int index = usersAdapter.indexOfRoom(room.getRoom_key());
                    if (index != -1) {
                        usersAdapter.removeKey(index);
                        usersAdapter.removeRoom(index);
                        usersAdapter.addRoom(0, room);
                        usersAdapter.addKey(0, room.getRoom_key());
                    } else {
                        usersAdapter.addRoom(0, room);
                        usersAdapter.addKey(0, room.getRoom_key());
                    }
                //  }

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
