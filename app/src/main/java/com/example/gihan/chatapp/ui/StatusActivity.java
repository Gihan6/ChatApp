package com.example.gihan.chatapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gihan.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mChangeStatus;

    private DatabaseReference mDatabaseStatus;
    private FirebaseUser curentUser;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mToolbar=(Toolbar)findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        curentUser=FirebaseAuth.getInstance().getCurrentUser();
        String uid=curentUser.getUid();
        mDatabaseStatus= FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        mProgress=new ProgressDialog(this);


        mStatus=(TextInputLayout) findViewById(R.id.status_input);
        mChangeStatus=(Button)findViewById(R.id.status_chnge_status);

        String statusVlaue=getIntent().getExtras().getString("status_value");
        mStatus.getEditText().setText(statusVlaue);


        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status=mStatus.getEditText().getText().toString();

                if(! TextUtils.isEmpty(status)){
                    mProgress.setTitle("saving Change");
                    mProgress.setMessage("Wait to save changing");
                    mProgress.show();

                    mDatabaseStatus.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mProgress.dismiss();
                                mStatus.getEditText().setText("");
                            }else {

                                Toast.makeText(getApplicationContext(),"Error while saving changes",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




                }
            }
        });


    }
}
