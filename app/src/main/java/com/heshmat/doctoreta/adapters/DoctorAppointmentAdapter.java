package com.heshmat.doctoreta.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.LoadingDialog;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.activities.DoctorVideoCallActivity;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.DoctorReservation;
import com.heshmat.doctoreta.models.Reservation;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.patientui.AppointmentsActivity;
import com.heshmat.doctoreta.utils.FormattingDate;
import com.heshmat.doctoreta.utils.NTPServerConnect;
import com.heshmat.doctoreta.utils.NTPServerListener;
import com.heshmat.doctoreta.utils.RequestPermissions;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DoctorAppointmentAdapter extends FirestoreRecyclerAdapter<DoctorReservation, DoctorAppointmentAdapter.AppointmentViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;

    public DoctorAppointmentAdapter(@NonNull FirestoreRecyclerOptions<DoctorReservation> options, Context context) {
        super(options);
        this.context = context;


    }

    @Override
    protected void onBindViewHolder(@NonNull DoctorAppointmentAdapter.AppointmentViewHolder holder, int position, @NonNull DoctorReservation model) {
        holder.userNameAppointmentTv.setText(model.getClientName());
        holder.specialityLL.setVisibility(View.GONE);
        holder.dateAppointmentTv.setText(model.getDate());
        holder.hourAppointmentTv.setText(model.getTime());
        holder.priceLL.setVisibility(View.GONE);
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
                                                Intent intent = new Intent(context, DoctorVideoCallActivity.class);
                                                intent.putExtra("ID", model.getClientId());
                                                context.startActivity(intent);
                                            }
                                            else if (startAndEndCal!=null && !Reservation.hasMeetingStarted(now,startAndEndCal)&& !Reservation.hasMeetingFinished(now, startAndEndCal[1])){
                                                new AlertDialog.Builder(context).setMessage(context.getString(R.string.meeting_hasnt_start))
                                                        .setPositiveButton(context.getText(R.string.ok), null)
                                                        .show();
                                            }
                                            else if (startAndEndCal != null &&Reservation.hasMeetingFinished(now,startAndEndCal[1])){
                                                new AlertDialog.Builder(context).setMessage(context.getString(R.string.doctor_forogt_meeting))
                                                        .setPositiveButton(context.getText(R.string.ok), null)
                                                        .show();
                                                DatabaseInstance.getInstance().collection(StaticFields.DOCTORS).document(Doctor.currentLoggedDoctor.getId())
                                                        .collection(StaticFields.RESERVATIONS).document(model.getReservationId()).update("status",StaticFields.RESERVATION_STATUS_TERMINATED);
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
        String imgUrl = String.format("profileImages/clients/%s/%s", model.getClientId(), model.getClientId());
        Log.i("IMGURL", imgUrl);
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
    public ObservableSnapshotArray<DoctorReservation> getSnapshots() {
        if (super.getSnapshots().isEmpty()) {
            new AlertDialog.Builder(context).setMessage("Empty").show();

        }
        return super.getSnapshots();

    }

    @NonNull
    @Override
    public DoctorAppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_layout, parent, false);
        return new DoctorAppointmentAdapter.AppointmentViewHolder(view);
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView userNameAppointmentTv, dateAppointmentTv, hourAppointmentTv;
        CircularImageView appointmentIv;
        Button button;
        LinearLayout priceLL;
        LinearLayout specialityLL;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameAppointmentTv = itemView.findViewById(R.id.userNameAppointmentTv);
            dateAppointmentTv = itemView.findViewById(R.id.dateAppointmentTv);
            hourAppointmentTv = itemView.findViewById(R.id.hourAppointmentTv);
            appointmentIv = itemView.findViewById(R.id.appointmentIv);
            button = itemView.findViewById(R.id.startMeetingBt);
            specialityLL = itemView.findViewById(R.id.appointmentSpecialityLL);
            priceLL = itemView.findViewById(R.id.appointmentFeesLL);
        }

    }


}
