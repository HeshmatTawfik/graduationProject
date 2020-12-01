package com.heshmat.doctoreta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.heshmat.doctoreta.R;

public class SignInUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);
    //    startActivity(new Intent(this,LoginActivity.class));
    }
}
