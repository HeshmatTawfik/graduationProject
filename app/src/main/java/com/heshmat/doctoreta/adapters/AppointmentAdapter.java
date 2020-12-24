package com.heshmat.doctoreta.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.LoadingDialog;
import com.heshmat.doctoreta.R;

import com.heshmat.doctoreta.activities.DoctorVideoCallActivity;
import com.heshmat.doctoreta.activities.PatientVideoCallActivity;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.PatientReservation;
import com.heshmat.doctoreta.models.Reservation;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.models.User;
import com.heshmat.doctoreta.patientui.AppointmentsActivity;
import com.heshmat.doctoreta.utils.FormattingDate;
import com.heshmat.doctoreta.utils.NTPServerConnect;
import com.heshmat.doctoreta.utils.NTPServerListener;
import com.heshmat.doctoreta.utils.RequestPermissions;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Calendar;
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
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RequestPermissions.requestLocationPermission((AppointmentsActivity) context)) {
                    LoadingDialog loadingDialog=new LoadingDialog((AppointmentsActivity) context);
                    loadingDialog.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {



                            new NTPServerConnect(new NTPServerListener() {
                                @Override
                                public void onInternetConnect(Long timeInMs) {
                                    loadingDialog.dismissDialog();
                                    if (timeInMs != -1) {
                                        Calendar now = Calendar.getInstance();
                                        now.setTimeInMillis(timeInMs);
                                        try {
                                            Calendar startAndEndCal[] = FormattingDate.reservationDateFormatted(model.getDate(), model.getTime());

                                            if (startAndEndCal != null && Reservation.hasMeetingStarted(now, startAndEndCal) && !Reservation.hasMeetingFinished(now, startAndEndCal[1])) {
                                                Intent intent = new Intent(context, PatientVideoCallActivity.class);
                                                intent.putExtra("ID", model.getDoctorId());
                                                context.startActivity(intent);
                                            }
                                            else if (startAndEndCal!=null && !Reservation.hasMeetingStarted(now,startAndEndCal)&& !Reservation.hasMeetingFinished(now, startAndEndCal[1])){
                                                new AlertDialog.Builder(context).setMessage(context.getString(R.string.meeting_hasnt_start))
                                                        .setPositiveButton(context.getText(R.string.ok), null)
                                                        .show();
                                            }
                                            else if (startAndEndCal != null &&Reservation.hasMeetingFinished(now,startAndEndCal[1])){
                                                new AlertDialog.Builder(context).setMessage(context.getString(R.string.patient_forogt_meeting))
                                                        .setPositiveButton(context.getText(R.string.ok), null)
                                                        .show();
                                             //   model.setStatus(StaticFields.RESERVATION_STATUS_TERMINATED);
                                                DatabaseInstance.getInstance().collection(StaticFields.CLIENTS).document(User.currentLoggedUser.getId())

                                                        .collection(StaticFields.RESERVATIONS).document(model.getReservationId()).update("status",StaticFields.RESERVATION_STATUS_TERMINATED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()){
                                                     String s=       task.getException().toString();
                                                        }
                                                    }
                                                });
                                            }
                                        } catch (Exception e) {

                                        }

                                    } else {
                                        new AlertDialog.Builder(context).setMessage(context.getString(R.string.check_ur_internet))
                                                .setPositiveButton(context.getText(R.string.ok), null)
                                                .show();
                                    }
                                }
                            }).execute();

                        }


                    }, 3000);




                }
            }
        });
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
