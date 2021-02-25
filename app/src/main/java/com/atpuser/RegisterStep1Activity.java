package com.atpuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.atpuser.Database.DB;
import com.atpuser.Database.Models.Municipal;
import com.atpuser.Database.Models.Province;
import com.atpuser.Database.Models.User;
import com.atpuser.Fragments.DatePickerFragment;
import com.atpuser.Helpers.SharedPref;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterStep1Activity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE = 1;
    public static final String GATEWAY_NUMBER = "+639630711082";




    AlertDialog.Builder municipalDialog, barangayDialog;

    ImageView user_image;
    Uri userImageLink = null;

    CheckBox termsAndPrivacy;
    Bitmap selectedImage;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step1);


        termsAndPrivacy = findViewById(R.id.termsAndPrivacyCheckbox);

        TextView termsText = findViewById(R.id.termsAndPrivacy);
        termsAndPrivacyDialog();
        termsText.setOnClickListener(v -> termsAndPrivacyDialog());


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
        EditText spinnerProvince = findViewById(R.id.province);
        EditText spinnerMunicipality = findViewById(R.id.municipality);
        EditText spinnerBarangay = findViewById(R.id.barangay);

        user_image = findViewById(R.id.user_image);

        Spinner gender = findViewById(R.id.gender);
        Spinner spinnerCivilStatus = findViewById(R.id.civil_status);
        
        // Change the text color of spinner whenever the user select an item.
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCivilStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        AlertDialog.Builder provinceDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
        provinceDialog.setTitle("Select Province");
        provinceDialog.setCancelable(false);
        
        // Get all list of provinces.
        List<Province> provinces = DB.getInstance(getApplicationContext()).provinceDao().all();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterStep1Activity.this, android.R.layout.simple_spinner_dropdown_item);

        // Load all provinces.
        for(Province province :provinces) {
            arrayAdapter.add(province.getName());
        }

        provinceDialog.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());




        provinceDialog.setAdapter(arrayAdapter, (dialog, which) -> {
            Province province = provinces.get(which);
            spinnerProvince.setText(province.getName());
            spinnerMunicipality.setText("");


            initMunicipalityDialog(spinnerMunicipality, spinnerBarangay, province.getCode());

        });


        birthDateText.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragmentList = fragmentManager.getFragments();

             // Dialog fragment not show.
            if(fragmentList.size() == 0) {
                DialogFragment DateFragment = new DatePickerFragment();
                DateFragment.show(getSupportFragmentManager(), "datePicker");
            }

        });

        spinnerProvince.setOnClickListener((v) -> provinceDialog.show());

        spinnerMunicipality.setOnClickListener(v -> {
            if(municipalDialog != null) {
                municipalDialog.show();
            } else {
                Toast.makeText(this, "Please select province first.", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerBarangay.setOnClickListener(v -> {
            if(barangayDialog != null) {
                barangayDialog.show();
            } else {
                Toast.makeText(this, "Please select municipality first.", Toast.LENGTH_SHORT).show();
            }
        });


        AlertDialog.Builder cameraDialog = new AlertDialog.Builder(RegisterStep1Activity.this);

        List<String> options = new ArrayList<>();
        options.add("Take Photo");
//            options.add("Choose from Gallery");


        final ArrayAdapter<String> cameraAdapter = new ArrayAdapter<>(RegisterStep1Activity.this, android.R.layout.simple_spinner_dropdown_item, options);



        cameraDialog.setAdapter(cameraAdapter, (dialog, which) -> {
            String selectedType = cameraAdapter.getItem(which);
            int position = cameraAdapter.getPosition(selectedType);
            if(position == 0) {
                // Opening the camera.
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
               // Picking image
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    photoPickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                }
                photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                photoPickerIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(photoPickerIntent, "CHOOSE PHOTO"), PICK_IMAGE);
            }
        });


        findViewById(R.id.btnCamera).setOnClickListener(v -> {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

        // When user click the image then take a photo
        user_image.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });




        findViewById(R.id.submit).setOnClickListener(v -> {



            AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            mAwesomeValidation.addValidation(this, R.id.lastName, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.firstName, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.middleName, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.birthDateText, input -> !birthDateText.getText().toString().isEmpty(), R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.phone_number, "^(09|\\+639)\\d{9}$", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.gender, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.civil_status, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.province, input -> !spinnerProvince.getText().toString().isEmpty(), R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.municipality, input -> !spinnerMunicipality.getText().toString().isEmpty(), R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.barangay, input -> !spinnerBarangay.getText().toString().isEmpty(), R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.phone_number, input -> DB.getInstance(getApplicationContext()).userDao().findByPhone(phoneNumber.getText().toString()) == null, R.string.duplicate_phone_number);

            // Check if user already registered
            if(DB.getInstance(getApplicationContext()).userDao().isUserAlreadyExists(
                    firstname.getText().toString().toUpperCase(), middlename.getText().toString().toUpperCase(), lastname.getText().toString().toUpperCase(),birthDateText.getText().toString()
            ) != 0) {
                AlertDialog.Builder accountRegisterDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
                accountRegisterDialog.setMessage("Sorry, you already have account with ATP (Action Trace & Protect)");
                accountRegisterDialog.show();
                return;
            }



            if(!termsAndPrivacy.isChecked()) {
                termsText.setError("Please read this.");
            }

            if(mAwesomeValidation.validate()) {

                if(userImageLink == null) {
                    Toast toast = Toast.makeText(this,"Please attach an image or take photo.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
                confirmationDialog.setTitle("Important Message");
                confirmationDialog.setMessage("Did you properly read the Terms of Use and Privacy Policy?");
                confirmationDialog.setNegativeButton("NO", (dialog, which) -> {
                    dialog.dismiss();
                    termsAndPrivacyDialog();
                });

                confirmationDialog.setPositiveButton("YES", (dialog, which) -> {
                    String[] birthdateString = birthDateText.getText().toString().split("/");
                    int birthYear = Integer.parseInt(birthdateString[2]);
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    int userAge = currentYear - birthYear;

                    if(userAge <= 0) {
                        Toast toast = Toast.makeText(this,"Please check your date of birth", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }

                        User user = new User();
                        user.setLastname(lastname.getText().toString().toUpperCase());
                        user.setFirstname(firstname.getText().toString().toUpperCase());
                        user.setMiddlename(middlename.getText().toString().toUpperCase());
                        user.setSuffix(suffix.getText().toString().toUpperCase());
                        user.setAge(userAge);
                        user.setCivil_status(spinnerCivilStatus.getSelectedItem().toString());
                        user.setPhone_number(phoneNumber.getText().toString());
                        user.setEmail(email.getText().toString());
                        user.setProvince(spinnerProvince.getText().toString());
                        user.setMunicipality(spinnerMunicipality.getText().toString());
                        user.setBarangay(spinnerBarangay.getText().toString());
                        user.setPurok(purok.getText().toString());
                        user.setStreet(street.getText().toString());
                        user.setGender(gender.getSelectedItem().toString());
                        user.setLandline_number(landlineNumber.getText().toString());
                        user.setDate_of_birth(birthDateText.getText().toString());
                        user.setOtp_code("");
                        user.setImage(userImageLink.toString());
                        long userId = DB.getInstance(this).userDao().create(user);

                        this.redirectToStep2(phoneNumber.getText().toString(),  userId);
                });

                confirmationDialog.show();
            }

        });
    }








    private void termsAndPrivacyDialog() {
        AlertDialog.Builder termsAndPolicyDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
        termsAndPolicyDialog.setTitle("Terms of Use and Privacy Policy");
        termsAndPolicyDialog.setMessage("Terms of Use and Privacy Policy\n" +
                "The PROVINCIAL GOVERNMENT OF TANDAG CITY (\"we, us\") is committed to protect and respect your personal data privacy.\n" +
                "\n" +
                "In order to protect the health of our constituents in the threat of the COVID-19 pandemic, we are gathering information to process their entry.\n" +
                "\n"+
                "We are at the forefront of not only implementing but also\n" +
                "complying with RA No. 11332 or the Mandatory and Health Events of Public Health the Data \n" +
                "Privacy Act of 2012 and its Implementing Rules and Regulations (IRR) with respect to\n" +
                "gathering information to help the Contact Tracing of Close Contacts of\n" +
                "COVID-19 Cases.\n" +
                "\n" +
                "We are committed to protecting the heal and well-being\n" +
                "of our constituents. In order to protect the health of local citizens of the province,\n" +
                "we are collecting your information for contact tracing\n" +
                "\n" +
                "Your information might be disclosed to DOH, other agencies, and\n" +
                "authorized persons to provide and effective response during this\n" +
                "COVID-19 pandemic.\n" +
                "\n" +
                "You will be asked to provide basic information including your full name, gender, birth date, address, employment information, place of origin and health/medical history, travel history, and other information deemed necessary for the purpose of contact tracing.\n" +
                "\n" +
                "By entering information asked on the form, you understand and agree to the use of the\n" +
                "Provincial Government of Tandag City to process and disclose your data to other parties within\n" +
                "the bounds of law mentioned above.");

        termsAndPolicyDialog.setPositiveButton("I AGREE", (dialog, which) -> termsAndPrivacy.setChecked(true));
        termsAndPolicyDialog.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        termsAndPolicyDialog.show();

    }

    private void redirectToStep2(String phoneNumber, long userId) {
        Intent step2Activity = new Intent(RegisterStep1Activity.this, RegisterStep2Activity.class);
        step2Activity.putExtra("PHONE_NUMBER", phoneNumber);
        SharedPref.setSharedPreferenceString(this,"USER_PHONE_NUMBER", phoneNumber);
        SharedPref.setSharedPreferenceLong(this,"USER_ID", userId);
        startActivity(step2Activity);
    }

    private void requestCode() {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
        SmsManager.getDefault().sendTextMessage(GATEWAY_NUMBER, null, "REQUEST_CODE", sentPI, deliveredPI);
    }

    // For camera take photo process.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            userImageLink = tempUri;

            user_image.setImageBitmap(photo);
        } else if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                userImageLink = imageUri;

                user_image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    private void initMunicipalityDialog(EditText spinnerMunicipality, EditText spinnerBarangay, String selectedProvince) {
        List<Municipal> municipals =  DB.getInstance(getApplicationContext()).municipalDao().findByProvince(selectedProvince);
        ArrayAdapter<String> municipalAdapter = new ArrayAdapter<>(RegisterStep1Activity.this, android.R.layout.simple_spinner_dropdown_item);

        // Load all municipals.
        for(Municipal municipal : municipals) {
            municipalAdapter.add(municipal.getName());
        }
        municipalAdapter.notifyDataSetChanged();

        municipalDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
        municipalDialog.setTitle("Select Municipal");
        municipalDialog.setCancelable(false);


        municipalDialog.setNegativeButton("CANCEL", (municipalDialog, municipalWhich) -> municipalDialog.dismiss());



        municipalDialog.setAdapter(municipalAdapter, (d, w) -> {
            Municipal municipal = municipals.get(w);
            spinnerMunicipality.setText(municipal.getName());
            spinnerBarangay.setText("");

            this.initBarangayDialog(spinnerBarangay, municipal.getCode());
        });
    }

    private void initBarangayDialog(EditText spinnerBarangay, String selectedMunicipality) {
        ArrayAdapter<String> barangayAdapter = new ArrayAdapter<>(RegisterStep1Activity.this, android.R.layout.simple_spinner_dropdown_item, DB.getInstance(this).barangayDao().getByMunicipal(selectedMunicipality));
        barangayAdapter.notifyDataSetChanged();

        barangayDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
        barangayDialog.setTitle("Select Barangay");
        barangayDialog.setCancelable(false);

        barangayDialog.setNegativeButton("CANCEL", (barangayDialog, barangayWhich) -> barangayDialog.dismiss());

        barangayDialog.setAdapter(barangayAdapter, (barangayD, barangayW) -> {
            String selectedBarangay = barangayAdapter.getItem(barangayW);
            spinnerBarangay.setText(selectedBarangay);
        });
    }


}