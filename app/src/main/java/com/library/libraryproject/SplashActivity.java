package com.library.libraryproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends AppCompatActivity {

    Animation anim1, anim2, anim3;
    ImageView imageViewlogo;
    LinearLayout linearlayoutabove, linearlayoutbelow;
    TransitionDrawable transition;
    EditText rollnoET, passwordET;
    String rollno, password;
    ProgressDialog dialog;
    SharedPreferences prefs;
    Button signup;
    public static final String preference = "UserData";
    private static final String TAG = "SplashActivity";
    private String nameF, branchF, contactF, courceF, emailF;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rollnoET = findViewById(R.id.logginrollno);
        passwordET = findViewById(R.id.logginpassword);
        signup = findViewById(R.id.signup_key);

        TextInputLayout usernameTextObj = findViewById(R.id.inputlayout123);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf");
        usernameTextObj.setTypeface(font);


        imageViewlogo = findViewById(R.id.logo);
        linearlayoutabove = findViewById(R.id.linearlayoutabove);
        linearlayoutbelow = findViewById(R.id.linearlayoutbelow);

        //transition = (TransitionDrawable) findViewById(R.id.parent_relativelayout).getBackground();
        anim1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation1);
        anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation2);
        anim3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation3);

        /*EditText password = (EditText) findViewById(R.id.register_password_text);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());*/
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUp(v);

            }
        });

        new Handler().postDelayed(() -> {
            imageViewlogo.animate().translationY(50).setDuration(800);
            imageViewlogo.startAnimation(anim1);

            //transition.startTransition(1500);
            linearlayoutabove.setVisibility(View.VISIBLE);
            linearlayoutbelow.setVisibility(View.VISIBLE);
            linearlayoutabove.startAnimation(anim2);
            linearlayoutbelow.startAnimation(anim3);
        }, 2000);

    }

    public void alertDialog(String message) {
        SweetAlertDialog dialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText("Oops...");
        dialog.setContentText(message);
        dialog.setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void progressDialogStart() {

        sweetAlertDialog = Utility.sweetAlertDialogStart(this, "Loading...", "Please wait...", SweetAlertDialog.PROGRESS_TYPE);

    }

    public void signUp(View view) {
        startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
    }

    public void signIn(View view) {

        String password = passwordET.getText().toString().trim();
        String rollno = rollnoET.getText().toString().trim();

        if (!Utility.isConnected(SplashActivity.this)) {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        } else if (rollno.isEmpty()) {
            rollnoET.setError("Enter Registration No.");
            rollnoET.requestFocus();
        } else if (password.isEmpty() || password.length() < 5) {
            passwordET.setError("Enter at least 5 characters");
            passwordET.requestFocus();
        } else {
            rollno = rollno.toUpperCase();
            progressDialogStart();
            checkFromFirebase(rollno, password);

        }
    }

    private void checkFromFirebase(String rollno, String password) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("students").orderByKey().equalTo(rollno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sweetAlertDialog.dismissWithAnimation();
                    try {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String rollnoF = ds.getKey();
                            if (rollnoF != null) {
                                HashMap as = (HashMap) ds.getValue();
                                if (as != null) {
                                    String passwordF = (String) as.get("password");
                                    if (passwordF.isEmpty()) {
                                        alertDialog("You are not registered.");
                                    } else if (passwordF.equals(password)) {
                                        sucessfullyVerified((String) as.get("name"),
                                                (String) as.get("email"),
                                                rollnoF,
                                                (String) as.get("contact"),
                                                passwordF,
                                                (String) as.get("course"),
                                                (String) as.get("branch"),
                                                (Long) as.get("batch"),
                                                (String) as.get("imageurl"));
                                        Log.e(TAG, "onDataChange: Sucessfully added");
                                    } else {
                                        alertDialog("Wrong password.");
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    sweetAlertDialog.dismissWithAnimation();
                    alertDialog("Invalid Registration number.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void sucessfullyVerified(String strName, String strEmail, String strRollno, String strContact, String strPassword, String strCourse,
                                     String strBranch, Long intbatch, String imageurl) {
        prefs = getSharedPreferences(preference, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", strName);
        editor.putString("email", strEmail);
        editor.putString("rollno", strRollno);
        editor.putString("contact", strContact);
        editor.putString("password", strPassword);
        editor.putString("course", strCourse);
        editor.putString("branch", strBranch);
        editor.putString("imageurl", imageurl);
        editor.putLong("batch", intbatch);
        editor.apply();
        Intent intent = new Intent(SplashActivity.this, CheckInOutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    protected void onResume() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            prefs = getSharedPreferences(preference, Context.MODE_PRIVATE);
            if (prefs.contains("rollno")) {
                if (prefs.contains("password")) {
                    Intent i = new Intent(this, CheckInOutActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        } else {
            Toast.makeText(this, "This App doesn't work properly in your phone.", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onResume();
    }

    public void forgetPassword(View view) {
        startActivity(new Intent(SplashActivity.this, ForgetPassword.class));
    }
}
