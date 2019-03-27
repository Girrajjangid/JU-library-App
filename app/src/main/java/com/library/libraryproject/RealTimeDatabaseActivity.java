package com.library.libraryproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import androidx.annotation.RequiresApi;

public class RealTimeDatabaseActivity extends AppCompatActivity {

    DatabaseReference databaseArtists;
    String atif;
    CameraManager mCameraManager;
    String mCameraId;
    private static final String TAG = "RealTimeDatabaseActivit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlashAvailable) {
            showNoFlashError();
        }

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchFlashLight(isChecked);
            }
        });
    }

    public void showNoFlashError() {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Oops!");
        alert.setMessage("Flash not available in this device...");
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void switchFlashLight(boolean status) {
        try {
            mCameraManager.setTorchMode(mCameraId, status);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
        //atif = "girraj";
        //databaseArtists = FirebaseDatabase.getInstance().getReference("artists"); //root node
        //    databaseArtists.setValue("Hello, World!");


    /*
    public void update(View view) {
        String id = databaseArtists.push().getKey();  // this will uniqe every time
        // now we have two values id and atif
        if (id != null) {
            databaseArtists.child(atif).setValue(id);
            Toast.makeText(this, "added", Toast.LENGTH_SHORT).show();
        }

    }


    public void retrieve(View view) {
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.child(atif).getValue(String.class);
                Toast.makeText(RealTimeDatabaseActivity.this, value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());

            }
        });

    }*/

