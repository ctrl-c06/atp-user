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
import android.widget.Toast;

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

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    Button btnRegister;
    Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if(SharedPref.getSharedPreferenceBoolean(this, "IS_USER_HAS_ACCOUNT", false)) {
            this.redirectToPin();
        }

        this.askForPermissions();

        // Check register stage of the user.
        this.checkRegisterStageSession();

        EditText phoneNumber = findViewById(R.id.phoneNumber);
        EditText password = findViewById(R.id.password);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin  = findViewById(R.id.btnLogin);
        if(!SharedPref.getSharedPreferenceString(this, "USER_PHONE_NUMBER", "").isEmpty()) {
            phoneNumber.setText(SharedPref.getSharedPreferenceString(this, "USER_PHONE_NUMBER", ""));
        }



        btnLogin.setOnClickListener(v -> {
            AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            mAwesomeValidation.addValidation(this, R.id.phoneNumber, "^(09|\\+639)\\d{9}$", R.string.login_validation_error);
            mAwesomeValidation.addValidation(this, R.id.password, "[0-9]+", R.string.login_validation_error);
            if(mAwesomeValidation.validate()) {
                char[] arrayPhoneNumber = phoneNumber.getText().toString().toCharArray();
                // Check if phone number has area code.
                if(String.valueOf(arrayPhoneNumber[0]).equals("+") && String.valueOf(arrayPhoneNumber[1]).equals("6")
                        && String.valueOf(arrayPhoneNumber[2]).equals("3")
                        && String.valueOf(arrayPhoneNumber[3]).equals("9")) {
                    // process
                    phoneNumber.getText().toString().replace("+63", "9");
                    Toast.makeText(this, "With area code", Toast.LENGTH_SHORT).show();
                } else if(String.valueOf(arrayPhoneNumber[0]).equals("0") && String.valueOf(arrayPhoneNumber[1]).equals("9")
                        ) { // No Area code
                    Toast.makeText(this, "No area code", Toast.LENGTH_SHORT).show();
                }
                // Check if number start with +639, 639 or 09

                User user = DB.getInstance(this).userDao().findByPhone(phoneNumber.getText().toString());
                if(user != null && user.getOtp_code().equals(password.getText().toString())) {
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


    @AfterPermissionGranted(123)
    private void askForPermissions() {
        String[] perms = new String[0];
            perms = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "We need permissions", 123, perms);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            askForPermissions();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
        }
    }





}