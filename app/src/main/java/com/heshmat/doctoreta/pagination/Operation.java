package com.heshmat.doctoreta.pagination;

import com.heshmat.doctoreta.models.Doctor;

public  class Operation {
   public Doctor doctor;
    public int type;

    Operation(Doctor doctor, int type) {
        this.doctor = doctor;
        this.type = type;
    }
}