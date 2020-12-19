package com.heshmat.doctoreta.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
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
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.DoctorReservation;
import com.mikhaellopez.circularimageview.CircularImageView;

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
        String imgUrl=String.format("profileImages/clients/%s/%s",model.getClientId(),model.getClientId());
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
            button=itemView.findViewById(R.id.startMeetingBt);
            specialityLL=itemView.findViewById(R.id.appointmentSpecialityLL);
            priceLL=itemView.findViewById(R.id.appointmentFeesLL);
        }

    }


}
