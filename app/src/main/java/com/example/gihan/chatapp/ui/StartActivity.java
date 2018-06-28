package com.example.gihan.chatapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.gihan.chatapp.R;

public class StartActivity extends AppCompatActivity {


    private Button mRegBtn;
    private Button mHaveAccount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        mRegBtn=(Button)findViewById(R.id.start_activity_new_account);
        mHaveAccount=(Button)findViewById(R.id.start_have_account);

        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regIntent=new Intent(getApplicationContext(),RegesterActivity.class);
                startActivity(regIntent);
            }
        });
    }


}
