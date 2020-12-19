package com.heshmat.doctoreta.patientui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.adapters.timeslots.TimeSlotAdapter;
import com.heshmat.doctoreta.adapters.timeslots.TimeSlotListLiveData;
import com.heshmat.doctoreta.adapters.timeslots.TimeSlotListViewModel;
import com.heshmat.doctoreta.adapters.timeslots.TimeSlotOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DOCTOR_ID = "DOCTOR_ID";
    private static final String DOCTOR_NAME = "DOCTOR_NAME";
    private static final String DOCTOR_EMAIL = "DOCTOR_EMAIL";
    private static final String PRICE = "PRICE";
    private static final String SPECIALITY = "SPECIALITY";




    // TODO: Rename and change types of parameters
    private String mParamDoctorID;
    private String mParamDoctorName;
    private String mParamDoctorEmail;
    private String mSpeciality;
    private double mPrice;

    String id;
    private List<HashMap<String, HashMap<String, String>>> slotsList = new ArrayList<HashMap<String, HashMap<String, String>>>();
    private RecyclerView doctorsRecyclerView;
    private TimeSlotAdapter timeSlotAdapter;
    public TimeSlotListViewModel timeSlotListViewModel;
    private boolean isScrolling;
    private static FirebaseFirestore firebaseFirestore;
    CollectionReference doctorsRef;
    Query query;
    HashMap<String, HashMap<String, String>> slot;
    Context context;

    BroadcastReceiver receiver;
    public ReservationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ReservationFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static ReservationFragment newInstance(String doctorID,String doctorName,String doctorEmail,double price,String speciality) {
        ReservationFragment fragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putString(DOCTOR_ID, doctorID);
        args.putString(DOCTOR_NAME, doctorName);
        args.putString(DOCTOR_EMAIL, doctorEmail);
        args.putDouble(PRICE, price);
        args.putString(SPECIALITY,speciality);




        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamDoctorID = getArguments().getString(DOCTOR_ID);
            mParamDoctorName = getArguments().getString(DOCTOR_NAME);
            mParamDoctorEmail =getArguments().getString(DOCTOR_EMAIL);
            mPrice=getArguments().getDouble(PRICE);
            mSpeciality=getArguments().getString(SPECIALITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_reservation, container, false);
        doctorsRecyclerView = view.findViewById(R.id.timeSlotRv);
        ((SimpleItemAnimator) Objects.requireNonNull(doctorsRecyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        id = mParamDoctorID;
        context = this.getContext();
        slot = new HashMap<>();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (timeSlotAdapter !=null)
                    timeSlotAdapter.updateTime();
            }
        };

        doctorsRef = DatabaseInstance.getInstance().collection("reservations");


        doctorsRef.whereEqualTo(FieldPath.documentId(), id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    queryDocumentSnapshots.getDocuments();
                }
            }
        });

        init();
        return view;
    }
    private void init() {
        query = doctorsRef.whereEqualTo(FieldPath.documentId(), id);
        TimeSlotListLiveData.query = query;
        initDoctorsRecyclerView();
        initDoctorsAdapter();
        initDoctorListViewModel();
        getDoctors();
        initRecyclerViewOnScrollListener();

    }

    private void initDoctorsRecyclerView() {
        //doctorsRecyclerView = context.findViewById(R.id.timeSlotRv);
        doctorsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
    private void initDoctorsAdapter() {
        //  ArrayList<TimeSlot> slots = TimeSlot.timeSlots(doctorList.get(i));
        timeSlotAdapter = new TimeSlotAdapter(slotsList, context,id,mParamDoctorName,mParamDoctorEmail,mPrice,mSpeciality);
        timeSlotAdapter.setHasStableIds(false);
        doctorsRecyclerView.setAdapter(timeSlotAdapter);

    }
    private void initDoctorListViewModel() {
        timeSlotListViewModel = new ViewModelProvider(this).get(TimeSlotListViewModel.class);
    }
    TimeSlotListLiveData doctorListLiveData;
    private void getDoctors() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference doctorsRef = firebaseFirestore.collection("reservations");

         doctorListLiveData = timeSlotListViewModel.getTimeSlotListLiveData(query);
        if (doctorListLiveData != null) {
            doctorListLiveData.observe(requireActivity(), new Observer<TimeSlotOperation>() {
                @Override
                public void onChanged(TimeSlotOperation operation) {
                    switch (operation.type) {
                        case R.string.added:
                            HashMap<String, HashMap<String, String>> addedDoctor = operation.timeSlot;
                            addDoctor(addedDoctor);


                            break;

                        case R.string.modified:
                            HashMap<String, HashMap<String, String>> modifiedDoctor = operation.timeSlot;
                            modifyDoctor(modifiedDoctor);
                            break;

                        case R.string.removed:
                            HashMap<String, HashMap<String, String>> removedDoctor = operation.timeSlot;
                            //     removeDoctor(removedDoctor);
                            //doctorIDs.remove(removedDoctor.getId());

                    }
                    timeSlotAdapter.update(slotsList);
                }
            });
        }
    }

    private void addDoctor(HashMap<String, HashMap<String, String>> addedDoctor) {
        slotsList.add(addedDoctor);
    }
    private void modifyDoctor(HashMap<String, HashMap<String, String>> modifiedDoctor) {

        for (int i = 0; i < slotsList.size(); i++) {
            slotsList.remove(i);
            slotsList.add(modifiedDoctor);


        }
    }
    private void initRecyclerViewOnScrollListener() {
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                if (layoutManager != null) {
                    int firstVisibleDoctorPosition = layoutManager.findFirstVisibleItemPosition();
                    int visibleDoctorCount = layoutManager.getChildCount();
                    int totalDoctorCount = layoutManager.getItemCount();

                    if (isScrolling && (firstVisibleDoctorPosition + visibleDoctorCount == totalDoctorCount)) {
                        isScrolling = false;
                        getDoctors();
                    }
                }
            }
        };
        doctorsRecyclerView.addOnScrollListener(onScrollListener);
    }

    @Override
    public void onResume() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        context.registerReceiver(receiver, filter);
        if (slotsList != null) {
            slotsList.clear();
//            doctorIDs.clear();
        }
        super.onResume();
    }
    @Override
    public void onPause() {
        context.unregisterReceiver(receiver);
        super.onPause();

    }

    @Override
    public void onDestroy() {
        if (doctorListLiveData!=null){
            Log.i("reservationFrag", "onDestroy: was destored");
            doctorListLiveData.removeObservers(this);
        }
        super.onDestroy();

    }
}
