package com.fekrah.halan.driver.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.common.SamplePresenter;
import com.fekrah.halan.common.activities.SplashActivity;
import com.fekrah.halan.driver.DissconnectService;
import com.fekrah.halan.driver.FloatingService;
import com.fekrah.halan.driver.fragments.TalabatFragment;
import com.fekrah.halan.helper.CalculateDistanceTime;
import com.fekrah.halan.helper.SharedHelper;
import com.fekrah.halan.models.Driver;
import com.fekrah.halan.models.Order;
import com.fekrah.halan.models.User;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yayandroid.locationmanager.base.LocationBaseActivity;
import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.ProcessType;

import java.util.ArrayList;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityDriver extends LocationBaseActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, SamplePresenter.SampleView {

    private static final String TAG = "MainActivityDriver";
    public static final int PLACE_PICKER_REQUEST = 3;

    @BindView(R.id.receiver_place_actv)
    AutoCompleteTextView mSearchText;

    @BindView(R.id.arrival_place_tv)
    TextView arrivalPlaceTv;

    @BindView(R.id.arrival_place_v)
    View arrival_place_v;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.order_state_text)
    TextView orderStateText;

    @BindView(R.id.dragView)
    View mainPanel;

    @BindView(R.id.go_map)
    ImageView goMap;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.no_inter)
    TextView noInter;

    //@BindView(R.id.driver_state)
    MaterialAnimatedSwitch materialAnimatedSwitch;

    //    @BindView(R.id.driver_state_text)
    TextView driverstateText;


    private View navHeader;
    TextView txtName;
    private SimpleDraweeView imgProfile;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private SlidingUpPanelLayout mLayout;
    MarkerOptions currentLocationMarkerOption;
    MarkerOptions receiverLocationMarkerOption;
    MarkerOptions arrivalLocationMarkerOption;
    Marker currentLocationMarker;
    Marker receiverLocationMarker;
    Marker arrivalLocationMarker;

    Polyline polyline1;
    Polyline polyline2;
    private ProgressDialog progressDialog;
    private SamplePresenter samplePresenter;
    private Location location;
    private String recieverLocationAdress;

    public static String[] results = new String[2];

    private View view;
    private Animation animation;
    public static Driver driver;

    public static CountDownTimer mCountDownTimer;
    public static int i = 0;
    int c = 0;

    public static boolean orderSent = false;
    public static final int MY_PERMISSION = 34;
    Double receiverLat;
    Double receiverLng;
    double arrivalLat;
    double arrivalLng;
    private String serverKey = "AIzaSyAnKvay92-zyf4Or37UL6tsEF7BL8PiC6U";
    private Polyline distancePoly;
    private Polyline distancePoly2;

    private DatabaseReference orderRef;
    private boolean online;

    private boolean logout = false;
    private boolean onActivit = false;
    public static User user;

    public static boolean accepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        registerBroadCastReceiver();
        samplePresenter = new SamplePresenter(this);
        removeLocation();

        initializeMainView();

        displayOverAppsAndConnectToClient();

        getDriverInfo();

    }

    public static boolean isOrderSent() {
        return orderSent;
    }

    public static void setOrderSent(boolean orderSent) {
        MainActivityDriver.orderSent = orderSent;
    }

    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (!noConnectivity) {
                onConnectionFound();
            } else {
                onConnectionLost();
            }
        }
    };

    private void displayOverAppsAndConnectToClient() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setPositiveButton("الاعدادات", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, MY_PERMISSION);
                finish();
            }
        });


        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(MainActivityDriver.this)) {
                dialog.show();
            } else { // ADD THIS.
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            }
        } else { // ADD THIS.
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
    }

    private void initializeMainView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
        }
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            view = inflater.inflate(R.layout.layout_custom_app_bar, null);
        }
        if (actionBar != null) {
            actionBar.setCustomView(view);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mLayout = findViewById(R.id.sliding_layout);
        mLayout.setAnchorPoint(1f);


        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    ValueEventListener orderListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                final Order order = dataSnapshot.getValue(Order.class);
                if (order != null) {

                    Log.d("aaaaaa", "onDataChange: " + order.getArrival_location());

                    final LatLng arrivalLatLng = new LatLng(order.getA_l_lat(), order.getA_l_lng());
                    final LatLng receiverLatLng = new LatLng(order.getR_l_lat(), order.getR_l_lng());
                    final LatLng myLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    receiverLocationMarkerOption = new MarkerOptions()
                            .position(receiverLatLng)
                            // .icon(bitmapDescriptorFromVector(MainActivityDriver.this, R.drawable.ic_house_marker))
                            .title(getString(R.string.receiver_place) + " : " + order.getReceiver_location());

                    arrivalLocationMarkerOption = new MarkerOptions()
                            .position(arrivalLatLng)
                            .icon(bitmapDescriptorFromVector(MainActivityDriver.this, R.drawable.ic_my_location_marker))
                            .title(getString(R.string.arrival_area) + " : " + order.getArrival_location());

                    receiverLocationMarker = mMap.addMarker(receiverLocationMarkerOption);
                    arrivalLocationMarker = mMap.addMarker(arrivalLocationMarkerOption);

                    GoogleDirection.withServerKey(serverKey)
                            .from(myLocationLatLng)
                            .to(arrivalLatLng)
                            .transportMode(TransportMode.DRIVING)
                            .alternativeRoute(true)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(Direction direction, String rawBody) {

                                    Route route = direction.getRouteList().get(0);

                                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                    distancePoly = mMap.addPolyline(DirectionConverter.createPolyline(MainActivityDriver.this, directionPositionList, 5, getResources().getColor(R.color.colorPrimary)));
                                    // setCameraWithCoordinationBounds(route);
                                    GoogleDirection.withServerKey(serverKey)
                                            .from(arrivalLatLng)
                                            .to(receiverLocationMarker.getPosition())
                                            .transportMode(TransportMode.DRIVING)
                                            .alternativeRoute(true)
                                            .execute(new DirectionCallback() {
                                                @Override
                                                public void onDirectionSuccess(Direction direction, String rawBody) {

                                                    Route route = direction.getRouteList().get(0);

                                                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                                    distancePoly2 = mMap.addPolyline(DirectionConverter.createPolyline(MainActivityDriver.this, directionPositionList, 5, getResources().getColor(R.color.colorPrimary)));
                                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                    builder.include(arrivalLatLng);
                                                    builder.include(receiverLatLng);
                                                    builder.include(myLocationLatLng);

                                                    LatLngBounds bounds = builder.build();
                                                    int padding = 0; // offset from edges of the map in pixels
                                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                                                    mMap.animateCamera(cu);


                                                }

                                                @Override
                                                public void onDirectionFailure(Throwable t) {

                                                }
                                            });

                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {

                                }
                            });

//                    CalculateDistanceTime distance_task = new CalculateDistanceTime(MainActivityDriver.this);
//
//                    distance_task.getDirectionsUrl(new LatLng(arrivalLatLng.latitude, arrivalLatLng.longitude),
//                            new LatLng(myLocationLatLng.latitude, myLocationLatLng.longitude), "AIzaSyAnKvay92-zyf4Or37UL6tsEF7BL8PiC6U");
//
//                    distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
//                        @Override
//                        public void taskCompleted(String[] time_distance) {
////                approximate_time.setText("" + time_distance[1]);
////                approximate_diatance.setText("" + time_distance[0]);
////                results[0]= Float.parseFloat(time_distance[1]);
//                            results[0] = time_distance[0];
//                            results[1] = time_distance[1];
//                        }
//
//                    });

//                    Location.distanceBetween(arrivalLatLng.latitude, arrivalLatLng.longitude,
//                            myLocationLatLng.latitude, myLocationLatLng.longitude, results);

                    talabatFragment.change(order);

                    receiverLat = receiverLatLng.latitude;
                    receiverLng = receiverLatLng.longitude;

                    arrivalLat = order.getA_l_lat();
                    arrivalLng = order.getA_l_lng();


                    goMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (Build.VERSION.SDK_INT >= 23) {
                                /** check if we already  have permission to draw over other apps */
                                if (!Settings.canDrawOverlays(MainActivityDriver.this)) { // WHAT IF THIS EVALUATES TO FALSE.
                                    /** if not construct intent to request permission */
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:" + getPackageName()));
                                    /** request permission via start activity for result */
                                    startActivityForResult(intent, MY_PERMISSION);
                                } else { // ADD THIS.

                                    launchGoogleMap(recieverLocationAdress,
                                            receiverLat, receiverLng, arrivalLat, arrivalLng);
                                    //launchGoogleMap();
                                }

                            } else { // ADD THIS.
                                launchGoogleMap(recieverLocationAdress,
                                        receiverLat, receiverLng, arrivalLat, arrivalLng);
                                //launchGoogleMap();

                            }


                        }
                    });
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(order.getUser_key())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        user = dataSnapshot.getValue(User.class);
                                        if (user != null) {

                                            goMap.setVisibility(View.VISIBLE);
                                            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                            c = 0;
                                            animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                                            animation.setDuration(500); // duration - half a second
                                            animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                                            animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                                            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                                            orderStateText.startAnimation(animation);
                                            orderStateText.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(final View view) {
                                                    orderStateText.clearAnimation();
                                                }
                                            });

                                            counter();
                                            mCountDownTimer.start();
                                            setOrderSent(false);
                                            mProgressBar.setVisibility(View.VISIBLE);
                                            notificationContent(order.getDetails(), order.getArrival_location(), order.getReceiver_location());
                                            createNotify(user.getName(), user.getImg(), notificationContent(order.getDetails(), order.getArrival_location(), order.getReceiver_location()), "orders", 125);
                                            CalculateDistanceTime distance_task = new CalculateDistanceTime(MainActivityDriver.this);

                                            distance_task.getDirectionsUrl(new LatLng(order.getA_l_lat(), order.getA_l_lng()), new LatLng(location.getLatitude(), location.getLongitude()), serverKey);

                                            distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                                                @Override
                                                public void taskCompleted(String[] time_distance) {
//                                                  approximate_time.setText("" + time_distance[1]);
//                                                  approximate_diatance.setText("" + time_distance[0]);
//                                                  results[0] = Float.parseFloat(time_distance[1]);
                                                    results[0] = time_distance[0];
                                                    results[1] = time_distance[1];

                                                    Log.d("aaaaaaaaaaaaaa", "distance =" + results[0]);
                                                    Log.d("aaaaaaaaaaaaaa", "time =" + results[1]);
                                                }

                                            });
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                orderStateText.setText(getString(R.string.new_order));
            } else {
                if (polyline1 != null)
                    polyline1.remove();
                if (polyline2 != null)
                    polyline2.remove();
                if (arrivalLocationMarker != null)
                    arrivalLocationMarker.remove();
                if (receiverLocationMarker != null)
                    receiverLocationMarker.remove();
                if (distancePoly != null)
                    distancePoly.remove();
                if (distancePoly2 != null)
                    distancePoly2.remove();

                orderStateText.setText(getString(R.string.no_orders_yet));

                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                goMap.setVisibility(View.GONE);
                CameraPosition SENDBIS = CameraPosition.builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(17)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SENDBIS), 5000, null);


                FirebaseDatabase.getInstance().getReference().child("drivers")
                        .child(FirebaseAuth.getInstance().getUid()).child("available_balance")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    String balance = dataSnapshot.getValue().toString();
                                    if (Integer.parseInt(balance) < 3) {
                                        materialAnimatedSwitch.toggle();
                                        Toast.makeText(MainActivityDriver.this, R.string.charge_balance, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void fetchOrder() {
        orderRef = FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                .child(FirebaseAuth.getInstance().getUid()).child("order");

        orderRef.addValueEventListener(orderListener);
        //NotifyAccept();
    }

    private void launchGoogleMap(String address, double r1, double r2, double a1, double a2) {

        String format = "geo:0,0?q=" + a1 + "," + a2 + address;

        Uri uri = Uri.parse(format);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);

        Intent intent2 = new Intent(getApplicationContext(), FloatingService.class);
        intent2.putExtra("aLat", a1);
        intent2.putExtra("aLng", a2);
        intent2.putExtra("rLat", r1);
        intent2.putExtra("rLng", r2);

        startService(intent2);

    }

    public void createNotify(final String name, String img, final String content, final String id, final int id2) {

        Intent notifyIntent = new Intent(this, MainActivityDriver.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifyIntent.putExtra("seen", true);
        // Create the PendingIntent
        final PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(id, "New Order", importance);
            channel.setDescription("Notifications for new orders ");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        final Bitmap[] theBitmap = {null};
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_dummy_user);
        requestOptions.error(R.drawable.ic_dummy_user);
        Glide.with(getApplicationContext()).asBitmap().
                load(img).into(new SimpleTarget<Bitmap>(90, 90) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                theBitmap[0] = resource;
                CharSequence charSequence = (CharSequence) content;
                NotificationCompat.Style d = new NotificationCompat.BigTextStyle()
                        .bigText(charSequence);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivityDriver.this, id)
                        .setSmallIcon(R.drawable.shoppingcart)
                        .setContentTitle(name)
                        .setLargeIcon(resource)
                        .setContentText(content)
                        .setStyle(d)
                        .setColor(ContextCompat.getColor(MainActivityDriver.this, R.color.colorPrimary))
                        .setContentIntent(notifyPendingIntent)
                        .setAutoCancel(true)
                        .setColorized(true)
                        .setVibrate(new long[]{1000, 100, 1000, 100})
                        .setAutoCancel(true)
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_order))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivityDriver.this);

                notificationManager.notify(id2, mBuilder.build());

            }

        });

    }

    private String notificationContent(String details, String arrival_location, String receiver_location) {
        return
                getString(R.string.new_order) + "\n \n"
                        +
                        getString(R.string.from) + arrival_location + "\n \n" + getString(R.string.to) + receiver_location;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    private void getDriverInfo() {
        FirebaseDatabase.getInstance().getReference().child("drivers")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            driver = dataSnapshot.getValue(Driver.class);
                            navHeader = navigationView.getHeaderView(0);
                            txtName = (TextView) navHeader.findViewById(R.id.usernameTxt);
                            imgProfile = navHeader.findViewById(R.id.profile_image);
                            txtName.setText(driver.getName());
                            imgProfile.setImageURI(driver.getImg());
                            //  startService(new Intent(getApplicationContext(), AcceptOrderIntentService.class));

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startService(new Intent(getApplicationContext(), DissconnectService.class));
        getLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkStateReceiver);
//        locationManager.onDestroy();
        if (samplePresenter != null)
            samplePresenter.destroy();

        if (!logout)
            removeLocation();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void registerBroadCastReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);

