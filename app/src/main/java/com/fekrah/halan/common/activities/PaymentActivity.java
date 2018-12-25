package com.fekrah.halan.common.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.models.Article;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rafakob.drawme.DrawMeButton;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

public class PaymentActivity extends AppCompatActivity {


    @BindView(R.id.article_img)
    SimpleDraweeView articleImg;

    @BindView(R.id.article_add_image)
    ImageView addImg;

    @BindView(R.id.article_edt)
    EditText artticleEdt;

    @BindView(R.id.progress_add)
    ProgressBar progressBar;

    @BindView(R.id.addarticleBtn)
    DrawMeButton addarticleBtn;

    public static final String IMAGE_PROFILE = "profile_image";
    private static final int FIRST_REQUEST = 0;
    private static final int SECOND_REQUEST = 1;
    private String REQUEST_FOR;

    Uri resultUri;
    byte[] thumbByte;
    private Bitmap thumbBitmap = null;

    String userId;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mCurrentUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);


        resultUri = null;
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCurrentUserReference = mFirebaseDatabase.getReference().child("payment").child(userId);

        addarticleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String key = mCurrentUserReference.push().getKey();
                progressBar.setVisibility(View.VISIBLE);
                if (resultUri != null) {
                    final StorageReference reference = FirebaseStorage.getInstance().getReference().child("payment_image")
                            .child(userId).child(System.currentTimeMillis() + resultUri.getLastPathSegment());
                    reference.putBytes(thumbByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Article article = new Article(
                                            artticleEdt.getText().toString(),
                                            key,
                                            uri.toString(),
                                            userId
                                    );
                                    mCurrentUserReference.child(key).setValue(article).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(PaymentActivity.this, "تم ارسال الفاتورة", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                                }


                            });
                        }
                    });

                } else {
                    Article article = new Article(
                            artticleEdt.getText().toString(),
                            key,
                            null,
                            userId
                    );
                    mCurrentUserReference.child(key).setValue(article).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            }
                        }
                    });
                }
            }
        });

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUEST_FOR = IMAGE_PROFILE;
                trySelector();
            }
        });
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String destinationFileName = "SAMPLE_CROPPED_IMAGE_NAME" + ".jpg";
        if (resultCode == RESULT_OK && requestCode == FIRST_REQUEST) {

            final Uri selectedUri = data.getData();
            if (selectedUri != null)
                UCrop.of(selectedUri, Uri.fromFile(new File(getCacheDir(), selectedUri.getLastPathSegment() + ".jpg")))
                        .start(this);

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resultUri = UCrop.getOutput(data);
            //  if (resultUri!=null)

            assert resultUri != null;
            bitmapCompress(resultUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);

            thumbByte = byteArrayOutputStream.toByteArray();

            articleImg.setVisibility(View.VISIBLE);
            articleImg.setImageURI(resultUri);

        }
    }

    private void bitmapCompress(Uri resultUri) {
        final File thumbFilepathUri = new File(resultUri.getPath());

        try {
            thumbBitmap = new Compressor(this)
                    .setMaxHeight(300)
                    .setMaxWidth(300)
                    .setQuality(50)
                    .compressToBitmap(thumbFilepathUri);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
