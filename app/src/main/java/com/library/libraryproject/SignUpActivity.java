package com.library.libraryproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity {

    private static final int REQUEST_FOR_OTPACTIVITY = 789 , REQUEST_FOR_ACTIVITY = 456;
    EditText nameET, contactET, passwordET;
    Button rollnoBT;
    String rollnoOnButton, newbarcode;
    ProgressDialog dialog;
    DatabaseReference ref;
    String name, contact , rollno , password;
    String branchF , contactF , emailF , nameF , courceF  , passwordF , rollnoF , batchF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        PermissionHelper.askPermission(this,SignUpActivity.this);

        TextInputLayout usernameTextObj = findViewById(R.id.inputlayout12);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lekton_bold.ttf");
        usernameTextObj.setTypeface(font);

        ref = FirebaseDatabase.getInstance().getReference();

        rollnoOnButton = getResources().getString(R.string.roll_no_scan_id);
        nameET = findViewById(R.id.manual_name);
        contactET = findViewById(R.id.manual_contact);
        passwordET = findViewById(R.id.manual_password);
        rollnoBT = findViewById(R.id.scanidbarcode);
    }

    public void submitResult(View view) {
        name = nameET.getText().toString().trim();
        contact = contactET.getText().toString().trim();
        password = passwordET.getText().toString().trim();
        rollno = newbarcode;

        if(!Utility.isConnected(SignUpActivity.this)){
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else
        if (name.isEmpty()) {
            nameET.setError("Enter Name");
            nameET.requestFocus();
        } else if (contact.isEmpty()|| contact.length() != 10) {
            contactET.setError("Invalid Contact");
            contactET.requestFocus();
        } else if (password.isEmpty() || password.length() < 5) {
            passwordET.setError("Enter at least 5 characters");
            passwordET.requestFocus();
        }
        else if (rollno == null) {
            alertDialog("Scan your Id card.");
        }
        else {
            progressDialog();
            contact = "+91" + contact;
            name = name.trim().replaceAll("\\s+", " ").toUpperCase();
            checkVerification(name , rollno, contact, password);
        }

    }

    public void scanBarcode(View view) {
        if (PermissionHelper.allPermissionsGranted(this)) {
            Intent intent = new Intent(SignUpActivity.this, BarcodeActivity.class);
            startActivityForResult(intent, REQUEST_FOR_ACTIVITY);
        } else {
            PermissionHelper.getRuntimePermissions(this, SignUpActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_FOR_ACTIVITY == requestCode) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                newbarcode = (Objects.requireNonNull(data.getData())).toString();
                if (rollnoBT.getText() != newbarcode) {
                    rollnoBT.setText(newbarcode);
                }
            }
        }

    }


    private void progressDialog() {
        dialog = ProgressDialog.show(SignUpActivity.this, "Loading...", "Please wait...", true);
    }

    private void checkVerification(String name , String rollno, String contact, String password) {

        ref.child("students").orderByChild("contact").equalTo(contact).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dialog.dismiss();
                    try {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            rollnoF = ds.getKey();
                            Log.e("TAG", "onDataChange: execute 1" );
                            if (rollnoF != null) {
                                Log.e("TAG", "onDataChange: execute 2" );
                                if (rollnoF.equals(rollno)) {
                                    Log.e("TAG", "onDataChange: execute 3" );
                                    HashMap as = (HashMap) ds.getValue();
                                    if (as != null) {
                                        Log.e("TAG", "onDataChange: execute 4" );
                                        passwordF = (String) as.get("password");
                                        nameF = (String) as.get("name");
                                        if (name.equalsIgnoreCase(nameF)) {
                                            Log.e("TAG", "onDataChange: execute 5" );
                                            if (passwordF.isEmpty()) {
                                                Log.e("TAG", "onDataChange: execute 6" );
                                                branchF = (String) as.get("branch");
                                                contactF = (String) as.get("contact");
                                                courceF = (String) as.get("course");
                                                emailF = (String) as.get("email");
                                                //batchF = (String) as.get("batch");
                                                alertDialogOTP("You receive an OTP on "+ contact + " number. \nPlease click on send.",
                                                        nameF,
                                                        branchF,
                                                        courceF,
                                                        emailF,
                                                        password,
                                                        contact,
                                                        rollnoF
                                                        );
                                            } else {
                                                alertDialog("You already registered.");
                                            }
                                        } else {
                                            alertDialog("Invalid Name.");
                                        }
                                    }
                                } else {
                                    alertDialog("We didn't recognize this details. Please try again.");
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    dialog.dismiss();
                    alertDialog("We didn't recognize this details. Please try again..");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void alertDialogOTP(String message, String nameF, String branchF, String courceF, String emailF, String password,
                                String contact, String rollnoF) {
        Log.e("tg", "alertDialogOTP:  77" );
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false).
                setPositiveButton("Send", (dialogInterface, i) -> {

                    Intent intent = new Intent(SignUpActivity.this , OTPActivity.class);
                    intent.putExtra(AppConstant.Name, nameF);
                    intent.putExtra(AppConstant.Branch, branchF);
                    intent.putExtra(AppConstant.Contact, contact);
                    intent.putExtra(AppConstant.Course, courceF);
                    intent.putExtra(AppConstant.Email, emailF);
                    intent.putExtra(AppConstant.Password, password);
                    intent.putExtra(AppConstant.RollNo, rollnoF);

                    startActivity(intent);

                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel()).create().show();
    }

    public void alertDialog(String message) {
        SweetAlertDialog dialog = new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE);
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
}
