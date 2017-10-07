package com.example.guru.trackthebusdriv;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.app.Service;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;

public class Location extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap,map;
    GPSTracker gps;
    public double latitude, longitude,lat,log;
     public String z,lt,lg;
    SessionManager sessionManager;
    ConnectionDetector cd;
    JSONParser jsonParser = new JSONParser();
    Button b5;
    public String distance,duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gps = new GPSTracker(Location.this);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        cd = new ConnectionDetector(getApplicationContext());
        sessionManager=new SessionManager(getApplicationContext());
        b5=(Button)findViewById(R.id.button5);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName("ccamp",1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            lat = addresses.get(0).getLatitude();
            log = addresses.get(0).getLongitude();
        }
        //Toast.makeText(getApplicationContext(),String.valueOf(lat)+String.valueOf(log),Toast.LENGTH_LONG).show();
        LatLng sydney = new LatLng(15.7743054, 78.0572698);
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
        map.addMarker(new MarkerOptions().position(sydney).title(z));
        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        String url = getDirectionsUrl();

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(android.location.Location location) {
                lt = String.valueOf(location.getLatitude());
                lg = String.valueOf(location.getLongitude());
                //Toast.makeText(getApplication(), lt, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplication(), lg, Toast.LENGTH_LONG).show();
                new AttemptLogin().execute();

                //TODO: Send longitude and latitude to JAVA app
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logoutUser();
            }
        });
        //startService(new Intent(Location.this,Servicecla.class));
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
    }
    private String getDirectionsUrl() {

        // Origin of route
        String str_origin = "origin=15.7743054,78.0572698";

        // Destination of route
        String str_dest = "destination=15.8330238,78.0496799";

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                LatLng zeba = new LatLng(15.8330238, 78.0496799);
                map.addMarker(new MarkerOptions().position(zeba).title(distance+duration)).showInfoWindow();
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
            //tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gps = new GPSTracker(Location.this);

        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
                //Toast.makeText(getApplicationContext(), (CharSequence) addresses.get(0),Toast.LENGTH_LONG).show();
                System.out.println(addresses.get(0).getLocality());
             z = addresses.get(0).getLocality();
                String zeba = addresses.get(0).getAddressLine(0);
                //Toast.makeText(getApplicationContext(), zeba, LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), z, LENGTH_LONG).show();
            } else {
                //Toast.makeText(getApplicationContext(), "cannot get address", LENGTH_LONG).show();
            }

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
        mMap.addMarker(new MarkerOptions().position(sydney).title(z));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    //public void onPause(){
      //  super.onPause();
        //startService(new Intent(Location.this,Servicecla.class));
    //}
    class AttemptLogin extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
public String activity;

        boolean failure = false;


        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // here Check for success tag
            int success;
            String LOGIN_URL = "https://guruzeba.000webhostapp.com/guru.php";

            HashMap<String,String> user=sessionManager.getUserDetails();
            activity=user.get(SessionManager.KEY_NAME);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", String.valueOf(lt)));
            params.add(new BasicNameValuePair("password", String.valueOf(lg)));
            params.add(new BasicNameValuePair("pass", activity));


            Log.d("request!", "starting");


            jsonParser.makeHttpRequest(
                    LOGIN_URL, "POST", params);

            // checking  log for json response


            // success tag for json


            return null;


        }


    }
}
