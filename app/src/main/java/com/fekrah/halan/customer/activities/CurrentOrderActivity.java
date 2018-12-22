package com.fekrah.halan.customer.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.models.OldOrder;
import com.fekrah.halan.models.Order;
import com.fekrah.halan.models.OrderResponse;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafakob.drawme.DrawMeButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentOrderActivity extends AppCompatActivity {

    @BindView(R.id.from_location)
    TextView from;

    @BindView(R.id.to_location)
    TextView to;

    @BindView(R.id.distance_location)
    TextView distance;

    @BindView(R.id.cost_location)
    TextView cost;

    @BindView(R.id.details)
    TextView orderDetails;

    @BindView(R.id.rout_time)
    TextView rout_time;

    @BindView(R.id.current_order_drivers)
    RecyclerView driversRecyclerView;

    @BindView(R.id.empty_drivers)
    TextView emptyDrivers;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.empty_order)
    TextView emptyOrder;

    @BindView(R.id.order_view)
    View orderView;

    @BindView(R.id.aceptedDriverView)
    View acceptedDriverView;

    List<OrderResponse> drivers;
    LinearLayoutManager messagesLinearLayoutManager;

    Order order;
    int driver = 0;

    String userId;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mCurrentUserOrderReference;
    private String TAG = "current order";
    private boolean sent = false;

    List<String> keys = new ArrayList<>();
    private boolean empty = false;
    private String acceptedDriverKey;
    private float acceptedDriverRate;
    float newRate;
    private int acceptedDriverRateCount;
    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order);
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
        mCurrentUserOrderReference = mFirebaseDatabase.getReference()
                .child("Customer_current_order").child(userId);
        mCurrentUserOrderReference.child("order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    showOrder();
                    order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        from.setText(order.getArrival_location());
                        to.setText(order.getReceiver_location());
                        distance.setText(order.getDistance());
                        cost.setText(order.getCost());
                        orderDetails.setText(order.getDetails());
                        rout_time.setText(order.getRout_time());
                        if (!sent) {
                            sent = true;
                            DatabaseReference ref = FirebaseDatabase.getInstance()
                                    .getReference().child("drivers_location");
                            GeoFire geoFire = new GeoFire(ref);
                            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(order.getA_l_lat(), order.getA_l_lng()), 30);
                            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                                @Override
                                public void onKeyEntered(final String key, GeoLocation location) {
                                    if (!key.equals(FirebaseAuth.getInstance().getUid()) && driver <= 3) {
                                        driver++;
                                        if (!keys.contains(key))
                                            FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                                                    .child(key).child("order").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() == null) {
                                                        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                                                                .child(key).child("order").setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "onComplete: ");
                                                                    keys.add(key);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                    }
                                }

                                @Override
                                public void onKeyExited(String key) {

                                }

                                @Override
                                public void onKeyMoved(String key, GeoLocation
                                        location) {

                                }

                                @Override
                                public void onGeoQueryReady() {

                                }

                                @Override
                                public void onGeoQueryError(DatabaseError error) {

                                }
                            });

                        }
                    }
                } else {
                    empty = true;
                    showEmptyView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        drivers = new ArrayList<>();

        messagesLinearLayoutManager = new LinearLayoutManager(this);

//
//        driversRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        messagesLinearLayoutManager.setSmoothScrollbarEnabled(true);
//        driversRecyclerView.setNestedScrollingEnabled(false);
//        driversRecyclerView.setLayoutManager(messagesLinearLayoutManager);
//        driversRecyclerView.setAdapter(adapter);
//
//        mCurrentUserOrderReference.child("drivers").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if (dataSnapshot.getValue() != null) {
//                    OrderResponse response = dataSnapshot.getValue(OrderResponse.class);
//                    adapter.add(response);
//                    emptyDrivers.setVisibility(View.GONE);
//                    driversRecyclerView.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        mCurrentUserOrderReference.child("accepted_driver").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    emptyOrder.setVisibility(View.GONE);
                    OrderResponse driver = dataSnapshot.getValue(OrderResponse.class);
                    acceptedDriverKey = driver.getDriver_key();
                    SimpleDraweeView profileImage;
                    TextView userName;
                    DrawMeButton chats;
                    DrawMeButton finish;

                    TextView time;
                    TextView distance;
                    TextView cost;
                    profileImage = findViewById(R.id.profile_image2);
                    userName = findViewById(R.id.user_name2);

                    chats = findViewById(R.id.chats);
                    finish = findViewById(R.id.finish);
                    time = findViewById(R.id.time2);
                    cost = findViewById(R.id.cost2);
                    distance = findViewById(R.id.distance2);
                    profileImage.setImageURI(driver.getDriver_image());
                    userName.setText(driver.getDriver_name());
                    chats.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            Intent intent = new Intent(CurrentOrderActivity.this, ChatsRoomsDriverActivity.class);
//                            startActivity(intent);
                        }
                    });
                    finish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.show();
                        }
                    });

                    time.setText(driver.getEdtimated_time());
                    distance.setText(driver.getDistance());
                    acceptedDriverView.setVisibility(View.VISIBLE);
                } else {

                    acceptedDriverView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.finish_order));
        dialog.setCancelable(false);
        dialog.setMessage(R.string.do_finish_order);
        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton(getString(R.string.finish_order), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                        .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            finishTheOrder();
                        }

                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_order_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        cancelTheOrder();

        return super.onOptionsItemSelected(item);
    }

    private OldOrder createOldOrder(){
        return  new OldOrder(
                order.getOrder_key(),
                acceptedDriverKey,
                order.getCost(),
                order.getReceiver_location(),
                order.getArrival_location(),
                order.getDistance(),
                order.getUser_key(),
                order.getCreated_at(),
                order.getDetails()
        );
    }
    private void cancelTheOrder() {
        FirebaseDatabase.getInstance().getReference().child("customer_canceled_order").child(userId).push().setValue(createOldOrder()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mCurrentUserOrderReference.child("order").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void finishTheOrder(){
        FirebaseDatabase.getInstance().getReference().child("customer_finished_order").child(userId).push().setValue(createOldOrder()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mCurrentUserOrderReference.child("order").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_order);
        builder.setMessage(R.string.cancel_order);
        builder.setPositiveButton(R.string.delete_order, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                cancelTheOrder();
//                mCurrentUserOrderReference.child("order").removeValue()
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    if (keys.size() > 0) {
//                                        for (String key : keys) {
//                                            FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
//                                                    .child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
//                                                            .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()) {
//                                                                Log.d(TAG, "onComplete: deleted");
//                                                            }
//                                                        }
//                                                    });
//                                                }
//                                            });
//                                        }
//                                    }
//                                    finish();
//                                }
//                            }
//                        });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (empty) {
            super.onBackPressed();
            empty = false;
        } else
            builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurrentUserOrderReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: ");
                }
            }
        });
    }

    private void showOrder() {
        orderView.setVisibility(View.VISIBLE);
        emptyOrder.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        orderView.setVisibility(View.GONE);
        emptyOrder.setVisibility(View.VISIBLE);
    }


}

