package com.atpuser;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
import com.atpuser.Helpers.StringToASCII;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;

public class DashboardActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    public final static int READ_EXTERNAL_CODE = 1001;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 140;
    public final static int HEIGHT = 140;

//    public final static int TRACK_RECORDS = 0;
    public final static int COVID_STATS = 0;
    public final static int PREVENT_COVID = 1;
    public final static int SIGN_OUT = 2;

    List<String> userOptions = new ArrayList<>(Arrays.asList("COVID-19 stats", "Prevent the spread of COVID-19", "Sign out"));


    AlertDialog.Builder userOptionDialog;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Setting the user account
        setUserAccount();

        if(!checkPermissionForReadExtertalStorage()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int userLoggedId = SharedPref.getSharedPreferenceInt(this, "USER_LOGGED_IN", 0);
        user = DB.getInstance(this).userDao().find(userLoggedId);

        ImageView userImage = findViewById(R.id.user_image);
        ImageView userQr = findViewById(R.id.user_qr);
        TextView userName = findViewById(R.id.userName);

        // User has middlename.
        if(!user.getMiddlename().isEmpty()) {
            userName.setText(String.format("%s %s. %s", user.getFirstname().toUpperCase(), user.getMiddlename().toUpperCase().toCharArray()[0], user.getLastname().toUpperCase()));
        } else {
            userName.setText(String.format("%s %s", user.getFirstname().toUpperCase(), user.getLastname().toUpperCase()));
        }



        final Uri imageUri = Uri.parse(user.getImage());
        final InputStream imageStream;
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap userProfile = BitmapFactory.decodeStream(imageStream);
            userImage.setImageBitmap(userProfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(SharedPref.getSharedPreferenceBoolean(this,"FIRST_VISIT", true)) {
            this.notificationDialog();
            new GuideView.Builder(this)
                    .setTitle("Guide")
                    .setContentText(getString(R.string.guide_text))
                    .setTargetView(userQr)
                    .build()
                    .show();
            SharedPref.setSharedPreferenceBoolean(this,"FIRST_VISIT", false);
        }




        try {
            String STR_QR = user.getLastname() + "|" + user.getFirstname()
                                               + "|" + user.getMiddlename()
                                               + "|" + user.getSuffix()
                                               + "|" + user.getAge()
                                               + "|" + user.getCivil_status()
                                               + "|" + user.getPhone_number()
                                               + "|" + user.getEmail()
                                               + "|" + user.getProvince()
                                               + "|" + user.getMunicipality()
                                               + "|" + user.getBarangay()
                                               + "|" + user.getPurok()
                                               + "|" + user.getStreet()
                                               + "|" + user.getDate_of_birth()
                                               + "|" + user.getLandline_number()
                                               + "|" + user.getGender()
                                               + "|" + "MOBILE" ;

            Bitmap bitmap = encodeAsBitmap(StringToASCII.convert(STR_QR));
            userQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        userQr.setOnClickListener(v -> {
            ArrayAdapter<String> userOptionAdapter = new ArrayAdapter<>(DashboardActivity.this, android.R.layout.simple_spinner_dropdown_item, userOptions);
            userOptionDialog = new AlertDialog.Builder(DashboardActivity.this);
            userOptionDialog.setTitle("Select");
            userOptionDialog.setAdapter(userOptionAdapter, (d, w) -> {
                String selectedOption = userOptionAdapter.getItem(w);
                int select = userOptionAdapter.getPosition(selectedOption);
                if (select == SIGN_OUT) {
                    signOut();
                } else if(select == COVID_STATS) {
                   redirectToStats();
                } else if(select == PREVENT_COVID) {
                    redirectToPinActivity();
                }
            });

            userOptionDialog.show();
        });

    }

    private void redirectToPinActivity() {
        Intent mainActivity = new Intent(DashboardActivity.this, PreventActivity.class);
        startActivity(mainActivity);
    }

    private void redirectToStats() {
        Intent mainActivity = new Intent(DashboardActivity.this, CovidStatsActivity.class);
        startActivity(mainActivity);
    }

    private void setUserAccount() {
        SharedPref.setSharedPreferenceBoolean(this, "IS_USER_HAS_ACCOUNT", true);
    }

    private void signOut() {
        SharedPref.setSharedPreferenceBoolean(getApplicationContext(), "IS_USER_HAS_ACCOUNT", false);
        Intent mainActivity = new Intent(DashboardActivity.this, LoginActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 400, 400, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }


    private void notificationDialog() {
        AlertDialog.Builder notificationDialog = new AlertDialog.Builder(DashboardActivity.this);
        notificationDialog.setTitle("ATP Notification");
        notificationDialog.setCancelable(false);
        notificationDialog.setMessage("Welcome to ATP (Action Trace & Protect) Surigao del Sur COVID-19 Contact Tracing App.\n\nYou may now connect with the ATP through the APP or call us at our\nmobile hotline : 09193693499, \nIf you need any COVID-19 related Assistance.");
        notificationDialog.setNegativeButton("CLOSE", (dialog, which) -> dialog.dismiss());
        notificationDialog.show();
    }


    public boolean checkPermissionForReadExtertalStorage() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        }
        else {
            Toast toast = Toast.makeText(this,"Tap again to exit", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        mBackPressed = System.currentTimeMillis();
    }


}