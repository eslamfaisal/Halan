package com.fekrah.halan.customer.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.driver.adapters.ChatsAdapter;
import com.fekrah.halan.driver.adapters.MessageAdapter;
import com.fekrah.halan.models.Message;
import com.fekrah.halan.models.User;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_send_message)
    FloatingActionButton mSendMessageButton;

    @BindView(R.id.fab_send_image)
    FloatingActionButton mSendImageButton;

    @BindView(R.id.chat_input_message)
    EditText mInputMessage;

//    @BindView(R.id.send_message_view)
//    View sendMessageView;

    @BindView(R.id.friend_chat_recycler_view)
    RecyclerView messagesRecyclerView;

    View view;
    TextView userName;
    SimpleDraweeView userImage;

    User user ;

    String recieverId;

    String userId;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mCurrentUserReference;

    List<Message> messages;
    MessageAdapter adapter;
    LinearLayoutManager messagesLinearLayoutManager;

    private Bitmap thumbBitmap = null;

    private static final int FIRST_REQUEST = 0;
    private static final int SECOND_REQUEST = 1;
    UCrop.Options options;
    private byte[] imageBytes;

    private static final String TAG = "ChatActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
        }
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            view = inflater.inflate(R.layout.chat_custombar, null);
            userName = view.findViewById(R.id.task_room_name);
            userImage = view.findViewById(R.id.task_room_image);
        }
        if (actionBar != null) {
            actionBar.setCustomView(view);
        }

        user = (User) getIntent().getSerializableExtra(ChatsAdapter.USER_ID);
        recieverId = user.getUser_key();
        userName.setText(user.getName());
        userImage.setImageURI(user.getImg());
      //  userImage.setImageURI(user.getImg());

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCurrentUserReference = mFirebaseDatabase.getReference().child("messages").child(userId).child(recieverId);
        mCurrentUserReference.keepSynced(true);

        messages = new ArrayList<>();
        messagesLinearLayoutManager = new LinearLayoutManager(this);
        adapter = new MessageAdapter( messages,this,recieverId);
        messagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messagesLinearLayoutManager.setStackFromEnd(true);
       // messagesLinearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setReverseLayout(true);
        messagesLinearLayoutManager.setSmoothScrollbarEnabled(true);
        messagesRecyclerView.setHasFixedSize(true);
       //messagesRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));
        messagesRecyclerView.setLayoutManager(messagesLinearLayoutManager);

        messagesRecyclerView.setAdapter(adapter);

        mCurrentUserReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue()!=null){
                    Message message = dataSnapshot.getValue(Message.class);
//                    adapter.add(message);
//                    adapter.notifyDataSetChanged();
                    messages.add(message);
                  //  messageKey.add(dataSnapshot.getKey());
                    adapter.notifyItemInserted(messages.size() - 1);
                    messagesRecyclerView.scrollToPosition(messages.size()-1);
                  //  messagesRecyclerView.ite
                }else {

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mSendImageButton.show(true);
        mSendMessageButton.hide(false);
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mSendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(ChatActivity.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(false)
                        .start();
            }
        });
        options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            //Toast.makeText(this, "", Toast.LENGTH_LONG).show();
            return;
        }
        String destinationFileName = "SAMPLE_CROPPED_IMAGE_NAME" + ".jpg";

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            Image image = ImagePicker.getFirstImageOrNull(data);
            Uri res_url = Uri.fromFile(new File((image.getPath())));
            CropImage(image, res_url);

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            //  if (resultUri!=null)
            assert resultUri != null;
            bitmapCompress(resultUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
            imageBytes = byteArrayOutputStream.toByteArray();
            uploadThumbImage(imageBytes);
            Log.d(TAG, "onActivityResult: "+ Arrays.toString(imageBytes));
        }


    }

    //upload thumb image
    private void uploadThumbImage(byte[] thumbByte) {
        final StorageReference thumbFilePathRef = FirebaseStorage.getInstance().getReference().
                child(FirebaseAuth.getInstance().getUid())
                .child("chat_images").child(System.currentTimeMillis() + ".jpg");
        thumbFilePathRef.putBytes(thumbByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                thumbFilePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri thumbUri) {
                        sendMessage(String.valueOf(thumbUri));
                        //  myProfileImage.setImageURI(thumbUri);
                    }
                });
            }
        });
    }


    private void CropImage(Image image, Uri res_url) {
        UCrop.of(res_url, Uri.fromFile(new File(getCacheDir(), image.getName())))
                .withOptions(options)
                .start(ChatActivity.this);
    }

    private void bitmapCompress(Uri resultUri) {
        final File thumbFilepathUri = new File(resultUri.getPath());

        try {
            thumbBitmap = new Compressor(this)
                    .setQuality(50)
                    .compressToBitmap(thumbFilepathUri);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializSendMessage() {

        // Enable Send button when there's text to send
        mInputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendMessageButton.show(true);
                    mSendImageButton.hide(true);
                }else {
                    mSendMessageButton.hide(true);
                    mSendImageButton.show(true);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        initializSendMessage();

    }

    private void sendMessage() {

        String key = FirebaseDatabase.getInstance().getReference().child("messages")
                .child(userId).child(recieverId).push().getKey();

        // public Message(String message, String type, long time, String imageUrl, String from) {
        Message message = new Message(mInputMessage.getText().toString(),
                "text", System.currentTimeMillis(), null, "fromMe", key);
        Message message2 = new Message(mInputMessage.getText().toString(),
                "text", System.currentTimeMillis(), null, "toMe", key);

        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(userId).child(recieverId).child(key).setValue(message);
        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(recieverId).child(userId).child(key).setValue(message2);

        FirebaseDatabase.getInstance().getReference().child("rooms")
                .child(userId).child(recieverId).child("last_message").setValue(mInputMessage.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("rooms").child(recieverId)
                .child(userId).child("last_message").setValue(mInputMessage.getText().toString());

        HashMap<String, Object> updateTome = new HashMap<>();
        updateTome.put("last_message", mInputMessage.getText().toString());
        updateTome.put("time", System.currentTimeMillis());
        updateTome.put("from", "him");

        HashMap<String, Object> updateFromMe = new HashMap<>();
        updateFromMe.put("last_message", mInputMessage.getText().toString());
        updateFromMe.put("time", System.currentTimeMillis());
        updateFromMe.put("from", "me");

        FirebaseDatabase.getInstance().getReference().child("rooms").child(recieverId)
                .child(userId).updateChildren(updateTome);

        FirebaseDatabase.getInstance().getReference().child("rooms").child(userId)
                .child(recieverId).updateChildren(updateFromMe);

        mInputMessage.setText("");

    }


    private void sendMessage(String url) {

        String key = FirebaseDatabase.getInstance().getReference().child("messages")
                .child(userId).child(recieverId).push().getKey();

        // public Message(String message, String type, long time, String imageUrl, String from) {
        Message message = new Message(mInputMessage.getText().toString(),
                "photo", System.currentTimeMillis(), url, "fromMe", key);
        Message message2 = new Message(mInputMessage.getText().toString(),
                "photo", System.currentTimeMillis(), url, "toMe", key);


        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(userId).child(recieverId).child(key).setValue(message);
        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(recieverId).child(userId).child(key).setValue(message2);

        FirebaseDatabase.getInstance().getReference().child("rooms")
                .child(userId).child(recieverId).child("last_message").setValue(mInputMessage.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("rooms").child(recieverId)
                .child(userId).child("last_message").setValue("photo");

        HashMap<String, Object> updateTome = new HashMap<>();
        updateTome.put("last_message", "photo");
        updateTome.put("time", System.currentTimeMillis());
        updateTome.put("from", "him");

        HashMap<String, Object> updateFromMe = new HashMap<>();
        updateFromMe.put("last_message", "photo");
        updateFromMe.put("time", System.currentTimeMillis());
        updateFromMe.put("from", "me");

        FirebaseDatabase.getInstance().getReference().child("rooms").child(recieverId)
                .child(userId).updateChildren(updateTome);

        FirebaseDatabase.getInstance().getReference().child("rooms").child(userId)
                .child(recieverId).updateChildren(updateFromMe);


    }


    public void backFinish(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fileList();
    }
}
