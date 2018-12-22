package com.fekrah.halan.customer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.common.SamplePresenter;
import com.fekrah.halan.common.activities.LoginActivity;
import com.fekrah.halan.customer.activities.ChatsRoomsActivity;
import com.fekrah.halan.customer.activities.CurrentOrderActivity;
import com.fekrah.halan.customer.activities.MyOrdersActivity;
import com.fekrah.halan.customer.places.PlacesActivity;
import com.fekrah.halan.customer.places.Results;
import com.fekrah.halan.driver.activities.LoginActivityDriver;
import com.fekrah.halan.helper.CalculateDistanceTime;
import com.fekrah.halan.helper.SharedHelper;
import com.fekrah.halan.models.Order;
import com.fekrah.halan.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafakob.drawme.DrawMeButton;
import com.yayandroid.locationmanager.base.LocationBaseActivity;
import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.constants.FailType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerMainActivity extends LocationBaseActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, SamplePresenter.SampleView, DirectionCallback {
    private String TAG = "customer_main activity";

    @BindView(R.id.target_place)
    TextView targetPlace;

    @BindView(R.id.rout_cost)
    TextView rout_cost;

    @BindView(R.id.rout_distance)
    TextView rout_distance;

    @BindView(R.id.rout_time)
    TextView rout_time;

    @BindView(R.id.next)
    TextView next;

    @BindView(R.id.routs_details_view)
    View routs_details_view;

    @BindView(R.id.near_by_places)
    ImageView near_by_places;

    @BindView(R.id.my_places)
    ImageView my_places;

    @BindView(R.id.order_details)
    EditText orderDetails;

    private PlaceAutocompleteFragment autocompleteFragment;
    private PlaceAutocompleteFragment autocompleteFragment2;
    private TextView mTextMessage;
    private ProgressDialog progressDialog;
    private SamplePresenter samplePresenter;
    public static Location location;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_more:
                    //   mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_orders:
                    startActivity(new Intent(getApplicationContext(), MyOrdersActivity.class));
                    return true;
                case R.id.navigation_notifications:
                    //    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    private static final int GET_PLACE_REQUEST = 68;
    private LatLng receiver;
    private String recieverLocationAdress;

    LatLng currentLocationLatLng;
    MarkerOptions currentLocationMarkerOption;
    MarkerOptions receiverLocationMarkerOption;
    MarkerOptions arrivalLocationMarkerOption;
    Marker currentLocationMarker;
    Marker receiverLocationMarker;
    Marker arrivalLocationMarker;

    private Polyline distancePoly;
    Polyline polyline1;
    private LatLng arrival;
    private String arrivalLocationAddress;
    private BottomSheetBehavior bottomSheetBehavior;
    private String targetPlaceAddress;
    private LatLng targetPlaceLatlang;
    public static final String LOG_TYP = "LOG_TYP";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String DRIVER = "DRIVER";
    public static final int LOG_IN_REQUEST = 321;
    public static final String ORDER_ID = "ORDER_ID";
    private ProgressDialog orderDialog;
    public static User currentUser;
    public static View userInfiView;
    public View navHeader;
    public static TextView txtName;
    public static SimpleDraweeView imgProfile;
    public static View loginView;
    public static NavigationView navigationView;

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
    public LocationConfiguration getLocationConfiguration() {
        return Configurations.defaultConfiguration("Gimme the permission!",
                "Would you mind to turn GPS on?");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cutomer);
        ButterKnife.bind(this);

        initButtomSheet();

        samplePresenter = new SamplePresenter(this);
        initViews();

        connectClientWithCountry();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                currentUser = dataSnapshot.getValue(User.class);
                                showUserInfo();
                            } else {
                                showLogin();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            showLogin();
        }

    }

    private void initButtomSheet() {
        // get the bottom sheet view
        RelativeLayout llBottomSheet = findViewById(R.id.bottom_sheet);

// init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

// change the state of the bottom sheet
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


// set the peek height
        bottomSheetBehavior.setPeekHeight(0);

// set hideable or not
        bottomSheetBehavior.setHideable(true);

// set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                Log.d("ssssssss", "onStateChanged: " + newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void connectClientWithCountry() {
        if (!SharedHelper.getKey(CustomerMainActivity.this, "lang").equals("EG") &&
                !SharedHelper.getKey(CustomerMainActivity.this, "lang").equals("SA")) {
            Log.d("hahahahah", "onCreate: null");
            final Dialog builder = new Dialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.cuntry_layout, null);
            builder.setContentView(view);
            DrawMeButton egypt = view.findViewById(R.id.egypt);
            DrawMeButton saudia = view.findViewById(R.id.saudia);
            egypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedHelper.putKey(CustomerMainActivity.this, "lang", "EG");
                    builder.dismiss();
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();
                }
            });
            saudia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedHelper.putKey(CustomerMainActivity.this, "lang", "SA");
                    builder.dismiss();
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();
                }
            });
            builder.setCancelable(false);
            builder.show();
        } else {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
    }

    void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.usernameTxt);
        imgProfile = navHeader.findViewById(R.id.profile_image);
        loginView = navHeader.findViewById(R.id.log_in_view);
        userInfiView = navHeader.findViewById(R.id.user_info_view);
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(CustomerMainActivity.this, LoginActivity.class);
                logIntent.putExtra(LOG_TYP, CUSTOMER);
                startActivityForResult(logIntent, LOG_IN_REQUEST);
            }
        });
    }

    void showLogin() {
        loginView.setVisibility(View.VISIBLE);
        userInfiView.setVisibility(View.GONE);
        navigationView.getMenu().getItem(0).setVisible(false);
        navigationView.getMenu().getItem(1).setVisible(false);
        navigationView.getMenu().getItem(2).setVisible(false);
    }

    public static void showUserInfo() {
        loginView.setVisibility(View.GONE);
        userInfiView.setVisibility(View.VISIBLE);
        imgProfile.setImageURI(currentUser.getImg());
        txtName.setText(currentUser.getName());
        navigationView.getMenu().getItem(0).setVisible(true);
        navigationView.getMenu().getItem(1).setVisible(true);
        navigationView.getMenu().getItem(2).setVisible(true);
    }

    void initPlaceFragment(LatLng latLng) {
        LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(latLng, latLng);

        //  new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS).build();
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .setCountry(SharedHelper.getKey(CustomerMainActivity.this, "lang"))
                .build();

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                moveCameraArrivalLocation(place.getLatLng(), "Arrival ResultLocation");
                targetPlace.setText(place.getAddress());
                routs_details_view.setVisibility(View.VISIBLE);
                targetPlaceAddress = place.getAddress().toString();
                targetPlaceLatlang = place.getLatLng();
            }

            @Override
            public void onError(Status status) {

            }
        });

        autocompleteFragment.setBoundsBias(LAT_LNG_BOUNDS);
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment2 = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment2);

        autocompleteFragment2.setBoundsBias(LAT_LNG_BOUNDS);
        autocompleteFragment2.setFilter(typeFilter);
        final EditText etPlace2 = (EditText) autocompleteFragment2.getView()
                .findViewById(R.id.place_autocomplete_search_input);
        etPlace2.setHint(getString(R.string.choose_another_place));
        etPlace2.setTextSize(18.0f);
        etPlace2.setTextColor(Color.WHITE);
        etPlace2.setHintTextColor(getResources().getColor(R.color.gray));
        etPlace2.setTextColor(getResources().getColor(android.R.color.white));
        AppCompatImageButton clearbtn3 = autocompleteFragment2.getView()
                .findViewById(R.id.place_autocomplete_search_button);
        clearbtn3.setImageResource(R.drawable.ic_close_for_fragment);
        AppCompatImageButton clearbtn2 = autocompleteFragment2.getView()
                .findViewById(R.id.place_autocomplete_clear_button);
        clearbtn2.setImageResource(R.drawable.ic_close_for_fragment);

        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                moveCameraReceiverLocation(place.getLatLng(), "Arrival ResultLocation");

                //targetPlace.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {

            }
        });
        my_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiverLocationMarker != null || arrivalLocationMarker != null) {

                    moveCameraReceiverLocation((new LatLng(location.getLatitude(), location.getLongitude())),
                            recieverLocationAdress);
                    etPlace2.setText(getString(R.string.my_location));
                } else {
                    moveCameraCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()), getString(R.string.my_location));
                    receiver = new LatLng(location.getLatitude(), location.getLongitude());
                    etPlace2.setText(getString(R.string.my_location));
                }
            }
        });
        near_by_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null) {
                    Intent intent = new Intent(CustomerMainActivity.this, PlacesActivity.class);
                    startActivityForResult(intent, GET_PLACE_REQUEST);
                }

            }
        });
