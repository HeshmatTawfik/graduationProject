package com.heshmat.doctoreta.patientui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.adapters.AppointmentAdapter;
import com.heshmat.doctoreta.adapters.DoctorAppointmentAdapter;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.DoctorReservation;
import com.heshmat.doctoreta.models.PatientReservation;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.models.User;

import java.util.Objects;

public class AppointmentsActivity extends AppCompatActivity {
    @BindView(R.id.patientAppointmentsRv)
    RecyclerView recyclerView;
    AppointmentAdapter patientAdapter;
    DoctorAppointmentAdapter doctorAppointmentAdapter;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);
        ButterKnife.bind(this);
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        Intent intent = getIntent();
        String role = intent.getStringExtra("ROLE");
        if (role.equals(StaticFields.PATIENT_ROLE)) {
            query = DatabaseInstance.getInstance().collection(StaticFields.CLIENTS).document(User.currentLoggedUser.getId())
                    .collection(StaticFields.RESERVATIONS).whereNotEqualTo("status", "terminated").orderBy("status").orderBy("createdAt", Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<PatientReservation> options
                    = new FirestoreRecyclerOptions.Builder<PatientReservation>()
                    .setQuery(query, PatientReservation.class)
                    .build();
            patientAdapter = new AppointmentAdapter(options, this);
            recyclerView.setAdapter(patientAdapter);
        } else {
            query = DatabaseInstance.getInstance().collection(StaticFields.DOCTORS).document(Doctor.currentLoggedDoctor.getId())
                    .collection(StaticFields.RESERVATIONS).whereNotEqualTo("status", "terminated").orderBy("status").orderBy("createdAt", Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<DoctorReservation> options
                    = new FirestoreRecyclerOptions.Builder<DoctorReservation>()
                    .setQuery(query, DoctorReservation.class)
                    .build();
            doctorAppointmentAdapter = new DoctorAppointmentAdapter(options, this);
            recyclerView.setAdapter(doctorAppointmentAdapter);
        }


//        adapter.

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (patientAdapter!=null)
        patientAdapter.startListening();
        if (doctorAppointmentAdapter!=null)
            doctorAppointmentAdapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stoping of the activity
    @Override
    protected void onStop() {
        super.onStop();
        if (patientAdapter!=null)
            patientAdapter.stopListening();
        if (doctorAppointmentAdapter!=null)
            doctorAppointmentAdapter.stopListening();


    }
}
