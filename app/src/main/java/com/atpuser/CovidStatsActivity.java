package com.atpuser;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CovidStatsActivity extends AppCompatActivity {

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
    }
}