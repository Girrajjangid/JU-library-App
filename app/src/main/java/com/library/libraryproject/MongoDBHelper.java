package com.library.libraryproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
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

import org.bson.BsonValue;
import org.bson.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Set;

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
    ProgressDialog dialog;
    Activity activity;
    TextView tvname , tvqrcodename;
    ImageView qrcodeiv;

    public static final String preference = AppConstant.PREFERENCENAME;

    public MongoDBHelper(Activity activity,
            Context context, String appid, String database, String collection, SharedPreferences prefs , ProgressDialog dialog) {
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
        //client = Stitch.initializeDefaultAppClient(appid);
        //client = Stitch.initializeDefaultAppClient(appid);
        //client = Stitch.initializeDefaultAppClient(appid);

        //client = Stitch.initializeAppClient(appid);

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
                .append(AppConstant.Name, prefs.getString(AppConstant.Name,null))
                .append(AppConstant.STREAM, new Document()
                        .append(AppConstant.Course, prefs.getString(AppConstant.Course,null))
                        .append(AppConstant.Branch, prefs.getString(AppConstant.Branch,null))
                        .append(AppConstant.BATCH, prefs.getString(AppConstant.BATCH,null))
                )
                .append(AppConstant.RollNo,prefs.getString(AppConstant.RollNo ,null))
                .append(AppConstant.Contact, prefs.getString(AppConstant.Contact , null))
                .append(AppConstant.Email,prefs.getString(AppConstant.Email,null))
                .append(AppConstant.TIME, new Document()
                        .append(AppConstant.ChECKIN, date)
                        .append(AppConstant.ChECKOUT, "")
                );

        final Task<RemoteInsertOneResult> insertTask = coll.insertOne(newItem);

        insertTask.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                if (task.isSuccessful()) {
                    editor.putString(AppConstant.ChECKINOUT_UNIQUEID , random_id);
                    editor.apply();
                    dialog.dismiss();
                    Toast.makeText(context, "Successfully checked In", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, String.format("successfully inserted item with id %s", task.getResult().getInsertedId()));

                } else {
                    Log.e("app", "failed to insert document with: ", task.getException());
                    uiUpdate();
                    dialog.dismiss();
                }
                uiUpdate();

            }
        });
    }
    public void uiUpdate() {
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
                    dialog.dismiss();
                    Toast.makeText(context, "Successfully checked Out", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("app", "failed to update document with: ", task.getException());
                    dialog.dismiss();
                }
                uiUpdate();


            }
        });
    }

    private class MyErrorListener implements ErrorListener {
        @Override
        public void onError(BsonValue documentId, Exception error) {
            Log.e("Stitch", error.getLocalizedMessage());
            Set<BsonValue> docsThatNeedToBeFixed = (Set<BsonValue>) coll.sync().getPausedDocumentIds();
            for (BsonValue doc_id : docsThatNeedToBeFixed) {
                // Add your logic to inform the user.
                // When errors have been resolved, call
                coll.sync().resumeSyncForDocument(doc_id);
            }
            // refresh the app view, etc.
        }
    }

    private class MyUpdateListener implements ChangeEventListener<Document> {
        @Override
        public void onEvent(final BsonValue documentId, final ChangeEvent<Document> event) {
            if (!event.hasUncommittedWrites()) {
                // Custom actions can go here
            }
            // refresh the app view, etc.
        }
    }
}

