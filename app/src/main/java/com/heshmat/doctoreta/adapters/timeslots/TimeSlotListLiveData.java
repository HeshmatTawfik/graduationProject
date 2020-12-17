package com.heshmat.doctoreta.adapters.timeslots;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.heshmat.doctoreta.R;

import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import static com.heshmat.doctoreta.models.StaticFields.LIMIT;


@SuppressWarnings("ConstantConditions")

public class TimeSlotListLiveData extends LiveData<TimeSlotOperation> implements EventListener<QuerySnapshot> {
    public static Query query;
    private ListenerRegistration listenerRegistration;
    private OnLastVisibleDoctorDateCallback onLastVisibleDoctorDateCallback;
    private OnLastDoctorDateReachedCallback onLastDoctorDateReachedCallback;

    TimeSlotListLiveData(Query query, OnLastVisibleDoctorDateCallback onLastVisibleDoctorCallback, OnLastDoctorDateReachedCallback onLastDoctorReachedCallback) {
        this.query = query;
        this.onLastVisibleDoctorDateCallback = onLastVisibleDoctorCallback;
        this.onLastDoctorDateReachedCallback = onLastDoctorReachedCallback;
    }

    @Override
    protected void onActive() {
        listenerRegistration = query.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        listenerRegistration.remove();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) return;

        for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
            switch (documentChange.getType()) {
                case ADDED:
                    HashMap<String, HashMap<String, String>> addedDoctorSchedule = (HashMap<String, HashMap<String, String>>) documentChange.getDocument().get("dates");
                    TimeSlotOperation addOperation = new TimeSlotOperation(addedDoctorSchedule, R.string.added);
                    setValue(addOperation);
                    break;

                case MODIFIED:
                    HashMap<String, HashMap<String, String>> modifiedDoctorSchedule = (HashMap<String, HashMap<String, String>>) documentChange.getDocument().get("dates");
                    TimeSlotOperation modifyOperation = new TimeSlotOperation(modifiedDoctorSchedule, R.string.modified);
                    setValue(modifyOperation);
                    break;

                case REMOVED:
                    HashMap<String, HashMap<String, String>> removedDoctorSchedule = (HashMap<String, HashMap<String, String>>) documentChange.getDocument().get("dates");
                    TimeSlotOperation removeOperation = new TimeSlotOperation(removedDoctorSchedule, R.string.removed);
                    setValue(removeOperation);
            }
        }

        int querySnapshotSize = querySnapshot.size();
        if (querySnapshotSize < LIMIT) {
            onLastDoctorDateReachedCallback.setLastDoctorDateReached(true);
        } else {
            DocumentSnapshot lastVisibleDoctor = querySnapshot.getDocuments().get(querySnapshotSize - 1);
            onLastVisibleDoctorDateCallback.setLastVisibleDoctorDate(lastVisibleDoctor);
        }
    }

    interface OnLastVisibleDoctorDateCallback {
        void setLastVisibleDoctorDate(DocumentSnapshot lastVisibleDoctorDate);
    }

    interface OnLastDoctorDateReachedCallback {
        void setLastDoctorDateReached(boolean isLastDoctorDateReached);
    }
}
