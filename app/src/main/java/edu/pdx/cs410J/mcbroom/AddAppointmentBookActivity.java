package edu.pdx.cs410J.mcbroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddAppointmentBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment_book);

        setTitle("Add new appointment book");

        findViewById(R.id.buttonAddAppointmentBook).setOnClickListener((View view) -> {
            String owner = ((EditText) findViewById(R.id.editOwner)).getText().toString();

            if(!owner.equals("")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("owner", owner);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}