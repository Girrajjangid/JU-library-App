package com.library.libraryproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchAuthListener;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ErrorListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.bson.BsonValue;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MongoDBHelper {
    String appid = null;
    Context context;
    private String database = null;
    private String collection = null;
    private static final String TAG = "MongoDBHelper";
    private RemoteMongoCollection coll = null;
    private StitchAppClient client = null;
    private RemoteMongoClient mongoClient = null;
    Date checkindate;
    SharedPreferences prefs = null;
    SweetAlertDialog dialog;
    Activity activity;
    TextView tvname , tvqrcodename;
    ImageView qrcodeiv;

    public static final String preference = AppConstant.PREFERENCENAME;

    public MongoDBHelper(Activity activity,
            Context context, String appid, String database, String collection, SharedPreferences prefs , SweetAlertDialog dialog) {
        this.appid = appid;
        this.context = context;
        this.database = database;
        this.collection = collection;
        this.prefs = prefs;
        this.dialog = dialog;
        this.activity = activity;
    }

    public void initilizing() {
        //client = Stitch.initializeDefaultAppClient(appid);
        //client = Stitch.initializeAppClient(appid);

        client = Stitch.getDefaultAppClient();
        mongoClient = client.getServiceClient(RemoteMongoClient.factory, AppConstant.MONGODB_SERVICE);
        coll = mongoClient.getDatabase(database).getCollection(collection);
        Log.e(TAG, "initilizing: Successfully initilizing");
        if (prefs.contains(AppConstant.ChECKINOUT_UNIQUEID)) {
            CheckOut(prefs.getString(AppConstant.ChECKINOUT_UNIQUEID, null));
        } else {
            checkIn();
        }
    }

    public void loginWithCredential() {
        client = Stitch.getDefaultAppClient();
        client.getAuth().loginWithCredential(new AnonymousCredential())
                .addOnCompleteListener(new OnCompleteListener<StitchUser>() {
                    @Override
                    public void onComplete(@NonNull Task<StitchUser> task) {
                        mongoClient = client.getServiceClient(RemoteMongoClient.factory, AppConstant.MONGODB_SERVICE);
                        coll = mongoClient.getDatabase(database).getCollection(collection);
                        Log.e(TAG, "onComplete: Successfully logged In");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(AppConstant.MONGODB_INIT, true);
                        editor.apply();
                        if (prefs.contains(AppConstant.ChECKINOUT_UNIQUEID)) {
                            CheckOut(prefs.getString(AppConstant.ChECKINOUT_UNIQUEID, null));
                        } else {
                            checkIn();
                        }
                    }
                });


    }
    public void checkIn() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();

        SharedPreferences.Editor editor = prefs.edit();
        final int min = 10000000;
        final int max = 90000000;
        Random random = new Random();
        int random_no1 = random.nextInt((max - min) + 1) + min;
        int random_no2 = random.nextInt((max - min) + 1) + min;
        String random_id = String.valueOf(random_no1) + String.valueOf(random_no2);
        Log.e(TAG, "insertingNew: " + String.valueOf(random_no1) + String.valueOf(random_no2));
        final Document newItem = new Document(AppConstant.OWNER_ID, client.getAuth().getUser().getId())
                .append(AppConstant.UNIQUE_ID, random_id)
                .append(AppConstant.Name, prefs.getString(AppConstant.Name, null))
                .append(AppConstant.STREAM, new Document()
                        .append(AppConstant.Course, prefs.getString(AppConstant.Course, null))
                        .append(AppConstant.Branch, prefs.getString(AppConstant.Branch, null))
                        .append(AppConstant.BATCH, prefs.getLong(AppConstant.BATCH, 0))
                )
                .append(AppConstant.RollNo, prefs.getString(AppConstant.RollNo, null))
                .append(AppConstant.Contact, prefs.getString(AppConstant.Contact, null))
                .append(AppConstant.Email, prefs.getString(AppConstant.Email, null))
                .append(AppConstant.TIME, new Document()
                        .append(AppConstant.ChECKIN, date)
                        .append(AppConstant.ChECKOUT, "")
                );

        final Task<RemoteInsertOneResult> insertTask = coll.insertOne(newItem);

        insertTask.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                if (task.isSuccessful()) {
                    editor.putString(AppConstant.ChECKINOUT_UNIQUEID, random_id);
                    editor.apply();
                    Log.e(TAG, String.format("successfully inserted item with id %s", task.getResult().getInsertedId()));

                } else {
                    Log.e("app", "failed to insert document with: ", task.getException());
                    uiUpdate();

                }
                uiUpdate();
                dialog.dismissWithAnimation();
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_firebase_image_upload);
                ImageView imageviewloadurl = dialog.findViewById(R.id.imageviewloadurl);
                ProgressBar progressBar = dialog.findViewById(R.id.homeprogress);

                //TextView textViewincharge = dialog.findViewById(R.id.showthismsg);
                TextView checkedin = dialog.findViewById(R.id.successfullychedkid);
                TextView timings = dialog.findViewById(R.id.timings);

                timings.setText(timeDate());

                Picasso.with(context)
                        .load(prefs.getString(AppConstant.IMAGEURL,null))
                        .fit()
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .centerCrop()
                        .into(imageviewloadurl, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                                //textViewincharge.setVisibility(View.VISIBLE);
                                checkedin.setVisibility(View.VISIBLE);
                                timings.setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void onError() {
                                Toast.makeText(context, "No profile photo", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                //textViewincharge.setVisibility(View.VISIBLE);
                                checkedin.setVisibility(View.VISIBLE);
                                timings.setVisibility(View.VISIBLE);
                            }});

                Button accept = dialog.findViewById(R.id.pressedbutton);
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();


            }
        });
    }

    public String timeDate() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minutes = c.get(Calendar.MINUTE);
        int a = c.get(Calendar.AM_PM);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String AM_PM;
        if (a == Calendar.AM) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        @SuppressLint("DefaultLocale") String date = String.format("%02d", day) + "/" + String.format("%02d", month+1) + "/" + year;
        @SuppressLint("DefaultLocale") String time = hour + ":" + String.format("%02d", minutes) + " " + AM_PM;
        return (date +"  "+ time);
    }

    private void uiUpdate() {
        Log.e(TAG, " UI-Update called ");
        tvqrcodename = ((Activity) context).findViewById(R.id.scanqrcode);
        qrcodeiv = ((Activity) context).findViewById(R.id.qrcodeiv);
        if (prefs.contains(AppConstant.ChECKINOUT_UNIQUEID)) {
            tvqrcodename.setText(AppConstant.CHECKOUT_MESSAGE);
            qrcodeiv.setImageResource(R.drawable.ic_qr_code_checkout);
        } else {
            tvqrcodename.setText(AppConstant.CHECKIN_MESSAGE);
            qrcodeiv.setImageResource(R.drawable.ic_qr_code_checkin);
        }
    }

    public void CheckOut(String checkinId) {

        Calendar c = Calendar.getInstance();
        Date dateee = c.getTime();

        Document filterDoc = new Document().append(AppConstant.UNIQUE_ID, checkinId);
        Document updateing = new Document().append("$set", new Document().append(AppConstant.TIME_CHECKOUT, dateee));

        final Task<RemoteUpdateResult> updateTask = coll.updateOne(filterDoc, updateing);
        updateTask.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.e("app", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove(AppConstant.ChECKINOUT_UNIQUEID);
                    editor.apply();

                } else {
                    Log.e("app", "failed to update document with: ", task.getException());

                }
                uiUpdate();
                dialog.getProgressHelper().setRimColor(R.color.custom1PrimaryDarkcheckin);
                dialog.setTitleText("Checked-Out")
                        .showContentText(false)
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            }
        });
    }


}

