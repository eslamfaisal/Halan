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


import java.util.List;

public class PlaceAdapter2  extends RecyclerView.Adapter<PlaceAdapter2.PlacesViewHolder> {
    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Results> placesResults;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

//    public PlacesAdapter2(Context context) {
//        this.context = context;
////        this.mCallback = (PaginationAdapterCallback) context;
//        placesResults = new ArrayList<>();
//    }


    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_places_item_list, viewGroup, false);
        return new PlacesViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        final PlacesViewHolder placesViewHolder = (PlacesViewHolder) holder;
        Results result = placesResults.get(position);

        placesViewHolder.placeImage.setImageURI(result.getIcon());
        placesViewHolder.placeName.setText(result.getFormatted_address());
        // placesViewHolder.

    }

    @Override
    public int getItemCount() {
        return placesResults.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView placeImage;
        TextView placeName;
        TextView placeDistance;
        View mainView;

        public PlacesViewHolder(@NonNull View rootView) {
            super(rootView);
            mainView = rootView;
            placeDistance = rootView.findViewById(R.id.place_distance);
            placeName = rootView.findViewById(R.id.place_name);
            placeImage = rootView.findViewById(R.id.place_image);
        }
    }


    @Override
    public int getItemViewType(int position) {

        return  ITEM;

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
        notifyDataSetChanged();
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


}
