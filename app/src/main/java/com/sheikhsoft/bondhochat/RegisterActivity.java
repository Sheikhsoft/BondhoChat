package com.sheikhsoft.bondhochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDispalayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;

    private Toolbar mToolbar;

    private ProgressDialog mRegPrograss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegPrograss = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();

        mDispalayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_register_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayname = mDispalayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(displayname) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    mRegPrograss.setTitle("Registraing User");
                    mRegPrograss.setMessage("Prease Wait while we create your account");
                    mRegPrograss.setCanceledOnTouchOutside(false);
                    mRegPrograss.show();

                    register_user(displayname,email,password);

                }


            }
        });
    }

    private void register_user(final String displayname, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                         if(task.isSuccessful()) {

                             FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

                             String uid = current_user.getUid();
                             String deviceToken = FirebaseInstanceId.getInstance().getToken();

                             mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                             HashMap<String, String> userMAp = new HashMap<String, String>();
                             userMAp.put("device_token", deviceToken);
                             userMAp.put("name", displayname);
                             userMAp.put("status", "Hi there, I'm Using Bondho chat");
                             userMAp.put("image","defalt");
                             userMAp.put("thumb_image","defalt");

                             mDatabase.setValue(userMAp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                         mRegPrograss.dismiss();
                                         Intent mainIntent = new Intent( RegisterActivity.this,MainActivity.class);
                                         mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(mainIntent);
                                         finish();
                                     }
                                 }
                             });
                        }
                        else{
                             mRegPrograss.hide();
                             Toast.makeText(RegisterActivity.this, "zcannot Sign in.Please Check the form And Try Again",
                                     Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
