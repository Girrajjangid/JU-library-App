package com.library.libraryproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;



public class BarcodeActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    TextView textView;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    ToggleButton toggleButton;

    private static  int RC_PERMISSION = 0x123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        toggleButton = findViewById(R.id.tv_pmpSwitch);
        surfaceView = findViewById(R.id.camera_surfaceview_outer);
        textView = findViewById(R.id.tv);

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.CODE_128).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1024, 768)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (!PermissionHelper.hasCameraPermission(BarcodeActivity.this)) {
                    PermissionHelper.requestCameraPermission(BarcodeActivity.this, RC_PERMISSION);
                    surfaceView.refreshDrawableState();
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> codes = detections.getDetectedItems();
                if (codes.size() != 0) {
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                    String barcode = String.valueOf(codes.valueAt(0).displayValue);
                    if (barcode.length() != 9) {
                        Toast.makeText(BarcodeActivity.this, "Wrong Barcode", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(barcode));
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            cameraSource.release();
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionHelper.hasCameraPermission(this)) {
            if (!PermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                PermissionHelper.launchPermssionSettings(this);
            } else {
                Toast.makeText(this, "Camera permission need to Scan Barcode", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}

