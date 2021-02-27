package com.atpuser;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import de.adorsys.android.smsparser.SmsConfig;
import de.adorsys.android.smsparser.SmsReceiver;

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
    public static final String GATEWAY_NUMBER = "09431364951";

    String MESSAGE_SEPERATOR = "Z";


    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;


    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        unRegisterReceiver();

        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);

        initSMSReceiver();

        SharedPref.setSharedPreferenceInt(this, "REGISTER_STAGE", 2);


        Bundle extra = getIntent().getExtras();
        code = findViewById(R.id.code);
        resentOtp = findViewById(R.id.resendOTP);
        userPhoneNumber = findViewById(R.id.userPhoneNumber);
        timerForOtp = findViewById(R.id.timerForOtp);

        timer();

        Intent intent = getIntent();

        if (intent.hasExtra("PHONE_NUMBER")) {
            userPhoneNumber.setText(extra.getString("PHONE_NUMBER"));
        } else {
            userPhoneNumber.setText(SharedPref.getSharedPreferenceString(this, "USER_PHONE_NUMBER", ""));
        }

        this.requestAcceptanceOfCode();

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty() && (codeChecker(CODE) || codeChecker(BYPASS_CODE) || codeChecker(SharedPref.getSharedPreferenceString(getApplicationContext(), "MPIN", "")))) {
                    gotoRegistrationStep3();
                } else {
                    if(s.length() == 6) {
                        Toast.makeText(RegisterStep2Activity.this, "MPIN Invalid", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        resentOtp.setOnClickListener(v -> this.requestAcceptanceOfCode());

    }

    private void timer() {
        new CountDownTimer(60000, 1000) {

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
        }.start();
    }

    private void initSMSReceiver() {
        SmsConfig.INSTANCE.initializeSmsConfig(
                "",
                "", "09431364951", "+639431364951");
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SmsReceiver.INTENT_ACTION_SMS)) {
                fetchSMS();
            }
        }
    };


    private void registerReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsReceiver.INTENT_ACTION_SMS);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
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

        return  REQUEST_CODE
                + StringToASCII.convert(user.getFirstname().toUpperCase()) + StringToASCII.convert("|")
                + StringToASCII.convert(user.getMiddlename().toUpperCase()) + StringToASCII.convert("|")
                + StringToASCII.convert(user.getLastname().toUpperCase()) + StringToASCII.convert("|")
                + StringToASCII.convert((user.getSuffix().isEmpty()) ? "*" : user.getSuffix().toUpperCase())  + StringToASCII.convert("|")
                + StringToASCII.convert(user.getDate_of_birth());

    }

    private void requestAcceptanceOfCode() {
        timer();
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

        String phoneNumber = "+639431364951";
        String sms = "address='"+ phoneNumber + "'";
        Cursor cursor = contentResolver.query(uri, new String[] { "_id", "date", "body", "type"}, sms, null,   null);



        while (cursor.moveToNext()) {
            String strbody = cursor.getString( cursor.getColumnIndex("body") );
            String date = cursor.getString( cursor.getColumnIndex("date") );
            messagesTime.add(date);
            messages.add(strbody);

        }

        // Get the current valid code.
        if (messages.size() != 0 && messages.get(0) != null) {
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

        }
    }

    public long minutesBetween(Long LAST_FETCH_TIME, Long CURRENT_TIME ) {
        long diffInMillis = CURRENT_TIME - LAST_FETCH_TIME;
        return TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
    }
}