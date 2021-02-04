package com.atpuser;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;

public class PinActivity extends AppCompatActivity {

    NumberKeyboard numberKeyboard;

    EditText mPin;
    boolean isCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        numberKeyboard = findViewById(R.id.pinKeyboard);
        mPin = findViewById(R.id.code);

        numberKeyboard.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int i) {
                mPin.setText(mPin.getText().toString().concat(String.valueOf(i)));
                if(mPin.length() >= 4) {
                    int userId = SharedPref.getSharedPreferenceInt(getApplicationContext(), "USER_LOGGED_IN", 0);
                    User user = DB.getInstance(getApplicationContext()).userDao().find(userId);

                    if(mPin.getText().toString().equals(user.getOtp_code())) {
                        if(!isCorrect) {
                            isCorrect = true;
                            redirectToDashboard();
                        }

                    } else {
                        mPin.setError("Invalid MPIN");
                        mPin.setText("");
                        Toast toast = Toast.makeText(getApplicationContext(), "Ooops! your enter invalid MPIN", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                }
            }

            @Override
            public void onLeftAuxButtonClicked() {

            }

            @Override
            public void onRightAuxButtonClicked() {
                erase();
            }
        });
    }

    private void redirectToDashboard() {
        Intent mainActivity = new Intent(PinActivity.this, DashboardActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }

    private void erase() {
        if(mPin.getText().length() != 0) {
            mPin.setText(
                    mPin.getText().toString().substring(0, mPin.getText().length() - 1)
            );
        }
    }
}