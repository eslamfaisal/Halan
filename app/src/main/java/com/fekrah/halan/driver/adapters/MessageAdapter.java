package com.fekrah.halan.driver.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.helper.GetTimeAgo;
import com.fekrah.halan.models.Message;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM_FORM = 3;
    private final int VIEW_TYPE_ITEM_FROM_ME = 0;
    private final int VIEW_TYPE_ITEM_TO_ME = 2;
    private final int VIEW_TYPE_LOADING = 1;

    private List<Message> mMessagesList;
    private List<String> mMessagekey;
    String roomId;
    private Context context;
    int currentPosition;
    Dialog messsageDialog;
    String receiverId;

    public MessageAdapter(List<Message> mMessagesList, Context context, String receiverId) {
        this.mMessagesList = mMessagesList;
        this.mMessagekey = mMessagekey;
        this.roomId = roomId;
        this.context = context;
        this.receiverId = receiverId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessagesList.get(position);

        String from_or_to = message.getFrom();

        if (message.getType().equals("فاتورة")){
            return VIEW_TYPE_ITEM_FORM;
        }else {
            if (from_or_to.equals("toMe")) {
                return VIEW_TYPE_ITEM_TO_ME;
            } else {
                return VIEW_TYPE_ITEM_FROM_ME;
            }
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM_FROM_ME) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_from_me, parent, false);
            return new MessageFromMeViewHolder(view);
        } else if(viewType == VIEW_TYPE_ITEM_TO_ME){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_to_me, parent, false);
            return new MessageToMeViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_receipt_item, parent, false);
            return new ReceiptViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Message message = mMessagesList.get(position);
        if (holder instanceof MessageFromMeViewHolder) {
            final MessageFromMeViewHolder fromMeViewHolder = (MessageFromMeViewHolder) holder;
            if (message.getType().equals("text")) {
                fromMeViewHolder.messageImage.setVisibility(View.GONE);
                fromMeViewHolder.messageText.setText(message.getMessage());
                fromMeViewHolder.messageTime.setText(GetTimeAgo.getTimeAgo(message.getTime(), context));
            }else {
                final String[] photo = new String[]{message.getImageUrl()};
                fromMeViewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImageViewer.Builder<>(context, photo)
                                .setStartPosition(0)
                                .show();
                    }
                });
                fromMeViewHolder.messageImage.setImageURI(message.getImageUrl());
                fromMeViewHolder.messageImage.setVisibility(View.VISIBLE);
                fromMeViewHolder.messageText.setVisibility(View.GONE);
                fromMeViewHolder.messageTime.setText(GetTimeAgo.getTimeAgo(message.getTime(), context));
            }

            fromMeViewHolder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //createMenu(view,position,message.getMessage_key(),message.getMessage());
                    return true;
                }
            });
            fromMeViewHolder.main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //createMenu(view,position,message.getMessage_key(),message.getMessage());
                    return true;
                }
            });

            fromMeViewHolder.messageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fromMeViewHolder.messageTime.setVisibility(View.VISIBLE);
                }
            });

            fromMeViewHolder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fromMeViewHolder.messageTime.setVisibility(View.VISIBLE);

                }
            });

        } else if (holder instanceof MessageToMeViewHolder) {
            final MessageToMeViewHolder toMeViewHolder = (MessageToMeViewHolder) holder;
            if (message.getType().equals("text")) {
                toMeViewHolder.messageImage.setVisibility(View.GONE);
                toMeViewHolder.messageText.setText(message.getMessage());
                toMeViewHolder.messageTime.setText(GetTimeAgo.getTimeAgo(message.getTime(), context));
            }else {
                final String[] photo = new String[]{message.getImageUrl()};
                toMeViewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImageViewer.Builder<>(context, photo)
                                .setStartPosition(0)
                                .show();
                    }
                });
                toMeViewHolder.messageImage.setImageURI(message.getImageUrl());
                toMeViewHolder.messageImage.setVisibility(View.VISIBLE);
                toMeViewHolder.messageText.setVisibility(View.GONE);
                toMeViewHolder.messageTime.setText(GetTimeAgo.getTimeAgo(message.getTime(), context));
            }

            toMeViewHolder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //createMenu(view,message.getMessage());
                    return true;
                }
            });
            toMeViewHolder.main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //createMenu(view,message.getMessage());
                    return true;
                }
            });

            toMeViewHolder.messageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toMeViewHolder.messageTime.setVisibility(View.VISIBLE);
                }
            });

            toMeViewHolder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toMeViewHolder.messageTime.setVisibility(View.VISIBLE);

                }
            });
        }else if (holder instanceof ReceiptViewHolder){
            final ReceiptViewHolder rholder = (ReceiptViewHolder) holder;
            final String[] photo = new String[]{message.getImageUrl()};
            rholder.messageImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ImageViewer.Builder<>(context, photo)
                            .setStartPosition(0)
                            .show();
                }
            });
            rholder.messageImage.setImageURI(message.getImageUrl());
            rholder.delivery_cost.setText(String.valueOf(message.getDelivery_cost())+context.getString(R.string.sr));
            rholder.purchases_cost.setText(String.valueOf(message.getPurchases_cost())+context.getString(R.string.sr));
            rholder.totalCost.setText((message.getDelivery_cost()+message.getPurchases_cost())+context.getString(R.string.sr));
        }
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

