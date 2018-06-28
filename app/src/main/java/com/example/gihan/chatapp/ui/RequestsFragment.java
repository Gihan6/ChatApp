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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {


    private RecyclerView mRequestLisr;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference mRequestDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String current_user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_requests, container, false);


        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(current_user_id);
        mRequestDatabase.keepSynced(true);
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("users");
        mUserDatabase.keepSynced(true);

        mRequestLisr = (RecyclerView) v.findViewById(R.id.request_list);
        mLayoutManager = new LinearLayoutManager(getContext());

        mRequestLisr.setHasFixedSize(true);
        mRequestLisr.setLayoutManager(mLayoutManager);


        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

//-----------------------------------------


        FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder>(
                Friends.class,
                R.layout.user_single_layout,
                FriendsFragment.FriendsViewHolder.class,
                mRequestDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsFragment.FriendsViewHolder friendsViewHolder, Friends model, int position) {


                friendsViewHolder.setDate(model.getDate());
                final String user_list_id = getRef(position).getKey();

                mUserDatabase.child(user_list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThump = dataSnapshot.child("themp_up").getValue().toString();
                        String status=dataSnapshot.child("status").getValue().toString();



                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThump);
                        friendsViewHolder.setDate(status);

                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", user_list_id);
                                startActivity(profileIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };



    mRequestLisr.setAdapter(adapter);

    //************************************************************************
    }

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
