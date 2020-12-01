package com.heshmat.doctoreta.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.heshmat.doctoreta.models.Patient;
import com.heshmat.doctoreta.models.User;
import com.heshmat.doctoreta.utils.ChoosePhoto;
import com.heshmat.doctoreta.utils.FormattingDate;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.heshmat.doctoreta.models.StaticFields.CLIENTS;
import static com.heshmat.doctoreta.models.StaticFields.FEMALE;
import static com.heshmat.doctoreta.models.StaticFields.MALE;
import static com.heshmat.doctoreta.models.StaticFields.USED_PHONES;
import static com.heshmat.doctoreta.utils.FormattingDate.getAge;

public class PatientFormActivity extends AppCompatActivity {
    @BindView(R.id.patientNameInL)
    TextInputLayout patientNameInL;
    @BindView(R.id.patientEmailInL)
    TextInputLayout patientEmailInL;
    @BindView(R.id.patientPhoneInL)
    TextInputLayout patientPhoneInL;
    @BindView(R.id.patientAgeInL)
    TextInputLayout patientAgeInL;
    @BindView(R.id.patientGenderInL)
    TextInputLayout patientGenderInL;
    @BindView(R.id.patient_gender_radio_group)
    RadioGroup patientGenderRadioGroup;
    @BindView(R.id.profile_imageView)
    CircularImageView image;
    Intent intent;
    Context context;
    Activity activity;
    DatePickerDialog picker;
    Calendar ageCalender;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_form);
        ButterKnife.bind(this);
        context = this;
        activity = this;
        final View circle1 = findViewById(R.id.circle_one);
        circle1.setBackground(getResources().getDrawable(R.drawable.ic_check_circle_pink));
        final View view = findViewById(R.id.circle_two);
        view.setBackground(getResources().getDrawable(R.drawable.ic_checked_stepper_circle));
        final View line = findViewById(R.id.line_one);
        line.setBackground(getResources().getDrawable(R.drawable.ic_checked_line));
        TextView textView = findViewById(R.id.stepperTv);
        textView.setTextColor(Color.parseColor("#000000"));
        requestMultiplePermissions();

        loadingDialog = new LoadingDialog(activity);

        if (User.currentLoggedUser != null) {
            patientNameInL.getEditText().setText(User.currentLoggedUser.getName() != null && !User.currentLoggedUser.getName().trim().isEmpty() ? User.currentLoggedUser.getName() : "");
            patientEmailInL.getEditText().setText(User.currentLoggedUser.getEmail() != null && !User.currentLoggedUser.getEmail().trim().isEmpty() ? User.currentLoggedUser.getEmail() : "");
            patientPhoneInL.getEditText().setText(User.currentLoggedUser.getPhoneNumber() != null && !User.currentLoggedUser.getPhoneNumber().trim().isEmpty() ? User.currentLoggedUser.getPhoneNumber() : "");
        }
        ageCalender = Calendar.getInstance();
        patientAgeInL.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ageCalender.set(year, month, dayOfMonth);
                        patientAgeInL.getEditText().setText(FormattingDate.formattedDate(ageCalender.getTime()));
                        age = ageCalender.getTimeInMillis();
                        if (age < 16) {
                            patientAgeInL.setError(getString(R.string.required_field));
                        } else
                            patientAgeInL.setError(null);


                    }
                }, ageCalender.get(Calendar.YEAR), ageCalender.get(Calendar.MONTH), ageCalender.get(Calendar.DAY_OF_MONTH));
                picker.show();
            }

        });


    }

    private boolean validateName() {
        String name = patientNameInL.getEditText().getText().toString();
        if (name.isEmpty()) {
            patientNameInL.setError(getString(R.string.required_field));
            return false;
        }
        patientNameInL.setError(null);
        return true;
    }

    private boolean validateEmail() {
        String email = patientEmailInL.getEditText().getText().toString();
        if (email.isEmpty()) {
            patientEmailInL.setError(getString(R.string.required_field));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            patientEmailInL.setError(getString(R.string.invalid_email));
            return false;
        }
        patientEmailInL.setError(null);

        return true;
    }

    private boolean validatePhone() {
        String phone = patientPhoneInL.getEditText().getText().toString();
        if (phone.isEmpty()) {
            patientPhoneInL.setError(getString(R.string.required_field));
            return false;
        }
        //0957344563
        else if (phone.length() < 10) {
            patientNameInL.setError(getString(R.string.short_phone_num));
            return false;
        } else if (phone.length() > 15) {
            patientNameInL.setError(getString(R.string.long_phone_num));
            return false;
        }
        patientPhoneInL.setError(null);

        return true;
    }

    private boolean validateGender() {
        int id = patientGenderRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(R.id.radio_button_female);
        if (id == -1) {
            patientGenderInL.setError(getString(R.string.required_field));
            radioButton.setError(getString(R.string.required_field));
            return false;
        }
        radioButton.setError(null);
        patientGenderInL.setError(null);
        return true;
    }

    private boolean validateAge() {
        if (age == -1) {
            patientAgeInL.setError(getString(R.string.required_field));
            return false;
        } else if (getAge(age) < 16) {
            patientAgeInL.setError(getString(R.string.under_age,16));
            return false;
        }
        patientAgeInL.setError(null);
        return true;
    }

    private boolean validation() {
        validateEmail();
        validateGender();
        validateAge();
        validatePhone();
        return validateName() && validateEmail() && validateGender() && validatePhone() && validateAge();
    }

    String phone = "";
    String name = "";
    String email = "";
    String gender = "";
    long age = -1;
    File file;
    Uri muri;


    @OnClick(R.id.addPatientInfoBt)
    public void addPatientDetails(View view) {
        if (validation()) {
            name = patientNameInL.getEditText().getText().toString();
            email = patientEmailInL.getEditText().getText().toString();
            phone = patientPhoneInL.getEditText().getText().toString();
            name = patientNameInL.getEditText().getText().toString();
            gender = patientGenderRadioGroup.getCheckedRadioButtonId() == R.id.radio_button_female ? FEMALE : MALE;
            if (User.currentLoggedUser.getId() != null)
                checkIfPhoneAlreadyUsed(phone, User.currentLoggedUser.getId());

        }
    }

    @OnClick(R.id.img_plus)
    public void showPictureDialog(View view) {

        ChoosePhoto.choosePhoto(this);
    }

    @OnClick(R.id.profile_imageView)
    public void showFull(View view) {
        if (intent != null)
            startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                muri = result.getUri();
                try {
                    file = new File(String.valueOf(resultUri));
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    file.getAbsolutePath();
                    Glide.with(this).load(bitmap).into(image);
                    intent = new Intent(this, FullScreenActivity.class);
                    intent.putExtra("img", resultUri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(
                        new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    Toast.makeText(getApplicationContext(),
                                            "All permissions are granted by user!",
                                            Toast.LENGTH_SHORT).show();
                                }

//                      check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // show alert dialog navigating to Settings

                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(
                                    List<PermissionRequest> permissions,
                                    PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(),
                                "Some Error! ",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
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
                            addClient(id, name, phoneNum, email, age, gender, muri);

                        }
                    });
                }
            }
        });
    }

    private void addClient(String id, String name, String phoneNumber, String email, long age, String gender, Uri image) {
        User.currentLoggedUser = new Patient(id, name, phoneNumber, email);
        User.currentLoggedUser.setBirthDate(age);
        User.currentLoggedUser.setGender(gender);
        DatabaseInstance.getInstance().collection(CLIENTS).document(id).set(User.currentLoggedUser, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (image != null)
                    uploadUserPhoto(image, id);

            }
        });


    }

    static LoadingDialog loadingDialog;

    private void uploadUserPhoto(Uri image, String id) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Uri file = Uri.fromFile(image);
        StorageReference storageReference = storageRef.child("profileImages/" + CLIENTS + "/" + id + "/" + id);
        UploadTask uploadTask = storageReference.putFile(image);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                loadingDialog.startLoadingDialog();

            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                loadingDialog.dismissDialog();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        User.currentLoggedUser.setPhotoURL(uri.toString());
                        DatabaseInstance.getInstance().collection(CLIENTS).document(id).set(User.currentLoggedUser, SetOptions.merge());
                    }
                });

                loadingDialog.dismissDialog();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismissDialog();

            }
        });


    }

   /* private int getAge(long dateInMillis) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.setTimeInMillis(dateInMillis);
        //  dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Log.i("getAge", "getAge: " + age);
        return age;
    }*/
}
