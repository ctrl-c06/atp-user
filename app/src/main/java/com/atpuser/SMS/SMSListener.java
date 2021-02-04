package com.atpuser.SMS;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atpuser.DashboardActivity;
import com.atpuser.Helpers.SharedPref;
import com.atpuser.R;
import com.atpuser.RegisterStep1Activity;
import com.atpuser.RegisterStep2Activity;
import com.google.common.base.CharMatcher;

public class SMSListener extends BroadcastReceiver  {
    private static final String TAG = "GATEWAY_APP";
    public static final String pdu_type = "pdus";


    private final int USER_ID_INDEX = 0;
    private final int CHECKER_ID_INDEX  = 1;
    private final int LOCATION_INDEX = 2;
    private final int TEMPERATURE_INDEX = 3;
    private final int PURPOSE_INDEX = 4;
    private final int TIME_INDEX = 5;








    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast.
     *
     * @param context  The Context in which the receiver is running.
     * @param intent   The Intent received.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0);

        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strMessage = "";
        String messageFrom = "";
        String format = bundle.getString("format");
        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            // Check the Android version.
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            // Fill the msgs array.
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                messageFrom += msgs[i].getOriginatingAddress();
                strMessage = msgs[i].getMessageBody();
            }

            Toast.makeText(context, "Execute", Toast.LENGTH_SHORT).show();

            if(messageFrom.equals("+639431364951") || messageFrom.equals("09431364951")) {
                String value = strMessage.replaceAll("[^0-9]", "");
                Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
                SharedPref.setSharedPreferenceString(context, "OTP_CODE", value);
            }

        }
    }



}
