package com.atpuser;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import de.adorsys.android.smsparser.SmsConfig;
import de.adorsys.android.smsparser.SmsReceiver;

public class CovidStatsActivity extends AppCompatActivity {
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_stats);

        AlertDialog.Builder notificationDialog = new AlertDialog.Builder(CovidStatsActivity.this);
        notificationDialog.setTitle("ATP Notification");
        notificationDialog.setCancelable(false);
        notificationDialog.setMessage("This page require an internet connection do you want to proceed?");
        notificationDialog.setPositiveButton("PROCEED", (dialog, which) -> {

        });

        notificationDialog.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.dismiss();
            Intent mainActivity = new Intent(CovidStatsActivity.this, DashboardActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainActivity);
        });

        notificationDialog.show();

        SmsConfig.INSTANCE.initializeSmsConfig(
                "BEGIN",
                "END",
                "09431364951");


    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SmsReceiver.INTENT_ACTION_SMS)) {
                String receivedSender = intent.getStringExtra(SmsReceiver.KEY_SMS_SENDER);
                String receivedMessage = intent.getStringExtra(SmsReceiver.KEY_SMS_MESSAGE);
                Toast.makeText(context, "Received message from library", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void registerReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsReceiver.INTENT_ACTION_SMS);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onResume() {
        registerReceiver();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unRegisterReceiver();
        super.onPause();
    }
}