package com.atpuser;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.atpuser.Contracts.IUserUpdateImage;
import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
import com.atpuser.Helpers.StringToASCII;
import com.atpuser.Service.RetrofitService;
import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;

public class DashboardActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1001;
    private long mBackPressed;

    public final static int READ_EXTERNAL_CODE = 1001;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 140;
    public final static int HEIGHT = 140;

    public final static int PROFILE = 0;
    public final static int COVID_STATS = 1;
    public final static int PREVENT_COVID = 2;
    public final static int SIGN_OUT = 3;

    ProgressDialog progressdialog;

    List<String> userOptions = new ArrayList<>(Arrays.asList("Your Profile" ,"COVID-19 stats", "Prevent the spread of COVID-19", "Sign out"));


    AlertDialog.Builder userOptionDialog;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }



        // Setting the user account
        setUserAccount();


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







        Uri imageUri = Uri.parse(user.getImage());

        Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.user_image)
                .into(userImage);

//        final InputStream imageStream;
//        try {
//
//            imageStream = getContentResolver().openInputStream(imageUri);
//            final Bitmap userProfile = BitmapFactory.decodeStream(imageStream);
//            userImage.setImageBitmap(userProfile);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

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


        String buildAddress = user.getPurok() + " " + user.getStreet() + " " + user.getBarangay() + " " + user.getMunicipality() + " " + user.getProvince();

        try {
            String STR_QR = user.getLastname() + "|" + user.getFirstname()
                                               + "|" + user.getMiddlename()
                                               + "|" + user.getSuffix()
                                               + "|" + user.getAge()
                                               + "|" + user.getCivil_status()
                                               + "|" + user.getPhone_number()
                                               + "|" + user.getEmail()
                                               + "|" + buildAddress
                                               + "|" + user.getDate_of_birth()
                                               + "|" + user.getLandline_number()
                                               + "|" + user.getGender()
                                               + "|" + user.getPerson_second_id()
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
                } else if(select == PROFILE) {
                    redirectToProfile();
                }
            });

            userOptionDialog.show();
        });

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
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private void uploadImage(Uri imageUri) {
        progressdialog = new ProgressDialog(DashboardActivity.this);
        progressdialog.setMessage("Getting ATP (Action Trace & Protect) ready don't close this dialog.");
        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdialog.show();

        Retrofit retrofit = RetrofitService.RetrofitInstance(getApplicationContext());
        IUserUpdateImage service = retrofit.create(IUserUpdateImage.class);

        RequestBody personId = RequestBody.create(MediaType.parse("multipart/form-data"), "166819001-1");
        MultipartBody.Part image = null;

        File file = new File(getRealPathFromURI(imageUri));
        RequestBody requsetFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        image = MultipartBody.Part.createFormData("image", file.getName(), requsetFile);

        Call<ResponseBody> call = service.upload(personId, image);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressdialog.dismiss();
                SharedPref.setSharedPreferenceBoolean(getApplicationContext(), "IS_IMAGE_UPLOAD", true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressdialog.dismiss();
            }
        });
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void redirectToProfile() {
        Intent mainActivity = new Intent(DashboardActivity.this, ProfileActivity.class);
        startActivity(mainActivity);
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
        SharedPref.setSharedPreferenceString(getApplicationContext(), "USER_PHONE_NUMBER", user.getPhone_number());
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