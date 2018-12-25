package com.fekrah.halan.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.common.SendComplaintActivity;
import com.fekrah.halan.helper.SharedHelper;
import com.fekrah.halan.models.Driver;
import com.fekrah.halan.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.profile_image)
    SimpleDraweeView profileImage;

    @BindView(R.id.usernameTxt)
    TextView usernameTxt;

    @BindView(R.id.sent_complaint)
    TextView sent_complaint;

    @BindView(R.id.pay_view)
    View pay_view;

    @BindView(R.id.share)
    TextView share;

    @BindView(R.id.privacy)
    TextView privacy;

    @BindView(R.id.no_user)
    TextView no_user;

    @BindView(R.id.account_view)
    View account_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ButterKnife.bind(this);
        sent_complaint.setOnClickListener(this);
        pay_view.setOnClickListener(this);
        if (!SharedHelper.getKey(this, "uses_type").equals("driver")) {
            pay_view.setVisibility(View.GONE);
            if (FirebaseAuth.getInstance().getCurrentUser()!=null){
                no_user.setVisibility(View.GONE);
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue()!=null){
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user!=null){
                                        profileImage.setImageURI(user.getImg());
                                        usernameTxt.setText(user.getName());
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }else{
                account_view.setVisibility(View.GONE);
            }

        }else {
            if (FirebaseAuth.getInstance().getCurrentUser()!=null){
                no_user.setVisibility(View.GONE);
                FirebaseDatabase.getInstance().getReference().child("drivers").child(FirebaseAuth.getInstance().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue()!=null){
                                    Driver user = dataSnapshot.getValue(Driver.class);
                                    if (user!=null){
                                        profileImage.setImageURI(user.getImg());
                                        usernameTxt.setText(user.getName());
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }else{
                account_view.setVisibility(View.GONE);

            }

        }

    }

    @Override
    public void onClick(View v) {

        int id =v.getId();
        if(id==R.id.pay_view){
            startActivity(new Intent(this, PaymentActivity.class));

        }else if (id==R.id.sent_complaint){
            startActivity(new Intent(MyAccountActivity.this,SendComplaintActivity.class));
        }else if (id==R.id.share){

        }
    }
}
