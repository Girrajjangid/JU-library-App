package com.library.libraryproject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import static android.support.constraint.Constraints.TAG;

public class FlashLight {


    public static void flashLightOn(boolean flashlightStatus, Activity activity) {
        CameraManager mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId = mCameraManager.getCameraIdList()[0];
                mCameraManager.setTorchMode(cameraId, true);
                flashlightStatus = true;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void flashLightOff(boolean flashlightStatus, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, false);
            } catch (CameraAccessException e) {
                Log.e(TAG, "flashLightOff: ", e.getCause());
            }
        }
    }
}
