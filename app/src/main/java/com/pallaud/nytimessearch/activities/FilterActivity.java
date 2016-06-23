package com.pallaud.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.pallaud.nytimessearch.R;
import com.pallaud.nytimessearch.SearchFilter;
import com.pallaud.nytimessearch.extra.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    @BindView(R.id.etDatePicker) EditText etDatePicker;
    @BindView(R.id.mySpinner) Spinner mySpinner;
    SearchFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        filter = new SearchFilter(null,null,new ArrayList<String>());
        ButterKnife.bind(this);
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        Log.d("calendar",String.valueOf(monthOfYear));
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        etDatePicker.setText(monthOfYear + "/" + (dayOfMonth+1) + "/" + year);

        SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy");
        Date newDate = null;
        try {
            newDate = format.parse(etDatePicker.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("yyyymmdd");
        String date = format.format(newDate);
        Log.d("DATE","date:" + date);
        Log.d("DATE","newDate:" + newDate);
        filter.setBegin_date(date);
    }

    public void getSpinner () {
        if(!mySpinner.getSelectedItem().toString().equals("None")) {
            filter.setSort(mySpinner.getSelectedItem().toString());
        }
    }

    public void onCheckboxClicked(View view) {
        // if checkbox is checked, adds the string from that checkbox to list, otherwise if unchecked, removes it
        boolean checked = ((CheckBox) view).isChecked();
        CheckBox cb = (CheckBox) findViewById(view.getId());
        if(checked) {
            ArrayList<String> newsDeskOpts = filter.getNewsDeskOpts();
            newsDeskOpts.add(cb.getText().toString());
            filter.setNewsDeskOpts(newsDeskOpts);
        } else {
            ArrayList<String> newsDeskOpts = filter.getNewsDeskOpts();
            newsDeskOpts.remove(cb.getText().toString());
            filter.setNewsDeskOpts(newsDeskOpts);
        }
    }

    public void onSubmit(View v) {
        getSpinner();

        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("sort", filter.getSort());
        data.putExtra("begin_date", filter.getBegin_date());
        data.putExtra("newsDesk", filter.getNewsDeskOpts());

        setResult(RESULT_OK, data);
        finish();
    }


}