//    private void createMenu(View view, final int position, final String key, final String messageText) {
//        //creating a popup menu
//        PopupMenu popup = new PopupMenu(context, view);
//        //inflating menu from xml resource
//        popup.inflate(R.menu.menu_item_chat_messages);
//        //adding click listener
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.delete_item:
//                        mMessagesList.remove(position);
//                        notifyItemRemoved(position);
//                        FirebaseDatabase.getInstance().getReference().child("messages")
//                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                .child(receiverId).child(key).removeValue();
//
//                        //  Toast.makeText(context, mMessagekey.get(position) + "", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.copy_text_iem:
//                        setClipboard(context, messageText);
//                        return true;
//
//                    default:
//                        return false;
//                }
//            }
//        });
//        //displaying the popup
//        popup.show();
//    }
//
//    private void createMenu(View view, final String messageText) {
//        //creating a popup menu
//        PopupMenu popup = new PopupMenu(context, view);
//        //inflating menu from xml resource
//        popup.inflate(R.menu.menu_item_chat_list);
//        //adding click listener
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.copy_item:
//                        setClipboard(context, messageText);
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });
//        //displaying the popup
//        popup.show();
//    }


    public void add(Message message) {
        mMessagesList.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

    public class MessageFromMeViewHolder extends RecyclerView.ViewHolder {


        TextView messageTime;
        TextView messageText;
        SimpleDraweeView messageImage;
        View main;

        public MessageFromMeViewHolder(View itemView) {
            super(itemView);
            main = itemView;
            messageText = itemView.findViewById(R.id.message_text_view_of_message);
            messageImage = itemView.findViewById(R.id.photo_image_view_of_message_from_me);
            messageTime = itemView.findViewById(R.id.message_time_from_me);
        }
    }

    public class MessageToMeViewHolder extends RecyclerView.ViewHolder {
        TextView messageTime;
        TextView messageText;
        SimpleDraweeView messageImage;
        View main;

        public MessageToMeViewHolder(View itemView) {
            super(itemView);
            main = itemView;
            messageTime = itemView.findViewById(R.id.message_time_to_me);
            messageImage = itemView.findViewById(R.id.photo_image_view_of_message_to_me);
            messageText = itemView.findViewById(R.id.message_text_view_of_message_tome);

        }
    }

    public class ReceiptViewHolder extends RecyclerView.ViewHolder{
        TextView purchases_cost;
        TextView delivery_cost;
        TextView totalCost;
        SimpleDraweeView messageImage;
        View main;

        public ReceiptViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView;
            purchases_cost = itemView.findViewById(R.id.purchases_cost);
            messageImage = itemView.findViewById(R.id.receipt_picture);
            delivery_cost = itemView.findViewById(R.id.delivery_cost);
            totalCost = itemView.findViewById(R.id.total_cost);
        }
    }

}
