package com.atpuser;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    Button btnRegister;
    Button btnLogin;


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    List<String> test;

    final int READ_SMS_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        test = DB.getInstance(this).provinceDao().all();
        if(SharedPref.getSharedPreferenceBoolean(this, "IS_USER_HAS_ACCOUNT", false)) {
            this.redirectToPin();
        }




        // Check register stage of the user.
        this.checkRegisterStageSession();

        EditText phoneNumber = findViewById(R.id.phoneNumber);
        EditText password = findViewById(R.id.password);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin  = findViewById(R.id.btnLogin);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
//            else {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//            }
        }

        btnLogin.setOnClickListener(v -> {
            AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            mAwesomeValidation.addValidation(this, R.id.phoneNumber, "^(09|\\+639)\\d{9}$", R.string.login_validation_error);
            mAwesomeValidation.addValidation(this, R.id.password, "[0-9]+", R.string.login_validation_error);
            if(mAwesomeValidation.validate()) {
                User user = DB.getInstance(this).userDao().findByPhone(phoneNumber.getText().toString());
                if(user.getOtp_code().equals(password.getText().toString())) {
                    SharedPref.setSharedPreferenceBoolean(this, "IS_USER_HAS_ACCOUNT", true);
                    SharedPref.setSharedPreferenceInt(this, "USER_LOGGED_IN", user.getId());
                    redirectToDashboard();
                } else {
                    phoneNumber.setError("Mobile Number / MPIN is invalid!");
                    password.setError("Mobile Number / MPIN is invalid!");
                }
            }


        });

        btnRegister.setOnClickListener(v -> {
            btnRegister.setEnabled(false);

            if(SharedPref.getSharedPreferenceInt(this, "REGISTER_STAGE", 0) != 0) {
                // Dialog for asking user if he/she want to continue with previous session.
                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(LoginActivity.this);
                confirmationDialog.setTitle("Register");
                confirmationDialog.setMessage("ATP detect that you already have a previous session of registration do you want to continue?");
                confirmationDialog.setPositiveButton("Continue", (dialog, which) -> {
                    this.checkRegisterStageSession();
                });
                confirmationDialog.setNegativeButton("New Account", (dialog, which) -> {
                    Intent mainActivity = new Intent(LoginActivity.this, RegisterStep1Activity.class);
                    startActivity(mainActivity);
                });
                confirmationDialog.show();

            } else {
                Intent mainActivity = new Intent(LoginActivity.this, RegisterStep1Activity.class);
                startActivity(mainActivity);
            }

        });
    }

    private void redirectToDashboard() {
        Intent mainActivity = new Intent(LoginActivity.this, DashboardActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }

    private void redirectToPin() {
        Intent mainActivity = new Intent(LoginActivity.this, PinActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }

    private void checkRegisterStageSession() {
        int registerStage = SharedPref.getSharedPreferenceInt(this, "REGISTER_STAGE", 0);
        if(registerStage == 2) {
            // Redirect to Activity 2
            Intent mainActivity = new Intent(LoginActivity.this, RegisterStep2Activity.class);
            startActivity(mainActivity);
        } else if(registerStage == 3) {
            // Redirect to Activity 3
            Intent mainActivity = new Intent(LoginActivity.this, RegisterStep3Activity.class);
            startActivity(mainActivity);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnRegister.setEnabled(true);
        btnLogin.setEnabled(true);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
            }
        }
    }

}