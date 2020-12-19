package com.heshmat.doctoreta.patientui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.LoadingDialog;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.DoctorReservation;
import com.heshmat.doctoreta.models.PatientReservation;
import com.heshmat.doctoreta.models.Reservation;
import com.heshmat.doctoreta.models.StaticFields;
import com.heshmat.doctoreta.models.User;
import com.heshmat.doctoreta.services.FirebaseFunction;
import com.heshmat.doctoreta.utils.RequestPermissions;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;
import com.stripe.android.view.CardMultilineWidget;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {
    private static final String DOCTOR_ID = "DOCTOR_ID";
    private static final String DOCTOR_NAME = "DOCTOR_NAME";
    private static final String DOCTOR_EMAIL = "DOCTOR_EMAIL";
    private static final String PRICE = "PRICE";
    private static final String RESERVATION_DATE = "DATE";
    private static final String RESERVATION_HOUR = "HOUR";
    private static final String SPECIALITY = "SPECIALITY";

    String getDoctorId;
    String getDoctorName;
    String getDoctorEmail;
    String getReservationDate;
    String getReservationHour;
    String getSpeciality;
    double getPrice;
    @BindView(R.id.card_multiline_widget)
    public CardMultilineWidget multilineWidget;
    Stripe stripe;
    Card card;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        getDoctorId = intent.getStringExtra(DOCTOR_ID);
        getDoctorName = intent.getStringExtra(DOCTOR_NAME);
        getDoctorEmail = intent.getStringExtra(DOCTOR_EMAIL);
        getReservationDate = intent.getStringExtra(RESERVATION_DATE);
        getReservationHour = intent.getStringExtra(RESERVATION_HOUR);
        getPrice = intent.getDoubleExtra(PRICE, -1);
        getSpeciality = intent.getStringExtra(SPECIALITY);
        multilineWidget.setPostalCodeRequired(false);
        stripe = new Stripe(this, "pk_test_51HyEB9Aa4NYHzz70UpgVUVSLgWwrepo9qL0XmXoBNGWgyuRwVmQfa8rYtrIBsjHdYOljkgQzWIm9gEjFXNLSNn8u00XvSIC2vy");
        multilineWidget.setShouldShowPostalCode(false);
        changeSlotStatus(String.valueOf(System.currentTimeMillis()));


        /* */


    }

    @OnClick(R.id.buy_button)
    public void purchase(View view) {
        if (RequestPermissions.requestLocationPermission(this)) {// check for location permission
            card = multilineWidget.getCard();
            if (card != null) { //check card info
                final LoadingDialog loadingDialog = new LoadingDialog(this);
                loadingDialog.startLoadingDialog();
                final SourceParams cardSourceParams = SourceParams.createCardParams(card)
                        .setAmount((long) getPrice * 100).setCurrency("UAH").setOwner(
                                new SourceParams.OwnerParams().copy(
                                        null, User.currentLoggedUser.getEmail(),
                                        User.currentLoggedUser.getName(), User.currentLoggedUser.getPhoneNumber()
                                )
                        );
                stripe.createSource(
                        cardSourceParams,
                        new ApiResultCallback<Source>() {
                            @Override
                            public void onSuccess(@NonNull Source source) {
                                try {
                                    com.heshmat.doctoreta.models.stripemodels.Source sourceModel =
                                            new com.heshmat.doctoreta.models.stripemodels.Source(source, card, source.getOwner());
                                    ObjectMapper oMapper = new ObjectMapper();
                                    oMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                                    Map<String, Object> sourceModelMap = oMapper.convertValue(sourceModel, Map.class);
                                    Task<HashMap<String, Object>> s = FirebaseFunction.callFirebaseFunction("stripeChargeCall",sourceModelMap);
                                    s.addOnSuccessListener(new OnSuccessListener<HashMap<String, Object>>() {
                                        @Override
                                        public void onSuccess(HashMap<String, Object> stringObjectHashMap) {
                                            loadingDialog.dismissDialog();
                                            for (String key : stringObjectHashMap.keySet()) {
                                                if (Objects.equals(stringObjectHashMap.get(key), "SUCCESSFUL")) {
                                                    changeSlotStatus(StaticFields.RESERVED);
                                                    long createdAt = System.currentTimeMillis();
                                                    DoctorReservation doctorReservation = new DoctorReservation(User.currentLoggedUser.getId(), User.currentLoggedUser.getName(),
                                                            User.currentLoggedUser.getEmail(), User.currentLoggedUser.getPhoneNumber(),
                                                            getReservationDate, getReservationHour, StaticFields.RESERVATION_STATUS_WAITING, createdAt);
                                                    addBooking(StaticFields.DOCTORS, getDoctorId, doctorReservation);
                                                    PatientReservation patientReservation = new PatientReservation(getDoctorId, getDoctorName,  getSpeciality, getReservationDate,
                                                            getReservationHour, StaticFields.RESERVATION_STATUS_WAITING, getPrice, createdAt
                                                    );
                                                    addBooking(StaticFields.CLIENTS, User.currentLoggedUser.getId(), patientReservation);


                                                } else {// payment failed

                                                }

                                            }


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loadingDialog.dismissDialog();
                                            //   changeSlotStatus(StaticFields.FREE_SLOT);


                                        }
                                    });
                                } catch (Exception e) {

                                }


                                // Store the source somewhere, use it, etc
                            }

                            @Override
                            public void onError(@NonNull Exception error) {
                                loadingDialog.dismissDialog();
                                changeSlotStatus(StaticFields.FREE_SLOT);

                                // Tell the user that something went wrong
                            }
                        });


            } else { // invalid card info
                new AlertDialog.Builder(this).setMessage("Please check your card details,invalid card information")
                        .setPositiveButton(getString(R.string.ok), null).show();
            }

        } else {// location permission denied

        }

        //  changeSlotStatus("reserved");
    }

    private Task changeSlotStatus(String status) {
        Task<DocumentSnapshot> task = DatabaseInstance.getInstance().collection(StaticFields.RESERVATIONS).document(getDoctorId).get();
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {

                    HashMap<String, HashMap<String, String>> doctorSlots = (HashMap<String, HashMap<String, String>>) documentSnapshot.get("dates");
                    if (doctorSlots != null) {
                        Objects.requireNonNull(doctorSlots.get(getReservationDate)).put(getReservationHour, String.valueOf(status));
                        DatabaseInstance.getInstance().collection(StaticFields.RESERVATIONS).document(getDoctorId)
                                .update("dates", doctorSlots);


                    }

                }
            }
        });
        return task;
    }

    public void addBooking(String path, String id, Reservation reservation) {
        DatabaseInstance.getInstance().collection(path).document(id).collection(StaticFields.RESERVATIONS).document(reservation.getReservationId())
                .set(reservation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (path.equals(StaticFields.CLIENTS)) {
                    new AlertDialog.Builder(PaymentActivity.this).setMessage("Booking was reserved, we are waiting for you ")
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent(PaymentActivity.this, AppointmentsActivity.class);
                                    startActivity(intent);
                                    intent.putExtra("ROLE",StaticFields.PATIENT_ROLE);
                                    finish();
                                }
                            }).show();
                }

            }
        });
    }

}
