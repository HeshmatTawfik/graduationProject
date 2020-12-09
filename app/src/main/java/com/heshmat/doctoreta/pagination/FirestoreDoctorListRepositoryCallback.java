package com.heshmat.doctoreta.pagination;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirestoreDoctorListRepositoryCallback implements DoctorListViewModel.DoctorListRepository,
        DoctorListLiveData.OnLastVisibleDoctorCallback, DoctorListLiveData.OnLastDoctorReachedCallback {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference doctorsRef = firebaseFirestore.collection("doctors");
    /*TODO: change orderby depend on user input*/
    //FieldPath fieldPath=FieldPath.of("addressInfo","city");

 //   private Query query = doctorsRef.whereEqualTo(fieldPath,"Kharkivs'ka oblast").orderBy("price", DESCENDING).limit(15);
    private DocumentSnapshot lastVisibleDoctor;
    private boolean isLastDoctorReached;

    @Override
    public DoctorListLiveData getDoctorListLiveData(Query query) {
        if (isLastDoctorReached) {
            return null;
        }
        if (lastVisibleDoctor != null) {
            query = query.startAfter(lastVisibleDoctor);
        }
        return new DoctorListLiveData(query, this, this);
    }

    @Override
    public void setLastVisibleDoctor(DocumentSnapshot lastVisibleDoctor) {
        this.lastVisibleDoctor = lastVisibleDoctor;
    }

    @Override
    public void setLastDoctorReached(boolean isLastDoctorReached) {
        this.isLastDoctorReached = isLastDoctorReached;
    }
}