//        if (isNetworkAvailable()) {
//            Log.d("adadad", "onConnectionLost: co");
//
//            FirebaseDatabase.getInstance().goOnline();
//        } else {
//
//            Log.d(TAG, "onConnectionLost: dis");
//            FirebaseDatabase.getInstance().goOffline();
//
//
//        }
    }

    public void onConnectionLost() {
        Log.d("adadad", "onConnectionLost: dis");
        FirebaseDatabase.getInstance().goOffline();
        noInter.setVisibility(View.VISIBLE);
    }

    public void onConnectionFound() {
        Log.d("adadad", "onConnectionLost: co");
        noInter.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().goOnline();
    }


    boolean changed = false;

    int a = 0;

    @Override
    public void onLocationChanged(Location location2) {
        location = location2;
        if (!changed) {
            changed = true;
            samplePresenter.onLocationChanged(location);
            initMap();
        } else {
            if (online)
                new LocationTask().execute();
        }

        Log.d("aaaa locatin", "onLocationChanged: " + location2.toString());
    }

    public class LocationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            updateDriverLocation();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (currentLocationMarker != null)
                currentLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    DatabaseReference ref = FirebaseDatabase.getInstance()
            .getReference().child("drivers_location");
    GeoFire geoFire = new GeoFire(ref);

    private void updateDriverLocation() {

        geoFire.setLocation(FirebaseAuth.getInstance().getUid(),
                new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {

                        } else {
                        }
                    }
                });
    }

    private void setDriverLocation() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("drivers_location");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(FirebaseAuth.getInstance().getUid(),
                new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                                    .child(FirebaseAuth.getInstance().getUid()).removeValue();
                        } else {
                            System.out.println("Location saved on server successfully!");
                        }
                    }
                });
    }


    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            MainActivityDriver.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("ahahahah", "run: tasker");
                    locationManager.get();
                }
            });
        }
    }

    @Override
    public void onLocationFailed(@FailType int failType) {
        samplePresenter.onLocationFailed(failType);
    }

    @Override
    public void onProcessTypeChanged(@ProcessType int processType) {
        samplePresenter.onProcessTypeChanged(processType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onActivit = true;
        if (getLocationManager().isWaitingForLocation()
                && !getLocationManager().isAnyDialogShowing()) {
            displayProgress();
        }
        hideKeyBoard();

    }


    @Override
    protected void onPause() {
        super.onPause();
        onActivit = false;
        dismissProgress();
    }

    private void displayProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage(getString(R.string.getting_locations));
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    String string;

    @Override
    public String getText() {
        return string;
    }

    @Override
    public void setText(String text) {
        string = text;
    }

    @Override
    public void updateProgress(String text) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(text);
        }
    }

    @Override
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    TalabatFragment talabatFragment = new TalabatFragment();

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return talabatFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }
    }

    @Override
    public LocationConfiguration getLocationConfiguration() {
        return Configurations.defaultConfiguration(getString(R.string.get_location_permistion),
                getString(R.string.gps_message));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        // if (!seen)
        initialMoveCamera(new LatLng(location.getLatitude(), location.getLongitude()), getResources().getString(R.string.my_location));
        init(new LatLng(location.getLatitude(), location.getLongitude()));

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission
                        (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

    }


    private void initialMoveCamera(LatLng latLng, String location) {
        CameraPosition SENDBIS = CameraPosition.builder()
                .target(latLng)
                .zoom(17)
                .build();
        Log.d(TAG, "movCamera: move to latlang " + latLng.toString());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SENDBIS), 5000, null);
        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        // mMap.addMarker(new MarkerOptions().position(latLng));
        hideKeyBoard();
    }

    private void moveCameraCurrentLocation(LatLng latLng, String location) {
        recieverLocationAdress = getString(R.string.my_location);
        if (currentLocationMarker != null)
            currentLocationMarker.remove();
        currentLocationMarkerOption = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker))
                .title(location);
        currentLocationMarker = mMap.addMarker(currentLocationMarkerOption);

        CameraPosition SENDBIS = CameraPosition.builder()
                .target(latLng)
                .zoom(17)
                .build();
        Log.d(TAG, "movCamera: move to latlang " + latLng.toString());

        // if (!seen)
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SENDBIS), 5000, null);
        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        // mMap.addMarker(new MarkerOptions().position(latLng));
        hideKeyBoard();

    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void hideKeyBoard() {
        Activity activity = MainActivityDriver.this;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void removeLocation() {
        if (distancePoly != null)
            distancePoly.remove();
        if (distancePoly2 != null)
            distancePoly2.remove();

        FirebaseDatabase.getInstance()
                .getReference().child("drivers_location").child(FirebaseAuth.getInstance().getUid()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                                .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: distroy");
                                }

                            }
                        });
                    }
                });


    }

    private void counter() {
        i = 0;
        mProgressBar.setProgress(0);
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;

        }
        mCountDownTimer = new CountDownTimer(40000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress" + i + millisUntilFinished);
                i++;
                mProgressBar.setProgress((int) i * 100 / (40000 / 1000));
            }

            @Override
            public void onFinish() {
                //Do what you want

                if (!isOrderSent()) {

                    FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.d(TAG, "onComplete: ");
                        }
                    });

                }
                i = 0;
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
            }
        };
    }

    private void initMap() {

        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
            }
        } else if (requestCode == MY_PERMISSION) {

        } else {
            locationManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void init(LatLng latLng) {


        goMap.setVisibility(View.GONE);
        //mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                // setDistance(results[0]);
                if (newState.equals("EXPANDED")) {
                    mainPanel.setVisibility(View.GONE);
                } else {
                    mainPanel.setVisibility(View.VISIBLE);
                }

                if (c == 2) {
                    if (animation != null) {
                        orderStateText.clearAnimation();
                    }
                } else {
                    c += 1;
                }
            }
        });

        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        driverstateText = view.findViewById(R.id.driver_state_text);
        materialAnimatedSwitch = view.findViewById(R.id.driver_state);

        materialAnimatedSwitch.setOnCheckedChangeListener(
                new MaterialAnimatedSwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(boolean isChecked) {
                        if (isChecked) {

                            FirebaseDatabase.getInstance().getReference().child("drivers")
                                    .child(FirebaseAuth.getInstance().getUid()).child("available_balance")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                String balance = dataSnapshot.getValue().toString();
                                                if (Integer.parseInt(balance) >= 3) {
                                                    driverstateText.setText(R.string.online);
                                                    driverstateText.setTextColor(Color.GREEN);
                                                    moveCameraCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()), "My location");
                                                    if (ActivityCompat.checkSelfPermission(MainActivityDriver.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivityDriver.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                        return;
                                                    }
                                                    mMap.setMyLocationEnabled(false);
                                                    setDriverLocation();
                                                    online = true;
                                                } else {
                                                    materialAnimatedSwitch.toggle();
                                                    Toast.makeText(MainActivityDriver.this, R.string.charge_balance, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                        } else {
                            if (mCountDownTimer != null) {
                                mCountDownTimer.cancel();
                                mCountDownTimer.onFinish();
                            }
                            driverstateText.setTextColor(Color.RED);
                            driverstateText.setText(getString(R.string.offline));
                            if (currentLocationMarker != null)
                                currentLocationMarker.remove();
                            mMap.setMyLocationEnabled(true);

                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            removeLocation();
                            online = false;

                        }
                    }
                });


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, talabatFragment);
        ft.commit();

        fetchOrder();

        arrival_place_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MainActivityDriver.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage());
                }
            }

        });

        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                .child(FirebaseAuth.getInstance().getUid()).child("offer")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue().equals("sent")) {
                                if (mProgressBar != null) {
                                    setOrderSent(true);
                                    mProgressBar.setVisibility(View.GONE);
                                    mCountDownTimer.onFinish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            removeLocation();
            online = false;
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                mCountDownTimer = null;
            }
            if (orderListener != null&&orderRef!=null)
                orderRef.removeEventListener(orderListener);
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_chats) {
            startActivity(new Intent(this, ChatsRoomsDriverActivity.class));
        }
