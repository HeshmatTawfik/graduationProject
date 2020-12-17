package com.heshmat.doctoreta.pagination;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.patientui.HomeActivity;
import com.heshmat.doctoreta.patientui.ReservationFragment;
import com.heshmat.doctoreta.patientui.SearchDoctorFragment;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    class DoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircularImageView profileImg;

        TextView doctorNameCardTv,doctorCardSpecialityTv,doctorCardLocationTv,doctorCardFeesTv,available;
        DoctorViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            profileImg=itemView.findViewById(R.id.doctorCardIv);
            doctorNameCardTv = itemView.findViewById(R.id.doctorNameCardTv);
            doctorCardSpecialityTv = itemView.findViewById(R.id.doctorCardSpecialityTv);
            doctorCardLocationTv = itemView.findViewById(R.id.doctorCardLocationTv);
            doctorCardFeesTv = itemView.findViewById(R.id.doctorCardFeesTv);
            available = itemView.findViewById(R.id.avaiableTextView);

        }

        void bindDoctor(Doctor doctor) {
            doctorNameCardTv.setText(doctor.getName());
            doctorCardSpecialityTv.setText(doctor.getSpeciality());
            doctorCardLocationTv.setText(doctor.getAddressInfo().getAddress());
            doctorCardFeesTv.setText(String.valueOf(doctor.getPrice()));
          Glide.with(context).load(doctor.getPhotoURL()).into(profileImg);

            DatabaseInstance.getInstance().collection("reservations").document(doctor.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists() && value.contains("dates")) {
                        HashMap<String, HashMap<String, String>> doctorSchedule = (HashMap<String, HashMap<String, String>>) value.get("dates");
                        if (doctorSchedule != null) {
                            String t=avaiableTime(doctorSchedule);
                            if (!t.equals("")){
                                available.setVisibility(View.VISIBLE);
                                available.setText("Available "+t);
                            }

                        }


                    }

                }
            });

        }

        @Override
        public void onClick(View v) {
            Doctor doctor=doctorList.get(getAdapterPosition());
            HomeActivity activity = (HomeActivity) context;
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragmentContainer, ReservationFragment.newInstance(doctor.getId())).addToBackStack(ReservationFragment.class.getName())
                    .commit();

        }
    }
    private String avaiableTime(HashMap<String, HashMap<String, String>> doctorSchedule) {
           Calendar dateNow = Calendar.getInstance();
        Calendar reser = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        for (String s : doctorSchedule.keySet()) {
            try {
                reser.setTime(dateFormat.parse(s));
                //check date is today
                if (reser.get(Calendar.DAY_OF_MONTH) == dateNow.get(Calendar.DAY_OF_MONTH) && reser.get(Calendar.MONTH) == dateNow.get(Calendar.MONTH)) {
                    for (String hour : doctorSchedule.get(s).keySet()) {
                        String[] startTimeStr = hour.split("-");
                        reser.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeStr[0].split(":")[0]));
                        reser.set(Calendar.MINUTE, Integer.parseInt(startTimeStr[0].split(":")[1]));
                        if (reser.after(dateNow)) {
                            if (doctorSchedule.get(s).get(hour).equals("free"))
                                return "Today";
                        }

                    }

                } else if (reser.get(Calendar.DAY_OF_MONTH) == dateNow.get(Calendar.DAY_OF_MONTH) + 1 && reser.get(Calendar.MONTH) == dateNow.get(Calendar.MONTH)) {
                    return "Tommorow";
                }


            } catch (Exception e) {

            }
        }

        return "";
    }
}