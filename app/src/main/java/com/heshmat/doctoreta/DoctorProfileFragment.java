package com.heshmat.doctoreta;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QuerySnapshot;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.models.TimeSlot;
import com.heshmat.doctoreta.patientui.HomeActivity;
import com.heshmat.doctoreta.patientui.ReservationFragment;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoctorProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.doctorProfileIv)
    public CircularImageView imageView;
    @BindView(R.id.DoctorProfileNameTv)
    public TextView name;
    @BindView(R.id.doctorProfileSpeciality)
    public TextView speciality;
    //  @BindView(R.id.doctorProfileBookBt)
    //public Button bookBt;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DoctorProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoctorProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DoctorProfileFragment newInstance(String param1, String param2) {
        DoctorProfileFragment fragment = new DoctorProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static DoctorProfileFragment newInstance() {

        return new DoctorProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);
        ButterKnife.bind(this, view);
        Glide.with(this).load(Doctor.getDoctorBeingViewed().getPhotoURL()).into(imageView);
        name.setText(Doctor.getDoctorBeingViewed().getName());
        speciality.setText(Doctor.getDoctorBeingViewed().getSpeciality());

        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.doctorProfileBookBt)
    public void goToDoctorReservation(View view) {
        final LoadingDialog loadingDialog = new LoadingDialog(this.requireActivity());
        loadingDialog.startLoadingDialog();
        DatabaseInstance.getInstance().collection(StaticFields.RESERVATIONS).document(Doctor.getDoctorBeingViewed().getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("dates") != null) {
                    HashMap<String, HashMap<String, String>> doctorSchedule = (HashMap<String, HashMap<String, String>>) documentSnapshot.get("dates");
                    ArrayList<TimeSlot> timeSlots = TimeSlot.timeSlots(doctorSchedule);
                    if (timeSlots.isEmpty() ) {
                        new AlertDialog.Builder(DoctorProfileFragment.this.getContext()).setMessage(getString(R.string.no_available_time))
                                .setPositiveButton(getString(R.string.ok),null).show();

                    }
                    else {
                        DoctorProfileFragment.this.requireActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                .replace(R.id.fragmentContainer, ReservationFragment.newInstance(Doctor.getDoctorBeingViewed().getId(), Doctor.getDoctorBeingViewed().getName()
                                        , Doctor.getDoctorBeingViewed().getEmail(), Doctor.getDoctorBeingViewed().getPrice(), Doctor.getDoctorBeingViewed().getSpeciality()
                                )).addToBackStack(ReservationFragment.class.getName())
                                .commit();

                    }


                }
                else {
                    new AlertDialog.Builder(DoctorProfileFragment.this.getContext()).setMessage(getString(R.string.no_available_time))
                            .setPositiveButton(getString(R.string.ok),null).show();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                loadingDialog.dismissDialog();
                if (!task.isSuccessful()) {
                    new AlertDialog.Builder(DoctorProfileFragment.this.getContext()).setMessage(getString(R.string.something_wrong))
                            .setPositiveButton(getString(R.string.ok),null).show();
                }

            }
        });

    }
}
