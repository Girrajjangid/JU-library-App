package com.library.libraryproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgetPassword extends AppCompatActivity {
    EditText contactET;
    SweetAlertDialog sweetAlertDialog;
    private static final String TAG = "ForgetPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        contactET = findViewById(R.id.forget_contact);

    }

    public void Continue(View view) {
        String cont = contactET.getText().toString().trim();
        if (!Utility.isConnected(ForgetPassword.this)) {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (cont.isEmpty() || cont.length() != 10) {
            contactET.setError("Invalid mobile number");
            contactET.requestFocus();
        } else {
            cont = "+91" + cont;
            progressDialogStart();
            checkFromFirebase(cont);
        }
    }

    private void checkFromFirebase(String cont) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("students").orderByChild(AppConstant.Contact).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        sweetAlertDialog.dismissWithAnimation();
                        alertDialogOTP(cont);
                    } catch (Exception e) {
                        Log.e(TAG, "onDataChange: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                sweetAlertDialog.getProgressHelper().setRimColor(R.color.custom1PrimaryDarkcheckin);
                sweetAlertDialog.setTitleText("You are not Registered.")
                        .showContentText(false)
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

    private void progressDialogStart() {
        sweetAlertDialog = Utility.sweetAlertDialogStart(this, "Loading...", "Please wait...", SweetAlertDialog.PROGRESS_TYPE);
    }

    private void alertDialogOTP(String contact) {
        String message = "You receive an OTP on " + contact + " number.";
        Log.e("tg", "alertDialogOTP:  77");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Send OTP", (dialogInterface, i) -> {
            Intent intent = new Intent(ForgetPassword.this, ForgetPasswordUpdate.class);
            intent.putExtra(AppConstant.Contact, contact);
            startActivity(intent);
        }).setNegativeButton("No", (dialog, which) -> dialog.cancel()).create().show();
    }
}
