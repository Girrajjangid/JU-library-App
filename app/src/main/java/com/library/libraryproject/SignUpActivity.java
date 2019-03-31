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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private static final int REQUEST_FOR_OTPACTIVITY = 789 , REQUEST_FOR_ACTIVITY = 456;
    EditText nameET, contactET, passwordET;
    Button rollnoBT;
    SharedPreferences prefs;
    public static final String preference = "UserData";
    String rollnoOnButton, newbarcode;
    ProgressDialog dialog;
    DatabaseReference ref;
    String name, contact , rollno , password;
    String branchF , contactF , emailF , nameF , courceF  , passwordF , rollnoF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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

        /*name = "asdasdf";
        contact = "8302701556";
        password = "asdasd";
        rollno = "16BCON046";*/

        if (name.isEmpty()) {
            nameET.setError("Enter Name");
            nameET.requestFocus();
        } else if (contact.isEmpty()|| contact.length() != 10) {
            contactET.setError("Enter Contact");
            contactET.requestFocus();
        } else if (password.isEmpty() || password.length() < 5) {
            passwordET.setError("Enter at least 5 characters");
            passwordET.requestFocus();
        }
        else if (newbarcode == null) {
            alertDialog("Scan your Id card.");
        }
        else {
            progressDialog();
            contact = "+91" + contact;
            checkVerification(rollno, contact, password);
        }

    }

    public void scanBarcode(View view) {
        Intent intent = new Intent(SignUpActivity.this, BarcodeActivity.class);
        startActivityForResult(intent, REQUEST_FOR_ACTIVITY);

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
        if (REQUEST_FOR_OTPACTIVITY == requestCode) {
                if(resultCode == RESULT_OK) {
                    if ( data != null && data.hasExtra(AppConstant.PhoneNumber) && data.getStringExtra(AppConstant.PhoneNumber) != null) {
                        String no = data.getStringExtra(AppConstant.PhoneNumber);
                        Toast.makeText(this, no + "Verified", Toast.LENGTH_SHORT).show();
                        sucessfullyVerified();
                    }
                }
                else{
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sucessfullyVerified() {
        ref.child("students").child(rollno).child("password").setValue(password);
        prefs = getSharedPreferences(preference, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", nameF);
        editor.putString("email", emailF);
        editor.putString("rollno", rollnoF);
        editor.putString("contact", contactF);
        editor.putString("password", password);
        editor.putString("course", courceF);
        editor.putString("branch", branchF);
        editor.apply();
        Intent intent = new Intent(SignUpActivity.this,CheckInOutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void progressDialog() {
        dialog = ProgressDialog.show(SignUpActivity.this, "Loading...", "Please wait...", true);
    }

    private void checkVerification(String rollno, String contact, String password) {

        ref.child("students").orderByChild("contact").equalTo(contact).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dialog.dismiss();
                    try {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            rollnoF = ds.getKey();
                            if (rollnoF != null) {
                                if (rollnoF.equals(rollno)) {
                                    HashMap as = (HashMap) ds.getValue();
                                    if (as != null) {
                                        passwordF = (String) as.get("password");
                                        if (passwordF.isEmpty()) {
                                            branchF   = (String) as.get("branch");
                                            contactF   = (String) as.get("contact");
                                            courceF  = (String) as.get("course");
                                            emailF = (String) as.get("email");
                                            nameF  = (String) as.get("name");
                                            alertDialogOTP("OTP send on this entered number." , contact );
                                        } else {
                                            alertDialog("You already registered");
                                        }
                                    }
                                } else {
                                    alertDialog("Registration number does not match with registered mobile number.");
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    dialog.dismiss();
                    alertDialog("Registration number does not match with registered mobile number.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignUpActivity.this, "Wrong Details", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void alertDialogOTP(String message,String contact) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false).
                setPositiveButton("Send", (dialogInterface, i) -> {

                    Intent intent = new Intent(SignUpActivity.this , OTPActivity.class);
                    intent.putExtra("PhoneNumber", contact);

                    startActivityForResult(intent, REQUEST_FOR_OTPACTIVITY);
                }).setNegativeButton("No", (dialog, which) -> dialog.cancel()).create().show();
    }

    public void alertDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false).
                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create().show();
    }
}
