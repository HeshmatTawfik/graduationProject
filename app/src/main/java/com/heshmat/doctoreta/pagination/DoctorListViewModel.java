package com.heshmat.doctoreta.pagination;

import com.google.firebase.firestore.Query;

import androidx.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class DoctorListViewModel  extends ViewModel {
    private DoctorListRepository doctorListRepository = new FirestoreDoctorListRepositoryCallback();

     public DoctorListLiveData getDoctorListLiveData(Query query) {
        return doctorListRepository.getDoctorListLiveData(query);
    }

    public interface DoctorListRepository {
        DoctorListLiveData getDoctorListLiveData(Query query);
    }
}
