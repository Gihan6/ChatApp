package com.example.gihan.chatapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.gihan.chatapp.R;
import com.example.gihan.chatapp.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUserList;

    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mToolbar=(Toolbar)findViewById(R.id.user_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("users");


        mUserList=(RecyclerView)findViewById(R.id.user_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UserViewHolder> adapter=new FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,R.layout.user_single_layout,UserViewHolder.class,mUserDatabase

        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Users model, int position) {

                viewHolder.setuserName(model.getName());
                viewHolder.setUserImage(model.getImage());
                viewHolder.setUserStatus(model.getStatus());

                final String userId=getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent=new Intent(getApplicationContext(),ProfileActivity.class);
                        profileIntent.putExtra("user_id",userId);

                        startActivity(profileIntent);

                    }
                });





            }
        };
        mUserList.setAdapter(adapter);
    }

   public static class UserViewHolder extends RecyclerView.ViewHolder{

       View mView;

       public UserViewHolder(View itemView) {
           super(itemView);
           mView=itemView;



       }
       public void setuserName(String name){
           TextView userName=(TextView)mView.findViewById(R.id.user_single_name);
           userName.setText(name);
       }

       public void setUserStatus(String status){
           TextView userStatus=(TextView)mView.findViewById(R.id.user_single_status);
           userStatus.setText(status);
       }

       public void  setUserImage(String image){
           CircleImageView userImage=(CircleImageView)mView.findViewById(R.id.user_single_image);
           Picasso.with(mView.getContext()).load(image).placeholder(R.drawable.sm).into(userImage);
       }
   }

}
