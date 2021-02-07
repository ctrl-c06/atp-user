package com.atpuser.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;



public class SMSReceiver extends BroadcastReceiver  {


    private static MessageListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (Object o : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String message = smsMessage.getMessageBody();
            mListener.messageReceived(sender, message);
        }

    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }

}
