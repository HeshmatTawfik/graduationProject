package com.heshmat.doctoreta.adapters.timeslots;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class TimeSlotGridAdapter extends BaseAdapter {
    Context  context;
    String []timeSlots;
    public TimeSlotGridAdapter(Context context,String []timeSlots) {
        this.context = context;
        this.timeSlots=timeSlots;
    }
    @Override
    public int getCount() {
        return timeSlots.length;
    }

    @Override
    public Object getItem(int position) {
        return timeSlots[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView==null){
            textView=new TextView(context);
            textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
            textView.setPadding(3, 3, 3, 3);

        }
        else
        {
            textView = (TextView) convertView;
        }
        textView.setText(timeSlots[position]);
        return textView;
    }
}
