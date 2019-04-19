
package com.library.libraryproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Utility {
    public static void hideKeyBoardFromView(Activity mActivity) {
        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = mActivity.getCurrentFocus();
        if (view == null) {
            view = new View(mActivity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity mActivity) {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static ProgressDialog initProgress(Object mActivity) {
        ProgressDialog mpDialog = null;
        if (mActivity instanceof Activity) {
            mpDialog = new ProgressDialog((Activity) mActivity);
        } else if (mActivity instanceof Context) {
            mpDialog = new ProgressDialog((Context) mActivity);
        }
        mpDialog.setTitle("Please wait...");
        mpDialog.setMessage("Verifying...");
        mpDialog.setCanceledOnTouchOutside(false);

        return mpDialog;
    }

    public static boolean isConnected(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isAvailable();
    }

    public static void dialogShow(Activity mActivity, final ProgressDialog mpDialog) {
        if (mpDialog != null && !mpDialog.isShowing()) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mpDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void dialogDismiss(Activity mActivity, final ProgressDialog mpDialog) {
        if (mpDialog != null && mpDialog.isShowing()) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mpDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public static void log(String message) {
        if (message != null) {
            Log.e("Phone Auth ", message);
        } else {
            Log.e("Message", "null");
        }
    }


    public static void showToast(final Context ctx, final String message) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static SweetAlertDialog sweetAlertDialogStart(Context context, String message ,String title , int x) {
        SweetAlertDialog dailog = new SweetAlertDialog(context, x);
        try {
            dailog.setCancelable(false);
            dailog.setTitleText(title)
                    .setContentText(message)
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dailog;
    }
    public static void sweetAlertDialogStop(SweetAlertDialog sweetAlertDialog) {

        sweetAlertDialog.dismissWithAnimation();

    }
}
