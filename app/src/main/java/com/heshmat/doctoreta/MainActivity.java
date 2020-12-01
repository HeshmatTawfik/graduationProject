package com.heshmat.doctoreta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.heshmat.doctoreta.activities.ChooseRoleActivity;
import com.heshmat.doctoreta.activities.DoctorFormActivity;
import com.heshmat.doctoreta.activities.DoctorHomeActivity;
import com.heshmat.doctoreta.activities.HomeActivity;
import com.heshmat.doctoreta.activities.LoginActivity;
import com.heshmat.doctoreta.activities.PatientFormActivity;
import com.heshmat.doctoreta.activities.UnverifiedDoctorActivity;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.Patient;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.models.User;

import static com.heshmat.doctoreta.models.StaticFields.CLIENTS;
import static com.heshmat.doctoreta.models.StaticFields.UNVERIFIED_DOCTORS;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getActionBar() != null)
            getActionBar().hide();
        setContentView(R.layout.activity_main);
        context = this;
        animatedLogo();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    DatabaseInstance.getInstance().collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            if (value.exists() && value.get("role") != null) {
                                String role = value.get("role", String.class);

                                assert role != null;

                                if (role.equals(StaticFields.DOCTOR_ROLE)) {
                                    if (value.getBoolean("isVerified") != null && value.getBoolean("isVerified"))
                                        doctorRedirect(user.getUid());
                                    else if (value.getBoolean("isVerified") != null && !value.getBoolean("isVerified"))
                                        unverifiedDoctorRedirect(user.getUid(), user);
                                    else
                                        startActivity(user.getUid(), ChooseRoleActivity.class);

                                    /*Todo:check doctor collections */

                                } else if (role.equals(StaticFields.PATIENT_ROLE)) {
                                    /*Todo:check patient collections */
                                    patientRedirect(user.getUid(), user);

                                } /*else if (role.equals(StaticFields.UNVERIFIED_DOCTOR_ROLE)) {
                                    unverifiedDoctorRedirect(user.getUid(), user);
                                } */else {
                                    startActivity(user.getUid(), ChooseRoleActivity.class);
                                    //   startActivity(new Intent(context, ChooseRoleActivity.class));
                                    //todo: redirect to signinup activity

                                }


                            } else {
                                startActivity(user.getUid(), ChooseRoleActivity.class);
                                // User is signed out, send to SignInUpActivity
                                //  startActivity(new Intent(MainActivity.this, ChooseRoleActivity.class));
                                //finish();
                            }
                        }
                    });


                } else {
                    // User is signed out, send to SignInUpActivity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 1500);

    }


    private void animatedLogo() {
        ImageView imageView = findViewById(R.id.splashLogo);
        final AnimatedVectorDrawable vectorDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.animated_logo);
        imageView.setImageDrawable(vectorDrawable);
        assert vectorDrawable != null;
        vectorDrawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                vectorDrawable.start();
            }
        });
        vectorDrawable.start();
    }

    private void doctorRedirect(String id) {
        DatabaseInstance.getInstance().collection("doctors").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.contains("name") && documentSnapshot.contains("phoneNumber") && documentSnapshot.contains("email")
                ) {
                    String name = documentSnapshot.get("name", String.class);
                    String phoneNumber = documentSnapshot.get("phoneNumber", String.class);
                    String email = documentSnapshot.get("email", String.class);
                    Doctor.currentLoggedDoctor = new Doctor(id, name, phoneNumber, email);
                    startActivity(new Intent(context, DoctorHomeActivity.class));
                    finish();

                } else {
                    startActivity(id, DoctorFormActivity.class);

                    //  startActivity(new Intent(context, DoctorFormActivity.class));
                    //finish();


                }
            }
        });


    }

    private void patientRedirect(String id, FirebaseUser user) {
        DatabaseInstance.getInstance().collection(CLIENTS).document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && documentSnapshot.exists()) {


                    if (documentSnapshot.contains("name") && documentSnapshot.contains("phoneNumber") && documentSnapshot.contains("email")
                    ) {
                        String name = documentSnapshot.get("name", String.class);
                        String phoneNumber = documentSnapshot.get("phoneNumber", String.class);
                        String email = documentSnapshot.get("email", String.class);
                        User.currentLoggedUser = documentSnapshot.toObject(Patient.class);
                        // new Patient(id, name, phoneNumber, email);
                        startActivity(new Intent(context, HomeActivity.class));
                        finish();

                    } else {
                        String email = user.getEmail();
                        String name = user.getDisplayName();
                        String phone = user.getPhoneNumber();
                        User.currentLoggedUser = new Patient(id, name, phone, email);
                        startActivity(id, PatientFormActivity.class);

                        //   startActivity(new Intent(context, PatientFormActivity.class));
                        // finish();

                    }
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                }
            }

        });

       /* DatabaseInstance.getInstance().collection(CLIENTS).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean na=documentSnapshot.contains("name");
                boolean ph=documentSnapshot.contains("phoneNumber");
                boolean em=documentSnapshot.contains("email");

                if (documentSnapshot.contains("name") && documentSnapshot.contains("phoneNumber") && documentSnapshot.contains("email")
                ) {
                    String name = documentSnapshot.get("name", String.class);
                    String phoneNumber = documentSnapshot.get("phoneNumber", String.class);
                    String email = documentSnapshot.get("email", String.class);
                    User.currentLoggedUser = new Patient(id, name, phoneNumber, email);
                    startActivity(new Intent(context, HomeActivity.class));
                    finish();

                }
                else {
                    String email=user.getEmail();
                    String name=user.getDisplayName();
                    String phone=user.getPhoneNumber();
                    User.currentLoggedUser=new Patient(id,name,phone,email);
                    startActivity(id,PatientFormActivity.class);

                 //   startActivity(new Intent(context, PatientFormActivity.class));
                   // finish();

                }
            }
        });*/


    }

    private void unverifiedDoctorRedirect(String id, FirebaseUser user) {
        DatabaseInstance.getInstance().collection(UNVERIFIED_DOCTORS).document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    boolean n = documentSnapshot.contains("name") && documentSnapshot.get("name") != null;
                    boolean n1 = documentSnapshot.contains("phoneNumber") && documentSnapshot.get("phoneNumber") != null;
                    boolean n2 = documentSnapshot.contains("email") && documentSnapshot.get("email") != null;
                    boolean n3 = documentSnapshot.contains("idCardUrl") && documentSnapshot.get("idCardUrl") != null;
                    boolean n4 = documentSnapshot.contains("medicalLicenseUrl") && documentSnapshot.get("medicalLicenseUrl") != null;

                    if (documentSnapshot.contains("name") && documentSnapshot.get("name") != null
                            && documentSnapshot.contains("phoneNumber") && documentSnapshot.get("phoneNumber") != null
                            && documentSnapshot.contains("email") && documentSnapshot.get("email") != null
                            && documentSnapshot.contains("idCardUrl") && documentSnapshot.get("idCardUrl") != null
                            && documentSnapshot.contains("medicalLicenseUrl") && documentSnapshot.get("medicalLicenseUrl") != null) {

                        Doctor.currentLoggedDoctor = documentSnapshot.toObject(Doctor.class);

                        // new Patient(id, name, phoneNumber, email);
                        startActivity(new Intent(context, UnverifiedDoctorActivity.class));
                        finish();

                    } else {
                        String email = user.getEmail();
                        String name = user.getDisplayName();
                        String phone = user.getPhoneNumber();
                        Doctor.currentLoggedDoctor = new Doctor(id, name, phone, email);
                        startActivity(id, DoctorFormActivity.class);

                    }
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                }
            }

        });


    }

    private void startActivity(String id, Class activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("ID", id);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }


}
