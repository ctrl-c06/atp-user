package com.atpuser;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atpuser.Helpers.PinGenerator;
import com.atpuser.Helpers.SharedPref;

public class RegisterStep2Activity extends AppCompatActivity {

    private static final String CODE = "123456";
    LinearLayout codeLayout;
    EditText code1, code2, code3, code4, code5, code6;
    TextView resentOtp, userPhoneNumber;
    public static final String GATEWAY_NUMBER = "+639431364951";
    String otpCode  = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);
        SharedPref.setSharedPreferenceInt(this, "REGISTER_STAGE", 2);


        Bundle extra = getIntent().getExtras();

        Button btnClearCode = findViewById(R.id.btnClearCode);
        resentOtp = findViewById(R.id.resendOTP);

        userPhoneNumber = findViewById(R.id.userPhoneNumber);

        Intent intent = getIntent();

        if (intent.hasExtra("PHONE_NUMBER")) {
            userPhoneNumber.setText(extra.getString("PHONE_NUMBER"));
        } else {
            userPhoneNumber.setText(SharedPref.getSharedPreferenceString(this, "USER_PHONE_NUMBER", ""));
        }

        this.requestAcceptanceOfCode();

        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);

        // By default focus must be in the first index of the sequence.
        findViewById(R.id.code1).requestFocus();
        findViewById(R.id.code1).callOnClick();


        codeLayout = findViewById(R.id.codeLayout);
        int childCount = codeLayout.getChildCount();

        for(int i = 0; i<childCount; i++) {
            if(codeLayout.getChildAt(i) instanceof EditText) {
                EditText e = (EditText) codeLayout.getChildAt(i);
                e.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(e.getText().length() != 0) {
                            // Last EditText.
                            if(!e.getTag().equals("5")) {
                                int nextEditEditTextIndex = Integer.parseInt(String.valueOf(e.getTag()));
                                codeLayout.getChildAt(++nextEditEditTextIndex).requestFocus();
                            }
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(codeChecker(CODE)) {
                            gotoRegistrationStep3();
                        }
                    }
                });
            }
        }

        btnClearCode.setOnClickListener(v -> {
            for(int i = 0; i<childCount; i++) {
                if (codeLayout.getChildAt(i) instanceof EditText) {
                    ((EditText) codeLayout.getChildAt(i)).setText("");
                }
            }
            findViewById(R.id.code1).requestFocus();
        });


        resentOtp.setOnClickListener(v -> {
            this.requestAcceptanceOfCode();
        });
        
    }

    private void requestAcceptanceOfCode() {
        otpCode = PinGenerator.generate();
        PendingIntent sentPI = PendingIntent.getBroadcast(this,
                0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
        SmsManager.getDefault().sendTextMessage(GATEWAY_NUMBER, null, "REQUEST_" + userPhoneNumber.getText().toString(), sentPI, deliveredPI);
    }


    private void gotoRegistrationStep3() {
        Intent mainActivity = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
        startActivity(mainActivity);
    }

    private String replaceOtherParts(String phone) {
        char[] chars = phone.toCharArray();
        int no_of_replace = 6;
        for(int i = chars.length - 1; i>(chars.length - no_of_replace); i--) {
            chars[i] = '*';
        }
        return String.valueOf(chars);
    }

    private boolean codeChecker(String SEND_CODE)
    {
        int valueCount = 0;

        for(int i = 0; i<codeLayout.getChildCount(); i++) {
            if (codeLayout.getChildAt(i) instanceof EditText) {
                EditText e = (EditText) codeLayout.getChildAt(i);
                if(!e.getText().toString().isEmpty()) {
                    valueCount++;
                }
            }
        }

        if(codeLayout.getChildCount() == valueCount) {
            String inputCode = code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString();
            return SEND_CODE.equals(inputCode);
        }

        return false;
    }
}