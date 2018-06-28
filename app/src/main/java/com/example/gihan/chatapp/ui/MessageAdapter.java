package com.example.gihan.chatapp.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gihan.chatapp.R;
import com.example.gihan.chatapp.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Gihan on 8/17/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mListMessages;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    private String displayName;
    private String mImageProfile;
    String userFrom;
    String currentUserId;

    public MessageAdapter(List<Messages> mListMessages) {
        this.mListMessages = mListMessages;
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mRootRef = FirebaseDatabase.getInstance().getReference();


        Messages c = mListMessages.get(position);
        userFrom = c.getFrom();

        if (userFrom.equals(currentUserId)) {
            holder.mMessageSender.setBackgroundResource(R.drawable.white_message_text);
            holder.mMessageSender.setTextColor(Color.BLACK);
            holder.mMessageSender.setVisibility(View.VISIBLE);
            holder.mChatImage.setVisibility(View.INVISIBLE);
            holder.mUser.setVisibility(View.INVISIBLE);
            holder.message_display_time.setVisibility(View.INVISIBLE);
            holder.mMessageText.setVisibility(View.INVISIBLE);


            //-----------------GET IMAGE AND DISPLAY  NAME----------------------

            mRootRef.child("users").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    displayName = dataSnapshot.child("name").getValue().toString();
                    mImageProfile = dataSnapshot.child("themp_up").getValue().toString();
                    holder.putImage(mImageProfile);
                    holder.mUser.setText(displayName);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } else {
            holder.mMessageText.setBackgroundResource(R.drawable.message_text_background);
            holder.mMessageText.setTextColor(Color.WHITE);

            //-----------------GET IMAGE AND DISPLAY  NAME----------------------
            mRootRef.child("users").child(userFrom).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    displayName = dataSnapshot.child("name").getValue().toString();
                    mImageProfile = dataSnapshot.child("themp_up").getValue().toString();
                    holder.putImage(mImageProfile);
                    holder.mUser.setText(displayName);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        if (c.getType().equals("text")) {
            holder.mMessageText.setText(c.getMessage());
            holder.mMessageSender.setText(c.getMessage());
        } else {


            if (userFrom.equals(currentUserId)) {
                //------------CURRENT USER SHOW IT
                holder.mMessageSender.setVisibility(View.VISIBLE);
                holder.senderMessageImage.setVisibility(View.INVISIBLE);
                holder.senderPutImageChat(c.getMessage());
            }else {
                //------------CHAT USER THAT SHOW IT
                holder.mmessageImage.setVisibility(View.VISIBLE);
                holder.mMessageText.setVisibility(View.INVISIBLE);
                holder.putImageChat(c.getMessage());

            }
        }


    }

    @Override
    public int getItemCount() {
        return mListMessages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView mUser;
        public CircleImageView mChatImage;
        public TextView mMessageText;
        public TextView mMessageSender;

        public TextView message_display_time;
        public ImageView mmessageImage;
        ImageView senderMessageImage;


        View v;


        public MessageViewHolder(final View itemView) {
            super(itemView);
            v = itemView;

            mUser = (TextView) itemView.findViewById(R.id.message_display_name);
            mChatImage = (CircleImageView) itemView.findViewById(R.id.message_image_layout);
            mMessageText = (TextView) itemView.findViewById(R.id.message_text_layout);
            mMessageSender = (TextView) itemView.findViewById(R.id.sender);


            message_display_time = (TextView) itemView.findViewById(R.id.message_time_display);
            mmessageImage = (ImageView) itemView.findViewById(R.id.message_image_chat);
            senderMessageImage = (ImageView) itemView.findViewById(R.id.sender_message_image_chat);


//---------------------------------------------------------------------------------------------

            mMessageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CharSequence options[] = new CharSequence[]{"Delete Message"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            //CLICK EVENT FOR EACH ITEM
                            if (i == 0) {

                                Toast.makeText(itemView.getContext(), "delete", Toast.LENGTH_LONG).show();

                            }


                        }
                    });
                    builder.show();
                }


            });

            mMessageSender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CharSequence options[] = new CharSequence[]{"Delete Message"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            //CLICK EVENT FOR EACH ITEM
                            if (i == 0) {
//
//                                DatabaseReference messageRef = mRootRef.child("messages").child(currentUserId).child(userFrom);
//                                messageRef.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        String mess=dataSnapshot.child("message").getValue().toString();
//                                        if(mess==mMessageSender.getText().toString()){
//
//                                        }

//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });

                            }


                        }
                    });
                    builder.show();
                }


            });
//--------------------------------------------------------------------------------

        }


        public void putImage(String c) {
            Picasso.with(v.getContext()).load(c).placeholder(R.drawable.sm).into(mChatImage);
        }

        public void putImageChat(String c) {
            Picasso.with(v.getContext()).load(c).placeholder(R.drawable.sm).into(mmessageImage);
        }
        public void senderPutImageChat(String c) {
            Picasso.with(v.getContext()).load(c).placeholder(R.drawable.sm).into(senderMessageImage);
        }

    }
}

