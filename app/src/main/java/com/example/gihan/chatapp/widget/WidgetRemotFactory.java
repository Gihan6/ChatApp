package com.example.gihan.chatapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.gihan.chatapp.ContentProvider.RequestProvider;
import com.example.gihan.chatapp.R;
import com.example.gihan.chatapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gihan on 11/18/2017.
 */

public class WidgetRemotFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    Cursor CR;
    List<Users> mList;



    private FirebaseAuth mAuth;
    String current_user_id;


    public WidgetRemotFactory(Context applicationContext) {

        mContext = applicationContext;

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {


        CR = mContext.getContentResolver().query(RequestProvider.CONTENT_URI, null, null, null, null);
        mList = new ArrayList<>();

        mList.clear();
        CR.moveToFirst();
        while ((CR.moveToNext())) {


            Users ob = new Users();

            current_user_id = CR.getString(3);
          // if (current_user_id.equals(mAuth.getCurrentUser().getUid())) {

                ob.setName(CR.getString(1));
                ob.setImage(CR.getString(2));



                mList.add(ob);

         //   }

        }
    }

    @Override
    public void onDestroy() {
        CR.close();

    }

    @Override
    public int getCount() {



        if (CR == null) {
            return 0;
        } else
            return mList.size();    }

    @Override
    public RemoteViews getViewAt(int position) {



        Users mItem = mList.get(position);
        String grdiant =mItem.getName() ;
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.user_single_layout);

        remoteView.setTextViewText(R.id.user_single_name, grdiant);
        remoteView.setTextColor(R.id.user_single_name,Color.WHITE);


        Intent intent = new Intent();

        remoteView.setOnClickFillInIntent(R.id.main_tabs, intent);

        //remoteView.setTextViewText(R.id.widget_recip_name, GridRemoteFactory.recipName);

        return remoteView;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
