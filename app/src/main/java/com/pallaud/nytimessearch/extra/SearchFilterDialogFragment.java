package com.pallaud.nytimessearch.extra;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.pallaud.nytimessearch.R;
import com.pallaud.nytimessearch.SearchFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pallaud on 6/23/16.
 */
public class SearchFilterDialogFragment extends DialogFragment
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.etDatePicker) EditText etDatePicker;
    @BindView(R.id.mySpinner) Spinner mySpinner;
    @BindView(R.id.btnFilter) Button btnSave;
    @BindView(R.id.cbFashion) CheckBox cbFashion;
    @BindView(R.id.cbSports) CheckBox cbSports;
    @BindView(R.id.cbPolitics) CheckBox cbPolitics;

    private SearchFilter filter;

    public SearchFilterDialogFragment() {
    }

    public static SearchFilterDialogFragment newInstance(SearchFilter filter) {
        SearchFilterDialogFragment frag = new SearchFilterDialogFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("filter", filter);
//        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container);
        ButterKnife.bind(this, view);
        // Resets filters every time filters is clicked
        filter = new SearchFilter(null,null,null,new ArrayList<String>());
        return view;
    }

    public interface OnFilterSearchListener {
        void onUpdateFilters(SearchFilter filters);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        filter = (SearchFilter) getArguments().getSerializable("filter");
        btnSave.setOnClickListener(this);
        etDatePicker.setOnClickListener(this);
        cbFashion.setOnClickListener(this);
        cbPolitics.setOnClickListener(this);
        cbSports.setOnClickListener(this);

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    // Deals with sort options
    public void getSpinner () {
        if(!mySpinner.getSelectedItem().toString().equals("None")) {
            filter.setSort(mySpinner.getSelectedItem().toString());
        }
    }

    // Deals with date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(this, 300);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        Log.d("calendar",String.valueOf(monthOfYear));
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        etDatePicker.setText((monthOfYear+1) + "/" + dayOfMonth + "/" + year);

        // reformats and sets date
        SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy");
        Date newDate = null;
        try {
            newDate = format.parse(etDatePicker.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("yyyymmdd");
        String date = format.format(newDate);
        filter.setBegin_date((date));
    }

    // if checkbox is checked, adds the string from that checkbox to list, otherwise if unchecked, removes it
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        CheckBox cb = (CheckBox) view.findViewById(view.getId());
        if(checked) {
            ArrayList<String> newsDeskOpts = filter.getNewsDeskOpts();
            newsDeskOpts.add("\"" + cb.getText().toString() + "\"");
            filter.setNewsDeskOpts(newsDeskOpts);
        } else {
            ArrayList<String> newsDeskOpts = filter.getNewsDeskOpts();
            newsDeskOpts.remove("\"" + cb.getText().toString() + "\"");
            filter.setNewsDeskOpts(newsDeskOpts);
        }
    }

    // This is the time to apply the filters by
    // sending back to the search activity
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.mySpinner):
                getSpinner();
                break;
            case (R.id.etDatePicker):
                showDatePickerDialog(v);
                break;
            case (R.id.btnFilter):
                getSpinner();
                // Return filters back to activity through the implemented listener
                OnFilterSearchListener listener = (OnFilterSearchListener) getActivity();
                listener.onUpdateFilters(filter);
                // Close the dialog to return back to the parent activity
                dismiss();
                break;
            default:
                onCheckboxClicked(v);
        }

    }
}