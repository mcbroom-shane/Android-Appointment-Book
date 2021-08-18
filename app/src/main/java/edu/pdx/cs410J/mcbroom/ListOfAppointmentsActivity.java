package edu.pdx.cs410J.mcbroom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class ListOfAppointmentsActivity extends AppCompatActivity {
    private final int ADD_APPT_RESULT = 35;
    private String filename;
    private List<Appointment> appointments = new Vector();
    private ArrayAdapter<Appointment> appointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_appointments);

        String owner = getIntent().getStringExtra("owner");
        setTitle(String.format("%s's appointments", owner));
        filename = owner + ".txt";

        appointmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointments);
        readFromInternalStorage();
        ListView listView = findViewById(R.id.listOfAppointments);
        listView.setAdapter(appointmentAdapter);

        findViewById(R.id.fabSearchAppointment)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ListOfAppointmentsActivity.this, SearchAppointmentActivity.class);
                        intent.putExtra("owner", owner);
                        startActivity(intent);
                    }
        });

        findViewById(R.id.fabAddAppointment).setOnClickListener((View view) -> {
            Intent intent = new Intent(ListOfAppointmentsActivity.this, AddAppointmentActivity.class);
            intent.putExtra("owner", owner);
            startActivityForResult(intent, ADD_APPT_RESULT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && requestCode == ADD_APPT_RESULT && intent != null) {
            String description = intent.getStringExtra("description");
            String beginString = intent.getStringExtra("begin");
            String endString   = intent.getStringExtra("end");

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                Date begin = sdf.parse(beginString);
                Date end = sdf.parse(endString);
                appointments.add(new Appointment(description, begin, end));
                Collections.sort(appointments);
                appointmentAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Added appointment", Toast.LENGTH_LONG).show();
                writeToInternalStorage();
            }
            catch(ParseException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void readFromInternalStorage() {
        Date begin = null, end = null;
        File file = new File(getApplicationContext().getDataDir(), filename);
        String[] splitter;

        if(!file.exists())
            return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while((line = reader.readLine()) != null) {
                splitter = line.split(";");

                begin = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(splitter[1]);
                end   = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(splitter[2]);

                appointments.add(new Appointment(splitter[0], begin, end));
            }
        }
        catch(Exception ex) {
            Toast.makeText(ListOfAppointmentsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writeToInternalStorage() {
        File file = new File(getApplicationContext().getDataDir(), filename);
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(file));

            for(Appointment appointment : appointments) {
                writer.write(appointment.getDescription() + ';');
                writer.write(appointment.getBeginTimeString() + ';');
                writer.write(appointment.getEndTimeString() + '\n');
            }
        }
        catch(Exception ex) {
            Toast.makeText(ListOfAppointmentsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        finally {
            if(writer != null)
                writer.flush();
        }
    }
}