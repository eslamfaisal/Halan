package com.fekrah.halan.common.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fekrah.halan.R;
import com.fekrah.halan.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

/**
 * Created by pkharche on 13/04/18.
 */
public class BaseActivity extends AppCompatActivity {
    protected Context mContext = null;
    protected Activity mActivity = null;
    protected CoordinatorLayout mCoordinatorLayout = null;
    protected FirebaseAuth mAuth;
    protected FirebaseDatabase mFirebaseDatabase;
    protected FirebaseUser currentUser;
    protected DatabaseReference mFirebaseReference;
    protected String currentUserId;

    protected User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/AdvertisingBold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        mContext = this;
        mActivity = this;

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseReference = mFirebaseDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
    }

    public boolean hasInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            //Toast.makeText(context, "Oops ! Connect your Internet", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    public final boolean requestInternet() {
        if (hasInternet()){
            return true;
        }
        //mProgressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setMessage("Check your Internet").setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                startActivity(NetworkAction);

            }
        });
        builder.show();
        return false;
    }

    public final void displaySnackbar(String toastString) {
        Log.e("displayMessage", "" + toastString);
        View v = getCurrentFocus();
        if (v!= null){
            Snackbar.make(this.getCurrentFocus(), toastString, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }



    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

//    protected void showNetworkError() {
//        showSnackBar(getString(R.string.network_error));
//    }

    protected void showSnackBar(String text) {
        showSnackBar(text, true);
    }

    protected void showSnackBar(String text, boolean isError) {
        if(mCoordinatorLayout != null) {
            Snackbar snackbar = Snackbar.make(
                    mCoordinatorLayout,
                    text,
                    Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();

            if(isError) {
                snackbarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.error_color));
            } else {
                snackbarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.text_color));
            }

            snackbar.show();

        } else {
            Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
        }
    }

    protected void launchActivity(Class activityClass) {
        Intent i = new Intent(mContext, activityClass);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInternetConnected();
    }

    protected boolean isInternetConnected() {
        if (mContext != null) {
            ConnectivityManager connectivityMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityMgr != null) {
                NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }


    protected void onFirebaseUpdated() {
    }

    protected void locationServicesEnabled() { //Override this method when location ON
    }

    protected String getFormattedTime(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat("hh:mm:a");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

}
