package com.heshmat.doctoreta.adapters.timeslots;

import com.google.firebase.firestore.Query;

import androidx.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class TimeSlotListViewModel extends ViewModel {
    private DoctorDateListRepository doctorDateListRepository = new FirestoreTimeSlotDateListRepositoryCallback();

     public TimeSlotListLiveData getTimeSlotListLiveData(Query query) {
        return doctorDateListRepository.getDoctorDateListLiveData(query);
    }

    public interface DoctorDateListRepository {
        TimeSlotListLiveData getDoctorDateListLiveData(Query query);
    }
}
