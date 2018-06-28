package com.example.gihan.chatapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gihan.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegesterActivity extends AppCompatActivity {


    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassord;
    private Button mCreateAccount;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regester);

        mAuth = FirebaseAuth.getInstance();


        mDisplayName = (TextInputLayout) findViewById(R.id.textInputLayout3);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassord = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateAccount = (Button) findViewById(R.id.reg_btn);

        mProgressDialog = new ProgressDialog(this);


        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String displayName = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassord.getEditText().getText().toString();

                if (!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    mProgressDialog.setMessage("Please Wait For Create New account");
                    mProgressDialog.setTitle("Regestration");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    regestration(displayName, email, password);


                }
                else {
                    Toast.makeText(getApplicationContext(), "Complete data", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    private void regestration(final String displayName, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            final String uid = currentUser.getUid();

                            final String device_tocken = FirebaseInstanceId.getInstance().getToken();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            HashMap<String, String> userMap = new HashMap<String, String>();

                            userMap.put("name", displayName);
                            userMap.put("status", "Hi i am here using chat app");
                            userMap.put("image", "default");
                            userMap.put("themp_up", "default");
                            userMap.put("device_tocken", device_tocken);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        //--------SAVE DEVICE TOCKEN

                                        mProgressDialog.dismiss();
                                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }

                                }
                            });


                        } else {
                            mProgressDialog.hide();
                            Toast.makeText(getApplicationContext(), "Error Try again !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
