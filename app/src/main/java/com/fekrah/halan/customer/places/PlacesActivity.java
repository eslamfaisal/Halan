package com.fekrah.halan.customer.places;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fekrah.halan.R;
import com.fekrah.halan.customer.CustomerMainActivity;
import com.google.android.gms.maps.model.LatLng;

import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesActivity extends AppCompatActivity implements PlacesAdapter.PlaceResultListener {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1023;


    @BindView(R.id.places_recycler_view)
    RecyclerView placesRecyclerView;

    @BindView(R.id.searchview)
    PersistentSearchView mSearchView;

    @BindView(R.id.view_search_tint)
    View mSearchTintView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.loading)
    ProgressBar loading;

    @BindView(R.id.empty_places)
    TextView emptyPlaces;

    PlacesAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    MenuItem mSearchMenuItem;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String nextPage;
    private String key = "AIzaSyAnKvay92-zyf4Or37UL6tsEF7BL8PiC6U";

    public static String myLocation = CustomerMainActivity.location.getLatitude()+","+CustomerMainActivity.location.getLongitude();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markets);

        ButterKnife.bind(this);
        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.nearby_places);
        }
        // setUpSearchView();
        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_RECOGNITION_REQUEST_CODE);
        if (delegate.isVoiceRecognitionAvailable()) {
            mSearchView.setVoiceRecognitionDelegate(delegate);
        }
        // mSearchView.openSearch("Text Query");
        mSearchView.setHomeButtonListener(new PersistentSearchView.HomeButtonListener() {

            @Override
            public void onHomeButtonClick() {
                //Hamburger has been clicked
                onBackPressed();
            }

        });
        mSearchTintView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.cancelEditing();
            }
        });
        mSearchView.setSuggestionBuilder(new SampleSuggestionsBuilder(this));
        mSearchView.setSearchListener(new PersistentSearchView.SearchListener() {

//            @Override
//            public boolean onSuggestion(SearchItem searchItem) {
//                Log.d("onSuggestion", searchItem.getTitle());
//                return false;
//            }

            @Override
            public void onSearchEditOpened() {
                //Use this to tint the screen
                mSearchTintView.setVisibility(View.VISIBLE);
                mSearchTintView
                        .animate()
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new SimpleAnimationListener())
                        .start();
            }

            @Override
            public void onSearchEditClosed() {
                //Use this to un-tint the screen
                mSearchTintView
                        .animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new SimpleAnimationListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mSearchTintView.setVisibility(View.GONE);
                            }
                        })
                        .start();
            }

            @Override
            public boolean onSearchEditBackPressed() {
                return false;
            }

            @Override
            public void onSearchExit() {

            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String string) {
                //   Toast.makeText(PlacesActivity.this, string + " Searched", Toast.LENGTH_LONG).show();
//                mRecyclerView.setVisibility(View.VISIBLE);
//                fillResultToRecyclerView(string);

                try {
                    getFirstPlaces(string);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    List<String> suggestions = (List<String>) InternalStorage.readObject(getApplicationContext(), "sug");
                    if (suggestions != null) {

                        if (!suggestions.contains(string)) {
                            Log.d("aaaa", "onSearch: yes added");
                            suggestions.add(0,string);
                            if (suggestions.size()>=6){
                                suggestions.remove(5);
                            }
                            InternalStorage.writeObject(getApplicationContext(), "sug", suggestions);
                        }else {
                            suggestions.remove(string);
                            suggestions.add(0,string);
                            if (suggestions.size()>=6){
                                suggestions.remove(5);
                            }
                            InternalStorage.writeObject(getApplicationContext(), "sug", suggestions);
                        }

                    }
                    Log.d("aaaa", "onSearch: tray");
                    mSearchView.setSuggestionBuilder(null);
                    mSearchView.setSuggestionBuilder(new SampleSuggestionsBuilder(PlacesActivity.this));


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("aaaa", "IOException: errore " + e.getMessage());
                   if (e.getMessage().contains("No such file or directory")){

                       List<String> suggestion = new ArrayList<>();
                       suggestion.add(0, string);
                       try {
                           InternalStorage.writeObject(getApplicationContext(), "sug", suggestion);
                       } catch (IOException e1) {
                           e1.printStackTrace();
                       }
                       Log.d("aaaa", "onSearch: added");
                   }

                    Log.d("aaaa", "onSearch: tray");
                    mSearchView.setSuggestionBuilder(null);
                    mSearchView.setSuggestionBuilder(new SampleSuggestionsBuilder(PlacesActivity.this));

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.d("aaaa", "ClassNotFoundException: " +e.getMessage());
                }



            }

            @Override
            public void onSearchCleared() {
                //Called when the clear button is clicked
            }

        });

        adapter = new PlacesAdapter(getApplicationContext(), new LatLng(30.2529128, 31.1646382), key,this );
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        placesRecyclerView.setLayoutManager(linearLayoutManager);
        placesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        placesRecyclerView.setAdapter(adapter);

        placesRecyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {

                try {
                    getNextPlaces();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean isLastPage() {
                return false;
            }


            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        try {
            getNearPlaces("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFirstPlaces(String string) throws IOException {
        isLoading = true;
        emptyPlaces.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        adapter.clear();
        String API_BASE_URL = "https://maps.googleapis.com/maps/";
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(getCacheDir(), cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain)
                            throws IOException {
                        Request request = chain.request();

                        int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale \
                        request = request
                                .newBuilder()
                                // .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .build();

                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                // .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        // Retrofit retrofit = builder.client( httpClient.build()).build();

        PlacesClient client = retrofit.create(PlacesClient.class);

        Call<Places> call = client.getFirstPlaces(
                string,
                // 500,
                myLocation,
                key,
                "ar",
                "distance"
        );

        // Execute the call asynchronously. Get a positive or negative callback.
        call.enqueue(new Callback<Places>() {
            @Override
            public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                if (response.body() != null) {
                    if (response.body().getResults().size() > 0) {
                        List<Results> s = response.body().getResults();
                        nextPage = response.body().getNext_page_token();
                        adapter.addAll(s);
                        adapter.addLoadingFooter();
                        isLoading = false;

                    } else {
                        emptyPlaces.setVisibility(View.VISIBLE);
                        Toast.makeText(PlacesActivity.this, R.string.no_places, Toast.LENGTH_SHORT).show();
                    }
                }

                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Places> call, Throwable t) {

            }
        });
    }

    private void getNearPlaces(String string) throws IOException {
        isLoading = true;
        adapter.clear();
        String API_BASE_URL = "https://maps.googleapis.com/maps/";
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(getCacheDir(), cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain)
                            throws IOException {
                        Request request = chain.request();

                        int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale \
                        request = request
                                .newBuilder()
                                // .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .build();

                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                // .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        // Retrofit retrofit = builder.client( httpClient.build()).build();

        PlacesClient client = retrofit.create(PlacesClient.class);

        Call<Places> call = client.getNearPlaces(
                "ar",
                myLocation
        );


        // Execute the call asynchronously. Get a positive or negative callback.
        call.enqueue(new Callback<Places>() {
            @Override
            public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                if (response.body() != null) {
                    if (response.body().getResults().size() > 0) {

                        List<Results> s = response.body().getResults();
                        nextPage = response.body().getNext_page_token();
                        adapter.addAll(s);
                        adapter.addLoadingFooter();
                        isLoading = false;
                        Log.d("aaaaaa", "onResponse: " + response.body().getResults().get(0).getFormatted_address());
                        Log.d("aaaaaa", "reee: " + response.body().getNext_page_token());

                    } else {
                        emptyPlaces.setVisibility(View.VISIBLE);
                        Toast.makeText(PlacesActivity.this, R.string.no_places, Toast.LENGTH_SHORT).show();
                    }
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Places> call, Throwable t) {

            }
        });
    }


    private void getNextPlaces() throws IOException {
        isLoading = true;
        String API_BASE_URL = "https://maps.googleapis.com/maps/";
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(getCacheDir(), cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain)
                            throws IOException {
                        Request request = chain.request();

                        int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale \
                        request = request
                                .newBuilder()
                                // .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .build();

                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        // Retrofit retrofit = builder.client( httpClient.build()).build();

        PlacesClient client = retrofit.create(PlacesClient.class);

        Call<Places> call = client.getNextPlaces(
                nextPage,
                "AIzaSyAnKvay92-zyf4Or37UL6tsEF7BL8PiC6U"
        );

        // Execute the call asynchronously. Get a positive or negative callback.
        call.enqueue(new Callback<Places>() {
            @Override
            public void onResponse(@NonNull Call<Places> call, @NonNull Response<Places> response) {
                if (response.body() != null) {
                    if (response.body().getResults().size() > 0) {
                        adapter.removeLoadingFooter();
                        nextPage = response.body().getNext_page_token();
                        adapter.addAll(response.body().getResults());
                        adapter.addLoadingFooter();
                        isLoading = false;
                        Log.d("aaaaaa", "onResponse: " + response.body().getResults().get(0).getFormatted_address());
                        Log.d("aaaaaa", "reee: " + response.body().getNext_page_token());
                    } else {
                        Toast.makeText(PlacesActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                        adapter.removeLoadingFooter();
                        isLoading = true;
                    }
                }

            }

            @Override
            public void onFailure(Call<Places> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mSearchView.populateEditText(matches);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_searchview, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search:
                if (mSearchMenuItem != null) {
                    openSearch();
                    return true;
                } else {
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSearch() {
        View menuItemView = findViewById(R.id.action_search);
        mSearchView.setStartPositionFromMenuItem(menuItemView);
        mSearchView.openSearch();
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearching()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setResult(Results result) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",result);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void onClickCalled(Results resul) {
    }
}
