package com.heshmat.doctoreta.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.LoadingDialog;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.adapters.SpecialityDialog;
import com.heshmat.doctoreta.models.AddressInfo;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.utils.ChoosePhoto;
import com.heshmat.doctoreta.utils.FormattingDate;
import com.heshmat.doctoreta.utils.RequestPermissions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.heshmat.doctoreta.models.StaticFields.DOCTORS;
import static com.heshmat.doctoreta.models.StaticFields.FEMALE;
import static com.heshmat.doctoreta.models.StaticFields.MALE;
import static com.heshmat.doctoreta.models.StaticFields.UNVERIFIED_DOCTORS;
import static com.heshmat.doctoreta.models.StaticFields.USED_PHONES;
import static com.heshmat.doctoreta.utils.FormattingDate.getAge;
import static com.heshmat.doctoreta.utils.RequestPermissions.requestMultiplePermissions;


public class DoctorFormActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    @BindView(R.id.specialityInl)
    TextInputLayout specialityInl;
    Context context;
    @BindView(R.id.doctorNameInL)
    TextInputLayout doctorNameInL;
    @BindView(R.id.doctorEmailInL)
    TextInputLayout doctorEmailInL;
    @BindView(R.id.doctorPhoneInL)
    TextInputLayout doctorPhoneInL;
    @BindView(R.id.doctorAgeInL)
    TextInputLayout doctorAgeInL;
    @BindView(R.id.doctorGenderInL)
    TextInputLayout doctorGenderInL;
    @BindView(R.id.idInl)
    TextInputLayout idInl;
    @BindView(R.id.idEt)
    TextInputEditText idEt;
    @BindView(R.id.licenseInl)
    TextInputLayout licenseInl;
    @BindView(R.id.licenseEt)
    TextInputEditText licenseEt;
    @BindView(R.id.doctorBioInl)
    TextInputLayout doctorBioInl;
    @BindView(R.id.locationInl)
    TextInputLayout locationInl;
    @BindView(R.id.locationEt)
    TextInputEditText locationEt;
    @BindView(R.id.feeInl)
    TextInputLayout feeInl;
    @BindView(R.id.doctor_gender_radio_group)
    RadioGroup doctorGenderRadioGroup;
    @BindView(R.id.profile_imageView)
    CircularImageView image;
    Intent intent;
    Activity activity;
    LoadingDialog loadingDialog;
    DatePickerDialog picker;
    Calendar ageCalender;
    Geocoder geocoder;
    AddressInfo addressInfo;
    long age = -1;
    String gender = "";

    File file;
    Uri muri;
    Uri idUri;
    Uri licenseUri;
    private static int imageRequest = -1;
    private final static int PROFILE_IMG = 0;
    private final static int ID_IMG = 1;
    private final static int LICENSE_IMG = 2;
    private final static int PLACE_PICKER_REQUEST = 97;
    private ArrayList<String> imagesUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getActionBar() != null)
            getActionBar().hide();
        setContentView(R.layout.activity_doctor_form);
        ButterKnife.bind(this);
        context = this;
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBSxO1KBNRzY2ya9KI1-149DiKIuk6z4pc");
        }

        imagesUri = new ArrayList<>(3);
        imagesUri.add("");
        imagesUri.add("");
        imagesUri.add("");

        final View circle1 = findViewById(R.id.circle_one);
        circle1.setBackground(getResources().getDrawable(R.drawable.ic_check_circle_pink));
        final View view = findViewById(R.id.circle_two);
        view.setBackground(getResources().getDrawable(R.drawable.ic_checked_stepper_circle));
        final View line = findViewById(R.id.line_one);
        line.setBackground(getResources().getDrawable(R.drawable.ic_checked_line));
        TextView textView = findViewById(R.id.stepperTv);
        textView.setTextColor(Color.parseColor("#000000"));
        requestMultiplePermissions(this);
        loadingDialog = new LoadingDialog(activity);
        if (Doctor.currentLoggedDoctor != null) {
            doctorNameInL.getEditText().setText(Doctor.currentLoggedDoctor.getName() != null && !Doctor.currentLoggedDoctor.getName().trim().isEmpty() ? Doctor.currentLoggedDoctor.getName() : "");
            doctorEmailInL.getEditText().setText(Doctor.currentLoggedDoctor.getEmail() != null && !Doctor.currentLoggedDoctor.getEmail().trim().isEmpty() ? Doctor.currentLoggedDoctor.getEmail() : "");
            doctorPhoneInL.getEditText().setText(Doctor.currentLoggedDoctor.getPhoneNumber() != null && !Doctor.currentLoggedDoctor.getPhoneNumber().trim().isEmpty() ? Doctor.currentLoggedDoctor.getPhoneNumber() : "");
        }
        ageCalender = Calendar.getInstance();
        doctorAgeInL.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ageCalender.set(year, month, dayOfMonth);
                        doctorAgeInL.getEditText().setText(FormattingDate.formattedDate(ageCalender.getTime()));
                        age = ageCalender.getTimeInMillis();
                        if (age < 21) {
                            doctorAgeInL.setError(getString(R.string.required_field));
                        } else
                            doctorAgeInL.setError(null);


                    }
                }, ageCalender.get(Calendar.YEAR), ageCalender.get(Calendar.MONTH), ageCalender.get(Calendar.DAY_OF_MONTH));
                picker.show();
            }

        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.specialities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialityInl.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpecialityDialog(context, getResources().getStringArray(R.array.specialities), specialityInl).show();

            }
        });


    }

    String selected;

    private boolean validateName() {
        String name = doctorNameInL.getEditText().getText().toString();
        if (name.isEmpty()) {
            doctorNameInL.setError(getString(R.string.required_field));
            return false;
        }
        doctorNameInL.setError(null);
        return true;
    }
    private boolean validateFees() {
        String fee = feeInl.getEditText().getText().toString();
        if (fee.isEmpty()) {
            feeInl.setError(getString(R.string.required_field));
            return false;
        }
        else if (!isaNumber(fee) && Double.parseDouble(fee)<0){
            feeInl.setError(getString(R.string.required_field));
            return false;
        }
        feeInl.setError(null);

        return true;
    }
    private boolean isaNumber(String strNum){
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;

    }

    private boolean validateEmail() {
        String email = doctorEmailInL.getEditText().getText().toString();
        if (email.isEmpty()) {
            doctorEmailInL.setError(getString(R.string.required_field));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            doctorEmailInL.setError(getString(R.string.invalid_email));
            return false;
        }
        doctorEmailInL.setError(null);

        return true;
    }

    private boolean validatePhone() {
        String phone = doctorPhoneInL.getEditText().getText().toString();
        if (phone.isEmpty()) {
            doctorPhoneInL.setError(getString(R.string.required_field));
            return false;
        }
        //0957344563
        else if (phone.length() < 10) {
            doctorNameInL.setError(getString(R.string.short_phone_num));
            return false;
        } else if (phone.length() > 15) {
            doctorNameInL.setError(getString(R.string.long_phone_num));
            return false;
        }
        doctorPhoneInL.setError(null);

        return true;
    }


    private boolean validateGender() {
        int id = doctorGenderRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(R.id.radio_button_female);
        if (id == -1) {
            doctorGenderInL.setError(getString(R.string.required_field));
            radioButton.setError(getString(R.string.required_field));
            return false;
        }
        radioButton.setError(null);
        doctorGenderInL.setError(null);
        return true;
    }

    private boolean validateAge() {
        if (age == -1) {
            doctorAgeInL.setError(getString(R.string.required_field));
            return false;
        } else if (getAge(age) < 22) {
            String a = getString(R.string.under_age, 22);
            doctorAgeInL.setError(getString(R.string.under_age, 22));
            return false;
        }
        doctorAgeInL.setError(null);
        return true;
    }

    private boolean validateProfileImg() {
        if (muri == null) {
            new AlertDialog.Builder(this).setMessage(getString(R.string.profile_img_required)).setPositiveButton(getString(R.string.ok), null)
                    .show();
            return false;
        }
        return true;
    }

    private boolean validateSpeciality() {
        String speciality = specialityInl.getEditText().getText().toString();
        if (speciality.isEmpty()) {
            specialityInl.setError(getString(R.string.required_field));
            return false;
        }
        specialityInl.setError(null);

        return true;
    }

    private boolean validateBio() {
        String bio = doctorBioInl.getEditText().getText().toString();
        if (bio.isEmpty()) {
            doctorBioInl.setError(getString(R.string.required_field));
            return false;
        }
        doctorBioInl.setError(null);

        return true;
    }

    private boolean validateIdImg() {
        String idurl = Objects.requireNonNull(idEt.getText()).toString();
        if (idurl.isEmpty()) {
            idInl.setError(getString(R.string.required_field));
            return false;
        }
        idInl.setError(null);

        return true;
    }

    private boolean validateLicImg() {
        String licurl = Objects.requireNonNull(licenseEt.getText()).toString();
        if (licurl.isEmpty()) {
            licenseInl.setError(getString(R.string.required_field));
            return false;
        }
        licenseInl.setError(null);

        return true;
    }
    private boolean validateAddress() {
        String locationAddress = Objects.requireNonNull(locationEt.getText()).toString();
        if (locationAddress.isEmpty()) {
            locationInl.setError(getString(R.string.required_field));
            return false;
        }
        locationInl.setError(null);

        return true;
    }

    private boolean validation() {
        validateName();
        validateEmail();
        validatePhone();
        validateAge();
        validateGender();
        validateBio();
        validateSpeciality();
        validateIdImg();
        validateLicImg();
        validateFees();
        validateAddress();

        return validateProfileImg() && validateName() && validateEmail() && validatePhone() && validateAge() &&
                validateGender() && validateBio() && validateSpeciality() && validateIdImg() && validateLicImg()&&validateAddress()&& validateFees();
    }

    @OnClick(R.id.licenseEt)
    public void showFullLic(View view) {// show the license in full screen
        Intent intent = new Intent(this, FullScreenActivity.class);
        if (licenseUri != null) {
            intent.putExtra("img", licenseUri.toString());
            startActivity(intent);

        }
    }

    @OnClick(R.id.idEt)
    public void showFullId(View view) { // show the id in full screen
        Intent intent = new Intent(this, FullScreenActivity.class);
        if (idUri != null) {
            intent.putExtra("img", idUri.toString());
            startActivity(intent);

        }
    }

    @OnClick(R.id.profile_imageView)
    public void showFullProfileImg(View view) {
        intent = new Intent(this, FullScreenActivity.class);
        if (muri != null) {
            intent.putExtra("img", muri.toString());
            startActivity(intent);

        }

    }
    Address doctorAddressInLocalLang;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                LatLng latLng = data.getParcelableExtra("latLng");
                 addressInfo = data.getParcelableExtra("addressInfo");

                if (latLng != null) {
                     doctorAddressInLocalLang = getAddress(latLng);
                    if (doctorAddressInLocalLang != null && doctorAddressInLocalLang.getMaxAddressLineIndex() > -1)
                        locationEt.setText(doctorAddressInLocalLang.getAddressLine(0));
                }


            } else {

            }
        }
        if (resultCode == this.RESULT_CANCELED) {
            return;
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    if (imageRequest == PROFILE_IMG) {
                        muri = result.getUri();
                        file = new File(String.valueOf(resultUri));
                        imagesUri.set(PROFILE_IMG, muri.toString());
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        file.getAbsolutePath();
                        Glide.with(this).load(bitmap).into(image);

                    } else if (imageRequest == ID_IMG) {
                        idUri = result.getUri();
                        idEt.setText(idUri.getLastPathSegment());
                        imagesUri.set(ID_IMG, idUri.toString());


                    } else if (imageRequest == LICENSE_IMG) {
                        licenseUri = result.getUri();
                        licenseEt.setText(licenseUri.getLastPathSegment());
                        imagesUri.set(LICENSE_IMG, licenseUri.toString());


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @OnClick(R.id.img_plus)
    public void chooseProfileImg(View view) {
        imageRequest = PROFILE_IMG;
        ChoosePhoto.choosePhoto(this);
    }

    @OnClick(R.id.chooseIDBt)
    public void chooseID(View view) {
        imageRequest = ID_IMG;
        ChoosePhoto.choosePhoto(this);

    }

    @OnClick(R.id.chooseLicenseBt)
    public void chooseLicense(View view) {
        imageRequest = LICENSE_IMG;
        ChoosePhoto.choosePhoto(this);


    }

    @OnClick(R.id.adddoctorInfoBt)
    public void addInfo(View view) {
        if (validation() && Doctor.currentLoggedDoctor != null&& addressInfo!=null) {
            String name = doctorNameInL.getEditText().getText().toString();
            String phone = doctorPhoneInL.getEditText().getText().toString();
            String email = doctorEmailInL.getEditText().getText().toString();
            String bio = doctorBioInl.getEditText().getText().toString();
            String speciality = specialityInl.getEditText().getText().toString();
            double fee=Integer.parseInt(feeInl.getEditText().getText().toString());
            gender = doctorGenderRadioGroup.getCheckedRadioButtonId() == R.id.radio_button_female ? FEMALE : MALE;

            Doctor.currentLoggedDoctor.setName(name);
            Doctor.currentLoggedDoctor.setPhoneNumber(phone);
            Doctor.currentLoggedDoctor.setEmail(email);
            Doctor.currentLoggedDoctor.setBio(bio);
            Doctor.currentLoggedDoctor.setAddressInfo(addressInfo);
            Doctor.currentLoggedDoctor.setPhoneNumber(phone);
            Doctor.currentLoggedDoctor.setBirthDate(age);
            Doctor.currentLoggedDoctor.setGender(gender);
            Doctor.currentLoggedDoctor.setSpeciality(speciality);
            Doctor.currentLoggedDoctor.setIsVerified(false);
            Doctor.currentLoggedDoctor.setStatus(StaticFields.IN_PROGRESS);
            Doctor.currentLoggedDoctor.setPrice(fee);

            checkIfPhoneAlreadyUsed(phone,Doctor.currentLoggedDoctor.getId());




        }





    }
    private void checkIfPhoneAlreadyUsed(String phoneNum, String id) {

        DatabaseInstance.getInstance().collection(USED_PHONES).document(phoneNum).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null && value.exists() && !Objects.equals(value.get(phoneNum, String.class), id)) {
                    new AlertDialog.Builder(context).setMessage(getString(R.string.phone_exist)).setPositiveButton(getString(R.string.ok), null).show();

                } else {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(phoneNum, id);
                    DatabaseInstance.getInstance().collection(USED_PHONES).document(phoneNum).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            for (int i = 0; i < imagesUri.size(); i++) {
                                uploadImages(Doctor.currentLoggedDoctor.getId(), imagesUri, i);
                            }
                        }
                    });
                }
            }
        });
    }
    @OnClick(R.id.chooseLocation)
    public void chooseLocation(View view) {
        if (RequestPermissions.requestLocationPermission(this))
            startActivityForResult(new Intent(this, MapsActivity.class), PLACE_PICKER_REQUEST);


    }

    private void uploadImages(String id, ArrayList<String> images, final int i) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String fileName = "";
        if (i == PROFILE_IMG)
            fileName = id + "ProfileImg";
        else if (i == ID_IMG)
            fileName = id + "idImg";
        else if (i == LICENSE_IMG)
            fileName = id + "licenseImg";
        else
            fileName = id;

        StorageReference storageReference = storageRef.child("doctorDocuments/" + DOCTORS + "/" + id + "/" + fileName);
        UploadTask uploadTask = storageReference.putFile(Uri.parse(images.get(i)));
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (i == PROFILE_IMG)
                            Doctor.currentLoggedDoctor.setPhotoURL(uri.toString());
                        else if (i == ID_IMG)
                            Doctor.currentLoggedDoctor.setIdCardUrl(uri.toString());
                        else if (i == LICENSE_IMG)
                            Doctor.currentLoggedDoctor.setMedicalLicenseUrl(uri.toString());

                        if (i == images.size() - 1) {
                            DatabaseInstance.getInstance().collection(UNVERIFIED_DOCTORS).document(id).set(Doctor.currentLoggedDoctor, SetOptions.merge());
                            loadingDialog.dismissDialog();
                        }

                    }
                });


            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                loadingDialog.dismissDialog();


            }
        });
    }

    Address getAddress(LatLng latLng) throws NullPointerException {
        Address address;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> latadd = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (latadd != null && latadd.size() > 0) {
                address = latadd.get(0);
                String country = "country: " + address.getCountryName();
                String countryCode = "code: " + address.getCountryCode();
                String locality = "locality: " + address.getLocality();
                String adminArea = "adminArea: " + address.getAdminArea();
                String add = "address: " + address.getAddressLine(0);
                String featureADdress = address.getFeatureName();


            } else {
                address = null;
            }
        } catch (IOException e) {
            loadingDialog.dismissDialog();
            e.printStackTrace();
            address = null;

        }
        return address;
    }

}
