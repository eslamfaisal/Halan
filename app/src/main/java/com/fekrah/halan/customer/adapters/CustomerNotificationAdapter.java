package com.fekrah.halan.customer.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.customer.activities.ChatActivity;
import com.fekrah.halan.driver.activities.ChatDriverActivity;
import com.fekrah.halan.helper.GetTimeAgo;
import com.fekrah.halan.helper.SharedHelper;
import com.fekrah.halan.models.Notification;
import com.fekrah.halan.models.Room;
import com.fekrah.halan.models.User;

import java.util.List;

public class CustomerNotificationAdapter extends RecyclerView.Adapter<CustomerNotificationAdapter.ChatsViewHolder> {

    List<Notification> notifications;
    Activity Context;

    public CustomerNotificationAdapter(List<Notification> notifications, Activity context) {
        this.notifications = notifications;
        Context = context;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_customer_notification_item, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int i) {
       final Notification notification = notifications.get(i);

       holder.content.setText(notification.getContent());
       holder.driverName.setText(notification.getDriver_name());
       holder.driverImage.setImageURI(notification.getImg());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView driverImage;
        TextView driverName;
        TextView content;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            driverImage = itemView.findViewById(R.id.profile_image);
            driverName = itemView.findViewById(R.id.user_name);
            content = itemView.findViewById(R.id.content);
        }
    }

    public void addRoom(Notification notification) {
        notifications.add(notification);
        notifyDataSetChanged();
    }

    public void addRoom(int index, Notification notification) {
        notifications.add(index, notification);
        notifyItemInserted(index);
    }

    public int indexOfRoom(String key) {
        return notifications.indexOf(key);
    }

    public void removeRoom(int position) {
        notifications.remove(position);
        notifyDataSetChanged();
    }


    public void removeKey(int p) {
        notifications.remove(p);
    }
}
