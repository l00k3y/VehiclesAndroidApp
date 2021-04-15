package com.example.vehiclesdb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String vehicleDetails[];
    Gson gson = new Gson();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //forcing all work to be done in main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //declaring handler for the ListView on main screen
        final ListView vehicleList = (ListView) findViewById(R.id.vehiclesListView);
        String response = collectVehicleData();
        System.out.println("Server response = " + response);
        TypeToken<ArrayList<Vehicle>> tokenList = new TypeToken<ArrayList<Vehicle>>() {
        };
        final ArrayList<Vehicle> allVehicles = gson.fromJson(response, tokenList.getType());
        int i = 0;
        vehicleDetails = new String[allVehicles.size()];
        //loop through every vehicle object in allVehicles ArrayList
        for (Vehicle v : allVehicles) {
            //pull vehicle details from vehicle object & store in vehicleDetails array to be displayed on the listview
            vehicleDetails[i] = v.getMake() + " " + v.getModel() + " (" + v.getYear() + ") \n" + v.getLicenseNumber();
            i = i + 1;
        }
        //declaring array adapter
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, vehicleDetails);
        //setting the vehicleList's adapter to the one populated with vehicle details
        vehicleList.setAdapter(arrayAdapter);
        //if an object on the listview is touched then swap to DetailsActivity and pass the corresponding vehicle object to the new activity too
        vehicleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("vehicle", allVehicles.get(position));
                startActivity(intent);
            }
        });
        //declaring handler for adding new vehicle button
        Button insertVehicle = findViewById(R.id.addVehicleButton);
        //if the insert vehicle button is touched then swap to InsertActivity
        insertVehicle.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                Intent intent = new Intent(getApplicationContext(), InsertActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    private String collectVehicleData() {
        //instantiating http connection object
        HttpURLConnection urlConnection;
        //instantiating input stream object
        InputStream in = null;
        try {
            //declare URL to connect to
            URL url = new URL("http://10.0.2.2:8005/api");
            //opening connection to URL
            urlConnection = (HttpURLConnection) url.openConnection();
            //getting response from server in an input stream
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //converting input stream to string
        String response = convertStreamToString(in);
        return response;
    }

    public String convertStreamToString(InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public void onResume() {
        super.onResume(); //when returning back to the main activity re-populate the listview with updated data
        final ListView vehicleList = (ListView) findViewById(R.id.vehiclesListView);
        String response = collectVehicleData(); //re-retrieve the json data from the api
        TypeToken<ArrayList<Vehicle>> tokenList = new TypeToken<ArrayList<Vehicle>>() {
        };
        //populating an ArrayList of vehicle objects from json data
        final ArrayList<Vehicle> allVehicles = gson.fromJson(response, tokenList.getType());
        int i = 0;
        vehicleDetails = new String[allVehicles.size()];
        //loop through every vehicle object in allVehicles ArrayList
        for (Vehicle v : allVehicles) {
            //pull vehicle details from vehicle object & store in vehicleDetails array to be displayed on the listview
            vehicleDetails[i] = v.getMake() + " " + v.getModel() + " (" + v.getYear() + ") \n" + v.getLicenseNumber();
            i = i + 1;
        }
        //declaring array adapter
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, vehicleDetails);
        //setting the vehicleList's adapter to the one populated with vehicle details
        vehicleList.setAdapter(arrayAdapter);
    }
}
