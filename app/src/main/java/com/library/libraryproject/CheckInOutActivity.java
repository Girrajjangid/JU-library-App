package com.library.libraryproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import java.util.Date;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckInOutActivity extends AppCompatActivity {
    SharedPreferences prefs;
    public static final String preference = "UserData";
    String name;
    private static final String TAG = "CheckInOutActivity";
    PopupMenu popup;
    MenuInflater inflater;
    String barcodeSaved;
    TextView tvname, tvqrcodename;
    ImageView qrcodeiv;
    String barcodereceived;
    ProgressDialog dialog;
    SweetAlertDialog sweetAlertDialog;
    private static final int REQUEST_FOR_ACTIVITY = 4568;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);

        prefs = getSharedPreferences(preference, Context.MODE_PRIVATE);
        barcodeSaved = getResources().getString(R.string.barcodesaved);
        name = prefs.getString("name", "");
        tvname = findViewById(R.id.namenote);
        tvname.setText(name);
    }

    public void showPopup(View v) {
        popup = new PopupMenu(this, v);
        inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logoutmenuitem:
                        if (!prefs.contains(AppConstant.ChECKINOUT_UNIQUEID)) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.clear();
                            editor.putBoolean(AppConstant.MONGODB_INIT, true);
                            editor.apply();
                            Intent intent = new Intent(CheckInOutActivity.this, SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                            Log.e(TAG, "onMenuItemClick: logout working");
                        } else {
                            progressDialogStart("You forget to Check-Out.");
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void checkIn(View view) {
        if(!Utility.isConnected(CheckInOutActivity.this)){
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        } else if (PermissionHelper.allPermissionsGranted(this)) {
            Intent intent = new Intent(CheckInOutActivity.this, CheckInOutBarcodeActivity.class);
            startActivityForResult(intent, REQUEST_FOR_ACTIVITY);
        } else {
            PermissionHelper.getRuntimePermissions(this, CheckInOutActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_FOR_ACTIVITY == requestCode) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                barcodereceived = (Objects.requireNonNull(data.getData())).toString();
                if (barcodereceived.equalsIgnoreCase(barcodeSaved)) {
                    barcodeCorrect();
                } else {
                    barcodeIncorrect();
                }
            }
        }
    }

    private void barcodeIncorrect() {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "You Scanned wrong QR-code ", Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }

    private void barcodeCorrect() {
        prefs = getSharedPreferences(preference, Context.MODE_PRIVATE);
        progressDialogStart2();
        MongoDBHelper mongoDBHelper = new MongoDBHelper(getParent(), this, getResources().getString(R.string.mongoDBappid), getResources().getString(R.string.mongoDBdatabase),
                getResources().getString(R.string.mongoDBcollection), prefs, sweetAlertDialog);

        //mongoDBHelper.initilizing();

        mongoDBHelper.loginWithCredential();

        /*if (!prefs.contains(AppConstant.MONGODB_INIT)) {
            mongoDBHelper.loginWithCredential();
        } else {
            mongoDBHelper.initilizing();
        }*/
    }

    private void progressDialogStart(String message) {
        SweetAlertDialog dialog = new SweetAlertDialog(CheckInOutActivity.this, SweetAlertDialog.WARNING_TYPE);
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

    private void progressDialogStart2() {
        sweetAlertDialog = new SweetAlertDialog(CheckInOutActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(R.color.custom1PrimaryDarkcheckin);
        sweetAlertDialog.setTitleText("Processing...");
        sweetAlertDialog.setContentText("Please wait...");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvqrcodename = findViewById(R.id.scanqrcode);
        qrcodeiv = findViewById(R.id.qrcodeiv);
        if (prefs.contains(AppConstant.ChECKINOUT_UNIQUEID)) {
            tvqrcodename.setText(AppConstant.CHECKOUT_MESSAGE);
            qrcodeiv.setImageResource(R.drawable.ic_qr_code_checkout);
        } else {
            tvqrcodename.setText(AppConstant.CHECKIN_MESSAGE);
            qrcodeiv.setImageResource(R.drawable.ic_qr_code_checkin);
        }
    }
}
