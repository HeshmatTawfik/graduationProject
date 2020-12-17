package com.heshmat.doctoreta.doctorui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.heshmat.doctoreta.DatabaseInstance;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.StaticFields;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class SetScheduleFragment extends Fragment {
    Context context;
    @BindView(R.id.linearLayoutMonday)
    public LinearLayout linearLayoutMonday;
    @BindView(R.id.switchMonday)
    public Switch switchMonday;
    @BindView(R.id.startAtMondayInL)
    TextInputLayout startAtMondayInL;
    @BindView(R.id.endAtMondayInL)
    TextInputLayout endAtMondayInL;
    @BindView(R.id.sessionTimeMondayInL)
    TextInputLayout sessionTimeMondayInL;
    //tuesday
    @BindView(R.id.linearLayoutTuesday)
    public LinearLayout linearLayoutTuesday;
    @BindView(R.id.switchTuesday)
    public Switch switchTuesday;
    @BindView(R.id.startAtTuesdayInL)
    TextInputLayout startAtTuesdayInL;
    @BindView(R.id.endAtTuesdayInL)
    TextInputLayout endAtTuesdayInL;
    @BindView(R.id.sessionTimeTuesdayInL)
    TextInputLayout sessionTimeTuesdayInL;
    //wednesday
    @BindView(R.id.linearLayoutWednesday)
    public LinearLayout linearLayoutWednesday;
    @BindView(R.id.switchWednesday)
    public Switch switchWednesday;
    @BindView(R.id.startAtWednesdayInL)
    TextInputLayout startAtWednesdayInL;
    @BindView(R.id.endAtWednesdayInL)
    TextInputLayout endAtWednesdayInL;
    @BindView(R.id.sessionTimeWednesdayInL)
    TextInputLayout sessionTimeWednesdayInL;
    //thursday
    @BindView(R.id.linearLayoutThursday)
    public LinearLayout linearLayoutThursday;
    @BindView(R.id.switchThursday)
    public Switch switchThursday;
    @BindView(R.id.startAtThursdayInL)
    TextInputLayout startAtThursdayInL;
    @BindView(R.id.endAtThursdayInL)
    TextInputLayout endAtThursdayInL;
    @BindView(R.id.sessionTimeThursdayInL)
    TextInputLayout sessionTimeThursdayInL;
    //friday
    @BindView(R.id.linearLayoutFriday)
    public LinearLayout linearLayoutFriday;
    @BindView(R.id.switchFriday)
    public Switch switchFriday;
    @BindView(R.id.startAtFridayInL)
    TextInputLayout startAtFridayInL;
    @BindView(R.id.endAtFridayInL)
    TextInputLayout endAtFridayInL;
    @BindView(R.id.sessionTimeFridayInL)
    TextInputLayout sessionTimeFridayInL;
    //satruday
    @BindView(R.id.linearLayoutSaturday)
    public LinearLayout linearLayoutSaturday;
    @BindView(R.id.switchSaturday)
    public Switch switchSaturday;
    @BindView(R.id.startAtSaturdayInL)
    TextInputLayout startAtSaturdayInL;
    @BindView(R.id.endAtSaturdayInL)
    TextInputLayout endAtSaturdayInL;
    @BindView(R.id.sessionTimeSaturdayInL)
    TextInputLayout sessionTimeSaturdayInL;
    //sunday
    @BindView(R.id.linearLayoutSunday)
    public LinearLayout linearLayoutSunday;
    @BindView(R.id.switchSunday)
    public Switch switchSunday;
    @BindView(R.id.startAtSundayInL)
    TextInputLayout startAtSundayInL;
    @BindView(R.id.endAtSundayInL)
    TextInputLayout endAtSundayInL;
    @BindView(R.id.sessionTimeSundayInL)
    TextInputLayout sessionTimeSundayInL;

    HashMap<String, ArrayList<String>> workingDaysPeriods;
    TextInputLayout[] startTextInputLayoutArr;
    TextInputLayout[] endTextInputLayoutArr;
    TextInputLayout[] durationTextInputLayoutArr;
    Switch[] switchesArr;
    String[] daysOfWeek;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SetScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetScheduleFragment newInstance(String param1, String param2) {
        SetScheduleFragment fragment = new SetScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SetScheduleFragment newInstance() {

        return new SetScheduleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static HashMap<String, ArrayList<String>> workingPeriods;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_schedule, container, false);
        ButterKnife.bind(this, view);
        context = this.getContext();
        workingDaysPeriods = new HashMap<String, ArrayList<String>>();

        startTextInputLayoutArr = new TextInputLayout[]{startAtMondayInL, startAtTuesdayInL, startAtWednesdayInL, startAtThursdayInL, startAtFridayInL, startAtSaturdayInL, startAtSundayInL};
        endTextInputLayoutArr = new TextInputLayout[]{endAtMondayInL, endAtTuesdayInL, endAtWednesdayInL, endAtThursdayInL, endAtFridayInL, endAtSaturdayInL, endAtSundayInL};
        durationTextInputLayoutArr = new TextInputLayout[]{sessionTimeMondayInL, sessionTimeTuesdayInL, sessionTimeWednesdayInL, sessionTimeThursdayInL, sessionTimeFridayInL, sessionTimeSaturdayInL, sessionTimeSundayInL};
        switchesArr = new Switch[]{switchMonday, switchTuesday, switchWednesday, switchThursday, switchFriday, switchSaturday, switchSunday};
        daysOfWeek = new String[]{getString(R.string.monday), getString(R.string.tuesday), getString(R.string.wednesday), getString(R.string.thursday), getString(R.string.friday), getString(R.string.saturday), getString(R.string.sunday)};

        toggleSwitch(switchMonday, linearLayoutMonday, startAtMondayInL, endAtMondayInL, sessionTimeMondayInL);
        toggleSwitch(switchTuesday, linearLayoutTuesday, startAtTuesdayInL, endAtTuesdayInL, sessionTimeTuesdayInL);
        toggleSwitch(switchThursday, linearLayoutThursday, startAtThursdayInL, endAtThursdayInL, sessionTimeThursdayInL);
        toggleSwitch(switchWednesday, linearLayoutWednesday, startAtWednesdayInL, endAtWednesdayInL, sessionTimeWednesdayInL);
        toggleSwitch(switchFriday, linearLayoutFriday, startAtFridayInL, endAtFridayInL, sessionTimeFridayInL);
        toggleSwitch(switchSaturday, linearLayoutSaturday, startAtSaturdayInL, endAtSaturdayInL, sessionTimeSaturdayInL);
        toggleSwitch(switchSunday, linearLayoutSunday, startAtSundayInL, endAtSundayInL, sessionTimeSundayInL);


        return view;
    }

    public void toggleSwitch(Switch sw, LinearLayout linearLayout, TextInputLayout start, TextInputLayout end, TextInputLayout duration) {
        final Calendar[] startAtCalendar = {Calendar.getInstance()};
        final Calendar[] endAtCalendar = {Calendar.getInstance()};

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //user enabled the day

                    showLayout(sw, linearLayout);
                    start.getEditText().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startAtCalendar[0] = Calendar.getInstance();
                            new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    startAtCalendar[0].set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    startAtCalendar[0].set(Calendar.MINUTE, minute);
                                    start.getEditText().setText(sdf.format(startAtCalendar[0].getTimeInMillis()));
                                }
                            }, startAtCalendar[0].get(Calendar.HOUR_OF_DAY), startAtCalendar[0].get(Calendar.MINUTE), true).
                                    show();


                        }
                    });
                    end.getEditText().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            endAtCalendar[0] = Calendar.getInstance();
                            new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    endAtCalendar[0].set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    endAtCalendar[0].set(Calendar.MINUTE, minute);
                                    end.getEditText().setText(sdf.format(endAtCalendar[0].getTimeInMillis()));

                                }
                            }, endAtCalendar[0].get(Calendar.HOUR_OF_DAY), endAtCalendar[0].get(Calendar.MINUTE), true).
                                    show();


                        }
                    });


                } else { //user disable it
                    hideLayout(sw, linearLayout);
                    start.getEditText().setText("");
                    end.getEditText().setText("");
                    duration.getEditText().setText("");
                }
            }
        });


    }

    public void showLayout(Switch sw, LinearLayout linearLayout) {
        sw.getTrackDrawable().setColorFilter(ContextCompat.getColor(Objects.requireNonNull(context), R.color.secondColorAccent), PorterDuff.Mode.SRC_IN);
        sw.getThumbDrawable().setColorFilter(ContextCompat.getColor(Objects.requireNonNull(context), R.color.secondColorAccent), PorterDuff.Mode.SRC_IN);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.setAlpha(0.0f);

// Start the animation
        linearLayout.animate()
                .translationY(-1)
                .alpha(1.0f)
                .setListener(null);
        linearLayout.clearAnimation();

    }

    public void hideLayout(Switch sw, LinearLayout linearLayout) {
        sw.getTrackDrawable().setColorFilter(ContextCompat.getColor(Objects.requireNonNull(context), R.color.disable_foreground_grey), PorterDuff.Mode.SRC_IN);
        sw.getThumbDrawable().setColorFilter(ContextCompat.getColor(Objects.requireNonNull(context), R.color.disable_foreground_grey), PorterDuff.Mode.SRC_IN);
        linearLayout.animate()
                .translationY(0)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linearLayout.setVisibility(View.GONE);
                        linearLayout.clearAnimation();
                    }
                });
    }

    private boolean checkSessionDuration(TextInputLayout textInputLayout) {
        String dur = textInputLayout.getEditText().getText().toString();

        if (dur.isEmpty()) {
            textInputLayout.setError(getString(R.string.required_field));
            return false;
        }
        if (!isNumeric(dur)) {
            textInputLayout.setError("Invalid input");
            return false;

        }
        textInputLayout.setError(null);
        return true;

    }

    private boolean checkInputTime(Switch[] switches, TextInputLayout[] start, TextInputLayout[] end, TextInputLayout[] duration, String[] days) throws ParseException {
        for (int i = 0; i < switches.length; i++) {
            if (switches[i].isChecked()) {
                if (checkValidationOFTime(start[i], end[i], duration[i])) {
                    String startStr = start[i].getEditText().getText().toString();
                    String endStr = end[i].getEditText().getText().toString();
                    int durationTime = Integer.parseInt(duration[i].getEditText().getText().toString());
                    ArrayList<String> periods = getPeriods(startStr, endStr, durationTime);
                    workingDaysPeriods.put(days[i], periods.isEmpty() ? null : periods);

                } else {
                    workingDaysPeriods.clear();
                    return false;
                }

            }
            else {
                workingDaysPeriods.put(days[i],  null );

            }

        }
        return true;
    }

    public ArrayList<String> getPeriods(String startStr, String endStr, int duration) {
        ArrayList<String> periods = new ArrayList<>();
        ArrayList<Date> fromArrayList = new ArrayList<>();
        ArrayList<Date> toArrayList = new ArrayList<>();

        Calendar st = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        Calendar intervals = Calendar.getInstance();

        DateFormat format = new SimpleDateFormat("HH:mm");
        try {


            st.setTime(Objects.requireNonNull(format.parse(startStr)));
            endCal.setTime(Objects.requireNonNull(format.parse(endStr)));
            intervals.setTime(st.getTime());
            while (intervals.getTime().before(endCal.getTime())) {
                fromArrayList.add(new Date(intervals.getTimeInMillis()));
                intervals.add(Calendar.MINUTE, duration);
                String isHourmor = intervals.get(Calendar.HOUR_OF_DAY) + " " + " " + endCal.get(Calendar.HOUR_OF_DAY);

                boolean isHourMoreThanEnd = intervals.get(Calendar.HOUR_OF_DAY) > endCal.get(Calendar.HOUR_OF_DAY);
                boolean isHourEqulButMinuteMore = (intervals.get(Calendar.HOUR_OF_DAY) >= endCal.get(Calendar.HOUR_OF_DAY) && intervals.get(Calendar.MINUTE) > endCal.get(Calendar.MINUTE));

                if (isHourMoreThanEnd || isHourEqulButMinuteMore) {
                    if (fromArrayList.size() > 0)
                        fromArrayList.remove(fromArrayList.size() - 1);
                    break;
                }

                toArrayList.add(new Date(intervals.getTimeInMillis()));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            if (fromArrayList.size() == toArrayList.size())
                for (int i = 0; i < toArrayList.size(); i++) {
                    periods.add(sdf.format(fromArrayList.get(i)) + "-" + sdf.format(toArrayList.get(i)));


                }


        } catch (Exception e) {
            Log.i("getTheExc", "getPeriods: " + e.getMessage());
        }


        return periods;
    }

    private boolean checkValidationOFTime(TextInputLayout start, TextInputLayout end, TextInputLayout duration) {
        try {
            Calendar minimuimWorkTimeStart = Calendar.getInstance();
            Calendar maximumWorkTimeStart=Calendar.getInstance();
            Calendar maximumWorkTimeEnd=Calendar.getInstance();

            DateFormat format = new SimpleDateFormat("HH:mm");
            minimuimWorkTimeStart.setTime(format.parse("09:00"));
            maximumWorkTimeStart.setTime(format.parse("22:00"));
            maximumWorkTimeEnd.setTime(format.parse("23:00"));


            if (!isTimeSelected(start) || !isTimeSelected(end) || !checkSessionDuration(duration))
                return false;
            Calendar st = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            st.setTime(Objects.requireNonNull(format.parse(start.getEditText().getText().toString())));
            endCal.setTime(Objects.requireNonNull(format.parse(end.getEditText().getText().toString())));
            if (Double.parseDouble(duration.getEditText().getText().toString()) < 15) {
                duration.setError("Session duration must be at least 15 minute");
                return false;
            } else {
                duration.setError(null);

            }
            if (endCal.get(Calendar.HOUR_OF_DAY) - st.get(Calendar.HOUR_OF_DAY) < 1) {
                end.setError("end time must differ from start time with at least 1 hour");
                return false;
            } else {
                end.setError(null);

            }
            if (st.getTime().before(minimuimWorkTimeStart.getTime()))
            {
                start.setError("Minimum work start time should not  be less than 9 o'clock");
                return false;
            }
            else
            {
                start.setError(null);

            }
            if (st.getTime().after(maximumWorkTimeStart.getTime())){
                start.setError("Maximum work start time should not  be more than 22 o'clock");
                return false;
            }
            else
            {
                start.setError(null);
            }
            if (endCal.getTime().after(maximumWorkTimeEnd.getTime())){
                end.setError("Maximum work end time should not  be more than 23 o'clock");
                return false;

            }
            else {
                end.setError(null);

            }
            Calendar interval = Calendar.getInstance();
            interval.setTime(st.getTime());
            interval.add(Calendar.MINUTE, Integer.parseInt(duration.getEditText().getText().toString()));

            if (interval.getTime().after(endCal.getTime())) {
                duration.setError("The duration time is longer than shift time");
                return false;
            } else {
                duration.setError(null);

            }

            return true;
        } catch (Exception e) {
            Log.i("getTheExc", "checkValidationOFTime: ");
            return false;
        }
        // if (setT)


    }

    private boolean isTimeSelected(TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText().getText().toString().isEmpty()) {
            textInputLayout.setError(getString(R.string.required_field));
            return false;
        }
        textInputLayout.setError(null);
        return true;
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    String s = "";

    @OnClick(R.id.saveAvailabilityBt)
    public void savePeriods(View view) {
        boolean didUserChooseAtLeastOneDay=false;
        s = "";
        try {
            StringBuilder stringBuilder = new StringBuilder();

            if (checkInputTime(switchesArr, startTextInputLayoutArr, endTextInputLayoutArr, durationTextInputLayoutArr, daysOfWeek)) {
                for (String key : workingDaysPeriods.keySet()) {
                    ArrayList<String> arrayList = workingDaysPeriods.get(key);
                    stringBuilder.append(key).append(": ");
                    if (arrayList != null) {
                        didUserChooseAtLeastOneDay=true;
                        for (String s : arrayList) {
                            stringBuilder.append(s).append("|");
                            Log.i("working time ", key + ":" + s);

                        }

                    } else {
                        stringBuilder.append(key).append("not working");

                        Log.i("working time ", key + "-----");

                    }
                    stringBuilder.append("\n");

                }

            }
            if (didUserChooseAtLeastOneDay){
                DatabaseInstance.getInstance().collection(StaticFields.DOCTORS).document(Doctor.currentLoggedDoctor.getId())
                        .update("availability",workingDaysPeriods).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        workingDaysPeriods.clear();
                        new AlertDialog.Builder(context).setMessage(stringBuilder).setPositiveButton("ok", null).show();
                    }
                });

            }
            else {
                new AlertDialog.Builder(context).setMessage(getString(R.string.choose_one_day)).setPositiveButton(getString(R.string.ok),null)
                        .show();
            }

        } catch (Exception e) {
            workingDaysPeriods.clear();

            e.printStackTrace();
            Log.i("working time", "savePeriods: " + e);
        }


    }
}
