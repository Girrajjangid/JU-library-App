package com.library.libraryproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
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

    final int REQUEST_FOR_ACTIVITY = 456;
    EditText nameET  , courseET , branchET , contactET , passwordET ;
    String rollnoOnButton , newbarcode;
    Button rollnoBT;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        newbarcode = "";

        TextInputLayout usernameTextObj = findViewById(R.id.inputlayout12);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lekton_bold.ttf");
        usernameTextObj.setTypeface(font);

        rollnoOnButton = getResources().getString(R.string.roll_no_scan_id);
        nameET = findViewById(R.id.manual_name);
        //courseET = findViewById(R.id.manual_course);
        //branchET = findViewById(R.id.manual_branch);
        contactET = findViewById(R.id.manual_contact);
        passwordET = findViewById(R.id.manual_password);
        rollnoBT = findViewById(R.id.scanidbarcode);
    }

    public void submitResult(View view) {
        String name = nameET.getText().toString().trim();
        //String branch = branchET.getText().toString().trim();
        //String course = courseET.getText().toString().trim();
        String contact = contactET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String rollno = newbarcode;
        if(name.isEmpty()){
            nameET.setError("Enter Name");
            nameET.requestFocus();
        }
        /*else if(branch.isEmpty()){
            branchET.setError("Enter Branch");
            branchET.requestFocus();
        }
        else if(course.isEmpty()){
            branchET.setError("Enter Course");
            branchET.requestFocus();
        }*/

        else if(contact.isEmpty()){
            contactET.setError("Enter Contact");
            contactET.requestFocus();
        }
        else if(password.isEmpty() || password.length()<5){
            passwordET.setError("Enter at least 5 characters");
            passwordET.requestFocus();
        }
        else if(rollno.equalsIgnoreCase(rollnoOnButton)){
            rollnoBT.setError("Scan ID Card");
            rollnoBT.requestFocus();
        }
        else {
            progressDialog();
            contact = "+91"+contact;
            checkVerification(rollno , contact);
        }

    }

    public void scanBarcode(View view) {
        Intent intent = new Intent(SignUpActivity.this , BarcodeActivity.class);
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

    private void progressDialog(){
        dialog = ProgressDialog.show(SignUpActivity.this, "Loading...", "Please wait...", true);
    }

    private void checkVerification(String rollno , String contact) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("students").orderByChild("contact").equalTo(contact).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    dialog.dismiss();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey(); //it return super key
                        HashMap as = (HashMap) ds.getValue();
                        String rl2 = key;
                        String bra = (String) as.get("branch");
                        String con = (String) as.get("contact");
                        String cour = (String) as.get("course");
                        String email = (String) as.get("email");
                        String name = (String) as.get("name");
                        //Toast.makeText(SplashActivity.this, key, Toast.LENGTH_SHORT).show();
                        Toast.makeText(SignUpActivity.this,
                                "rollno :"+rl2+
                                        "\nbranch :"+bra+
                                        "\ncon :"+con+
                                        "\ncour:"+cour+
                                        "\nemail:"+email+
                                        "\nname:"+name,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "details galat hai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
