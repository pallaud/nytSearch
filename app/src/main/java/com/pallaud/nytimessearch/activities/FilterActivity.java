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
import android.widget.RadioButton;

import com.pallaud.nytimessearch.R;
import com.pallaud.nytimessearch.extra.DatePickerFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String sort;
    String begin_date;
    ArrayList<String> newsDeskOpts;
    EditText etDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        etDatePicker = (EditText) findViewById(R.id.etDatePicker);
        newsDeskOpts = new ArrayList<String>();
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
        etDatePicker.setText(monthOfYear + "/" + dayOfMonth + "/" + year);

//        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
//        Date newDate = format.parse(""+year+monthOfYear+dayOfMonth);
//
//        format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
//        String date = format.format(newDate);
//        begin_date = ;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbOldest:
                if (checked)
                    sort = "oldest";
                    break;
            case R.id.rbNewest:
                if (checked)
                    sort = "newest";
                    break;
        }
    }

    public void onCheckboxClicked(View view) {
        // if checkbox is checked, adds the string from that checkbox to list, otherwise if unchecked, removes it
        boolean checked = ((CheckBox) view).isChecked();
        CheckBox cb = (CheckBox) findViewById(view.getId());
        if(checked) {
            newsDeskOpts.add(cb.getText().toString());
        } else {
            newsDeskOpts.remove(cb.getText());
        }
    }

    public void onSubmit(View v) {

        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("sort", sort);
        data.putExtra("begin_date", begin_date);
        data.putExtra("newsDesk", newsDeskOpts);

        setResult(RESULT_OK, data);
        finish();
    }


}
