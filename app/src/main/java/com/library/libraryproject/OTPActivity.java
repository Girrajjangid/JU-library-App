package com.library.libraryproject;

import android.app.Activity;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.library.libraryproject.Utility;

import java.util.concurrent.TimeUnit;
import java.util.Objects;

public class OTPActivity extends AppCompatActivity {
    boolean otpVerified;
    private Activity mActivity = OTPActivity.this;
    private AppCompatEditText etDigit1;
    private AppCompatEditText etDigit2;
    private AppCompatEditText etDigit3;
    private AppCompatEditText etDigit4;
    private AppCompatEditText etDigit5;
    private AppCompatEditText etDigit6;
    private AppCompatButton btnContinue;
    private AppCompatButton btnResendCode;
    private AppCompatTextView tvToolbarBack;
    private AppCompatTextView tvToolbarTitle;
    private AppCompatTextView tvCountDownTimer;
    private LinearLayout llContinue;
    private RelativeLayout rlResend;
    private ProgressBar pbVerify;
    private String strPhoneCode;
    private String strContact , strName , strCourse , strBranch , strPassword , strEmail , strRollno , strBatch;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private CountDownTimer countDownTimer;
    private DatabaseReference ref;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    SharedPreferences prefs;
    public static final String preference = "UserData";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        setUpUI();
        setUpToolBar();


        if (getIntent().getExtras() != null) {
            strContact = getIntent().getStringExtra(AppConstant.Contact);
            strName = getIntent().getStringExtra(AppConstant.Name);
            strCourse = getIntent().getStringExtra(AppConstant.Course);
            strBranch = getIntent().getStringExtra(AppConstant.Branch);
            strPassword = getIntent().getStringExtra(AppConstant.Password);
            strEmail = getIntent().getStringExtra(AppConstant.Email);
            strRollno = getIntent().getStringExtra(AppConstant.RollNo);
            strBatch = getIntent().getStringExtra(AppConstant.BATCH);

            tvToolbarBack.setText("<  ");
            tvToolbarTitle.setText(strContact);
        }

        mAuth = FirebaseAuth.getInstance();

