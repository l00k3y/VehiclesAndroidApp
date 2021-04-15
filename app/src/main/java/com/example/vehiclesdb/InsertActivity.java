package com.example.vehiclesdb;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
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

public class InsertActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        //declaring handler for insert button
        Button insertVehicleButton = findViewById(R.id.insertButton);
        //instantiating a Gson object
        final Gson gson = new Gson();
        //instantiating api URL
        final String url = "http://10.0.2.2:8005/api";
        //instantiating vehicle object
        final Vehicle vehicle = new Vehicle();
        //declaring handlers for text views on the activity
        final TextView vehicleIDText = findViewById(R.id.vehicleIDText);
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
        //instantiating a HashMap object for storing vehicle JSON & vehicledata parameter
        final HashMap<String, String> params = new HashMap<>();
        //if the button is clicked then do the following ->
        insertVehicleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //retrieve & parse vehicle data from the text views on the activity
                int vehicle_id = Integer.valueOf(vehicleIDText.getText().toString());
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
                //store all data in vehicle object
                Vehicle vehicle = new Vehicle(vehicle_id, make, model, year, price, license_number, colour, number_doors, transmission, mileage, fuel_type, engine_size, body_style, condition, notes);
                //convert to JSON using GSON
                String vehicleJson = gson.toJson(vehicle);
                //print JSON for debugging purposes
                System.out.println(vehicleJson);
                //store the vehicle JSON data in params HashMap with the identifier, "vehicledata"
                params.put("vehicledata", vehicleJson);
                //
                performPostCall(url, params);
                return false;
            }
        });
    }

    public String performPostCall(String requestURL, HashMap<String, String> putDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            //create connection object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //set timeouts
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            //declare request method
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //send POST data to the connection using output stream & buffered writer
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(putDataParams));
            writer.flush();
            writer.close();
            os.close();
            //get response code from server
            int responseCode = conn.getResponseCode();
            //print response code
            System.out.println("responseCode = " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //show a toast prompt if the server returns 200
                Toast.makeText(this, "Vehicle inserted", Toast.LENGTH_LONG).show();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                //show failed toast if server returns anything other than 200
                Toast.makeText(this, "Error. Failed to inserted vehicle", Toast.LENGTH_LONG).show();
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Response = " + response);
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        //instantiating StringBuilder object
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //loop through all entries in HashMap
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
}
