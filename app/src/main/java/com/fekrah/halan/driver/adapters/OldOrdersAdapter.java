package com.fekrah.halan.driver.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.models.OldOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class OldOrdersAdapter extends RecyclerView.Adapter<OldOrdersAdapter.OldOrdersViewHolder> {

    Context context;
    List<OldOrder> oldOrders;

    public OldOrdersAdapter(Context context, List<OldOrder> oldOrders) {
        this.context = context;
        this.oldOrders = oldOrders;
    }

    @NonNull
    @Override
    public OldOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_old_orders, parent, false);
        return new OldOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OldOrdersViewHolder holder, int position) {

        final OldOrder oldOrder = oldOrders.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(oldOrder.getUser_key())){
            holder.userName.setVisibility(View.GONE);
            holder.userImage.setVisibility(View.GONE);

        }else
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(oldOrder.getUser_key())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){
                    holder.userName.setText(dataSnapshot.child("name").getValue().toString());
                    holder.userImage.setImageURI(dataSnapshot.child("img").getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.from.setText(oldOrder.getArrival_location());
        holder.to.setText(oldOrder.getReceiver_location());
        holder.distance.setText(oldOrder.getDistance());
        holder.cost.setText(oldOrder.getCost());
        PrettyTime p = new PrettyTime();
        Date date = new Date(oldOrder.getTime());
        holder.time.setText(p.format(date));
    }

    @Override
    public int getItemCount() {
        return oldOrders.size();
    }

    public void add(OldOrder order) {
        oldOrders.add(0,order);
        notifyDataSetChanged();
    }

    public int indexOf( OldOrder order){
        return oldOrders.indexOf(order);
    }
    public class OldOrdersViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView userImage;
        TextView userName;
        TextView from;
        TextView to;
        TextView distance;
        TextView time;
        TextView cost;
        public OldOrdersViewHolder(View rootView) {
            super(rootView);

            userImage = rootView.findViewById(R.id.profile_image);
            userName = rootView.findViewById(R.id.user_name);
            from = rootView.findViewById(R.id.from_location);
            to = rootView.findViewById(R.id.to_location);
            distance = rootView.findViewById(R.id.distance_location);
            cost = rootView.findViewById(R.id.cost_location);
            time = rootView.findViewById(R.id.time);

        }
    }
}
