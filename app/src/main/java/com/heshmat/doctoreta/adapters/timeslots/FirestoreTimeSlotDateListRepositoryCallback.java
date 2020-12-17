package com.heshmat.doctoreta.adapters.timeslots;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirestoreTimeSlotDateListRepositoryCallback implements TimeSlotListViewModel.DoctorDateListRepository,
        TimeSlotListLiveData.OnLastVisibleDoctorDateCallback, TimeSlotListLiveData.OnLastDoctorDateReachedCallback {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    /*TODO: change orderby depend on user input*/
    //FieldPath fieldPath=FieldPath.of("addressInfo","city");

 //   private Query query = doctorsRef.whereEqualTo(fieldPath,"Kharkivs'ka oblast").orderBy("price", DESCENDING).limit(15);
    private DocumentSnapshot lastVisibleDoctorDate;
    private boolean isLastDoctorDateReached;

    @Override
    public TimeSlotListLiveData getDoctorDateListLiveData(Query query) {
        if (isLastDoctorDateReached) {
            return null;
        }
        if (lastVisibleDoctorDate != null) {
            query = query.startAfter(lastVisibleDoctorDate);
        }
        return new TimeSlotListLiveData(query, this, this);
    }

    @Override
    public void setLastVisibleDoctorDate(DocumentSnapshot lastVisibleDoctorDate) {
        this.lastVisibleDoctorDate = lastVisibleDoctorDate;
    }

    @Override
    public void setLastDoctorDateReached(boolean isLastDoctorDateReached) {
        this.isLastDoctorDateReached = isLastDoctorDateReached;
    }
}

