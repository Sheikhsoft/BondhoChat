package com.sheikhsoft.bondhochat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mStatus;
    private Button mStatusSubmitBtn;

    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrent_user;
    private ProgressDialog mPrograssDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrent_user.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);

        mToolbar = (Toolbar)findViewById(R.id.status_page_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatus = (TextInputLayout)findViewById(R.id.status_status);
        mStatusSubmitBtn =(Button)findViewById(R.id.status_submit_btn);

        String statusValue = getIntent().getStringExtra("status_value");
        mStatus.getEditText().setText(statusValue);



        mStatusSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrograssDialog = new ProgressDialog(StatusActivity.this);
                mPrograssDialog.setTitle("Saving Status");
                mPrograssDialog.setMessage("Please Wait While We Save Your Status");
                mPrograssDialog.show();

                String status = mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mPrograssDialog.dismiss();
                        }else{
                            Toast.makeText(StatusActivity.this, "There Is Some Error In Savin Status", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}
