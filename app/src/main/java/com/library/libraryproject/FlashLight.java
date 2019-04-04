package com.library.libraryproject;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import static android.support.constraint.Constraints.TAG;

public class FlashLight {


    public static boolean flashLightOn(boolean flashlightStatus, Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, true);
                flashlightStatus = true;
            } catch (CameraAccessException e) {
                Log.e(TAG, "flashLightOn: ", e.getCause());
            }
        }
        return flashlightStatus;
    }

    public static boolean flashLightOff(boolean flashlightStatus, Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, false);
                flashlightStatus = false;
            } catch (CameraAccessException e) {
                Log.e(TAG, "flashLightOff: ", e.getCause());
            }
        }
        return flashlightStatus;
    }
}
