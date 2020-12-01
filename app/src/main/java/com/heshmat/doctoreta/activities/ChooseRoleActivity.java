package com.heshmat.doctoreta.activities;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.models.StaticFields.*;

import java.util.HashMap;
import java.util.Map;

import static com.heshmat.doctoreta.models.StaticFields.DOCTOR_ROLE;
import static com.heshmat.doctoreta.models.StaticFields.PATIENT_ROLE;
import static com.heshmat.doctoreta.models.StaticFields.UNVERIFIED_DOCTOR_ROLE;
import static com.heshmat.doctoreta.models.StaticFields.USERS;

public class ChooseRoleActivity extends AppCompatActivity {
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.radio_button_doctor)
    RadioButton docRadio;
    @BindView(R.id.radio_button_client)
    RadioButton patientRadio;
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
    }

    @OnClick(R.id.choose_role_bt)
    public void chooseRole(View view) {

        int selectedId = radioGroup.getCheckedRadioButtonId();
        Intent intent;
        switch (selectedId) {
            case R.id.radio_button_doctor:
                docRadio.setError(null);
                patientRadio.setError(null);
                saveNewUSerRole(DOCTOR_ROLE);
                break;
            case R.id.radio_button_client:
                docRadio.setError(null);
                patientRadio.setError(null);
                saveNewUSerRole(PATIENT_ROLE);
                break;
            default:
                docRadio.setError("Please choose role");
                //  docRadio.setError(getString(R.string.choose_role_warning));
                patientRadio.setError(getString(R.string.choose_role_warning));

        }

    }

    private void saveNewUSerRole(String role) {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("role",role);
        if (role.equals(DOCTOR_ROLE))
            map.put("isVerified",false);


        DatabaseInstance.getInstance().collection(USERS).document(id).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (role.equals(UNVERIFIED_DOCTOR_ROLE)) {
                    redirectTo(id, DoctorFormActivity.class);

                } else if (role.equals(PATIENT_ROLE)) {
                    redirectTo(id, PatientFormActivity.class);

                }


            }
        });

    }

    private void redirectTo(String id, Class activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("ID", id);
        startActivity(intent);
        finish();
    }

}
