package com.fekrah.halan.driver.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.models.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rafakob.drawme.DrawMeButton;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

public class ReceiptOrderActivity extends AppCompatActivity {

    @BindView(R.id.purchases_cost)
    EditText purchasesCost;

    @BindView(R.id.delivery_cost)
    EditText deliveryCostEdt;

    @BindView(R.id.receipt_text)
    View receiptTxt;

    @BindView(R.id.receipt_picture)
    SimpleDraweeView receiptPic;

    @BindView(R.id.send_receipt)
    DrawMeButton sendReceipr;

    String userId;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mCurrentUserReference;

    private Bitmap thumbBitmap = null;

    byte[] formByte;
    private UCrop.Options options;
    private String recieverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_orderactivity);
        ButterKnife.bind(this);
        recieverId = ChatDriverActivity.recieverId;
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mFirebaseDatabase.getReference().child("Customer_current_order")
                .child(recieverId).child("accepted_driver").child("cost")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                deliveryCostEdt.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        receiptTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(ReceiptOrderActivity.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(true)
                        .start();
            }
        });

        sendReceipr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadThumbImage(formByte);
            }

        });

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            //Toast.makeText(this, "", Toast.LENGTH_LONG).show();
            return;
        }
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
            formByte = byteArrayOutputStream.toByteArray();
            receiptPic.setImageURI(resultUri);
            receiptPic.setVisibility(View.VISIBLE);
        }


    }

    private void CropImage(Image image, Uri res_url) {
        UCrop.of(res_url, Uri.fromFile(new File(getCacheDir(), image.getName())))
                .withOptions(options)
                .start(ReceiptOrderActivity.this);
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

    private void sendMessage(String url) {

        int pc = Integer.parseInt(purchasesCost.getText().toString());
        int dc = Integer.parseInt(purchasesCost.getText().toString());
        String key = FirebaseDatabase.getInstance().getReference().child("messages")
                .child(userId).child(recieverId).push().getKey();

        // public Message(String message, String type, long time, String imageUrl, String from) {
        Message message = new Message("فاتورة", System.currentTimeMillis(),
                url, "fromMe", key, dc, pc, pc + dc);
        Message message2 = new Message("فاتورة", System.currentTimeMillis(),
                url, "toMe", key, dc, pc, pc + dc);


        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(userId).child(recieverId).child(key).setValue(message);
        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(recieverId).child(userId).child(key).setValue(message2);

        FirebaseDatabase.getInstance().getReference().child("rooms")
                .child(userId).child(recieverId).child("last_message")
                .setValue(getString(R.string.receipt));

        FirebaseDatabase.getInstance().getReference().child("rooms").child(recieverId)
                .child(userId).child("last_message").setValue("photo");

        HashMap<String, Object> updateTome = new HashMap<>();
        updateTome.put("last_message", getString(R.string.receipt));
        updateTome.put("time", System.currentTimeMillis());
        updateTome.put("from", "him");

        HashMap<String, Object> updateFromMe = new HashMap<>();
        updateFromMe.put("last_message", getString(R.string.receipt));
        updateFromMe.put("time", System.currentTimeMillis());
        updateFromMe.put("from", "me");

        FirebaseDatabase.getInstance().getReference().child("rooms").child(recieverId)
                .child(userId).updateChildren(updateTome);

        FirebaseDatabase.getInstance().getReference().child("rooms").child(userId)
                .child(recieverId).updateChildren(updateFromMe);

        Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
