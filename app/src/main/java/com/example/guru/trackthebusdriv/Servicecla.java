package com.example.guru.trackthebusdriv;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by guru on 4/1/17.
 */

public class Servicecla extends Service{
    private GoogleMap mMap;
    SessionManager sessionManager;
    ConnectionDetector cd;
    GPSTracker gps;

    JSONParser jsonParser = new JSONParser();
    public String z;
    public double lt;
    public double lg;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
        Toast.makeText(getApplicationContext(),"this is background task running",Toast.LENGTH_LONG).show();
        //Location loc=new Location();
        while(true)
        {

           /* gps = new GPSTracker(Servicecla.this);

            if (gps.canGetLocation()) {

                lt = gps.getLatitude();
                lg = gps.getLongitude();
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(lt, lg, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() > 0) {
                    //Toast.makeText(getApplicationContext(), (CharSequence) addresses.get(0),Toast.LENGTH_LONG).show();
                    System.out.println(addresses.get(0).getLocality());
                    z = addresses.get(0).getLocality();
                    String zeba = addresses.get(0).getAddressLine(0);
                    Toast.makeText(getApplicationContext(), zeba, LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), z, LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "cannot get address", LENGTH_LONG).show();
                }

                // \n is for new line
               // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }*/
            new AttemptLogin().execute();

        }
        //return super.onStartCommand(intent, flags, startId);

    }
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show();
        //Log.i(tag, "Service created...");
    }
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
