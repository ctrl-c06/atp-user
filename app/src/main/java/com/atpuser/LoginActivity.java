package com.atpuser;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.atpuser.ContractModels.User.Login.UserLogin;
import com.atpuser.ContractModels.User.Login.UserLoginRequest;
import com.atpuser.ContractModels.User.Login.UserLoginResponse;
import com.atpuser.Contracts.IUser;
import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
import com.atpuser.Service.RetrofitService;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    Button btnRegister;
    Button btnLogin;
    CheckBox loggedInWithWebAccount;


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

        EditText phoneNumberOrUsername = findViewById(R.id.phoneOrUsername);
        EditText userMPIN = findViewById(R.id.password);

        loggedInWithWebAccount = findViewById(R.id.loggedInWithWebAccount);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin  = findViewById(R.id.btnLogin);


        if(!SharedPref.getSharedPreferenceString(this, "USER_PHONE_NUMBER", "").isEmpty()) {
            phoneNumberOrUsername.setText(SharedPref.getSharedPreferenceString(this, "USER_PHONE_NUMBER", ""));
        }

        loggedInWithWebAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                AlertDialog.Builder noteDialog = new AlertDialog.Builder(LoginActivity.this);
                noteDialog.setTitle("Important Message");
                noteDialog.setMessage("Enabling this means you want to login using your registered account in website make sure you have strong data.");
                noteDialog.setPositiveButton("YES I HAVE", (dialog, which) -> dialog.dismiss());

                noteDialog.setNegativeButton("NO, I DON'T", (dialog, which) -> {
                    buttonView.setChecked(false);
                    dialog.dismiss();
                });
                noteDialog.show();
            }
        });



        btnLogin.setOnClickListener(v -> {
            phoneNumberOrUsername.setText("tooshort01");
            userMPIN.setText("1234");

            AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            mAwesomeValidation.addValidation(this, R.id.phoneOrUsername, input -> !phoneNumberOrUsername.getText().toString().isEmpty(), R.string.login_validation_error);
            mAwesomeValidation.addValidation(this, R.id.password, "[0-9]+", R.string.login_validation_error);
            if(mAwesomeValidation.validate()) {
                if(loggedInWithWebAccount.isChecked()) {

                    ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();

                    boolean userUsePhoneNumber = false;

                    char[] arrayPhoneNumber = phoneNumberOrUsername.getText().toString().toCharArray();

                    // Check first if the user want to use it's username or phone number.
                    if(String.valueOf(arrayPhoneNumber[0]).equals("+") && String.valueOf(arrayPhoneNumber[1]).equals("6")
                            && String.valueOf(arrayPhoneNumber[2]).equals("3")
                            && String.valueOf(arrayPhoneNumber[3]).equals("9")) {
                        userUsePhoneNumber = true;

                    } else if(String.valueOf(arrayPhoneNumber[0]).equals("0") && String.valueOf(arrayPhoneNumber[1]).equals("9")) {
                        userUsePhoneNumber = true;
                    }

                    Retrofit retrofit = RetrofitService.RetrofitInstance(this);
                    IUser service = retrofit.create(IUser.class);
                    UserLoginRequest userLoginRequest = new UserLoginRequest();

                    if(userUsePhoneNumber) {
                        userLoginRequest.setPhone_number(phoneNumberOrUsername.getText().toString());
                    } else {
                        userLoginRequest.setUsername(phoneNumberOrUsername.getText().toString());
                    }

                    userLoginRequest.setMpin(userMPIN.getText().toString());
                    Call<UserLoginResponse> userLoginResponse = service.login(userLoginRequest);

                    userLoginResponse.enqueue(new Callback<UserLoginResponse>() {
                        @Override
                        public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                            if(response.body().getSuccess()) {
                                progressDialog.dismiss();
                                // Set data locally.
                                UserLogin userInformationLoginResponse = response.body().getUser();
                                setDataForLocallyStorage(userInformationLoginResponse);
                            } else {
                                progressDialog.dismiss();
                                phoneNumberOrUsername.setError("Please check your credentials");
                                userMPIN.setError("Please check your credentials");
                            }
                        }

                        @Override
                        public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    this.loginAccountLocally(phoneNumberOrUsername, userMPIN);
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

    private void setDataForLocallyStorage(UserLogin userInformationLoginResponse) {
        User user = new User();

        user.setLastname(userInformationLoginResponse.getLastname());
        user.setFirstname(userInformationLoginResponse.getFirstname());
        user.setMiddlename(userInformationLoginResponse.getMiddlename());
        user.setSuffix(userInformationLoginResponse.getSuffix() != null ? userInformationLoginResponse.getSuffix().toString() : "");
        user.setAge(userInformationLoginResponse.getAge());
        user.setCivil_status(userInformationLoginResponse.getCivilStatus());
        user.setPhone_number(userInformationLoginResponse.getPhoneNumber());
        user.setEmail(userInformationLoginResponse.getEmail() != null ? userInformationLoginResponse.getEmail().toString() : "");
        user.setProvince(userInformationLoginResponse.getProvince());
        user.setMunicipality(userInformationLoginResponse.getCity());
        user.setBarangay(userInformationLoginResponse.getBarangay());
        user.setPurok(userInformationLoginResponse.getAddress());
        user.setStreet(userInformationLoginResponse.getAddress());
        user.setDate_of_birth(userInformationLoginResponse.getDateOfBirth());
        user.setGender(userInformationLoginResponse.getGender());
        user.setLandline_number(userInformationLoginResponse.getLandlineNumber() != null ? userInformationLoginResponse.getLandlineNumber().toString() : "");
        user.setOtp_code(userInformationLoginResponse.getMpin());
        user.setPerson_second_id(userInformationLoginResponse.getPersonId());

        long userId = DB.getInstance(getApplicationContext()).userDao().create(user);

        int toIntUserId = Integer.parseInt(String.valueOf(userId));

        SharedPref.setSharedPreferenceBoolean(getApplicationContext(), "IS_USER_HAS_ACCOUNT", true);
        SharedPref.setSharedPreferenceInt(getApplicationContext(), "USER_LOGGED_IN", toIntUserId);
        redirectToDashboard();
    }

    private void loginAccountLocally(EditText phoneNumber, EditText password) {
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