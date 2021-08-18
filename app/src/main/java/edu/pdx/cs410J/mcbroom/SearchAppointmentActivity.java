package edu.pdx.cs410J.mcbroom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SearchAppointmentActivity extends AppCompatActivity
    implements View.OnClickListener {
    private String filename;
    private ArrayAdapter<Appointment> searchAdapter;
    Button buttonSearch;
    TextView editBeginDateSearch, editBeginTimeSearch, editEndDateSearch, editEndTimeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appointment);
        String owner = getIntent().getStringExtra("owner");
        setTitle(String.format("Search appointments for %s", owner));
        filename = String.format("%s.txt", owner);
        searchAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        ListView listView = findViewById(R.id.listOfAppointments);
        listView.setAdapter(searchAdapter);

        buttonSearch = findViewById(R.id.buttonSearch);
        editBeginDateSearch = findViewById(R.id.editBeginDateSearch);
        editBeginTimeSearch = findViewById(R.id.editBeginTimeSearch);
        editEndDateSearch = findViewById(R.id.editEndDateSearch);
        editEndTimeSearch = findViewById(R.id.editEndTimeSearch);

        buttonSearch.setOnClickListener(this);
        editBeginDateSearch.setOnClickListener(this);
        editBeginTimeSearch.setOnClickListener(this);
        editEndDateSearch.setOnClickListener(this);
        editEndTimeSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editBeginDateSearch:
                setTextViewWithDateDialog(editBeginDateSearch);
                break;
            case R.id.editBeginTimeSearch:
                setTextViewWithTimeDialog(editBeginTimeSearch);
                break;
            case R.id.editEndDateSearch:
                setTextViewWithDateDialog(editEndDateSearch);
                break;
            case R.id.editEndTimeSearch:
                setTextViewWithTimeDialog(editEndTimeSearch);
                break;
            case R.id.buttonSearch:
                searchBetweenTimes();
                break;
        }
    }

    protected void searchBetweenTimes() {
        String beginDate = ((TextView) findViewById(R.id.editBeginDateSearch))
                .getText().toString();
        String beginTime = ((TextView) findViewById(R.id.editBeginTimeSearch))
                .getText().toString();
        String endDate = ((TextView) findViewById(R.id.editEndDateSearch))
                .getText().toString();
        String endTime = ((TextView) findViewById(R.id.editEndTimeSearch))
                .getText().toString();

        if (beginDate.equals("") || beginTime.equals("")
                || endDate.equals("") || endTime.equals("")) {
            Toast.makeText(this, "No fields may be empty before submission", Toast.LENGTH_LONG).show();
            return;
        }

        Date searchBegin = null, searchEnd = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            searchBegin = sdf.parse(beginDate + " " + beginTime);
            searchEnd = sdf.parse(endDate + " " + endTime);
        }
        catch(ParseException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        Date begin = null, end = null;
        File file = new File(getApplicationContext().getDataDir(), filename);
        String[] splitter;

        if(!file.exists())
            return;

        searchAdapter.clear();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while((line = reader.readLine()) != null) {
                splitter = line.split(";");

                begin = DateFormat
                        .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .parse(splitter[1]);
                end = DateFormat
                        .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .parse(splitter[2]);

                if(searchBegin.compareTo(begin) <= 0 && searchEnd.compareTo(begin) >= 0)
                    searchAdapter.add(new Appointment(splitter[0], begin, end));
            }
        }
        catch(Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        if(searchAdapter.isEmpty())
            Toast.makeText(this, "No appointments found", Toast.LENGTH_LONG).show();
    }

    private void setTextViewWithDateDialog(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(
                SearchAppointmentActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month += 1;
                        textView.setText(month + "/" + day + "/" + year);
                    }
                }, year, month, day
        );
        dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dateDialog.show();
    }

    private void setTextViewWithTimeDialog(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timeDialog = new TimePickerDialog(SearchAppointmentActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String cycle = (hour >= 12 ? "PM" : "AM");
                        String leadingZero = (minute < 10 ? "0" : "");

                        if(hour > 12)
                            hour -= 12;
                        else if(hour == 0)
                            hour += 12;

                        textView.setText(hour + ":" + leadingZero + minute + " " + cycle);
                    }
                }, hour, minute, false
        );

        timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        timeDialog.show();
    }
}