package com.example.gihan.chatapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
;
import com.example.gihan.chatapp.R;
import com.example.gihan.chatapp.model.Messages;
import com.example.gihan.chatapp.time.GetTimeAgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {

    private String mChattUser;
    private String userName;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private StorageReference mImageStorage;
    private TextView mDisplayName;
    private TextView mLastSeen;
    private CircleImageView mImage;
    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatText;
    private String mCurrentUserId;
    private RecyclerView mMessagesList;
    private final List<Messages> mList = new ArrayList<>();
    private LinearLayoutManager mLinewrLayout;
    private MessageAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private static final int TOTAL_MESSAGE_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private static final int GALLERY_PICK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mImageStorage = FirebaseStorage.getInstance().getReference();


        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_par);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mChattUser = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("user_name");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(actionBarView);

        //CUSTOM ACTION BAR IDENTIFICATION
        mDisplayName = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeen = (TextView) findViewById(R.id.customr_last_seen);
        mImage = (CircleImageView) findViewById(R.id.custom_bar_iamge);

        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_button);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_button);
        mChatText = (EditText) findViewById(R.id.chat_message_et);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);

        ///------------------------------------------------------------
                mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mLinewrLayout = new LinearLayoutManager(this);
        mAdapter = new MessageAdapter(mList);




        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinewrLayout);
        mMessagesList.setAdapter(mAdapter);


        loadMessages();


        mDisplayName.setText(userName);


        mRootRef.child("users").child(mChattUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("themp_up").getValue().toString();

                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.sm).into(mImage);

                if (online.equals("true")) {
                    mLastSeen.setText("Online");
                } else {
                    GetTimeAgo ob = new GetTimeAgo();

                    Long lastSeen = Long.parseLong(online);
                    String timeago = ob.getTimeAgo(lastSeen, getApplicationContext());
                    mLastSeen.setText(timeago);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mRootRef.child("chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChattUser)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatAddMap.put("chat/" + mCurrentUserId + "/" + mChattUser, chatAddMap);
                    chatAddMap.put("chat/" + mChattUser + "/" + mChattUser, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Toast.makeText(getApplicationContext(), "error :" + databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();

                            }

                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();

            }
        });
//-------------------------------ADD IMAGE FROM GALLERY---------------------------------
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatActivity.this);

            }
        });


        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;
                mList.clear();
                loadMessages();
            }
        });
    }


    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChattUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_MESSAGE_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                mList.add(messages);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(mList.size() - 1);
                mSwipeRefresh.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void sendMessage() {

        String message = mChatText.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String currentUserRef = "messages/" + mCurrentUserId + "/" + mChattUser;
            String chatUserRef = "messages/" + mChattUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").
                    child(mCurrentUserId).child(mCurrentUserId).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seend", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);


            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + push_id, messageMap);
            messageUserMap.put(chatUserRef + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Toast.makeText(getApplicationContext(), "Error when senfd message", Toast.LENGTH_LONG).show();
                    } else {
                        mChatText.setText("");
                        Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_LONG).show();

                    }

                }
            });


        }


    }

    //----------------------------UPLOAD IMAGE IN STORAGE-----------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCurrentUserId = mAuth.getCurrentUser().getUid();


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


                final Uri resultUri = result.getUri();

                File thump_filePath = new File(resultUri.getPath());
                try {
                    Bitmap thump_pitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200).setQuality(75)
                            .compressToBitmap(thump_filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thump_pitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thump_byte = baos.toByteArray();


                    StorageReference filePath = mImageStorage.child("chat_image").child(mCurrentUserId + ".jpg");
                    final StorageReference thump_file = mImageStorage.child("thump_up").child(mCurrentUserId + ".jpg");

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
                                        String thump_downloadUrl = thump_task.getResult().getDownloadUrl().toString();

                                        if (thump_task.isSuccessful()) {
                                            Map update_hashmap = new HashMap();
                                            update_hashmap.put("image", download_uri);
                                            update_hashmap.put("themp_up", thump_downloadUrl);


                                            String currentUserRef = "messages/" + mCurrentUserId + "/" + mChattUser;
                                            String chatUserRef = "messages/" + mChattUser + "/" + mCurrentUserId;

                                            DatabaseReference user_message_push = mRootRef.child("messages").
                                                    child(mCurrentUserId).child(mCurrentUserId).push();

                                            String push_id = user_message_push.getKey();

                                            Map messageMap = new HashMap();
                                            messageMap.put("message", thump_downloadUrl);
                                            messageMap.put("seend", false);
                                            messageMap.put("type", "image");
                                            messageMap.put("time", ServerValue.TIMESTAMP);
                                            messageMap.put("from", mCurrentUserId);


                                            Map messageUserMap = new HashMap();
                                            messageUserMap.put(currentUserRef + "/" + push_id, messageMap);
                                            messageUserMap.put(chatUserRef + "/" + push_id, messageMap);

                                            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                    if (databaseError != null) {
                                                        Toast.makeText(getApplicationContext(), "Error when senfd message", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        mChatText.setText("");
                                                        Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            });

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error when loading thump File", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(getApplicationContext(), "Error when loading file", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
