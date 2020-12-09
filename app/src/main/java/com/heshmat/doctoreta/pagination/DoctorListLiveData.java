package com.heshmat.doctoreta.pagination;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.Doctor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import static com.heshmat.doctoreta.models.StaticFields.LIMIT;

@SuppressWarnings("ConstantConditions")

public class DoctorListLiveData extends LiveData<Operation> implements EventListener<QuerySnapshot> {
    public static Query query;
    private ListenerRegistration listenerRegistration;
    private OnLastVisibleDoctorCallback onLastVisibleDoctorCallback;
    private OnLastDoctorReachedCallback onLastDoctorReachedCallback;

    DoctorListLiveData(Query query, OnLastVisibleDoctorCallback onLastVisibleDoctorCallback, OnLastDoctorReachedCallback onLastDoctorReachedCallback) {
        this.query = query;
        this.onLastVisibleDoctorCallback = onLastVisibleDoctorCallback;
        this.onLastDoctorReachedCallback = onLastDoctorReachedCallback;
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
                    Doctor addedDoctor = documentChange.getDocument().toObject(Doctor.class);
                    Operation addOperation = new Operation(addedDoctor, R.string.added);
                    setValue(addOperation);
                    break;

                case MODIFIED:
                    Doctor modifiedDoctor = documentChange.getDocument().toObject(Doctor.class);
                    Operation modifyOperation = new Operation(modifiedDoctor, R.string.modified);
                    setValue(modifyOperation);
                    break;

                case REMOVED:
                    Doctor removedDoctor = documentChange.getDocument().toObject(Doctor.class);
                    Operation removeOperation = new Operation(removedDoctor, R.string.removed);
                    setValue(removeOperation);
            }
        }

        int querySnapshotSize = querySnapshot.size();
        if (querySnapshotSize < LIMIT) {
            onLastDoctorReachedCallback.setLastDoctorReached(true);
        } else {
            DocumentSnapshot lastVisibleDoctor = querySnapshot.getDocuments().get(querySnapshotSize - 1);
            onLastVisibleDoctorCallback.setLastVisibleDoctor(lastVisibleDoctor);
        }
    }

    interface OnLastVisibleDoctorCallback {
        void setLastVisibleDoctor(DocumentSnapshot lastVisibleDoctor);
    }

    interface OnLastDoctorReachedCallback {
        void setLastDoctorReached(boolean isLastDoctorReached);
    }
}
