package com.heshmat.doctoreta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.heshmat.doctoreta.activities.ChooseRoleActivity;
import com.heshmat.doctoreta.activities.DoctorFormActivity;
import com.heshmat.doctoreta.activities.DoctorHomeActivity;
import com.heshmat.doctoreta.patientui.HomeActivity;
import com.heshmat.doctoreta.activities.LoginActivity;
import com.heshmat.doctoreta.activities.PatientFormActivity;
import com.heshmat.doctoreta.activities.UnverifiedDoctorActivity;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.Patient;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.models.User;

import java.util.Arrays;
import java.util.List;

import static com.heshmat.doctoreta.models.StaticFields.CLIENTS;
import static com.heshmat.doctoreta.models.StaticFields.UNVERIFIED_DOCTORS;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    Context context;
    private static final int RC_SIGN_IN = 9001;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getActionBar() != null)
            getActionBar().hide();
        setContentView(R.layout.activity_main);
   //     FakeDoctors.main();
        context = this;
        providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );
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

                                }
                                else if (role.equals(StaticFields.PATIENT_ROLE)) {
                                    /*Todo:check patient collections */
                                    patientRedirect(user.getUid(), user);

                                }
                                else {
                                    startActivity(user.getUid(), ChooseRoleActivity.class);
                                    //   startActivity(new Intent(context, ChooseRoleActivity.class));
                                    //todo: redirect to signinup activity

                                }


                            }
                            else { //user didnt choose a role
                                startActivity(user.getUid(), ChooseRoleActivity.class);
                                // User is signed out, send to SignInUpActivity
                                //  startActivity(new Intent(MainActivity.this, ChooseRoleActivity.class));
                                //finish();
                            }
                        }
                    });


                }
                else {// firebase user is null
                    // User is signed out, send to SignInUpActivity
                    /*startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();*/
                    AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                            .Builder(R.layout.activity_login)
                            .setGoogleButtonId(R.id.google_login_button)
                            .setFacebookButtonId(R.id.fb_loginButton)
                            .build();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setAuthMethodPickerLayout(customLayout)
                                    .setTheme(R.style.mystle)
                                    .build(),
                            RC_SIGN_IN);

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


                    if (documentSnapshot.contains("name") && documentSnapshot.contains("phoneNumber") && documentSnapshot.contains("email")) {

                        User.currentLoggedUser = documentSnapshot.toObject(Patient.class);
                        // new Patient(id, name, phoneNumber, email);
                        startActivity(new Intent(context, HomeActivity.class));
                        finish();

                    }
                    else {
                        String email = user.getEmail();
                        String name = user.getDisplayName();
                        String phone = user.getPhoneNumber();
                        User.currentLoggedUser = new Patient(id, name, phone, email);
                        startActivity(id, PatientFormActivity.class);
                    }
                }
                else {
                    String email = user.getEmail();
                    String name = user.getDisplayName();
                    String phone = user.getPhoneNumber();
                    User.currentLoggedUser = new Patient(id, name, phone, email);
                    startActivity(id, PatientFormActivity.class);
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
                    String email = user.getEmail();
                    String name = user.getDisplayName();
                    String phone = user.getPhoneNumber();
                    Doctor.currentLoggedDoctor = new Doctor(id, name, phone, email);
                    startActivity(id, DoctorFormActivity.class);
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

    class Authentication extends AsyncTask<FirebaseUser, Void, Void> {
        FirebaseUser user;

        //
        @Override
        protected Void doInBackground(FirebaseUser... users) {
            // loadingDialog.startLoadingDialog();
            user = users[0];

            if (user != null) {/**successfully logged in*/
                DatabaseInstance.getInstance().collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        if (value.exists()) {
                            String ss = value.toString();
                            String role = value.get("role", String.class);
                            // assert role != null;
                            if (role != null && role.equals(StaticFields.DOCTOR_ROLE)) {

                                boolean isVerified = value.getBoolean("isVerified");

                                /*Todo:check doctor collections */
                                /**TODO code will be modificated as we be sure about doctor stare where he is enabled or no*/
                                if (isVerified)
                                    DatabaseInstance.getInstance().collection("doctors").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.contains("name") && documentSnapshot.contains("phoneNumber") && documentSnapshot.contains("email")) {
                                                /**all info is found */
                                                Doctor.currentLoggedDoctor = new Doctor(user.getUid(), documentSnapshot.get("name", String.class), documentSnapshot.get("phoneNumber", String.class), documentSnapshot.get("email", String.class));
                                                startActivity(DoctorHomeActivity.class);

                                            } else {
                                                //user info is not completed
                                                startActivity(user.getUid(), DoctorFormActivity.class);


                                            }
                                        }
                                    });
                                else {
                                    DatabaseInstance.getInstance().collection(StaticFields.UNVERIFIED_DOCTORS).document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.contains("name") && documentSnapshot.contains("phoneNumber") && documentSnapshot.contains("email")) {
                                                /**all info is found */
                                                Doctor.currentLoggedDoctor = new Doctor(user.getUid(), documentSnapshot.get("name", String.class), documentSnapshot.get("phoneNumber", String.class), documentSnapshot.get("email", String.class));
                                                startActivity(HomeActivity.class);

                                            } else {
                                                Doctor.currentLoggedDoctor = new Doctor(user.getUid(), user.getDisplayName(), user.getPhoneNumber(), user.getEmail());
                                                startActivity(user.getUid(), DoctorFormActivity.class);


                                            }
                                        }
                                    });
                                }

                            } else if (role != null && role.equals(StaticFields.PATIENT_ROLE)) {
                                /*Todo:check patient collections */
                                DatabaseInstance.getInstance().collection("clients").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.contains("name") && documentSnapshot.contains("phoneNumber") && documentSnapshot.contains("email")) {
                                            /**all info is found */
                                            //User.currentLoggedUser = new Patient(user.getUid(), documentSnapshot.get("name", String.class), documentSnapshot.get("phoneNumber", String.class), documentSnapshot.get("email", String.class));
                                            User.currentLoggedUser=documentSnapshot.toObject(Patient.class);
                                            startActivity(HomeActivity.class);

                                        } else {
                                            User.currentLoggedUser = new Patient(user.getUid(), user.getDisplayName(), user.getPhoneNumber(), user.getEmail());
                                            startActivity(user.getUid(), PatientFormActivity.class);


                                        }
                                    }
                                });

                            }
                            //           else if (role!=null&&role.equals(StaticFields.UNVERIFIED_DOCTOR_ROLE)) { }

                            else {

                                //todo: redirect to choose role activity
                                startActivity(user.getUid(), ChooseRoleActivity.class);

                            }


                        } else {

                            //todo: redirect to choose role activity
                            startActivity(user.getUid(), ChooseRoleActivity.class);

                        }
                    }
                });
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // if (loadingDialog!=null)
            // loadingDialog.dismissDialog();
        }
    }



    public void startActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //   mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                new Authentication().execute(user);
                // ...
            } else {
                Log.i(TAG, "onActivityResult: "+response.getError());
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
            /*
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert result != null;
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
              //  firebaseAuthWithGoogle(account);
            } else {
                Log.i("google sign in fail", "onActivityResult: " + result.getStatus().getStatusMessage() + " +" + result.getStatus().getStatusCode());
                // Google Sign In failed
            }*/

        }
    }
}
