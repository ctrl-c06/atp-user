package com.atpuser;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;

public class RegisterStep3Activity extends AppCompatActivity     {


    EditText code;

    boolean isConfirmed = false;
    String firstPinInput = "";


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
                            SharedPref.setSharedPreferenceInt(getApplicationContext(), "USER_LOGGED_IN", user.getId());


                            // Redirect to dashboard.
                            Intent mainActivity = new Intent(RegisterStep3Activity.this, DashboardActivity.class);
                            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainActivity);
                        } else {
                            code.setError("MPin not match!");
                            Toast.makeText(RegisterStep3Activity.this, "First and Confirmed MPIN not Match!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });


    }

    private void erase() {
        if(code.getText().length() != 0) {
            code.setText(
                    code.getText().toString().substring(0, code.getText().length() - 1)
            );
        }
    }


}