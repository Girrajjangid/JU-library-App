package com.library.libraryproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class RealTimeDatabaseActivity extends AppCompatActivity {

    DatabaseReference databaseArtists;
    String atif ;
    private static final String TAG = "RealTimeDatabaseActivit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_database);
        atif = "girraj";
        databaseArtists = FirebaseDatabase.getInstance().getReference("artists"); //root node
    //    databaseArtists.setValue("Hello, World!");



    }

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

    }
}
