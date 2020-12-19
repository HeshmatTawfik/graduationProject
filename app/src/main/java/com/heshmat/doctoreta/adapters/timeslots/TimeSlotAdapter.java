package com.heshmat.doctoreta.adapters.timeslots;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heshmat.doctoreta.activities.SignInUpActivity;
import com.heshmat.doctoreta.models.TimeSlot;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.patientui.PaymentActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {
    private final double price;
    private List<TimeSlot> timeSlots;
    private List<HashMap<String, HashMap<String, String>>> doctorList;
    Context context;
    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;
    private RecyclerView recyclerView;
   private String doctorID;
    private String doctorName;
    private String doctorEmail;
    private String speciality;
    private static final String DOCTOR_ID = "DOCTOR_ID";
    private static final String DOCTOR_NAME = "DOCTOR_NAME";
    private static final String DOCTOR_EMAIL = "DOCTOR_EMAIL";
    private static final String PRICE = "PRICE";
    private static final String RESERVATION_DATE="DATE";
    private static final String RESERVATION_HOUR="HOUR";
    private static final String SPECIALITY = "SPECIALITY";




    public TimeSlotAdapter(List<HashMap<String, HashMap<String, String>>> doctorList, Context context,String doctorID,String doctorName,String doctorEmail,
                           double price,String speciality) {
        this.doctorID=doctorID;
        this.doctorName=doctorName;
        this.doctorEmail=doctorEmail;
        this.price=price;
        this.speciality=speciality;
        this.doctorList = doctorList;
        if (!doctorList.isEmpty())
            this.timeSlots = TimeSlot.timeSlots(doctorList.get(0));
        else
            this.timeSlots = new ArrayList<>();
        Collections.sort(timeSlots, new Comparator<TimeSlot>() {
            @Override
            public int compare(TimeSlot o1, TimeSlot o2) {
                if (o1 != null && o2 != null)
                    return o1.getDate().compareTo(o2.getDate());
                else
                    return 0;

            }
        });
        this.context = context;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slots_card, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {

        final boolean isExpanded = position == mExpandedPosition;

        //This line hides or shows the layout in question
        holder.linearlayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.imageButton.setImageResource(isExpanded ? R.drawable.ic_expnad_less : R.drawable.ic_expand_more);

        // I do not know what the heck this is :)
        holder.itemView.setActivated(isExpanded);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if the clicked item is already expaned then return -1
//else return the position (this works with notifyDatasetchanged )
                mExpandedPosition = isExpanded ? -1 : position;
                // fancy animations can skip if like
                TransitionManager.beginDelayedTransition(recyclerView);
                //This will call the onBindViewHolder for all the itemViews on Screen
                notifyDataSetChanged();
            }
        });
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mExpandedPosition = isExpanded ? -1 : position;
                TransitionManager.beginDelayedTransition(recyclerView);
                notifyDataSetChanged();
            }
        });
        TimeSlot timeSlot = timeSlots.get(position);
        holder.bindDoctor(timeSlot);

    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    class TimeSlotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dateTimeSlotTv;
        ImageButton imageButton;
        CardView cardView;
        LinearLayout linearlayout;

        TimeSlotViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            dateTimeSlotTv = itemView.findViewById(R.id.dateTimeSlotTv);
            imageButton = itemView.findViewById(R.id.expandTimeSlotIB);
            cardView = itemView.findViewById(R.id.timeSlotCardView);
            linearlayout = itemView.findViewById(R.id.timeLL);

        }

        void bindDoctor(TimeSlot timeSlot) {
            dateTimeSlotTv.setText(timeSlot.getDate());


            ArrayList<String> list = new ArrayList<String>(timeSlot.getSlots().keySet());
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if (o1 != null && o2 != null) {
                        return o1.compareTo(o2);
                    } else {
                        return 0;
                    }

                }
            });
            linearlayout.removeAllViews();
            LinearLayout horiLL = new LinearLayout(context);
            int i = 0;
            for (; i < list.size(); i++) {
                int j = i;
                for (; j < i + 3; j++) {
                    if (j < list.size()) {
                        Button textView = new Button(new ContextThemeWrapper(context, R.style.time_slot_tv_style), null, 0);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0F);
                        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
                        params.setMargins(margin, margin, margin, margin);
                        params.setMargins(margin, margin, margin, margin);
                        textView.setLayoutParams(params);
                        textView.setText(list.get(j));
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(context, PaymentActivity.class);
                                intent.putExtra(DOCTOR_ID,doctorID);
                                intent.putExtra(DOCTOR_NAME,doctorName);
                                intent.putExtra(DOCTOR_EMAIL,doctorEmail);
                                intent.putExtra(PRICE,price);
                                intent.putExtra(RESERVATION_DATE,timeSlot.getDate() );
                                intent.putExtra(RESERVATION_HOUR,textView.getText());
                                intent.putExtra(SPECIALITY,speciality);
                                context.startActivity(intent);




                                Toast.makeText(context, doctorID+" "+timeSlot.getDate() + " " + textView.getText(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        horiLL.addView(textView);

                    }

                }

                if (horiLL.getChildCount() > 0)
                    linearlayout.addView(horiLL);
                horiLL = new LinearLayout(context);
                i = j - 1;

            }


        }

        @Override
        public void onClick(View v) {


        }
    }


    public void update(List<HashMap<String, HashMap<String, String>>> doctorList) {
        if (!doctorList.isEmpty()) {
            timeSlots.clear();
            List<TimeSlot> ss = TimeSlot.timeSlots(doctorList.get(0));
            timeSlots.addAll(ss);
            Collections.sort(timeSlots, new Comparator<TimeSlot>() {
                @Override
                public int compare(TimeSlot o1, TimeSlot o2) {
                    if (o1 != null && o2 != null)
                        return o1.getDate().compareTo(o2.getDate());
                    else
                        return 0;

                }
            });
            this.notifyDataSetChanged();


        }

    }

    public void updateTime() {
        try {


            Calendar dateNow = Calendar.getInstance();
            Calendar reser = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            for (int i = 0; i < timeSlots.size(); i++) {
                dateNow = Calendar.getInstance();
                TimeSlot timeSlot = timeSlots.get(i);
                reser.setTime(dateFormat.parse(timeSlot.getDate()));
                String date = timeSlot.getDate();
                if (reser.get(Calendar.DAY_OF_MONTH) == dateNow.get(Calendar.DAY_OF_MONTH) && reser.get(Calendar.MONTH) == dateNow.get(Calendar.MONTH)) {

                    for (String hour : timeSlot.getSlots().keySet()) {
                        String[] startTimeStr = hour.split("-");
                        reser.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeStr[0].split(":")[0]));
                        reser.set(Calendar.MINUTE, Integer.parseInt(startTimeStr[0].split(":")[1]));
                        int hourNow = dateNow.get(Calendar.HOUR_OF_DAY);
                        int reservHour = reser.get(Calendar.HOUR_OF_DAY);
                        int minuteNow = dateNow.get(Calendar.MINUTE);
                        int reservMinute = reser.get(Calendar.MINUTE);
                        if (hourNow == reservHour) {
                            if (minuteNow > reservMinute) {
                                timeSlots.remove(i);
                                this.notifyDataSetChanged();
                            }
                        } else if (hourNow > reservHour) {
                            timeSlots.remove(i);
                            this.notifyDataSetChanged();
                        }


                        if (reser.before(dateNow.getTime())) {
                            timeSlots.remove(i);
                            this.notifyDataSetChanged();
                        }
                    }
                }
            }

        } catch (Exception e) {
            String s = e.getMessage();

        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }

}