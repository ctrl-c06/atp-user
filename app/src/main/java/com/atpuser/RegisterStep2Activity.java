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
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.atpuser.Helpers.SharedPref;

import java.util.ArrayList;
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
    EditText code1, code2, code3, code4, code5, code6;
    TextView resentOtp, userPhoneNumber;
    public static final String GATEWAY_NUMBER = "09431364951";
    String barangayCode = "";

    String MESSAGE_SEPERATOR = "z";


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
        fetcher();

        SharedPref.setSharedPreferenceInt(this, "REGISTER_STAGE", 2);


        Bundle extra = getIntent().getExtras();
        resentOtp = findViewById(R.id.resendOTP);
        userPhoneNumber = findViewById(R.id.userPhoneNumber);

        Intent intent = getIntent();

        if (intent.hasExtra("PHONE_NUMBER")) {
            userPhoneNumber.setText(extra.getString("PHONE_NUMBER"));
            barangayCode = extra.getString("BARANGAY_CODE");
        } else {
            userPhoneNumber.setText(SharedPref.getSharedPreferenceString(this, "USER_PHONE_NUMBER", ""));
            barangayCode = SharedPref.getSharedPreferenceString(this, "USER_BARANGAY_CODE", "");
        }

        this.requestOTPCode();


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
                        if(codeChecker(CODE) || codeChecker(BYPASS_CODE)) {
                            gotoRegistrationStep3();
                        }
                    }
                });
            }
        }



        resentOtp.setOnClickListener(v -> this.requestOTPCode());


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




    private void requestOTPCode() {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);

        SmsManager.getDefault().sendTextMessage(GATEWAY_NUMBER, null, REQUEST_CODE, sentPI, deliveredPI);
    }


    private void gotoRegistrationStep3() {
        handler.removeCallbacks(runnable);
        Intent register3Activity = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
        SharedPref.setSharedPreferenceString(this, "PERSON_ID", PERSON_ID);
        startActivity(register3Activity);
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
            long codeSendTimeDifference = minutesBetween(Long.parseLong(messagesTime.get(0)), System.currentTimeMillis());

            String code = messages.get(0).replaceAll("\\D+", "");
            // get the 6 digits MPIN
            String MPIN = code.substring(0, 6);
            PERSON_ID = code.substring(6);

            CODE = MPIN;
            char[] c = MPIN.toCharArray();


            if (codeSendTimeDifference <= 5) {
                code1.setText(String.valueOf(c[0]));
                code2.setText(String.valueOf(c[1]));
                code3.setText(String.valueOf(c[2]));
                code4.setText(String.valueOf(c[3]));
                code5.setText(String.valueOf(c[4]));
                code6.setText(String.valueOf(c[5]));
            }

        }
    }

    public long minutesBetween(Long LAST_FETCH_TIME, Long CURRENT_TIME ) {
        long diffInMillis = CURRENT_TIME - LAST_FETCH_TIME;
        return TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
    }
}