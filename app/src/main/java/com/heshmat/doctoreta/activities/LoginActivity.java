package com.heshmat.doctoreta.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.LoadingDialog;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.Patient;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.models.User;
import com.heshmat.doctoreta.patientui.HomeActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    public static GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginAct";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getActionBar() != null)
            getActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;


        LoginButton mFacebookSignInButton = (LoginButton) findViewById(R.id.fb_loginButton);

        mFacebookSignInButton.setLoginText(getString(R.string.continue_with_facebook));
        SignInButton mGoogleSignInButton = findViewById(R.id.google_login_button);
        setGooglePlusButtonText(mGoogleSignInButton, getString(R.string.continue_with_google));

        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        /**sigin in code*/
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookSignInButton.setReadPermissions("email", "public_profile");
        mFacebookSignInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG + "fb callback", Objects.requireNonNull(error.getMessage()));
            }
        });
        /** google sign in code*/
        mGoogleSignInButton.setOnClickListener(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    new Authentication().execute(user);


                } else {

                }
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_LONG).show();
                            Log.i(TAG, "onConnectionFailed: "+ task.getException());
                            new AlertDialog.Builder(LoginActivity.this).setMessage(task.getException().toString())
                                    .setPositiveButton("ok",null).show();
                            Disconnect_google();
                        } else {
                            //TODO:move to next activity and intialize
                            //   isNetworkConnected();
                            // startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            //  finish();
                        }
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        } else {
                            // isNetworkConnected();

                        }
                    }
                });
    }

    LoadingDialog loadingDialog = new LoadingDialog(this);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.google_login_button:
                loadingDialog.startLoadingDialog();
                googleSignIn();
                break;
            case R.id.fb_loginButton:
                loadingDialog.startLoadingDialog();

                break;
            default:
                return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert result != null;
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.i("google sign in fail", "onActivityResult: " + result.getStatus().getStatusMessage() + " +" + result.getStatus().getStatusCode());
                // Google Sign In failed
            }
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void Disconnect_google() {
        GoogleSignInClient mGoogleApiClient = GoogleSignIn.getClient(this, gso);

        try {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                mGoogleApiClient.revokeAccess().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error." + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onConnectionFailed: "+ connectionResult.getErrorMessage());
        new AlertDialog.Builder(this).setMessage(connectionResult.getErrorMessage())
                .setPositiveButton("ok",null).show();
    }

    private void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
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
                                                Doctor.currentLoggedDoctor = documentSnapshot.toObject(Doctor.class);
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

    private void startActivity(String id, Class activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("ID", id);
        startActivity(intent);
        finish();

    }

    public void startActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();

    }
}
