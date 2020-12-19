package com.heshmat.doctoreta.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.heshmat.doctoreta.R;

import com.heshmat.doctoreta.models.PatientReservation;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppointmentAdapter extends FirestoreRecyclerAdapter<PatientReservation, AppointmentAdapter.AppointmentViewHolder> {
    @Override
    public void updateOptions(@NonNull FirestoreRecyclerOptions<PatientReservation> options) {
        super.updateOptions(options);
    }

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;

    public AppointmentAdapter(@NonNull FirestoreRecyclerOptions<PatientReservation> options, Context context) {
        super(options);
        this.context = context;


    }

    @Override
    protected void onBindViewHolder(@NonNull AppointmentAdapter.AppointmentViewHolder holder, int position, @NonNull PatientReservation model) {
        holder.userNameAppointmentTv.setText(model.getDoctorName());
        holder.userSpecialityAppointmentTv.setText(model.getSpeciality());
        holder.dateAppointmentTv.setText(model.getDate());
        holder.hourAppointmentTv.setText(model.getTime());
        holder.priceTv.setText(String.format(Locale.ENGLISH,"% .1f UAH",model.getPrice()));
        holder.dateAppointmentTv.setText(model.getDate());
        String imgUrl=String.format("doctorDocuments/doctors/%s/%sProfileImg",model.getDoctorId(),model.getDoctorId());
        FirebaseStorage.getInstance().getReference(imgUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri.toString()).into(holder.appointmentIv);

            }
        });


    }


    @Override
    public void onDataChanged() {
        if (getItemCount() == 0) {
            new AlertDialog.Builder(context).setMessage(context.getString(R.string.no_appointments))
                    .setPositiveButton(context.getText(R.string.ok), null).show();
        }
        super.onDataChanged();
    }

    @NonNull
    @Override
    public ObservableSnapshotArray<PatientReservation> getSnapshots() {
        if (super.getSnapshots().isEmpty()) {
            new AlertDialog.Builder(context).setMessage("Empty").show();

        }
        return super.getSnapshots();

    }

    @NonNull
    @Override
    public AppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_layout, parent, false);
        return new AppointmentAdapter.AppointmentViewHolder(view);
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView userNameAppointmentTv, userSpecialityAppointmentTv, dateAppointmentTv, hourAppointmentTv,priceTv;
        CircularImageView appointmentIv;
        Button button;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameAppointmentTv = itemView.findViewById(R.id.userNameAppointmentTv);
            userSpecialityAppointmentTv = itemView.findViewById(R.id.userSpecialityAppointmentTv);
            dateAppointmentTv = itemView.findViewById(R.id.dateAppointmentTv);
            hourAppointmentTv = itemView.findViewById(R.id.hourAppointmentTv);
            appointmentIv = itemView.findViewById(R.id.appointmentIv);
            button=itemView.findViewById(R.id.startMeetingBt);
            priceTv=itemView.findViewById(R.id.userFeesAppointmentTv);
        }

    }


}
