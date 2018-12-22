package com.fekrah.halan.driver.fragments;

import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.driver.activities.ChatsRoomsDriverActivity;
import com.fekrah.halan.driver.activities.MainActivityDriver;
import com.fekrah.halan.models.OldOrder;
import com.fekrah.halan.models.Order;
import com.fekrah.halan.models.OrderResponse;
import com.fekrah.halan.models.Room;
import com.fekrah.halan.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafakob.drawme.DrawMeButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TalabatFragment extends Fragment {

    public static final String ORDER_ID = "order_id";


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

    @BindView(R.id.notes)
    TextView notes;

    @BindView(R.id.send_offer)
    DrawMeButton send_offer;

    @BindView(R.id.refuse_order)
    DrawMeButton refuse_order;

    @BindView(R.id.order_action_text)
    TextView offerActionText;

    @BindView(R.id.empty_order)
    TextView emptyOrder;

    @BindView(R.id.order_view)
    NestedScrollView orderView;

    @BindView(R.id.profile_image)
    SimpleDraweeView senderImage;

    @BindView(R.id.sender_name)
    TextView senderName;

    @BindView(R.id.offered_view)
    View offerdView;

    @BindView(R.id.order_action_buttons)
    View offerActionButtons;

    @BindView(R.id.accepted_order_view)
    View acceptedOrderView;

    @BindView(R.id.go_chats)
    DrawMeButton goChats;

    @BindView(R.id.finish_order)
    DrawMeButton finishOrder;

    private Ringtone ringtone;
    private String senderNameText;
    private String senderImgTextg;

    private void showofferdView() {
        offerdView.setVisibility(View.VISIBLE);
        offerActionButtons.setVisibility(View.GONE);
        acceptedOrderView.setVisibility(View.GONE);
    }

    private void showAccepted() {
        offerdView.setVisibility(View.GONE);
        offerActionButtons.setVisibility(View.GONE);
        acceptedOrderView.setVisibility(View.VISIBLE);
    }

    Order order;

    public static TalabatFragment newInstance() {
        return new TalabatFragment();
    }

    public TalabatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_talabat2, container, false);
        ButterKnife.bind(this, mainView);
        hideOrder();

        send_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivityDriver.setOrderSent(true);
                MainActivityDriver.mCountDownTimer.cancel();
                MainActivityDriver.mCountDownTimer.onFinish();
                FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                        .child(order.getUser_key())
                        .child("accepted_driver").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Toast.makeText(getActivity(), "تم قبول هذا الطلب من قبل سائق اخر", Toast.LENGTH_SHORT).show();
                        } else {
                            String responceKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                            OrderResponse orderResponse = new OrderResponse(
                                    MainActivityDriver.driver.getName(),
                                    responceKey,
                                    MainActivityDriver.results[0].toString(),
                                    FirebaseAuth.getInstance().getUid(),
                                    MainActivityDriver.driver.getImg(),
                                    MainActivityDriver.results[1].toString()
                            );
                            FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                                    .child(order.getUser_key()).child("accepted_driver")
                                    .setValue(orderResponse).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        final Room room = new Room(
                                                MainActivityDriver.driver.getImg(),
                                                "تم قبول الطلب",
                                                System.currentTimeMillis(),
                                                MainActivityDriver.driver.getUser_key(),
                                                MainActivityDriver.driver.getName()
                                        );
                                        FirebaseDatabase.getInstance().getReference().child("rooms")
                                                .child(order.getUser_key())
                                                .child(MainActivityDriver.driver.getUser_key())
                                                .setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    final Room room2 = new Room(
                                                            order.getImage(),
                                                            "تم قبول الطلب",
                                                            System.currentTimeMillis(),
                                                            order.getUser_key(),
                                                            order.getName());
                                                    FirebaseDatabase.getInstance().getReference().child("rooms")
                                                            .child(MainActivityDriver.driver.getUser_key())
                                                            .child(order.getUser_key())
                                                            .setValue(room2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                                                                        .child(MainActivityDriver.driver.getUser_key()).child("offer").setValue("accept").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                                                                                .child(FirebaseAuth.getInstance().getUid())
                                                                                .child("drivers").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                            }
                                                                        });


                                                                    }
                                                                });

                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//
//                Intent intent = new Intent(getActivity(), CurrentOrderActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent.putExtra(ORDER_ID, order);
//                getActivity().startActivity(intent);
            }
        });

        refuse_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                        .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("drivers_state")
                                    .child(FirebaseAuth.getInstance().getUid()).child("has_order").setValue("no")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                MainActivityDriver.setOrderSent(false);
                                                MainActivityDriver.mCountDownTimer.cancel();
                                                MainActivityDriver.mCountDownTimer.onFinish();
                                                Toast.makeText(getActivity(), R.string.order_refused, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                    }
                });
            }
        });
        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {


                    hideOrder();
                } else {
                    if (dataSnapshot.child("offer").getValue() != null) {

                        if (dataSnapshot.child("offer").getValue().toString().equals("accept")) {
                            showAccepted();
                        } else if (dataSnapshot.child("offer").getValue().toString().equals("sent")) {
                            showofferdView();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        goChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notifyIntent = new Intent(getActivity(), ChatsRoomsDriverActivity.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().startActivity(notifyIntent);
            }
        });

        finishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishOrder.setEnabled(false);

                FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                        .child(FirebaseAuth.getInstance().getUid()).child("order")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final Order order = dataSnapshot.getValue(Order.class);
                                String oldOrderKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                                OldOrder oldOrder = new OldOrder(
                                        oldOrderKey,
                                        FirebaseAuth.getInstance().getUid(),
                                        order.getCost(),
                                        order.getReceiver_location(),
                                        order.getArrival_location(),
                                        order.getDistance(),
                                        order.getUser_key(),
                                        order.getCreated_at(),
                                        order.getDetails()
                                );
                                FirebaseDatabase.getInstance().getReference().child("drivers_old_order")
                                        .child(FirebaseAuth.getInstance().getUid()).child(oldOrderKey)
                                        .setValue(oldOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                                                    .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        finishOrder.setEnabled(true);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
        return mainView;
    }

    public void change(Order order) {
        if (order != null) {
            this.order = order;
            to.setText(order.getReceiver_location());
            from.setText(order.getArrival_location());
            cost.setText(order.getCost());
            distance.setText(order.getDistance());
            orderDetails.setText(order.getDetails());

            FirebaseDatabase.getInstance().getReference()
                    .child("users").child(order.getUser_key())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    senderNameText = user.getName();
                                    senderName.setText(senderNameText);
                                    senderImgTextg = user.getImg();
                                    senderImage.setImageURI(senderImgTextg);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        showOrder();
    }

    private void showButtons() {

        offerActionText.setVisibility(View.GONE);
    }

    private void showText() {
        offerActionButtons.setVisibility(View.GONE);
        offerActionText.setVisibility(View.VISIBLE);
    }

    private void showOrder() {
        orderView.setVisibility(View.VISIBLE);
        offerActionText.setVisibility(View.GONE);
        offerActionButtons.setVisibility(View.VISIBLE);
        emptyOrder.setVisibility(View.GONE);
        acceptedOrderView.setVisibility(View.GONE);
        offerdView.setVisibility(View.GONE);

    }

    private void hideOrder() {
        orderView.setVisibility(View.GONE);
        offerActionText.setVisibility(View.GONE);
        offerActionButtons.setVisibility(View.GONE);
        emptyOrder.setVisibility(View.VISIBLE);
    }


}
