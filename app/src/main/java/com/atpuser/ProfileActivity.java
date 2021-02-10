package com.atpuser;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        int userId = Integer.parseInt(String.valueOf(SharedPref.getSharedPreferenceLong(this, "USER_ID", 0)));
        User user = DB.getInstance(this).userDao().find(userId);

        ImageView user_image = findViewById(R.id.user_image);
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

        personID.setText(user.getPerson_second_id());
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

        final Uri imageUri = Uri.parse(user.getImage());
        final InputStream imageStream;
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap userProfile = BitmapFactory.decodeStream(imageStream);
            user_image.setImageBitmap(userProfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}