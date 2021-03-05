package com.atpuser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atpuser.Database.DB;
import com.atpuser.Database.Models.Barangay;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1999;
    ImageView user_image;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        int userId = Integer.parseInt(String.valueOf(SharedPref.getSharedPreferenceInt(this, "USER_LOGGED_IN", 0)));
        user = DB.getInstance(this).userDao().find(userId);


        user_image = findViewById(R.id.user_image);

        TextView personID = findViewById(R.id.personID);
        EditText firstname = findViewById(R.id.firstName);
        EditText middlename = findViewById(R.id.middleName);
        EditText lastname = findViewById(R.id.lastName);
        EditText suffix = findViewById(R.id.suffix);
        EditText birthDateText = findViewById(R.id.birthDateText);
        EditText phoneNumber = findViewById(R.id.phone_number);
        EditText landlineNumber = findViewById(R.id.landline_number);
        EditText email = findViewById(R.id.email_address);
        EditText purok = findViewById(R.id.purok);
        EditText street = findViewById(R.id.street);
        EditText gender = findViewById(R.id.gender);
        EditText province = findViewById(R.id.province);
        EditText municipality = findViewById(R.id.municipality);
        EditText barangay = findViewById(R.id.barangay);

        EditText civil_status = findViewById(R.id.civil_status);
        personID.setText(String.format("%s", user.getPerson_second_id()));
        lastname.setText(user.getLastname());
        firstname.setText(user.getFirstname());
        middlename.setText(user.getMiddlename());
        suffix.setText(user.getSuffix());
        birthDateText.setText(user.getDate_of_birth());
        phoneNumber.setText(user.getPhone_number());
        landlineNumber.setText(user.getLandline_number());
        email.setText(user.getEmail());
        purok.setText(user.getPurok());
        street.setText(user.getStreet());
        gender.setText(user.getGender());
        civil_status.setText(user.getCivil_status());
        province.setText(user.getProvince());
        municipality.setText(user.getMunicipality());
        barangay.setText(user.getBarangay());


        if(user.getImage() != null) {
            Uri imageUri = Uri.parse(user.getImage());
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.user_image)
                    .into(user_image);
        } else {
            Glide.with(this)
                    .load(R.drawable.user_image)
                    .into(user_image);
        }

        user_image.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            user_image.setImageBitmap(photo);
            user.setImage(tempUri.toString());
            DB.getInstance(getApplicationContext()).userDao().update(user);
        }

    }

    @Override
    public void onBackPressed() {
        Intent dashboardActivity = new Intent(ProfileActivity.this, DashboardActivity.class);
        dashboardActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(dashboardActivity);
    }
}