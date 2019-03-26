package com.library.libraryproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    DatabaseReference databaseStudents;

    Animation animfadeIn , animfadeOut,animlogo;
    ImageView imageViewlogo;
    LinearLayout linearlayoutsplash;
    TransitionDrawable transition;
    final int REQUEST_FOR_ACTIVITY = 456;
    EditText nameET  , courseET , branchET , contactET ;
    String rollnoOnButton , newbarcode;
    Button rollnoBT;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        rollnoOnButton = getResources().getString(R.string.roll_no_scan_id);
        nameET = findViewById(R.id.manual_name);
        courseET = findViewById(R.id.manual_course);
        branchET = findViewById(R.id.manual_branch);
        contactET = findViewById(R.id.manual_contact);
        rollnoBT = findViewById(R.id.scanidbarcode);

        linearlayoutsplash = findViewById(R.id.linearlayoutsplash);
        imageViewlogo = findViewById(R.id.logo);

        transition = (TransitionDrawable) findViewById(R.id.parent_relativelayout).getBackground();
        animlogo =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation1);
        animfadeOut =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        animfadeIn =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        new Handler().postDelayed(() -> {
            imageViewlogo.startAnimation(animfadeIn);
            transition.startTransition(1500);
            linearlayoutsplash.setVisibility(View.VISIBLE);
            linearlayoutsplash.startAnimation(animfadeOut);
        }, 2000);

           }

    public void scanBarcode(View view) {
        Intent intent = new Intent(SplashActivity.this , BarcodeActivity.class);
        startActivityForResult(intent , REQUEST_FOR_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if(REQUEST_FOR_ACTIVITY == requestCode){
        if(resultCode == RESULT_OK){
            assert data != null;
            newbarcode = (Objects.requireNonNull(data.getData())).toString();
            if(rollnoBT.getText() != newbarcode){
                rollnoBT.setText(newbarcode);
            }
        }
    }
    }

    public void submitResult(View view) {
        String name = nameET.getText().toString().trim();
        String branch = branchET.getText().toString().trim();
        String course = courseET.getText().toString().trim();
        String contact = contactET.getText().toString().trim();
        String rollno = newbarcode;
        checkVerification("16BCON046" , "8302701556");
        /*if(name.isEmpty()){
            nameET.setError("Enter Name");
            nameET.requestFocus();
        }
        else if(branch.isEmpty()){
            branchET.setError("Enter Branch");
            branchET.requestFocus();
        }
        else if(course.isEmpty()){
            branchET.setError("Enter Course");
            branchET.requestFocus();
        }
        else if(contact.isEmpty()){
            branchET.setError("Enter Contact");
            branchET.requestFocus();
        }
        else if(rollno.equalsIgnoreCase(rollnoOnButton)){
            branchET.setError("Scan ID Card");
            branchET.requestFocus();
        }
        else {
            checkVerification(rollno , contact);

        }
*/
    }

    private void checkVerification(String rollno , String contact) {
        contact = "+91" + contact;
        mAuth = FirebaseAuth.getInstance();
        rollno = "16BCON622" +
                "";
        //databaseStudents = FirebaseDatabase.getInstance().getReference("students"); //root node
        //DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        /*DatabaseReference userNameRef = rootRef.child("students").child(rollno).child("contact");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()) {
                    //create new user
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
*/
/*
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("students");
        Query query = rootRef.child(rollno).orderByChild("contact").equalTo(contact);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String value = dataSnapshot.child(atif).getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })*/

        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference("students").child(rollno);
        ref.orderByChild("contact").equalTo(contact).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        dataSnapshot.child(atif).getValue(String.class);
                    Toast.makeText(SplashActivity.this, "", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SplashActivity.this, "exist", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SplashActivity.this, "galat hai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        contact = "+918302701556";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("students");
        String finalContact = contact;
        ref.orderByKey().equalTo(rollno).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    //Key exists
                    Toast.makeText(SplashActivity.this, "exists", Toast.LENGTH_SHORT).show();

                    ref.child("16BCON046").orderByChild("contact").equalTo(finalContact).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(SplashActivity.this, "number shi hai", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SplashActivity.this, "galat hai", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(SplashActivity.this, "nahi hai", Toast.LENGTH_SHORT).show();
                    //Key does not exist
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void alertDialog(String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                //*****you can also leave this function******
            }
        }).create().show();

    }

    private void progressDialog(){
        dialog = ProgressDialog.show(SplashActivity.this, "", "Loading. Please wait...", true);

    }
}
