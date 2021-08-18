package edu.pdx.cs410J.mcbroom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import edu.pdx.cs410J.mcbroom.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private List<String> owners = new Vector<>();
    private ArrayAdapter<String> ownerAdapter;
    public static final String FILENAME = "owners.txt";
    public static final int ADD_APPTBOOK_RESULT = 116;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setTitle("Appointment Books");

        ownerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, owners);
        readFromInternalStorage();
        ListView listView = findViewById(R.id.listOfOwners);
        listView.setAdapter(ownerAdapter);
        listView.setLongClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ListOfAppointmentsActivity.class);
                String owner = adapterView.getAdapter().getItem(i).toString();
                intent.putExtra("owner", owner);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if(which == DialogInterface.BUTTON_POSITIVE) {
                                owners.remove(i);
                                writeToInternalStorage();
                                ownerAdapter.notifyDataSetChanged();
                            }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Delete " + adapterView.getAdapter().getItem(i) + "?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            }
        });

        findViewById(R.id.fabAddOwner).setOnClickListener((View view) -> {
            Intent intent = new Intent(MainActivity.this, AddAppointmentBookActivity.class);
            startActivityForResult(intent, ADD_APPTBOOK_RESULT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && requestCode == ADD_APPTBOOK_RESULT && intent != null) {
            String owner = intent.getStringExtra("owner");
            Toast.makeText(this, "Added appointment book for " + owner, Toast.LENGTH_LONG).show();
            owners.add(owner);
            Collections.sort(owners);
            ownerAdapter.notifyDataSetChanged();
            writeToInternalStorage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                return true;
            case R.id.action_readme:
                openReadme();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openReadme() {
        Toast.makeText(this,
                "Program for creating and storing appointments, created by Shane McBroom",
                Toast.LENGTH_LONG).show();
    }

    private void readFromInternalStorage() {
        File file = new File(getApplicationContext().getDataDir(), FILENAME);

        if(!file.exists())
            return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while((line = reader.readLine()) != null)
                owners.add(line);
        }
        catch(Exception ex) {
            Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writeToInternalStorage() {
        File file = new File(getApplicationContext().getDataDir(), FILENAME);
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(file));

            for(String owner : owners)
                writer.println(owner);
        }
        catch(Exception ex) {
            Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        finally {
            if(writer != null)
                writer.flush();
        }
    }
}