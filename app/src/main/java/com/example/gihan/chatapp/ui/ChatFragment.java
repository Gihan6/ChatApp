package com.example.gihan.chatapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gihan.chatapp.R;
import com.example.gihan.chatapp.model.Friends;
import com.example.gihan.chatapp.model.Messages;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

    private RecyclerView mChatList;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private final List<String> mList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        mChatList = (RecyclerView) v.findViewById(R.id.chat_list_fragment);
        mLayoutManager = new LinearLayoutManager(v.getContext());

        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(mLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(current_user_id);
        mMessageDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDatabase.keepSynced(true);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder>(
                Friends.class,
                R.layout.user_single_layout,
                FriendsFragment.FriendsViewHolder.class,
                mMessageDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsFragment.FriendsViewHolder friendsViewHolder, Friends model, final int position) {


                final String user_list_id = getRef(position).getKey();


                mUserDatabase.child(user_list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThump = dataSnapshot.child("themp_up").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);

                        }

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThump);


                        //--------------------------GET LAST TEXT FROM CONVERSATION ------------------------------------------------------------

                        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference messageRef = mRootRef.child("messages").child(current_user_id).child(user_list_id);
                        Query messageQuery = messageRef.limitToLast(1);

                        messageQuery.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                Messages messages = dataSnapshot.getValue(Messages.class);
                                if(!messages.getType().equals("text")){
                                    friendsViewHolder.setDate("photo has sent");
                                }else {
                                    s = messages.getMessage();
                                    friendsViewHolder.setDate(s);
                                }

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



                        //--------------------------------------------------------------------------------------


                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("user_id", user_list_id);
                                chatIntent.putExtra("user_name", userName);

                                startActivity(chatIntent);


                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };


        mChatList.setAdapter(adapter);
    }

    //-----------------------------------------------------------------

    private ArrayList<String> loadMessages(String mCurrentUserId, String mChattUser) {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChattUser);
        Query messageQuery = messageRef.limitToLast(1);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Messages messages = dataSnapshot.getValue(Messages.class);
                s = messages.getMessage();
                mList.add(s);


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


        return (ArrayList<String>) mList;
    }

    //------------------------------------------------------------------

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);
        }

        public void setName(String name) {
            TextView displayName = (TextView) mView.findViewById(R.id.user_single_name);
            displayName.setText(name);
        }

        public void setUserImage(String image) {
            CircleImageView userImage = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(mView.getContext()).load(image).placeholder(R.drawable.sm).into(userImage);
        }

        public void setUserOnline(String status) {
            ImageView useronline = (ImageView) mView.findViewById(R.id.user_single_onlin);
            if (status.equals("true")) {
                useronline.setVisibility(View.VISIBLE);
            } else
                useronline.setVisibility(View.INVISIBLE);


        }

    }

}
