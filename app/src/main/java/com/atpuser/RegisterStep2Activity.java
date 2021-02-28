package com.atpuser;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
import com.atpuser.Helpers.StringToASCII;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class RegisterStep2Activity extends AppCompatActivity {


    private LocalBroadcastManager localBroadcastManager;

    private static String CODE = "";
    private static String PERSON_ID = "";
    private final static String BYPASS_CODE = "010697";
    private final static String REQUEST_CODE = "<#>";

    LinearLayout codeLayout;
    TextView userPhoneNumber, timerForOtp;
    EditText code;
    Button resentOtp;
    public static final String GATEWAY_NUMBER = "+639630711082";

    String MESSAGE_SEPERATOR = "Z";


    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;

    CountDownTimer timer;

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);


        SharedPref.setSharedPreferenceInt(this, "REGISTER_STAGE", 2);


        Bundle extra = getIntent().getExtras();
        code = findViewById(R.id.code);
        resentOtp = findViewById(R.id.resendOTP);
        userPhoneNumber = findViewById(R.id.userPhoneNumber);
        timerForOtp = findViewById(R.id.timerForOtp);

        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(resentOtp.getVisibility() != View.GONE) {
                    resentOtp.setVisibility(View.GONE);
                }
                timerForOtp.setText("Request another OTP Code : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerForOtp.setText("");
                if(resentOtp.getVisibility() == View.GONE) {
                    resentOtp.setVisibility(View.VISIBLE);
                }
            }
        };

        Intent intent = getIntent();

        if (intent.hasExtra("PHONE_NUMBER")) {
            userPhoneNumber.setText(extra.getString("PHONE_NUMBER"));
        } else {
            userPhoneNumber.setText(SharedPref.getSharedPreferenceString(this, "USER_PHONE_NUMBER", ""));
        }


        this.requestAcceptanceOfCode();

        findViewById(R.id.btnProceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code.getText().length() != 6) {
                    Toast toast = Toast.makeText(RegisterStep2Activity.this,"OTP Code must be 6 characters", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                if(codeChecker(CODE) || codeChecker(SharedPref.getSharedPreferenceString(getApplicationContext(), "MPIN", ""))) {
                    gotoRegistrationStep3();
                } else {
                    Toast toast = Toast.makeText(RegisterStep2Activity.this,"Invalid OTP Code", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });




        resentOtp.setOnClickListener(v -> this.requestAcceptanceOfCode());

        fetcher();

    }







    private void fetcher() {
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, delay);
            fetchSMS();
        }, delay);
    }



    private String buildMessage()
    {
        long userId = SharedPref.getSharedPreferenceLong(this, "USER_ID", 0);
        User user = DB.getInstance(this).userDao().find(userId);
        String barangayCode = SharedPref.getSharedPreferenceString(this, "BARANGAY_CODE", "");

        return REQUEST_CODE +
                barangayCode + MESSAGE_SEPERATOR
                + StringToASCII.convert(user.getFirstname().toUpperCase()) + StringToASCII.convert("|")
                + StringToASCII.convert(user.getMiddlename().toUpperCase()) + StringToASCII.convert("|")
                + StringToASCII.convert(user.getLastname().toUpperCase()) + StringToASCII.convert("|")
                + StringToASCII.convert((user.getSuffix().isEmpty()) ? "*" : user.getSuffix().toUpperCase())  + StringToASCII.convert("|")
                + StringToASCII.convert(user.getDate_of_birth());


    }

    private void requestAcceptanceOfCode() {
        timer.start();
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(buildMessage());
        sms.sendMultipartTextMessage(GATEWAY_NUMBER, null, parts, null, null);
    }


    private void gotoRegistrationStep3() {
        handler.removeCallbacks(runnable);
        Intent register3Activity = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
        SharedPref.setSharedPreferenceString(this, "PERSON_ID", PERSON_ID);
        startActivity(register3Activity);
    }


    private boolean codeChecker(String SEND_CODE)
    {
        String inputCode = code.getText().toString();
        return SEND_CODE.equals(inputCode);
    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }


    public void fetchSMS() {
        ArrayList<String> messages = new ArrayList<>();
        ArrayList<String> messagesTime = new ArrayList<>();
        Uri uri = Uri.parse("content://sms/");

        ContentResolver contentResolver = getContentResolver();

        String phoneNumber = GATEWAY_NUMBER;
        String sms = "address='"+ phoneNumber + "'";
        Cursor cursor = contentResolver.query(uri, new String[] { "_id", "date", "body", "type"}, sms, null,   null);



        while (cursor.moveToNext()) {
            String strbody = cursor.getString( cursor.getColumnIndex("body") );
            String date = cursor.getString( cursor.getColumnIndex("date") );
            messagesTime.add(date);
            messages.add(strbody);

        }

        // Get the current valid code.
        if (messages.size() != 0 && messages.get(0) != null && messages.get(0).contains("Your One-Time-Pin")) {

            findViewById(R.id.verificationLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.validationLayout).setVisibility(View.GONE);

            long codeMinutePassed = minutesBetween(Long.parseLong(messagesTime.get(0)), System.currentTimeMillis());

            String stringCode = messages.get(0).replaceAll("\\D+", "");


            // get the 6 digits MPIN
            String MPIN = stringCode.substring(0, 6);
            SharedPref.setSharedPreferenceString(this, "MPIN", MPIN);
            PERSON_ID = stringCode.substring(6);

            CODE = MPIN;
            char[] c = MPIN.toCharArray();

            if (c.length != 0 && codeMinutePassed <= 5) {
                code.setText(MPIN);
            }
        } else if(messages.size() != 0
                && messages.get(0) != null
                && messages.get(0).contains("Sorry")
                && minutesBetween(Long.parseLong(messagesTime.get(0)), System.currentTimeMillis()) <= 5) {

                timerForOtp.setVisibility(View.GONE);
                TextView waitingMessage = findViewById(R.id.waitingMessage);
                waitingMessage.setText("Sorry but the information that you give is already exists.");
                timer.cancel();

        }
    }

    public long minutesBetween(Long LAST_FETCH_TIME, Long CURRENT_TIME ) {
        long diffInMillis = CURRENT_TIME - LAST_FETCH_TIME;
        return TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
    }
}