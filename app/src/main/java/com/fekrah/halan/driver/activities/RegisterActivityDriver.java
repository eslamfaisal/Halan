package com.fekrah.halan.driver.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.halan.R;
import com.fekrah.halan.common.activities.BaseActivity;
import com.fekrah.halan.helper.Utility;
import com.fekrah.halan.models.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rafakob.drawme.DrawMeButton;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

public class RegisterActivityDriver extends BaseActivity {

    @BindView(R.id.name)
    EditText name;

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.phone)
    EditText phone;

    @BindView(R.id.city)
    EditText city;

    @BindView(R.id.national_id)
    EditText national_id;

    @BindView(R.id.car_id)
    EditText car_id;

    @BindView(R.id.car_color)
    EditText car_color;

    @BindView(R.id.car_model)
    EditText car_model;

    @BindView(R.id.car_form_pic)
    View carFormPic;

    @BindView(R.id.car_licence_pic)
    View car_licence_pic;

    @BindView(R.id.front_pic)
    View front_pic;

    @BindView(R.id.back_pic)
    View back_pic;

    @BindView(R.id.profile_image_view)
    View profile_image_view;

    @BindView(R.id.profile_image)
    SimpleDraweeView profile_image;

    @BindView(R.id.form_img)
    SimpleDraweeView form_img;

    @BindView(R.id.licence_img)
    SimpleDraweeView licence_img;

    @BindView(R.id.front_img)
    SimpleDraweeView front_img;

    @BindView(R.id.back_img)
    SimpleDraweeView back_img;

    @BindView(R.id.register)
    DrawMeButton registerBtn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private int APP_REQUEST_CODE = 010;
    ProgressDialog dialog;

    private String REQUEST_FOR_PICTURE;

    private static final String CAR_FORM_REQUEST = "CAR_FORM_REQUEST";
    private static final String CAR_LICENCE_REQUEST = "CAR_LICENCE_REQUEST";
    private static final String CAR_FRONT_REQUEST = "CAR_FRONT_REQUEST";
    private static final String CAR_BACK_REQUEST = "CAR_BACK_REQUEST";
    private static final String PROFILE_IMAGE_REQUEST = "PROFILE_IMAGE_REQUEST";

    private static final int FIRST_REQUEST = 0;
    private static final int SECOND_REQUEST = 1;
    private Bitmap thumbBitmap = null;

    byte[] formByte;
    byte[] frontByte;
    byte[] backbyte;
    byte[] licenceByte;
    byte[] profilebyte;

    // firebase variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMyProfileReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference licenceImageStorageReference;
    private StorageReference frontImageStorageReference;
    private StorageReference formImageStorageReference;
    private StorageReference backImageStorageReference;
    private StorageReference profileStorageReference;


    private String UserId;
    UCrop.Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.create_new_account);
        Utility.hideSoftKeyboard(this);
        options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        dialog = new ProgressDialog(this);
        dialog.setMessage("جار انشاء الحساب");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        frontImageStorageReference = mFirebaseStorage.getReference();
        backImageStorageReference = mFirebaseStorage.getReference();
        profileStorageReference = mFirebaseStorage.getReference();
        licenceImageStorageReference = mFirebaseStorage.getReference();
        formImageStorageReference = mFirebaseStorage.getReference();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(name.getText().toString());

                if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                    displaySnackbar(getString(R.string.email_validation));
                } else if (!isValidEmail(email.getText().toString())) {
                    displaySnackbar(getString(R.string.not_valid_email));
                } else if (name.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.first_name_empty));
                } else if (firstName.matches()) {
                    displaySnackbar(getString(R.string.first_name_no_number));
                } else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                    displaySnackbar(getString(R.string.password_validation));
                } else if (password.length() < 6) {
                    displaySnackbar(getString(R.string.password_size));
                } else if (phone.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.password_size));
                } else if (city.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.city_required));
                } else if (national_id.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.national_id_required));
                } else if (car_id.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.car_Id_required));
                } else if (car_color.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.car_color_required));
                } else if (car_model.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.car_model_required));
                } else if (profilebyte == null) {
                    displaySnackbar(getString(R.string.profile_image_required));
                } else if (formByte == null) {
                    displaySnackbar(getString(R.string.car_form_required));
                } else if (frontByte == null) {
                    displaySnackbar(getString(R.string.front_car_pic_required));
                } else if (licenceByte == null) {
                    displaySnackbar(getString(R.string.license_pic_required));
                } else if (backbyte == null) {
                    displaySnackbar(getString(R.string.back_car_required));
                } else {
                    if (hasInternet()) {
                        // verifyPhone(phone.getText().toString());
                        createUser();
                    } else {
                        displaySnackbar(getString(R.string.something_went_wrong_net));
                    }
                }
            }

        });

        carFormPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUEST_FOR_PICTURE = CAR_FORM_REQUEST;
                ImagePicker.create(RegisterActivityDriver.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(true)
                        .start();
            }
        });

        car_licence_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUEST_FOR_PICTURE = CAR_LICENCE_REQUEST;
                ImagePicker.create(RegisterActivityDriver.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(true)
                        .start();
            }
        });

        front_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUEST_FOR_PICTURE = CAR_FRONT_REQUEST;
                ImagePicker.create(RegisterActivityDriver.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(true)
                        .start();
            }
        });

        back_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUEST_FOR_PICTURE = CAR_BACK_REQUEST;
                ImagePicker.create(RegisterActivityDriver.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(true)
                        .start();
            }
        });

        profile_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUEST_FOR_PICTURE = PROFILE_IMAGE_REQUEST;
                ImagePicker.create(RegisterActivityDriver.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(true)
                        .start();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case SECOND_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    ImagePicker.create(this) // Activity or Fragment
//                            .start();
                }
        }
    }

    private void createUser() {
        dialog.show();
//        AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), password.getText().toString());
//        FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(RegisterActivityDriver.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("aaaaaa", "onComplete:== user created");
                            final StorageReference formRef = formImageStorageReference.
                                    child(FirebaseAuth.getInstance().getUid())
                                    .child("form_image_for_the_car")
                                    .child(System.currentTimeMillis() + ".jpg");
                            final StorageReference licenseRef = licenceImageStorageReference.
                                    child(FirebaseAuth.getInstance().getUid())
                                    .child("license_image_for_the_car")
                                    .child(System.currentTimeMillis() + ".jpg");
                            final StorageReference frontRef = frontImageStorageReference.
                                    child(FirebaseAuth.getInstance().getUid())
                                    .child("front_image_for_the_car")
                                    .child(System.currentTimeMillis() + ".jpg");
                            final StorageReference backRef = backImageStorageReference.
                                    child(FirebaseAuth.getInstance().getUid())
                                    .child("back_image_for_the_car")
                                    .child(System.currentTimeMillis() + ".jpg");

                            final StorageReference profileRef = profileStorageReference.
                                    child(FirebaseAuth.getInstance().getUid())
                                    .child("profile_image")
                                    .child(System.currentTimeMillis() + ".jpg");

                            formRef.putBytes(formByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    formRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri formUri) {
                                            Log.d("aaaaaa", "onComplete:== formUri ==" + formUri.toString());
                                            licenseRef.putBytes(licenceByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    licenseRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(final Uri licenseUri) {
                                                            Log.d("aaaaaa", "onComplete:== licenseUri ==" + licenseUri.toString());

                                                            frontRef.putBytes(frontByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    frontRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(final Uri frontUri) {
                                                                            Log.d("aaaaaa", "onComplete:== frontUri ==" + frontUri.toString());

                                                                            backRef.putBytes(backbyte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    backRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                        @Override
                                                                                        public void onSuccess(final Uri backUri) {
                                                                                            Log.d("aaaaaa", "onComplete:== backUri ==" + backUri.toString());
                                                                                            profileRef.putBytes(profilebyte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                @Override
                                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Uri profileUri) {
                                                                                                            Log.d("aaaaaa", "onComplete:== profileRef ==" + profileRef.toString());
                                                                                                            final Driver driver = new Driver(
                                                                                                                    name.getText().toString(),
                                                                                                                    email.getText().toString(),
                                                                                                                    profileUri.toString(),
                                                                                                                    phone.getText().toString(),
                                                                                                                    FirebaseInstanceId.getInstance().getToken(),
                                                                                                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                                                                                    city.getText().toString(),
                                                                                                                    car_color.getText().toString(),
                                                                                                                    car_model.getText().toString(),
                                                                                                                    formUri.toString(),
                                                                                                                    licenseUri.toString(),
                                                                                                                    frontUri.toString(),
                                                                                                                    backUri.toString(),
                                                                                                                    national_id.getText().toString(),
                                                                                                                    car_id.getText().toString(),
                                                                                                                    5,
                                                                                                                    0
                                                                                                            );
                                                                                                            FirebaseDatabase.getInstance().getReference().child("drivers")
                                                                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                                                    .setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    Log.d("aaaaaa", "onComplete:== data base created");
                                                                                                                    dialog.dismiss();
                                                                                                                    startActivity(new Intent(RegisterActivityDriver.this, MainActivityDriver.class));
                                                                                                                    finish();
                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        } else {
                            dialog.dismiss();
                            displaySnackbar("this email registered before");
                        }
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
        String destinationFileName = "SAMPLE_CROPPED_IMAGE_NAME" + ".jpg";

        if (requestCode == APP_REQUEST_CODE) {

        } else if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

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

            if (REQUEST_FOR_PICTURE.equals(CAR_FORM_REQUEST)) {
                formByte = byteArrayOutputStream.toByteArray();
                form_img.setImageURI(resultUri);
                form_img.setVisibility(View.VISIBLE);

            } else if (REQUEST_FOR_PICTURE.equals(CAR_LICENCE_REQUEST)) {
                licenceByte = byteArrayOutputStream.toByteArray();
                Log.d("aaaaaaaaaa", "onActivityResult: " + Arrays.toString(licenceByte));

                licence_img.setImageURI(resultUri);
                licence_img.setVisibility(View.VISIBLE);
            } else if (REQUEST_FOR_PICTURE.equals(CAR_FRONT_REQUEST)) {
                frontByte = byteArrayOutputStream.toByteArray();
                Log.d("aaaaaaaaaa", "onActivityResult: " + Arrays.toString(frontByte));
                front_img.setImageURI(resultUri);
                front_img.setVisibility(View.VISIBLE);
            } else if (REQUEST_FOR_PICTURE.equals(CAR_BACK_REQUEST)) {
                backbyte = byteArrayOutputStream.toByteArray();
                Log.d("aaaaaaaaaa", "onActivityResult: " + Arrays.toString(backbyte));
                back_img.setImageURI(resultUri);
                back_img.setVisibility(View.VISIBLE);
            } else if (REQUEST_FOR_PICTURE.equals(PROFILE_IMAGE_REQUEST)) {
                profilebyte = byteArrayOutputStream.toByteArray();
                Log.d("aaaaaaaaaa", "onActivityResult: " + Arrays.toString(backbyte));
                profile_image.setImageURI(resultUri);
                profile_image.setVisibility(View.VISIBLE);
            }

        }

    }

    private void CropImage(Image image, Uri res_url) {
        UCrop.of(res_url, Uri.fromFile(new File(getCacheDir(), image.getName())))
                .withOptions(options)
                .start(RegisterActivityDriver.this);
    }

    //upload thumb image
    private void uploadThumbImage(byte[] thumbByte, final StorageReference thumbFilePathRef) {

        thumbFilePathRef.putBytes(thumbByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                thumbFilePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri thumbUri) {
                        mMyProfileReference.child("user_thumb_image").setValue(thumbUri.toString());
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

//
//    public final void verifyPhone(String phone) {
//
//        Intent intent = new Intent(this, PhoneNumberActivity.class);
//        //Optionally you can add toolbar title
//        intent.putExtra("TITLE", getResources().getString(R.string.app_name));
//        //Optionally you can pass phone number to populate automatically.
//        intent.putExtra("PHONE_NUMBER", phone);
//        startActivityForResult(intent, APP_REQUEST_CODE);
//    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
