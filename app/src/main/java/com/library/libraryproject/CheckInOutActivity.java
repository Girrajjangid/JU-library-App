package com.library.libraryproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheckInOutActivity extends AppCompatActivity {
    SharedPreferences prefs;
    public static final String preference = "UserData";
    TextView textView,textView1,textView2,textView3,textView4,textView5,textView6,textView7;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);
        logout = findViewById(R.id.button3);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView2);
        textView2 = findViewById(R.id.textView3);
        textView3 = findViewById(R.id.textView4);
        textView4 = findViewById(R.id.textView5);
        textView5 = findViewById(R.id.textView6);
        textView6 = findViewById(R.id.textView7);
        prefs = getSharedPreferences(preference, Context.MODE_PRIVATE);
        textView.setText(String.valueOf(prefs.getString("name", "null")));
        textView1.setText(String.valueOf(prefs.getString("rollno", "null")));
        textView2.setText(String.valueOf(prefs.getString("contact", "null")));
        textView3.setText(String.valueOf(prefs.getString("course", "null")));
        textView4.setText(String.valueOf(prefs.getString("branch", "null")));
        textView5.setText(String.valueOf(prefs.getString("password", "null")));
        textView6.setText(String.valueOf(prefs.getString("email", "null")));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(CheckInOutActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);
            }
        });
    }
}
