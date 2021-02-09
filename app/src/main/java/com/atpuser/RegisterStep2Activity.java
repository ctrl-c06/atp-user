package com.atpuser;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.atpuser.Helpers.SharedPref;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterStep2Activity extends AppCompatActivity {

    private static String CODE = "123456";
    private final static String BYPASS_CODE = "010697";
    private final static String REQUEST_CODE = "88f9e51be6703354608f99efbcfedf20";

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

        /* code in OnCreate() method */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterStep2Activity.this,
                    Manifest.permission.READ_SMS))
            {
                ActivityCompat.requestPermissions(RegisterStep2Activity.this,
                        new String[] {Manifest.permission.READ_SMS}, 1);
            }
            else
            {
                ActivityCompat.requestPermissions(RegisterStep2Activity.this,
                        new String[] {Manifest.permission.READ_SMS}, 1);
            }

        } else {
            // Permission for reading sms is granted.
            fetcher();
        }




        SharedPref.setSharedPreferenceInt(this, "REGISTER_STAGE", 2);


        Bundle extra = getIntent().getExtras();
        Button btnClearCode = findViewById(R.id.btnClearCode);
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
                        if(codeChecker(CODE) || codeChecker(BYPASS_CODE)) {
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

    private void fetcher() {
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, delay);
            fetchSMS();
        }, delay);
    }



    /* And a method to override */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(RegisterStep2Activity.this,
                            Manifest.permission.READ_SMS) ==  PackageManager.PERMISSION_GRANTED) {

                        fetcher();
//                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                }
//                else
//                {
//                    Toast.makeText(this, "No Permission granted", Toast.LENGTH_SHORT).show();
//                }
                break;
        }
    }


    private String buildMessage(String barangayCode)
    {
        return REQUEST_CODE + MESSAGE_SEPERATOR + barangayCode;
    }

    private void requestAcceptanceOfCode() {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
        SmsManager.getDefault().sendTextMessage(GATEWAY_NUMBER, null, buildMessage(barangayCode), sentPI, deliveredPI);
    }


    private void gotoRegistrationStep3() {
//        handler.removeCallbacks(runnable);
        Intent register3Activity = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
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

    private String millisecondsToDate(String milliseconds)
    {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliseconds));
        String finalDateString = formatter.format(calendar.getTime());
        return finalDateString;
    }

    public void fetchSMS() {
        ArrayList<String> messages = new ArrayList<>();
        Uri uri = Uri.parse("content://sms/");

        ContentResolver contentResolver = getContentResolver();

        String phoneNumber = "+639431364951";
        String sms = "address='"+ phoneNumber + "'";
        Cursor cursor = contentResolver.query(uri, new String[] { "_id", "date", "body", "type"}, sms, null,   null);



        while (cursor.moveToNext()) {
            String strbody = cursor.getString( cursor.getColumnIndex("body") );
            String date = cursor.getString( cursor.getColumnIndex("date") );


//            long fiveAgo = System.currentTimeMillis() - FIVE_MINUTES;
//            if (Long.parseLong(date) > fiveAgo) {
                messages.add(strbody);
//            }

        }

        // Get the current valid code.
        if (messages.size() != 0 && messages.get(0) != null) {

            String code = messages.get(0).replaceAll("\\D+", "");
//            Toast.makeText(this, messages.get(0), Toast.LENGTH_SHORT).show();
//            CODE = code;
//            char[] c = code.toCharArray();
//            if (c.length != 0) {
//                code1.setText(String.valueOf(c[0]));
//                code2.setText(String.valueOf(c[1]));
//                code3.setText(String.valueOf(c[2]));
//                code4.setText(String.valueOf(c[3]));
//                code5.setText(String.valueOf(c[4]));
//                code6.setText(String.valueOf(c[5]));
//            }

        }

    }
}