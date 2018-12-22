package com.fekrah.halan.driver.adapters;

import android.content.Context;
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
import com.fekrah.halan.models.Room;
import com.fekrah.halan.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;


public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    private List<Room> rooms;
    private List<String> roomsKey;
    private Context context;
    DatabaseReference reference;
    DatabaseReference userImgRef;
    DatabaseReference userNameRef;
    Query lastMessageRef;
    Query lastQuery;
    public static final String USER_ID = "USER_ID";

    public ChatsAdapter(List<Room> rooms, List<String> roomsKey, Context context) {
        this.rooms = rooms;
        this.context = context;
        this.roomsKey = roomsKey;
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_list_item, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int i) {
        final Room room = rooms.get(i);

        holder.roomTime.setText(GetTimeAgo.getTimeAgo(room.getTime(), context));
        holder.roomLastMessage.setText(room.getLast_message());

        holder.roomImage.setImageURI(room.getImg());
        holder.roomName.setText(room.getReceiver_name());

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                if (SharedHelper.getKey(context, "uses_type").equals("driver"))
                    intent = new Intent(context, ChatDriverActivity.class);
                else
                    intent = new Intent(context, ChatActivity.class);
                User user = new User(
                        room.getReceiver_name(),
                        room.getImg(),
                        room.getRoom_key()
                );
                intent.putExtra(USER_ID, user);
                context.startActivity(intent);
            }
        });
//        reference.child("users").child(room.getRoom_key()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue()!=null){
//                    final User user = dataSnapshot.getValue(User.class);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView roomImage;
        TextView roomName;
        TextView roomTime;
        TextView roomLastMessage;
        View mainView;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            mainView = itemView;
            roomLastMessage = itemView.findViewById(R.id.room_chat_last_message);
            roomImage = itemView.findViewById(R.id.room_chat_image);
            roomName = itemView.findViewById(R.id.room_chat_name);
            roomTime = itemView.findViewById(R.id.room_chat_time);
        }
    }

    public void addRoom(Room userID) {
        rooms.add(userID);
        notifyDataSetChanged();
    }

    public void addRoom(int index, Room userID) {
        rooms.add(index, userID);
        notifyDataSetChanged();
    }

    public int indexOfRoom(String key) {
        return roomsKey.indexOf(key);
    }

    public void removeRoom(int position) {
        rooms.remove(position);
        notifyDataSetChanged();
    }

    public void addKey(int index, String key) {
        roomsKey.add(index, key);
        notifyDataSetChanged();
    }

    public void removeKey(int p) {
        roomsKey.remove(p);
    }
}