//        PlacesOptions.Builder filter = new PlacesOptions.Builder();
//
//        filter.build();
//
//        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(this, filter.build());
//
//        @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
//                mPlaceDetectionClient.getCurrentPlace(null);
//
//        placeResult.addOnCompleteListener
//                (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//
//                            //receiverPlace.setText(likelyPlaces.get(0).getPlace().getAddress());
//                            recieverLocationAdress = likelyPlaces.get(0).getPlace().getAddress().toString();
//
//                            likelyPlaces.release();
//
//                        } else {
//                            Log.e(TAG, "Exception: %s", task.getException());
//                        }
//                    }
//                });
//
//        targetPlace.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new PlaceAutocompleteFragment();
//
//            }
//        });
    }

    private String serverKey = "AIzaSyAnKvay92-zyf4Or37UL6tsEF7BL8PiC6U";
    String[] results = new String[2];

    public void moveCameraArrivalLocation(LatLng latLng, String location) {
        targetPlaceLatlang = latLng;
        if (arrivalLocationMarker != null) {
            arrivalLocationMarker.remove();
        }

        if (polyline1 != null)
            polyline1.remove();

        if (distancePoly != null) {
            distancePoly.remove();
        }

        arrivalLocationMarkerOption = new MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_location_green))
                .title(location);
        arrivalLocationMarker = mMap.addMarker(arrivalLocationMarkerOption);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng latLng1 = null;

        if (receiverLocationMarkerOption != null) {
            latLng1 = receiver;
//            builder.include(latLng1);
//            polyline1 = mMap.addPolyline(new PolylineOptions()
//                    .clickable(true)
//                    .color(R.color.colorPrimary)
//
//                    .add(receiverLocationMarkerOption.getPosition(),
//                            arrivalLocationMarkerOption.getPosition()));
            CalculateDistanceTime distance_task = new CalculateDistanceTime(this);

            distance_task.getDirectionsUrl(latLng1, latLng, serverKey);

            distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                @Override
                public void taskCompleted(String[] time_distance) {
//                approximate_time.setText("" + time_distance[1]);
//                approximate_diatance.setText("" + time_distance[0]);
//                results[0]= Float.parseFloat(time_distance[1]);
                    results[0] = time_distance[0];
                    results[1] = time_distance[1];
                    rout_time.setText(time_distance[0]);
                    rout_distance.setText(time_distance[1]);
                    Log.d("aaaaaaaaaaaaaa", "distance =" + results[0]);
                    Log.d("aaaaaaaaaaaaaa", "time =" + results[1]);
                }

            });
            GoogleDirection.withServerKey(serverKey)
                    .from(latLng1)
                    .to(latLng)
                    .transportMode(TransportMode.DRIVING)
                    .alternativeRoute(true)
                    .execute(this);
        } else {
            latLng1 = currentLocationMarkerOption.getPosition();
            builder.include(latLng1);
//            polyline1 = mMap.addPolyline(new PolylineOptions()
//                    .clickable(true)
//                    .color(R.color.colorPrimary)
//
//                    .add(currentLocationMarkerOption.getPosition(),
//                            arrivalLocationMarkerOption.getPosition()));
            CalculateDistanceTime distance_task = new CalculateDistanceTime(this);

            distance_task.getDirectionsUrl(latLng1, latLng, serverKey);

            distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                @Override
                public void taskCompleted(String[] time_distance) {
//                approximate_time.setText("" + time_distance[1]);
//                approximate_diatance.setText("" + time_distance[0]);
//                results[0]= Float.parseFloat(time_distance[1]);
                    results[0] = time_distance[0];
                    results[1] = time_distance[1];

                    rout_time.setText(results[0]);
                    rout_distance.setText(results[1]);
                    Log.d("aaaaaaaaaaaaaa", "distance =" + results[0]);
                    Log.d("aaaaaaaaaaaaaa", "time =" + results[1]);
                }

            });
            GoogleDirection.withServerKey(serverKey)
                    .from(latLng1)
                    .to(latLng)
                    .transportMode(TransportMode.DRIVING)
                    .alternativeRoute(true)
                    .execute(this);
        }

