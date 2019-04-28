package com.library.libraryproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class FirebaseImageUpload extends AppCompatActivity {
    ImageView imageView;
    String imageurl;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_image_upload);
       // imageView = findViewById(R.id.imageviewphoto);
        databaseReference  = FirebaseDatabase.getInstance().getReference("students/16BCON046");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageurl = dataSnapshot.child("imageurl").getValue(String.class);
                Toast.makeText(FirebaseImageUpload.this, imageurl, Toast.LENGTH_SHORT).show();


                Picasso.with(FirebaseImageUpload.this)
                        .load(imageurl)
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(imageView); }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(FirebaseImageUpload.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
        Picasso.with(this).load(imageurl).into(imageView);

    }

}
