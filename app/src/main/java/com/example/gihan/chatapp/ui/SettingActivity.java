package com.example.gihan.chatapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gihan.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private DatabaseReference mUserDataBase;
    private StorageReference mImageStorage;

    private TextView mDisplayName;
    private TextView mStatus;
    private Button mChangeStatus;
    private Button mChangeImage;
    private CircleImageView mImageDisplay;

    private ProgressDialog mProgrss;


    private static final int GALLERY_PICK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        mDisplayName = (TextView) findViewById(R.id.setting_tv_display_name);
        mStatus = (TextView) findViewById(R.id.setting_tv_hi);
        mChangeImage = (Button) findViewById(R.id.setting_change_image);
        mChangeStatus = (Button) findViewById(R.id.setting_change_status);
        mImageDisplay = (CircleImageView) findViewById(R.id.setting_image);


        mProgrss = new ProgressDialog(this);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mUserDataBase.keepSynced(true);

        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thempUp = dataSnapshot.child("themp_up").getValue().toString();

                mDisplayName.setText(name);
                mStatus.setText(status);
                //Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.sm).into(mImageDisplay);

                Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.sm)
                        .into(mImageDisplay, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(getApplicationContext()).load(image).into(mImageDisplay);


                            }
                        });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String statusValue = mStatus.getText().toString();
                Intent statusIntent = new Intent(getApplicationContext(), StatusActivity.class);
                statusIntent.putExtra("status_value", statusValue);
                startActivity(statusIntent);
            }
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent galeryIntent=new Intent();
                galeryIntent.setType("image/*");
                galeryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galeryIntent,"SELCT IMAGE"),GALLERY_PICK);
                */
                // start picker to get image for cropping and then use the image in cropping activity

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON).
                    setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgrss.setMessage("wait to upload image ");
                mProgrss.setTitle("upload image");
                mProgrss.show();

                String useruid = currentUser.getUid();
                final Uri resultUri = result.getUri();
                String currentUserId = currentUser.getUid();

                File thump_filePath = new File(resultUri.getPath());
                try {
                    Bitmap thump_pitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200).setQuality(75)
                            .compressToBitmap(thump_filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thump_pitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thump_byte = baos.toByteArray();


                    StorageReference filePath = mImageStorage.child("profile_images").child(currentUserId + ".jpg");
                    final StorageReference thump_file = mImageStorage.child("thump_up").child(currentUserId + ".jpg");

                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "work ", Toast.LENGTH_SHORT).show();


                                final String download_uri = task.getResult().getDownloadUrl().toString();
                                UploadTask uploadTask = thump_file.putBytes(thump_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thump_task) {
                                        String thump_downloadUrl=thump_task.getResult().getDownloadUrl().toString();

                                        if (thump_task.isSuccessful()){
                                            Map update_hashmap=new HashMap();
                                            update_hashmap.put("image",download_uri);
                                            update_hashmap.put("themp_up",thump_downloadUrl);


                                            mUserDataBase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(),"Sucess loading thump file",Toast.LENGTH_SHORT).show();
                                                        mProgrss.dismiss();

                                                    }

                                                }
                                            });
                                        }else {
                                            Toast.makeText(getApplicationContext(),"Error when loading thump File",Toast.LENGTH_SHORT).show();
                                            mProgrss.dismiss();
                                        }
                                    }
                                });





                            }else {
                                Toast.makeText(getApplicationContext(),"Error when loading file",Toast.LENGTH_SHORT).show();
                                mProgrss.dismiss();

                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
