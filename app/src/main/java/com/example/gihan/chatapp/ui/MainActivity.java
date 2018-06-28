package com.example.gihan.chatapp.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.gihan.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Toolbar mToolbar;

    private ViewPager mViewPager;

    private SectionBageAdapter mSectionBageAdapter;
    private TabLayout mTablLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mToolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MUFIX");



        if(mAuth.getCurrentUser() !=null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

        }


        mViewPager=(ViewPager)findViewById(R.id.main_tab_pager);
        mSectionBageAdapter=new SectionBageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionBageAdapter);

        mTablLayout=(TabLayout)findViewById(R.id.main_tabs);
        mTablLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();

        if(currentUser==null){
            sendToStart();

        }else {
            userRef.child("online").setValue("true");

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser=mAuth.getCurrentUser();

        if(currentUser !=null) {
            userRef.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }




    private void sendToStart() {
        Intent startIntent=new Intent(getApplication(),StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_logout){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        if(item.getItemId()==R.id.main_account_setting){
            Intent settingIntent=new Intent(getApplicationContext(),SettingActivity.class);
            startActivity(settingIntent);
        }
        if(item.getItemId()==R.id.main_all_user){
            Intent usersgIntent=new Intent(getApplicationContext(),UserActivity.class);
            startActivity(usersgIntent);
        }

        return true;
    }
}
