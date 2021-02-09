package com.atpuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atpuser.ContractModels.UserRegisterRequest;
import com.atpuser.ContractModels.UserRegisterResponse;
import com.atpuser.Contracts.IUser;
import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
import com.atpuser.Service.RetrofitService;
import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterStep3Activity extends AppCompatActivity     {


    EditText code;

    boolean isConfirmed = false;
    String firstPinInput = "";
    ProgressDialog progressdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step3);
        SharedPref.setSharedPreferenceInt(this, "REGISTER_STAGE", 3);

        NumberKeyboard numberKeyboard = findViewById(R.id.pinKeyboard);

        numberKeyboard.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int i) {
                code.setText(code.getText().toString().concat(String.valueOf(i)));
            }

            @Override
            public void onLeftAuxButtonClicked() {
            }

            @Override
            public void onRightAuxButtonClicked() {
                erase();
            }
        });

        TextView title = findViewById(R.id.title);
        code = findViewById(R.id.code);

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 4) {

                    if(!isConfirmed) {
                        title.setText("RE-ENTER YOUR MPIN");
                        isConfirmed = true;
                        firstPinInput = code.getText().toString();
                        code.setText("");
                        Toast.makeText(RegisterStep3Activity.this, "Confirmation of MPIN is required", Toast.LENGTH_LONG).show();
                    } else {
                        if(firstPinInput.equals(code.getText().toString())) {
                            // Update the otp pin of user
                            String userPhone = SharedPref.getSharedPreferenceString(getApplicationContext(), "USER_PHONE_NUMBER", "");
                            User user = DB.getInstance(getApplicationContext()).userDao().findByPhone(userPhone);
                            user.setOtp_code(code.getText().toString());
                            DB.getInstance(getApplicationContext()).userDao().update(user);

                            // Clear the stage of the Registration.
                            SharedPref.setSharedPreferenceInt(getApplicationContext(), "REGISTER_STAGE", 0);

                            // Save the user as logged in.
                            SharedPref.setSharedPreferenceInt(getApplicationContext(), "USER_LOGGED_IN", user.getId());

                            // Retrofit Request here..
                            sendToServer();
                        } else {
                            code.setError("MPin not match!");
                            Toast.makeText(RegisterStep3Activity.this, "First and Confirmed MPIN not Match!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });


    }

    private void sendToServer() {
        progressdialog = new ProgressDialog(RegisterStep3Activity.this);
        progressdialog.setMessage("Processing please wait...");
        progressdialog.setCancelable(false);
        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdialog.show();

        Retrofit retrofit = RetrofitService.RetrofitInstance(getApplicationContext());
        IUser service = retrofit.create(IUser.class);

        // Get user details.
        String userId = String.valueOf(SharedPref.getSharedPreferenceLong(this, "USER_ID", 0));
        User user = DB.getInstance(this).userDao().find(Integer.parseInt(userId));
        if(user != null) {
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setFirstname(user.getFirstname());
            userRegisterRequest.setMiddlename(user.getMiddlename());
            userRegisterRequest.setLastname(user.getLastname());
            userRegisterRequest.setSuffix(user.getSuffix());
            userRegisterRequest.setDate_of_birth(user.getDate_of_birth());
            userRegisterRequest.setGender(user.getGender());
            userRegisterRequest.setBarangay(user.getBarangay());
            userRegisterRequest.setCivil_status(user.getCivil_status());
            userRegisterRequest.setPhone_number(user.getPhone_number());
            userRegisterRequest.setLandline_number(user.getLandline_number());
            userRegisterRequest.setEmail(user.getEmail());
            userRegisterRequest.setRegistered_from("MOBILE");



            Call<UserRegisterResponse> userRegisterResponseCall = service.register(userRegisterRequest);
            userRegisterResponseCall.enqueue(new Callback<UserRegisterResponse>() {
                @Override
                public void onResponse(Call<UserRegisterResponse> call, Response<UserRegisterResponse> response) {
                    // Redirect to dashboard.
                    progressdialog.dismiss();
                    user.setPerson_second_id(response.body().getPerson_id());
                    // Update the user person id.
                    DB.getInstance(getApplicationContext()).userDao().update(user);
                    Intent dashboardActivity = new Intent(RegisterStep3Activity.this, DashboardActivity.class);
                    dashboardActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(dashboardActivity);
                }

                @Override
                public void onFailure(Call<UserRegisterResponse> call, Throwable t) {
                    progressdialog.dismiss();
                    Toast.makeText(RegisterStep3Activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressdialog.dismiss();
            Toast.makeText(this, "Oops something went wrong.", Toast.LENGTH_SHORT).show();
        }

    }

    private void erase() {
        if(code.getText().length() != 0) {
            code.setText(
                    code.getText().toString().substring(0, code.getText().length() - 1)
            );
        }
    }


}