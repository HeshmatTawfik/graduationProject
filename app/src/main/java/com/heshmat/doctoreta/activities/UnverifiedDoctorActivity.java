package com.heshmat.doctoreta.activities;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.StaticFields;

public class UnverifiedDoctorActivity extends AppCompatActivity {
    @BindView(R.id.logoutUnverifiedBt)
    Button logout;
    @BindView(R.id.unverfiedTv)
    TextView textView;
    @BindView(R.id.unverifiedIv)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getActionBar() != null)
            getActionBar().hide();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_unverified_doctor);
        ButterKnife.bind(this);
        if (Doctor.currentLoggedDoctor != null)
            DatabaseInstance.getInstance().collection(StaticFields.UNVERIFIED_DOCTORS).document(Doctor.currentLoggedDoctor.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String status = documentSnapshot.getString("status");
                    assert status != null;
                    if (status.equals(StaticFields.IN_PROGRESS)) {
                        setStatusUI(R.drawable.ic_wait_to_approve,R.string.waitting_approve);

                    } else if (status.equals(StaticFields.REJECTED)) {
                        setStatusUI(R.drawable.ic_rejected,R.string.rejected);

                    }

                }
            });
    }
    private void setStatusUI(int imgId,int textId){
        imageView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        imageView.setImageResource(imgId);
        textView.setText(getString(textId));
    }
}
