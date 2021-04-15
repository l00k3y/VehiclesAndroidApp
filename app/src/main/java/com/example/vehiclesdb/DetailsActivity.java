package com.example.vehiclesdb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //instantiating gson object
        final Gson gson = new Gson();
        //declaring api url
        final String url = "http://10.0.2.2:8005/api";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //retrieve vehicle object sent from main activity
        Bundle extras = getIntent().getExtras();
        //store the vehicle object from main activity in vehicle
        final Vehicle vehicle = (Vehicle) extras.get("vehicle");
        //declare handlers for text views on screen
        final TextView makeText = findViewById(R.id.makeText);
        final TextView modelText = findViewById(R.id.modelText);
        final TextView yearText = findViewById(R.id.yearText);
        final TextView priceText = findViewById(R.id.priceText);
        final TextView licenseText = findViewById(R.id.licenseText);
        final TextView colourText = findViewById(R.id.colourText);
        final TextView numberDoorsText = findViewById(R.id.doorsText);
        final TextView transmissionText = findViewById(R.id.transmissionText);
        final TextView mileageText = findViewById(R.id.mileageText);
        final TextView fuelText = findViewById(R.id.fuelText);
        final TextView engineText = findViewById(R.id.engineSizeText);
        final TextView bodyText = findViewById(R.id.bodyStyleText);
        final TextView conditionText = findViewById(R.id.conditionText);
        final TextView notesText = findViewById(R.id.notesText);
        TextView vehicleIDLabel = findViewById(R.id.vehicle_id);
        //populate text views and labels with corresponding vehicle data
        vehicleIDLabel.setText("Vehicle ID: " + String.valueOf(vehicle.getVehicleID()));
        makeText.setText(vehicle.getMake());
        modelText.setText(vehicle.getModel());
        yearText.setText(String.valueOf(vehicle.getYear()));
        priceText.setText(String.valueOf(vehicle.getPrice()));
        licenseText.setText(vehicle.getLicenseNumber());
        colourText.setText(vehicle.getColour());
        numberDoorsText.setText(String.valueOf(vehicle.getNumberDoors()));
        transmissionText.setText(vehicle.getTransmission());
        mileageText.setText(String.valueOf(vehicle.getMileage()));
        fuelText.setText(vehicle.getFuelType());
        engineText.setText(String.valueOf(vehicle.getEngineSize()));
        bodyText.setText(vehicle.getBodyStyle());
        conditionText.setText(vehicle.getCondition());
        notesText.setText(vehicle.getNotes());
        //declare handler for update button
        Button updateButton = (Button) findViewById(R.id.updateButton);
        //instantiate HashMap for transmitting updated vehicle data
        final HashMap<String, String> params = new HashMap<>();
        //when the button is clicked do the following ->
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //retrieve all data from the activity's text views & store them in a vehicle object
                int vehicle_id = vehicle.getVehicleID();
                String make = makeText.getText().toString();
                String model = modelText.getText().toString();
                int year = Integer.valueOf(yearText.getText().toString());
                int price = Integer.valueOf(priceText.getText().toString());
                String license_number = licenseText.getText().toString();
                String colour = colourText.getText().toString();
                int number_doors = Integer.valueOf(numberDoorsText.getText().toString());
                String transmission = transmissionText.getText().toString();
                int mileage = Integer.valueOf(mileageText.getText().toString());
                String fuel_type = fuelText.getText().toString();
                int engine_size = Integer.valueOf(engineText.getText().toString());
                String body_style = bodyText.getText().toString();
                String condition = conditionText.getText().toString();
                String notes = notesText.getText().toString();
                //storing vehicle data in vehicle object
                Vehicle vehicle = new Vehicle(vehicle_id, make, model, year, price, license_number, colour, number_doors, transmission, mileage, fuel_type, engine_size, body_style, condition, notes);
                //convert to json & store in vehicleJson
                String vehicleJson = gson.toJson(vehicle);
                //print json data for debugging purposes
                System.out.println(vehicleJson);
                //store the json data under the parameter "vehicledata"
                params.put("vehicledata", vehicleJson);

                performPutCall(url, params);
            }
        });


        //delete doesn't work, I can't get the writer to send the data correctly as it sends null rather than {vehicle_id=[number]}
        final HashMap<String, Integer> deleteParams = new HashMap<>();
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int deleteVehicleID = vehicle.getVehicleID();
                deleteParams.put("vehicle_id", deleteVehicleID);
                System.out.println(deleteParams);
                performDeleteCall(url, deleteParams);
            }
        });
    }

    public String performDeleteCall(String requestURL, HashMap<String, Integer> deleteParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("DELETE");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //send vehicle ID to the connection using output stream & buffered writer
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getDeleteDataString(deleteParams));
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode = " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(this, "Vehicle deleted", Toast.LENGTH_LONG).show();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                Toast.makeText(this, "Error. Failed to delete vehicle", Toast.LENGTH_LONG).show();
                response = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Response = " + response);
        return response;
    }

    public String performPutCall(String requestURL, HashMap<String, String> putDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            //create connection object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("PUT");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //send PUT data to the connection using output stream & buffered writer
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPutDataString(putDataParams));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(this, "Vehicle updated", Toast.LENGTH_LONG).show();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                Toast.makeText(this, "Error. Failed to update vehicle", Toast.LENGTH_LONG).show();
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Response = " + response);
        return response;
    }

    private String getPutDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private String getDeleteDataString(HashMap<String, Integer> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }
        return result.toString();
    }

}