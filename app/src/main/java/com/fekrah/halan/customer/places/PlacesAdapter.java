package com.fekrah.halan.customer.places;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.helper.CalculateDistanceTime;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Results> placesResults;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    LatLng myLatLng;
    String key;
    public static final String PLACE_RESULT="PLACE_RESULT";

    PlaceResultListener placeResultListener ;

    public interface PlaceResultListener{
        void setResult(Results resul);
    }

    public PlacesAdapter(Context context, LatLng myLatLng, String key, PlaceResultListener placeResultListener) {
        this.context = context;
        this.placeResultListener = placeResultListener;
//        this.mCallback = (PaginationAdapterCallback) context;
        placesResults = new ArrayList<>();
        this.key = key;
        this.myLatLng = myLatLng;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.layout_places_item_list, parent, false);
                viewHolder = new PlacesViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final Results result = placesResults.get(position);


        switch (getItemViewType(position)) {

            case ITEM:
                final PlacesViewHolder placesViewHolder = (PlacesViewHolder) holder;

                placesViewHolder.placeImage.setImageURI(result.getIcon());
                placesViewHolder.placeName.setText(result.getName());
                if (result.getFormatted_address()!=null)
                    placesViewHolder.placeLocation.setText(result.getFormatted_address());
                else
                    placesViewHolder.placeLocation.setText(result.getVicinity());
                CalculateDistanceTime distance_task = new CalculateDistanceTime(context);

                distance_task.getDirectionsUrl(new LatLng(result.getGeometry().getResultLocation().getLat(),
                        result.getGeometry().getResultLocation().getLng()), myLatLng, key);

                distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                    @Override
                    public void taskCompleted(String[] time_distance) {
//                approximate_time.setText("" + time_distance[1]);
//                approximate_diatance.setText("" + time_distance[0]);
//                results[0]= Float.parseFloat(time_distance[1]);
//                        results[0] = time_distance[0];
//                        results[1] = time_distance[1];
                        placesViewHolder.placeDistance.setText(time_distance[0]);
                    }

                });

//                    float[] results = new float[1];
//                Location.distanceBetween(result.getGeometry().getResultLocation().getLat(),
//                        result.getGeometry().getResultLocation().getLng(),
//                        myLatLng.latitude, myLatLng.longitude, results);
//
//
//                float finalDistance = 0f;
//                int distance1 = Math.round(results[0]);
//                float smal = results[0] - ((float) distance1);
//                String dis = String.valueOf(smal);
//                if (dis.length() >= 2) {
//                    char s = dis.charAt(2);
//                    float newsmallDistance = (float) (s / 10);
//                    finalDistance = ((float) distance1) + newsmallDistance;
//                }
//
//                float km = finalDistance / 1000.0f;
//                placesViewHolder.placeDistance.setText((String.valueOf(km)) + context.getString(R.string.k_m));

                placesViewHolder.mainView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                   //     ((PlacesActivity) context).onClickCalled("your argument here");
//                        Intent intent = new Intent();
//                        intent.putInt("position",position);
//                        setResult(RESULT_OK,intent);
////close this Activity...
//                        finish();
                        placeResultListener.setResult(result);
//                        Intent intent = new Intent(context, CustomerMainActivity.class);
//                        intent.putExtra(PLACE_RESULT,result);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        context.startActivity(intent);
                    }
                });
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                loadingVH.mProgressBar.setVisibility(View.VISIBLE);

                break;
        }

    }



    @Override
    public int getItemCount() {
        return placesResults.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView placeImage;
        TextView placeName;
        TextView placeLocation;
        TextView placeDistance;
        View mainView;

        public PlacesViewHolder(@NonNull View rootView) {
            super(rootView);
            mainView = rootView;
            placeLocation = rootView.findViewById(R.id.place_location);
            placeDistance = rootView.findViewById(R.id.place_distance);
            placeName = rootView.findViewById(R.id.place_name);
            placeImage = rootView.findViewById(R.id.place_image);
        }
    }


    @Override
    public int getItemViewType(int position) {

        return (position == placesResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;

    }

    public Results getItem(int position) {
        return placesResults.get(position);
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);

        }

    }

    public void add(Results r) {
        placesResults.add(r);
        notifyItemInserted(placesResults.size() - 1);
    }

    public void addAll(List<Results> moveResults) {
        for (Results result : moveResults) {
            add(result);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Results());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = placesResults.size() - 1;
        Results result = getItem(position);

        if (result != null) {
            placesResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        placesResults.clear();

        notifyDataSetChanged();
    }
}
