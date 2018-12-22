package com.fekrah.halan.common.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.fekrah.halan.R;
import com.fekrah.halan.customer.CustomerMainActivity;
import com.fekrah.halan.customer.activities.RegisterUserActivity;
import com.fekrah.halan.driver.activities.MainActivityDriver;
import com.fekrah.halan.driver.activities.RegisterActivityDriver;
import com.fekrah.halan.helper.SharedHelper;
import com.fekrah.halan.helper.Utility;
import com.fekrah.halan.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafakob.drawme.DrawMeButton;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fekrah.halan.customer.activities.RegisterUserActivity.REGISTER_OK;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.forget_pass)
    DrawMeButton forget;

    @BindView(R.id.new_account)
    DrawMeButton register;

    @BindView(R.id.login)
    DrawMeButton login;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ProgressDialog dialog;
    public static final int LOGIN_OK = 1080;
    public static final int REGISTER_REQUEST = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseAuth.getInstance().signOut();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Utility.hideSoftKeyboard(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("جار تسجيل الدخول");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAsUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logIntent = new Intent(LoginActivity.this, RegisterUserActivity.class);
                startActivityForResult(logIntent, REGISTER_REQUEST);

            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPassword.class);
                startActivity(intent);
            }
        });
    }

    private void loginAsUser() {
        dialog.show();
        if ((isValidEmail(email.getText().toString()))) {
            if (email.getText().toString().equals("") ||
                    email.getText().toString()
                            .equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                dialog.dismiss();
                displayMessage(getString(R.string.email_validation));

            } else if (password.getText().toString().equals("") ||
                    password.getText().toString()
                            .equalsIgnoreCase(getString(R.string.password_txt))) {
                dialog.dismiss();
                displaySnackbar(getString(R.string.password_validation));

            } else if (password.length() < 6) {
                dialog.dismiss();
                displaySnackbar(getString(R.string.password_size));
            } else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                dialog.dismiss();

                displaySnackbar(getString(R.string.password_validation));
            } else if (password.length() < 6) {
                dialog.dismiss();
                displaySnackbar(getString(R.string.password_size));
            } else {
                siginAsUser(email.getText().toString(), password.getText().toString());

            }
        } else {
            dialog.dismiss();
            displaySnackbar(getString(R.string.check_email_or_password));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_REQUEST) {
            if (resultCode == REGISTER_OK) {
                User registerdUser = (User) data.getSerializableExtra("current_user");
                Intent loginResult = new Intent();
                loginResult.putExtra("current_user", registerdUser);
                setResult(LOGIN_OK, loginResult);
                finish();
            }
        }
    }

    private void siginAsUser(String email, String password) {

        dialog.show();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() == null) {
                                                showSnackBar(getString(R.string.no_user));
                                                FirebaseAuth.getInstance().signOut();
                                                dialog.dismiss();

                                            } else {
                                                User user = dataSnapshot.getValue(User.class);
                                                Intent loginResult = new Intent();
                                                loginResult.putExtra("current_user", user);
                                                setResult(LOGIN_OK, loginResult);
                                                dialog.dismiss();
                                                finish();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        } else {
                            dialog.dismiss();
                            displaySnackbar("check email and password");
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e.equals("ERROR_USER_NOT_FOUND")) {
                            displaySnackbar("yes");
                        }
                    }
                });
    }

    private void siginAsDriver(String email, String password) {

        dialog.show();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("drivers")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() == null) {
                                                showSnackBar(getString(R.string.no_dirver));
                                                FirebaseAuth.getInstance().signOut();
                                                dialog.dismiss();
                                            } else {
                                                startActivity(new Intent(LoginActivity.this, MainActivityDriver.class));
                                                SharedHelper.putKey(getApplicationContext(),"uses_type","driver");
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        } else {
                            dialog.dismiss();
                            displaySnackbar("check email and password");
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e.equals("ERROR_USER_NOT_FOUND")) {
                            displaySnackbar("yes");
                        }
                    }
                });
    }


//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
//    }
}
