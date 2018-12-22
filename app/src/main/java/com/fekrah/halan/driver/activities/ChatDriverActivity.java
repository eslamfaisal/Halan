package com.fekrah.halan.driver.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.github.clans.fab.FloatingActionMenu;
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

public class ChatDriverActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton mSendMessageButton;

    @BindView(R.id.chat_input_message)
    EditText mInputMessage;

    @BindView(R.id.friend_chat_recycler_view)
    RecyclerView messagesRecyclerView;

    @BindView(R.id.right_menu)
    FloatingActionMenu rightMenu;

    @BindView(R.id.left_menu)
    FloatingActionMenu leftMenu;

    @BindView(R.id.send_form)
    FloatingActionButton sendForm;

    @BindView(R.id.send_img)
    FloatingActionButton sendImg;

    @BindView(R.id.send_form2)
    FloatingActionButton sendForm2;

    @BindView(R.id.send_img2)
    FloatingActionButton sendImg2;

    View view;
    TextView userName;
    SimpleDraweeView userImage;

    User user;

    public static String recieverId;

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

    String language;
    UCrop.Options options;
    private byte[] imageBytes;
    private String TAG="aaaaaaa";
    public static final String RECEIVER_ID="receiver_string";

    public static String type ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_driver);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        user = (User) getIntent().getSerializableExtra(ChatsAdapter.USER_ID);
        recieverId = user.getUser_key();
        userName.setText(user.getName());
        userImage.setImageURI(user.getImg());
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCurrentUserReference = mFirebaseDatabase.getReference().child("messages")
                .child(userId).child(recieverId);
        mCurrentUserReference.keepSynced(true);

        messages = new ArrayList<>();
        messagesLinearLayoutManager = new LinearLayoutManager(this);
        adapter = new MessageAdapter(messages, this, recieverId);
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
                if (dataSnapshot.getValue() != null) {
                    Message message = dataSnapshot.getValue(Message.class);
//                    adapter.add(message);
//                    adapter.notifyDataSetChanged();
                    messages.add(message);
                    //  messageKey.add(dataSnapshot.getKey());
                    adapter.notifyItemInserted(messages.size() - 1);
                    messagesRecyclerView.scrollToPosition(messages.size() - 1);
                    //  messagesRecyclerView.ite
                } else {

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

        initial();
    }

    private void initial() {
        options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        language = getString(R.string.arabic);
        mSendMessageButton.hide(false);
        rightMenu.setClosedOnTouchOutside(true);
        leftMenu.setClosedOnTouchOutside(true);
        if (language.equals("عربي")) {
            leftMenu.setVisibility(View.GONE);
            leftMenu.showMenu(false);
        } else {
            rightMenu.setVisibility(View.GONE);
            rightMenu.hideMenu(false);
        }

        //Toast.makeText(this, language, Toast.LENGTH_SHORT).show();
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        sendForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                leftMenu.close(true);
                rightMenu.close(true);
                Intent intent =  new Intent(ChatDriverActivity.this,ReceiptOrderActivity.class);
                intent.putExtra(RECEIVER_ID,user.getUser_key());
                startActivity(intent);
            }
        });

        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftMenu.close(true);
                rightMenu.close(true);
                ImagePicker.create(ChatDriverActivity.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(true)
                        .start();
            }
        });


        sendForm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                leftMenu.close(true);
                rightMenu.close(true);
                Intent intent =  new Intent(ChatDriverActivity.this,ReceiptOrderActivity.class);
                intent.putExtra(RECEIVER_ID,user.getUser_key());
                startActivity(intent);
            }
        });

        sendImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftMenu.close(true);
                rightMenu.close(true);
                ImagePicker.create(ChatDriverActivity.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(true)
                        .start();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.chat_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//
//        return super.onOptionsItemSelected(item);
//    }

    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SECOND_REQUEST);

            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select picture"), FIRST_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case SECOND_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
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

    private void CropImage(Image image, Uri res_url) {
        UCrop.of(res_url, Uri.fromFile(new File(getCacheDir(), image.getName())))
                .withOptions(options)
                .start(ChatDriverActivity.this);
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
                    if (language.equals("عربي")) {
                        rightMenu.hideMenu(true);
                    } else {
                        leftMenu.hideMenu(true);
                    }

                    mSendMessageButton.show(true);
                    rightMenu.hideMenu(true);
                } else {
                    mSendMessageButton.hide(true);

                    if (language.equals("عربي")) {
                        rightMenu.showMenu(true);
                    } else {
                        leftMenu.showMenu(true);
                    }
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
        finish();
    }
}
