package com.library.libraryproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import static android.support.constraint.Constraints.TAG;

public class FlashLight  {


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean flashLightOn(boolean flashlightStatus, Activity activity) {
        CameraManager cameraManager =(CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashlightStatus = true;

        } catch (CameraAccessException e) {
            Log.e(TAG, "flashLightOn: ", e.getCause());
        }
        return flashlightStatus;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public static boolean flashLightOff(boolean flashlightStatus, Activity activity) {
        CameraManager cameraManager =  (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashlightStatus = false;
        } catch (CameraAccessException e) {
        }
        return  flashlightStatus;
    }

    public static boolean flashLightOn2(boolean flashlightStatus, Activity activity) {

        CameraManager cameraManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            String cameraId = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cameraId = cameraManager.getCameraIdList()[0];
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
            }
            flashlightStatus = true;

        } catch (CameraAccessException e) {
            Log.e(TAG, "flashLightOn: ", e.getCause());
        }
        return flashlightStatus;
    }
}
