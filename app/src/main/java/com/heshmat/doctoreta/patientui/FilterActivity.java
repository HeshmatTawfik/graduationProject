package com.heshmat.doctoreta.patientui;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputLayout;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.adapters.SpecialityDialog;
import com.rizlee.rangeseekbar.RangeSeekBar;

import static com.heshmat.doctoreta.models.StaticFields.FEMALE;
import static com.heshmat.doctoreta.models.StaticFields.MALE;


public class FilterActivity extends AppCompatActivity {
    RangeSeekBar rangeSeekBar;
    int min=0;
    int max=2000;
    @BindView(R.id.filterSpecialityInl)
    TextInputLayout specialityInl;
    @BindView(R.id.filterCityInl)
    TextInputLayout cityInl;
    @BindView(R.id.filter_radio_group)
    RadioGroup filterRadioGroup;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        context=this;
        rangeSeekBar=findViewById(R.id.feeSeekBar);

        rangeSeekBar.setLeftText("");
        rangeSeekBar.setRightText("");
        rangeSeekBar.setCenterText("");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.specialities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialityInl.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpecialityDialog(context, getResources().getStringArray(R.array.specialities), specialityInl).show();

            }
        });
        ArrayAdapter<CharSequence> adapterCity = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityInl.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpecialityDialog(context, getResources().getStringArray(R.array.cities), cityInl).show();

            }
        });

        rangeSeekBar.setRange(0,2000,10);
        rangeSeekBar.setListenerPost(new RangeSeekBar.OnRangeSeekBarPostListener() {
            @Override
            public void onValuesChanged(float v, float v1) {
            }

            @Override
            public void onValuesChanged(int i, int i1) {
                rangeSeekBar.setLeftText(min+" UAH");
                rangeSeekBar.setRightText(max+" UAH");
                rangeSeekBar.setCenterText("");


                min=i;
                max=i1;

            }

        });
    }
    public void setFilterParams(View view){
        Intent data = new Intent();
        String speciality = specialityInl.getEditText().getText().toString().isEmpty()?null:specialityInl.getEditText().getText().toString();
        String city = cityInl.getEditText().getText().toString().isEmpty()?null:cityInl.getEditText().getText().toString();
        String gender = getGender();


        data.putExtra("fee",new int[]{min,max});
        data.putExtra("city",city);
        data.putExtra("speciality",speciality);
        data.putExtra("gender",gender);
        setResult(RESULT_OK, data);
        Log.i("filterActivity","city is "+(city==null));
        finish();
        /*
         *  spe fee city gen
         *   0   0   0   0
         *   0   0   0   1
         *   0   0   1   0
         *   0   0   1   1
         *   0   1   0   0
         *   0   1   0   1
         *   0   1   1   0
         *   0   1   1   1
         *   1   0   0   0
         *   1   0   0   1
         *   1   0   1   0
         *   1   0   1   1
         *   1   1   0   0
         *   1   1   0   1
         *   1   1   1   0
         *   1   1   1   1
         * */


    }
    private   String getGender(){
        if(filterRadioGroup.getCheckedRadioButtonId()==R.id.filter_radio_button_male)
            return MALE;
        else if ((filterRadioGroup.getCheckedRadioButtonId()==R.id.filter_radio_button_female))
            return FEMALE;
        else
            return null;
    }
}
