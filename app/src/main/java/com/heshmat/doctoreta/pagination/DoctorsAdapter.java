package com.heshmat.doctoreta.pagination;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.Doctor;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {
    private List<Doctor> doctorList;
    Context context;

   public DoctorsAdapter(List<Doctor> doctorList,Context context) {
        this.doctorList = doctorList;
        this.context=context;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_card, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.bindDoctor(doctor);
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    class DoctorViewHolder extends RecyclerView.ViewHolder {
        CircularImageView profileImg;

        TextView doctorNameCardTv,doctorCardSpecialityTv,doctorCardLocationTv,doctorCardFeesTv;
        DoctorViewHolder(View itemView) {
            super(itemView);
            profileImg=itemView.findViewById(R.id.doctorCardIv);
            doctorNameCardTv = itemView.findViewById(R.id.doctorNameCardTv);
            doctorCardSpecialityTv = itemView.findViewById(R.id.doctorCardSpecialityTv);
            doctorCardLocationTv = itemView.findViewById(R.id.doctorCardLocationTv);
            doctorCardFeesTv = itemView.findViewById(R.id.doctorCardFeesTv);
        }

        void bindDoctor(Doctor doctor) {
            doctorNameCardTv.setText(doctor.getName());
            doctorCardSpecialityTv.setText(doctor.getSpeciality());
            doctorCardLocationTv.setText(doctor.getAddressInfo().getAddress());
            doctorCardFeesTv.setText(String.valueOf(doctor.getPrice()));
          Glide.with(context).load(doctor.getPhotoURL()).into(profileImg);



        }
    }
}