//
//
// else if (id == R.id.nav_edit) {
//            startActivity(new Intent(this, EditProfileActivity.class));
//        }
//
        else if (id == R.id.nav_log_out) {
            logout = true;
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                mCountDownTimer = null;
            }

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference().child("drivers_location");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(FirebaseAuth.getInstance().getUid(), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(MainActivityDriver.this, SplashActivity.class);
                                startActivity(intent);
                                SharedHelper.putKey(getApplicationContext(), "uses_type", "customer");
                                finish();
                            }
                        }
                    });
                }
            });


        }

//        else if (id == R.id.nave_balance) {
//            startActivity(new Intent(this, BalanceActivity.class));
//        }
//
 else if (id == R.id.nav_old_orders) {
            startActivity(new Intent(this, OldOrdersDriverActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        // Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        //vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth() + 20, background.getIntrinsicHeight() + 20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        //vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

//    private void NotifyAccept() {
//        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
//                .child(FirebaseAuth.getInstance().getUid()).child("offer")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.getValue() != null) {
//                            if (dataSnapshot.getValue().toString().equals("accept")) {
//                                if (!accepted) {
//                                    accepted = true;
//                                    createNotifyAccept(user.getName(), user.getImg(), getString(R.string.accept_offer), "accept_order", 124);
//                                }
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }

    public void createNotifyAccept(final String name, String img, final String content, final String id, final int id2) {

//        Intent notifyIntent = new Intent(this, ChatsRoomsDriverActivity.class);
//        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        // Create the PendingIntent
//        final PendingIntent notifyPendingIntent = PendingIntent.getActivity(
//                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
//        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(id, "Accept Order", importance);
            channel.setDescription("Notifications for accepted orders ");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        final Bitmap[] theBitmap = {null};
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_dummy_user);
        requestOptions.error(R.drawable.ic_dummy_user);
        Glide.with(this).asBitmap().
                load(img).into(new SimpleTarget<Bitmap>(90, 90) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                theBitmap[0] = resource;
                CharSequence charSequence = (CharSequence) content;
                NotificationCompat.Style d = new NotificationCompat.BigTextStyle()
                        .bigText(charSequence);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivityDriver.this, id)
                        .setSmallIcon(R.drawable.shoppingcart)
                        .setContentTitle(name)
                        .setLargeIcon(resource)
                        .setContentText(content)
                        .setStyle(d)
                        .setColor(ContextCompat.getColor(MainActivityDriver.this, R.color.colorPrimary))
                        // .setContentIntent(notifyPendingIntent)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 100, 1000, 100})
                        .setAutoCancel(true)
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_order))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivityDriver.this);

                notificationManager.notify(id2, mBuilder.build());

//                try {
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//                    ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.new_order));
//                    if (!ringtone.isPlaying())
//                        ringtone.play();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }

        });

    }


}