//        builder.include(arrivalLocationMarkerOption.getPosition());
//        LatLngBounds bounds = builder.build();
//        int padding = 0; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 250);
//        mMap.animateCamera(cu);
//        ResultLocation.distanceBetween(latLng.latitude, latLng.longitude,
//                latLng1.latitude, latLng1.longitude, results);

        arrival = latLng;

        //   Toast.makeText(this, "" + results[0], Toast.LENGTH_SHORT).show();
    }

    public void moveCameraReceiverLocation(LatLng latLng, String location) {
        receiver = latLng;
        if (arrivalLocationMarker == null) {
            if (distancePoly != null) {
                distancePoly.remove();
            }
            if (currentLocationMarker != null)
                currentLocationMarker.remove();

            if (polyline1 != null) polyline1.remove();

            if (receiverLocationMarker != null)
                receiverLocationMarker.remove();
            receiverLocationMarkerOption = new MarkerOptions()
                    .position(latLng)
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_my_location_marker))
                    .title(location);
            receiverLocationMarker = mMap.addMarker(receiverLocationMarkerOption);

            CameraPosition SENDBIS = CameraPosition.builder()
                    .target(latLng)
                    .zoom(17)
                    .build();
            Log.d(TAG, "movCamera: move to latlang " + latLng.toString());

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SENDBIS), 5000, null);

            return;
        }

        if (distancePoly != null) {
            distancePoly.remove();
        }
        if (currentLocationMarker != null)
            currentLocationMarker.remove();

        if (polyline1 != null) polyline1.remove();

        if (receiverLocationMarker != null)
            receiverLocationMarker.remove();

        receiverLocationMarkerOption = new MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_my_location_marker))
                .title(location);
        receiverLocationMarker = mMap.addMarker(receiverLocationMarkerOption);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng latLng1 = arrivalLocationMarkerOption.getPosition();

        CalculateDistanceTime distance_task = new CalculateDistanceTime(this);

        distance_task.getDirectionsUrl(latLng1, latLng, serverKey);

        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
            @Override
            public void taskCompleted(String[] time_distance) {
//                approximate_time.setText("" + time_distance[1]);
//                approximate_diatance.setText("" + time_distance[0]);
//                results[0]= Float.parseFloat(time_distance[1]);
                results[0] = time_distance[0];
                results[1] = time_distance[1];
                rout_time.setText(results[0]);
                rout_distance.setText(results[1]);
                Log.d("aaaaaaaaaaaaaa", "distance =" + results[0]);
                Log.d("aaaaaaaaaaaaaa", "time =" + results[1]);

            }

        });


