package com.heshmat.doctoreta.patientui;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.pagination.DoctorListLiveData;
import com.heshmat.doctoreta.pagination.DoctorListViewModel;
import com.heshmat.doctoreta.pagination.DoctorsAdapter;
import com.heshmat.doctoreta.pagination.Operation;
import com.heshmat.doctoreta.utils.LocationHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.firestore.Query.Direction.ASCENDING;
import static com.google.firebase.firestore.Query.Direction.DESCENDING;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchDoctorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchDoctorFragment extends Fragment {
    private List<Doctor> doctorList = new ArrayList<>();
    @BindView(R.id.doctors_recycler_view)
    public RecyclerView doctorsRecyclerView;
    private DoctorsAdapter doctorsAdapter;
    public DoctorListViewModel doctorListViewModel;
    private boolean isScrolling;
    private static FirebaseFirestore firebaseFirestore;
    ArrayList<Doctor> doctors;
    HashMap<String, String> doctorIDs;
    CollectionReference doctorsRef;
    LocationHelper locationHelper;
    FieldPath fieldPath;
    String city;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchDoctorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchDoctorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchDoctorFragment newInstance(String param1, String param2) {
        SearchDoctorFragment fragment = new SearchDoctorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static SearchDoctorFragment newInstance(){return new SearchDoctorFragment();}

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
        View view=inflater.inflate(R.layout.fragment_search_doctor, container, false);
        ButterKnife.bind(this, view);

        doctors = new ArrayList<>();
        doctorIDs = new HashMap<String, String>();
        city = "Kyiv";
        if (firebaseFirestore == null)
            firebaseFirestore = FirebaseFirestore.getInstance();
        locationHelper = new LocationHelper(Objects.requireNonNull(getActivity()));

        firebaseFirestore = FirebaseFirestore.getInstance();
        doctorsRef = firebaseFirestore.collection("doctors");
        fieldPath = FieldPath.of("addressInfo", "city");
        if (locationHelper.getUserLocation() != null) {
            Location location = locationHelper.getUserLocation();
            Geocoder geocoder = new Geocoder(getContext(), Locale.ENGLISH);
            try {
                List<Address> latadd = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address address = latadd.get(0);
                String adminArea = address.getAdminArea();
                if (adminArea == null)
                    adminArea = "Kyiv";
                city = adminArea;
                init(adminArea);

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            init("Kyiv");
        }

        // Inflate the layout for this fragment
        return  view;
    }
    private void init(String city) {
        query = doctorsRef/*.whereEqualTo("speciality", speciality==null?"Oncology (Tumor)":speciality)*/.whereEqualTo(fieldPath, city)
                .whereLessThan("price", 3000.0).orderBy("price", DESCENDING).limit(15);
        DoctorListLiveData.query = query;
        initDoctorsRecyclerView();
        initDoctorsAdapter();
        initDoctorListViewModel();
        getDoctors();
        initRecyclerViewOnScrollListener();

    }

    private void initDoctorsRecyclerView() {
       // doctorsRecyclerView = this.findViewById(R.id.doctors_recycler_view);
        //doctorsRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initDoctorsAdapter() {
        doctorsAdapter = new DoctorsAdapter(doctorList, getContext());
        doctorsAdapter.setHasStableIds(false);

        doctorsRecyclerView.setAdapter(doctorsAdapter);
    }

    private void initDoctorListViewModel() {
        doctorListViewModel = new ViewModelProvider(this).get(DoctorListViewModel.class);
    }

    Query query;

    private void getDoctors() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference doctorsRef = firebaseFirestore.collection("doctors");
        FieldPath fieldPath = FieldPath.of("addressInfo", "city");

        DoctorListLiveData doctorListLiveData = doctorListViewModel.getDoctorListLiveData(query);
        if (doctorListLiveData != null) {
            doctorListLiveData.observe(Objects.requireNonNull(getActivity()), new Observer<Operation>() {
                @Override
                public void onChanged(Operation operation) {
                    switch (operation.type) {
                        case R.string.added:
                            Doctor addedDoctor = operation.doctor;
                            if (!doctorIDs.containsKey(addedDoctor.getId())){
                                addDoctor(addedDoctor);
                                doctorIDs.put(addedDoctor.getId(),"");
                            }

                            break;

                        case R.string.modified:
                            Doctor modifiedDoctor = operation.doctor;
                            modifyDoctor(modifiedDoctor);
                            break;

                        case R.string.removed:
                            Doctor removedDoctor = operation.doctor;
                            removeDoctor(removedDoctor);
                            doctorIDs.remove(removedDoctor.getId());

                    }
                    doctorsAdapter.notifyDataSetChanged();
                }
            });

        }
    }

    private void addDoctor(Doctor addedDoctor) {
        doctorList.add(addedDoctor);
    }

    private void modifyDoctor(Doctor modifiedDoctor) {
        for (int i = 0; i < doctorList.size(); i++) {
            Doctor currentDoctor = doctorList.get(i);
            if (currentDoctor.getId().equals(modifiedDoctor.getId())) {
                doctorList.remove(currentDoctor);
                doctorList.add(i, modifiedDoctor);
            }
        }
    }

    private void removeDoctor(Doctor removedDoctor) {
        for (int i = 0; i < doctorList.size(); i++) {
            Doctor currentDoctor = doctorList.get(i);
            if (currentDoctor.getId().equals(removedDoctor.getId())) {
                doctorList.remove(currentDoctor);
            }
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

    private static final int FILTER_ACTIVITY = 2;
    @OnClick(R.id.openFilterBt)
    public void showFilter(View view) {
        startActivityForResult(new Intent(getContext(), FilterActivity.class), FILTER_ACTIVITY);
    }
    private void structFilterQuery(String city, String gender, String speciality, int[] arr) {
        Query query = doctorsRef.whereEqualTo(fieldPath, city).whereGreaterThanOrEqualTo("price", arr[0])
                .whereLessThanOrEqualTo("price", arr[1]);
        if (gender != null) {
            query = query.whereEqualTo("gender", gender);
            // query = query1;
        }
        if (speciality != null) {
            query = query.whereEqualTo("speciality", speciality);
        }
        // query = query.orderBy("price", DESCENDING).limit(15);
        if (doctorList != null)
            doctorList.clear();
        String sort = "asce";
        if (sort.equals("asce")) {
            query = query.orderBy("price", ASCENDING).limit(15);

        } else {
            query = query.orderBy("price", DESCENDING).limit(15);

        }

        DoctorListLiveData.query = query;

        Query finalQuery = query;
        DoctorListLiveData.query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                    doctorList.clear();
                    doctorIDs.clear();
                    doctorsAdapter.notifyDataSetChanged();

                } else {
                    DoctorListLiveData.query = finalQuery;
                    //   DoctorListLiveData.query.orderBy("price",ASCENDING).get();
                }
            }
        });

    }
    String speciality;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTER_ACTIVITY && resultCode == RESULT_OK) {
            speciality = data.getStringExtra("speciality");
            //city from filter
            String citydata = data.getStringExtra("city");
            boolean cityFiledWasChosen = citydata != null;

            String gender = data.getStringExtra("gender");
            int[] fee = data.getIntArrayExtra("fee");
            String s = "speciality is " + speciality + " city is " + city + " gender is " + gender + " min fee is" + fee[0] + " max is " + fee[1];
            new AlertDialog.Builder(getContext()).setMessage(s).show();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference doctorsRef = firebaseFirestore.collection("doctors");
            FieldPath fieldPath = FieldPath.of("addressInfo", "city");
           /* query = doctorsRef.whereEqualTo("speciality", speciality == null ? "Oncology (Tumor)" : speciality).whereEqualTo(fieldPath, city)
                    .whereLessThan("price", 3000.0).orderBy("price", DESCENDING).limit(15);
            DoctorListLiveData.query = query;     */
            //     doctorListLiveData = doctorListViewModel.getDoctorListLiveData(query);
            structFilterQuery(cityFiledWasChosen ? citydata : city, gender, speciality, fee);

        }
    }
    @Override
    public void onResume() {
        if (doctorList != null) {
            doctorList.clear();
            doctorIDs.clear();
        }
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
