package com.heshmat.doctoreta.patientui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.MainActivity;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.activities.LoginActivity;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.Patient;
import com.heshmat.doctoreta.models.User;
import com.heshmat.doctoreta.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import static com.heshmat.doctoreta.activities.LoginActivity.gso;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.nested)
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    TextView drawerUserNameTv;

    TextView drawerUserEmailTv;

    ImageView userIv;
    public static Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = this;
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //  hideKeyboard(HomeActivity.this);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (fragment != null) {
                    showFragment(fragment);

                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        View hederView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.drawer_header, navigationView);

        drawerUserNameTv = hederView.findViewById(R.id.drawerUserNameTv);
        drawerUserEmailTv = hederView.findViewById(R.id.drawerUserEmailTv);
        userIv = hederView.findViewById(R.id.userIv);
        initializeUserUiInfo();

        //  showFragment(ProjectsFragment.class);
        if (savedInstanceState == null) {
            // navigationView.getMenu().getItem(0).setChecked(true);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, SearchDoctorFragment.newInstance())
                    .commit();
        }

    }

    Class fragment = null;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();
        switch (item.getItemId()) {
            case R.id.searchMenuItem:
                fragment = SearchDoctorFragment.class;
                break;


            case R.id.logoutMenuItem:
                //FirebaseAuth.getInstance().signOut();
                //LoginManager.getInstance().logOut();
                //User.currentLoggedUser = null;
                Disconnect_google();

                break;
        }
        closeDrawer();
        return false;
    }

    public void showFragment(Class fragmentClass) {
        Fragment fragment = null;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (!currentFragment.getClass().getName().equals(fragmentClass.getName())) {
            fragmentManager.beginTransaction()
                    //  .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragmentContainer, fragment, fragmentClass.getName()).addToBackStack(null)
                    .commit();

        }
    }

    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    private void initializeUserUiInfo() {
        if (User.currentLoggedUser != null) {
            if (User.currentLoggedUser.getName() != null)
                drawerUserNameTv.setText(User.currentLoggedUser.getName());
            if (User.currentLoggedUser.getEmail() != null)
                drawerUserEmailTv.setText(User.currentLoggedUser.getEmail());
            if (User.currentLoggedUser.getPhotoURL() != null) {


                Picasso.with(getApplicationContext()).load(User.currentLoggedUser.getPhotoURL()).transform(new CircleTransform())
                        .error(R.drawable.color_cursor).into(userIv);
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void Disconnect_google() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        User.currentLoggedUser=null;
                        Doctor.currentLoggedDoctor=null;
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();                    }
                });
       /* GoogleSignInOptions  gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
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
        }*/
    }
}
