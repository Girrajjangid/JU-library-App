package com.library.libraryproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SplashActivity extends AppCompatActivity {

    Animation anim1, anim2, anim3;
    ImageView imageViewlogo;
    LinearLayout linearlayoutabove, linearlayoutbelow;
    TransitionDrawable transition;
    EditText rollnoET, passwordET;
    String rollno, password;
    ProgressDialog dialog;
    SharedPreferences prefs;
    public static final String preference = "UserData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        rollnoET = findViewById(R.id.logginrollno);
        passwordET = findViewById(R.id.logginpassword);

        TextInputLayout usernameTextObj = findViewById(R.id.inputlayout123);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lekton_bold.ttf");
        usernameTextObj.setTypeface(font);


        imageViewlogo = findViewById(R.id.logo);
        linearlayoutabove = findViewById(R.id.linearlayoutabove);
        linearlayoutbelow = findViewById(R.id.linearlayoutbelow);

        transition = (TransitionDrawable) findViewById(R.id.parent_relativelayout).getBackground();
        anim1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation1);
        anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation2);
        anim3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation3);

        /*EditText password = (EditText) findViewById(R.id.register_password_text);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());*/

        new Handler().postDelayed(() -> {
            imageViewlogo.animate().translationY(50).setDuration(800);
            imageViewlogo.startAnimation(anim1);

            transition.startTransition(1500);
            linearlayoutabove.setVisibility(View.VISIBLE);
            linearlayoutbelow.setVisibility(View.VISIBLE);
            linearlayoutabove.startAnimation(anim2);
            linearlayoutbelow.startAnimation(anim3);
        }, 2000);

    }

    public void alertDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void progressDialog() {
        dialog = ProgressDialog.show(SplashActivity.this, "Loading...", "Please wait...", true);
    }


    public void signUp(View view) {
        startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
    }

    public void signIn(View view) {

        String password = passwordET.getText().toString().trim();
        String rollno = rollnoET.getText().toString().trim();
        if (rollno.isEmpty()) {
            rollnoET.setError("Enter Registration No.");
            rollnoET.requestFocus();
        } else if (password.isEmpty() || password.length() < 5) {
            passwordET.setError("Enter at least 5 characters");
            passwordET.requestFocus();
        } else {
            Toast.makeText(this, "thik", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onResume() {
        prefs = getSharedPreferences(preference, Context.MODE_PRIVATE);
        if (prefs.contains("rollno")) {
            if (prefs.contains("password")) {
                Intent i = new Intent(this, CheckInOutActivity.class);
                startActivity(i);
                finish();
            }
        }
        super.onResume();
    }

}