//        ResultLocation.distanceBetween(latLng.latitude, latLng.longitude,
//                latLng1.latitude, latLng1.longitude, results);

        GoogleDirection.withServerKey(serverKey)
                .from(latLng1)
                .to(latLng)
                .transportMode(TransportMode.DRIVING)
                .alternativeRoute(true)
                .execute(this);


    }


    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (sheetState) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle customer_navigation view item clicks here.
        // Handle customer_navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_current_order) {
//            Intent intent = new Intent(this, CurrentOrderActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//
//        } else
//
//
        if (id == R.id.nav_chats) {
            startActivity(new Intent(this, ChatsRoomsActivity.class));
        }

// else if (id == R.id.nav_edit) {
//            startActivity(new Intent(this, EditProfileActivity.class));
//        }
// else
        if (id == R.id.nav_log_out) {
            //  startActivity(new Intent(this, LoginActivity.class));
            FirebaseAuth.getInstance().signOut();
            showLogin();
            //  finish();
        } else if (id == R.id.nav_places) {
            if (location != null) {
                Intent intent = new Intent(CustomerMainActivity.this, PlacesActivity.class);
                startActivityForResult(intent, GET_PLACE_REQUEST);
            }
        } else if (id == R.id.nav_login_as_driver) {
            FirebaseAuth.getInstance().signOut();
            showLogin();
            Intent logIntent = new Intent(CustomerMainActivity.this, LoginActivityDriver.class);
            startActivity(logIntent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

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

        moveCameraCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()), "My location");
        //  init(new LatLng(location.getLatitude(), location.getLongitude()));

    }


    private void moveCameraCurrentLocation(LatLng latLng, String location) {
        recieverLocationAdress = getString(R.string.my_location);
        receiver = latLng;
        if (currentLocationMarker != null)
            currentLocationMarker.remove();

        currentLocationMarkerOption = new MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_my_location_marker))
                .title(location);
        CameraPosition SENDBIS = CameraPosition.builder()
                .target(latLng)
                .zoom(17)
                .build();
        Log.d(TAG, "movCamera: move to latlang " + latLng.toString());

        currentLocationMarker = mMap.addMarker(currentLocationMarkerOption);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SENDBIS), 5000, null);
        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        // mMap.addMarker(new MarkerOptions().position(latLng));
        hideKeyBoard();

    }

    public void hideKeyBoard() {
        Activity activity = CustomerMainActivity.this;
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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        // Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        //vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        //vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    boolean initFirst = false;

    @Override
    public void onLocationChanged(Location location2) {
        dismissProgress();
        location = location2;
        if (!initFirst) {
            initFirst = true;
            initMap();
            initPlaceFragment(new LatLng(location.getLatitude(), location.getLongitude()));
        }

    }


    private void initMap() {

        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onLocationFailed(@FailType int failType) {
        samplePresenter.onLocationFailed(failType);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (getLocationManager().isWaitingForLocation()
                && !getLocationManager().isAnyDialogShowing()) {
            displayProgress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (samplePresenter != null)
            samplePresenter.destroy();
    }


    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_PLACE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Results result = (Results) data.getSerializableExtra("result");
                // Toast.makeText(this, result.getName(), Toast.LENGTH_SHORT).show();
                arrivalLocationAddress = result.getName();
                autocompleteFragment.setText(arrivalLocationAddress);
                if (result.getFormatted_address() != null)
                    targetPlace.setText(result.getFormatted_address());
                else targetPlace.setText(arrivalLocationAddress);
                moveCameraArrivalLocation(new LatLng(result.getGeometry().getResultLocation().getLat(),
                        result.getGeometry().getResultLocation().getLng()), arrivalLocationAddress);
                routs_details_view.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == LOG_IN_REQUEST) {
            if (resultCode == LoginActivity.LOGIN_OK) {
                currentUser = (User) data.getSerializableExtra("current_user");
                showUserInfo();
                if (loginWithOrder) {
                    loginWithOrder = false;
                    uploadOrder();
                }
            }
        }
        locationManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        Route route = direction.getRouteList().get(0);
//        mMap.addMarker(new MarkerOptions().position(origin));
//        mMap.addMarker(new MarkerOptions().position(destination));

        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
        distancePoly = mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, getResources().getColor(R.color.colorPrimary)));
        setCameraWithCoordinationBounds(route);
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    boolean sheetState = false;

    public void next(View view) {
        //  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetState = true;
    }

    public void hidSheet(View view) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetState = false;
    }

    boolean loginWithOrder = false;

    public void send_order(View view) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uploadOrder();
        } else {
            loginWithOrder = true;
            Intent logIntent = new Intent(this, LoginActivity.class);
            logIntent.putExtra(LOG_TYP, CUSTOMER);
            startActivityForResult(logIntent, LOG_IN_REQUEST);
        }

    }

    void uploadOrder() {
        orderDialog = new ProgressDialog(this);
        orderDialog.setMessage("جار ارسال الطلب");
        orderDialog.show();
        Intent intent = new Intent(this, CurrentOrderActivity.class);
        String ordrKey = FirebaseDatabase.getInstance().getReference().push().getKey();

        final Order order = new Order(
                ordrKey,
                rout_cost.getText().toString(),
                recieverLocationAdress,
                targetPlaceAddress,
                rout_time.getText().toString(),
                orderDetails.getText().toString(),
                FirebaseAuth.getInstance().getUid(),
                receiver.latitude,
                receiver.longitude,
                targetPlaceLatlang.latitude,
                targetPlaceLatlang.longitude,
                currentUser.getName(),
                currentUser.getImg(),
                System.currentTimeMillis(),
                results[1]
        );

        FirebaseDatabase.getInstance().getReference()
                .child("Customer_current_order").child(FirebaseAuth.getInstance().getUid()).child("order").setValue(order)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), CurrentOrderActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra(ORDER_ID, order);
                        startActivity(intent);
                        orderDialog.dismiss();
                    }
                });
    }
}
