package com.heshmat.doctoreta.doctorui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.core.operation.Merge;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.LoadingDialog;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.utils.FormattingDate;
import com.heshmat.doctoreta.utils.NTPServerConnect;
import com.heshmat.doctoreta.utils.NTPServerListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetAvailabilityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetAvailabilityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SetAvailabilityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetAvailabilityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetAvailabilityFragment newInstance(String param1, String param2) {
        SetAvailabilityFragment fragment = new SetAvailabilityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SetAvailabilityFragment newInstance() {

        return new SetAvailabilityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_availability, container, false);
        ButterKnife.bind(this, view);
        context = this.getContext();
        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.setWeekScheduleBt)
    public void setSReservationDates(View view) {


        // if (TrueTime.isInitialized()) {

        getTimeFromServerAndSetSchedule();

    }

    public void getTimeFromServerAndSetSchedule() {
        LoadingDialog loadingDialog=new LoadingDialog(this.getActivity());
        loadingDialog.setText("Loading");
        loadingDialog.startLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new NTPServerConnect(new NTPServerListener() {
                    @Override
                    public void onInternetConnect(Long timeInMs) {
                        loadingDialog.dismissDialog();
                        if (timeInMs!=-1){
                            HashMap<String, String> week = FormattingDate.getDaysOfWeek(new Date(timeInMs), Calendar.MONDAY);
                            if (Doctor.currentLoggedDoctor.getAvailability() != null && !Doctor.currentLoggedDoctor.getAvailability().isEmpty()) {
                                setReservation(week);


                            } else {
                                showMessage(getString(R.string.set_ur_availability));

                            }

                        }
                        else {
                        new AlertDialog.Builder(context).setMessage(getString(R.string.check_ur_internet)).setPositiveButton(getString(R.string.retry),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getTimeFromServerAndSetSchedule();
                                    }
                                }).setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //TODO:redirect to profile page

                                        }
                                    }).show();
                        }

                    }
                }).execute();

            }
        }, 3000);

    }

    private void showMessage(String message) {
        new AlertDialog.Builder(context).setMessage(message).setPositiveButton(getString(R.string.ok), null)
                .show();
    }

    private void setReservation(HashMap<String, String> thisWeekDates) {
        DatabaseInstance.getInstance().collection(StaticFields.RESERVATIONS).document(Doctor.currentLoggedDoctor.getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.contains("dates")) {
                    HashMap<String, HashMap<String, String>> doctorSchedule = (HashMap<String, HashMap<String, String>>) documentSnapshot.get("dates");
                    if (!didUserInitializeScheduleThisWeek(doctorSchedule, thisWeekDates)) {
                        initializeSchedule(generateWeekSchedule(thisWeekDates));


                    } else {
                        showMessage(getString(R.string.u_have_set_ur_schedule));
                    }
                } else {
                    initializeSchedule(generateWeekSchedule(thisWeekDates));

                }

            }
        });

    }

    private boolean didUserInitializeScheduleThisWeek(HashMap<String, HashMap<String, String>> schedule, HashMap<String, String> thisWeekDates) {
        if (schedule == null || schedule.isEmpty()) {
            return false;
        }
        boolean thisMondayExist = schedule.containsKey(thisWeekDates.get(getString(R.string.monday)));
        if (thisMondayExist) {
            return true;

        }
        return false;
    }

    private static HashMap<String, HashMap<String, String>> generateWeekSchedule(HashMap<String, String> weekDates) {
        HashMap<String, ArrayList<String>> doctorWorkingTime = Doctor.currentLoggedDoctor.getAvailability();
        HashMap<String, HashMap<String, String>> hashMap = new HashMap<>();
        for (String dayOfWeek : weekDates.keySet()) {
            ArrayList<String> periods = doctorWorkingTime.get(dayOfWeek);
            if (periods != null && !periods.isEmpty()) {
                HashMap<String, String> periodsStatus = new HashMap<>();
                for (String period : periods) {
                    periodsStatus.put(period, StaticFields.FREE_SLOT);

                }
                hashMap.put(weekDates.get(dayOfWeek), periodsStatus);
            } else {
                hashMap.put(weekDates.get(dayOfWeek), null);

            }
        }
        return hashMap;
    }

    private void initializeSchedule(HashMap<String, HashMap<String, String>> hashMap) {
        HashMap<String, HashMap<String, HashMap<String, String>>> schedule = new HashMap<>();
        schedule.put("dates", hashMap);
        FirebaseFirestore.getInstance().collection(StaticFields.RESERVATIONS).document(Doctor.currentLoggedDoctor.getId())
                .set(schedule, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage(getString(R.string.shedule_set_success));
            }
        });
    }



}