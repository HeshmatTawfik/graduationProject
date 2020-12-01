package com.heshmat.doctoreta.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.heshmat.doctoreta.R;

import java.util.Objects;

public class SpecialityDialog extends Dialog implements View.OnClickListener {

    private ListView list;
    private EditText filterText = null;
    ArrayAdapter<String> adapter = null;
    private TextInputLayout textInputLayout;
    private static final String TAG = "Speciality";
    private String selectedSpeciality;

    public SpecialityDialog(Context context, String[] cityList, TextInputLayout textInputLayout) {
        super(context);
        this.textInputLayout = textInputLayout;
        /** Design the dialog in main.xml file */
        setContentView(R.layout.specialite_layout);
        this.setTitle("Select you Speciality");
        int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, context.getResources().getDisplayMetrics());
        int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, context.getResources().getDisplayMetrics());
        Objects.requireNonNull(this.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        Dialog alertDialog = this;
        filterText = (EditText) findViewById(R.id.EditBox);
        filterText.addTextChangedListener(filterTextWatcher);
        list = (ListView) findViewById(R.id.List);
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cityList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Log.d(TAG, "Selected Item is = " + list.getItemAtPosition(position));
                // Toast.makeText(context, list.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                textInputLayout.getEditText().setText(list.getItemAtPosition(position).toString());
                alertDialog.dismiss();


            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private void setSpeciality(String speciality) {
        this.selectedSpeciality = speciality;
    }

    public String getSelected() {
        return this.selectedSpeciality;
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter.getFilter().filter(s);
        }
    };

    @Override
    public void onStop() {
        filterText.removeTextChangedListener(filterTextWatcher);
    }
}