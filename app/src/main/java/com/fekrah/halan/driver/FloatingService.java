package com.fekrah.halan.driver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bsk.floatingbubblelib.FloatingBubbleConfig;
import com.bsk.floatingbubblelib.FloatingBubbleService;
import com.fekrah.halan.R;
import com.fekrah.halan.driver.activities.MainActivityDriver;
import com.rafakob.drawme.DrawMeButton;

public class FloatingService extends FloatingBubbleService {

    private View view;
    DrawMeButton go_arrival;
    DrawMeButton go_receiver;
    DrawMeButton go_main;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    double a1;
    double a2;
    double r1;
    double r2;
    ImageView a7a;

    @Override
    protected boolean onGetIntent(@NonNull Intent intent) {

        Bundle extras = intent.getExtras();

        a1 = extras.getDouble("aLat");
        a2 = extras.getDouble("aLng");
        r1 = extras.getDouble("rLat");
        r2 = extras.getDouble("rLng");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.sample_view_1, null);
        go_arrival = view.findViewById(R.id.go_arrival);

        go_arrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String format = "geo:0,0?q=" + a1 + "," + a2 + "";
                Uri uri = Uri.parse(format);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                setState(false);

            }
        });

        go_receiver = view.findViewById(R.id.go_receiver);

        go_receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String format = "geo:0,0?q=" + r1 + "," + r2 + "";
                Uri uri = Uri.parse(format);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                setState(false);

            }
        });

        go_main = view.findViewById(R.id.go_main);

        go_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notifyIntent = new Intent(getApplicationContext(), MainActivityDriver.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(notifyIntent);
                setState(false);
                onDestroy();
            }
        });

        return true;
    }

    @Override
    protected FloatingBubbleConfig getConfig() {


        Context context = getApplicationContext();
        return new FloatingBubbleConfig.Builder()
                .bubbleIcon(ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round))
                .removeBubbleIcon(ContextCompat.getDrawable(context, R.drawable.error))
                .bubbleIconDp(54)
                .expandableView(view)
                .removeBubbleIconDp(65)
                .paddingDp(8)
                .borderRadiusDp(8)
                .physicsEnabled(true)
                .expandableColor(R.color.gray)
                .triangleColor(R.color.gray)
                .gravity(Gravity.LEFT)
                .build();


    }
}
