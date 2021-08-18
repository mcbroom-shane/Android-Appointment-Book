package edu.pdx.cs410J.mcbroom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddAppointmentActivity extends AppCompatActivity
    implements View.OnClickListener {
    EditText editDescription;
    TextView editBeginDate, editBeginTime, editEndDate, editEndTime;
    Button buttonAddAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        String owner = getIntent().getStringExtra("owner");
        setTitle(String.format("Add appointment for %s", owner));

        editDescription = findViewById(R.id.editDescription);
        editBeginDate = findViewById(R.id.editBeginDate);
        editBeginTime = findViewById(R.id.editBeginTime);
        editEndDate = findViewById(R.id.editEndDate);
        editEndTime = findViewById(R.id.editEndTime);
        buttonAddAppointment = findViewById(R.id.buttonAddAppointment);

        editDescription.setOnClickListener(this);
        editBeginDate.setOnClickListener(this);
        editBeginTime.setOnClickListener(this);
        editEndDate.setOnClickListener(this);
        editEndTime.setOnClickListener(this);
        buttonAddAppointment.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.editBeginDate:
                setTextViewWithDateDialog(editBeginDate);
                break;
            case R.id.editBeginTime:
                setTextViewWithTimeDialog(editBeginTime);
                break;
            case R.id.editEndDate:
                setTextViewWithDateDialog(editEndDate);
                break;
            case R.id.editEndTime:
                setTextViewWithTimeDialog(editEndTime);
                break;
            case R.id.buttonAddAppointment:
                sendAppointmentBack();
                break;
        }
    }

    private void sendAppointmentBack() {
        String description = editDescription.getText().toString();
        String beginDate = editBeginDate.getText().toString();
        String beginTime = editBeginTime.getText().toString();
        String endDate = editEndDate.getText().toString();
        String endTime = editEndTime.getText().toString();

        if(description.equals("") || beginDate.equals("") || beginTime.equals("")
                || endDate.equals("") || endTime.equals("")) {
            Toast.makeText(this, "No fields may be empty before submission", Toast.LENGTH_LONG).show();
            return;
        }

        String begin = beginDate + " " + beginTime;
        String end = endDate + " " + endTime;

        Intent intent = new Intent(AddAppointmentActivity.this, ListOfAppointmentsActivity.class);
        intent.putExtra("description", description);
        intent.putExtra("begin", begin);
        intent.putExtra("end", end);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setTextViewWithDateDialog(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(
                AddAppointmentActivity.this,
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

        TimePickerDialog timeDialog = new TimePickerDialog(AddAppointmentActivity.this,
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