        //This callback trigger whenever we receive an OTP
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            //This trigger on AutoDetection OTP
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Utility.log("onVerificationCompleted: " + credential);
                signInWithPhoneAuthCredential(credential);
                pbVerify.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Utility.log("onVerificationFailed" + e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }
                pbVerify.setVisibility(View.GONE);
            }

            // We have to enter conde manually
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Utility.log("onCodeSent: " + verificationId);
                Utility.log("token: " + token);
                pbVerify.setVisibility(View.GONE);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        //sending OTP
        startPhoneNumberVerification(strContact);

    }
    private void sucessfullyVerified() {
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("students").child(strRollno).child("password").setValue(strPassword);
        prefs = getSharedPreferences(preference, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", strName);
        editor.putString("email", strEmail);
        editor.putString("rollno", strRollno);
        editor.putString("contact", strContact);
        editor.putString("password", strPassword);
        editor.putString("course", strCourse);
        editor.putString("branch", strBranch);
        editor.putString("batch", strBatch);
        editor.apply();
        Intent intent = new Intent(OTPActivity.this,CheckInOutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Utility.log("signInWithCredential:success");
                            pbVerify.setVisibility(View.GONE);
                            new Handler().postDelayed(() -> {
                                sucessfullyVerified();
                                }, 1000);
                        } else {
                            // Sign in failed, display a message and update the UI
                            pbVerify.setVisibility(View.GONE);
                            Utility.log("signInWithCredential:failure " + task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Utility.showToast(OTPActivity.this, "OTP is incorrect");
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void setUpUI() {
        rlResend = findViewById(R.id.rlResend);
        llContinue = findViewById(R.id.llContinue);
        llContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnContinue.isClickable())
                    btnContinue.performClick();
            }
        });
        pbVerify = findViewById(R.id.pbVerify);
        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(mActivity);
                if (validate()) {
                    if (!TextUtils.isEmpty(mVerificationId)) {
                        verifyPhoneNumberWithCode(mVerificationId,
                                etDigit1.getText().toString().trim() +
                                        etDigit2.getText().toString().trim() +
                                        etDigit3.getText().toString().trim() +
                                        etDigit4.getText().toString().trim() +
                                        etDigit5.getText().toString().trim() +
                                        etDigit6.getText().toString().trim());
                    } else {
                        Utility.showToast(OTPActivity.this, "Press resend");
                    }
                }
            }
        });

        btnResendCode = findViewById(R.id.btnResendCode);
        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(mActivity);
                if (mResendToken != null)
                    resendVerificationCode( strPhoneCode, mResendToken);
                else {

                    Toast.makeText(mActivity, "Try again", Toast.LENGTH_SHORT).show();
                    //Utility.showToast(OTPActivity.this, "Try again later.");
                    //onBackPressed();
                }
            }
        });


        tvToolbarBack = findViewById(R.id.tvToolbarBack);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        tvCountDownTimer = findViewById(R.id.tvCountDownTimer);

        etDigit1 = findViewById(R.id.etDigit1);
        etDigit2 = findViewById(R.id.etDigit2);
        etDigit3 = findViewById(R.id.etDigit3);
        etDigit4 = findViewById(R.id.etDigit4);
        etDigit5 = findViewById(R.id.etDigit5);
        etDigit6 = findViewById(R.id.etDigit6);

        setButtonContinueClickbleOrNot();
        tvToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                finish();
            }
        });

        etDigit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit2.requestFocus();
                }
            }
        });
        etDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit3.requestFocus();
                } else {
                    etDigit1.requestFocus();
                }
            }
        });
        etDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit4.requestFocus();
                } else {
                    etDigit2.requestFocus();
                }
            }
        });
        etDigit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit5.requestFocus();
                } else {
                    etDigit3.requestFocus();
                }
            }
        });
        etDigit5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit6.requestFocus();
                } else {
                    etDigit4.requestFocus();
                }
            }
        });
        etDigit6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                } else {
                    etDigit5.requestFocus();
                }
            }
        });

        etDigit1.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (etDigit1.getText().toString().trim().length() == 0)
                    etDigit1.requestFocus();
            } else {
                if (etDigit1.getText().toString().trim().length() == 1) {
                    etDigit2.requestFocus();
                }
            }
            return false;
        });
        etDigit2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (etDigit2.getText().toString().trim().length() == 0)
                    etDigit1.requestFocus();
            } else {
                if (etDigit2.getText().toString().trim().length() == 1) {
                    etDigit3.requestFocus();
                }
            }
            return false;
        });
        etDigit3.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (etDigit3.getText().toString().trim().length() == 0)
                    etDigit2.requestFocus();
            } else {
                if (etDigit3.getText().toString().trim().length() == 1) {
                    etDigit4.requestFocus();
                }
            }
            return false;
        });
        etDigit4.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (etDigit4.getText().toString().trim().length() == 0)
                    etDigit3.requestFocus();
            } else {
                if (etDigit4.getText().toString().trim().length() == 1) {
                    etDigit5.requestFocus();
                }
            }
            return false;
        });
        etDigit5.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (etDigit5.getText().toString().trim().length() == 0)
                    etDigit4.requestFocus();
            } else {
                if (etDigit5.getText().toString().trim().length() == 1) {
                    etDigit6.requestFocus();
                }
            }
            return false;
        });
        etDigit6.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (etDigit6.getText().toString().trim().length() == 0)
                    etDigit5.requestFocus();
            }
            return false;
        });

    }

    private boolean validate() {
        if (TextUtils.isEmpty(etDigit1.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit2.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit3.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit4.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit5.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit6.getText().toString().trim())) {
            return false;
        }
        return true;
    }

    private void setButtonContinueClickbleOrNot() {
        if (!validate()) {
            llContinue.setAlpha(.5f);
            btnContinue.setClickable(false);
        } else {
            llContinue.setAlpha(1.0f);
            btnContinue.setClickable(true);
        }
    }

    private void setUpToolBar() {
        Toolbar mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
    }


    private void signOut() {
        mAuth.signOut();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        signOut();
    }

    private void startCounter() {
        if (countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountDownTimer.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvCountDownTimer.setText("");
                btnResendCode.setEnabled(true);
                setResendButtonEnableDisable();
            }

        };
        countDownTimer.start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
        startCounter();
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
        startCounter();
        btnResendCode.setEnabled(false);
        setResendButtonEnableDisable();
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pbVerify.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void setResendButtonEnableDisable() {
        if (btnResendCode.isEnabled()) {
            rlResend.setBackgroundResource(R.drawable.border_red_dark);
            btnResendCode.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        } else {
            rlResend.setBackgroundResource(R.drawable.border_red_light);
            btnResendCode.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        }
    }

}

