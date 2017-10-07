package com.example.guru.trackthebusdriv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
    Button b1,b2;
    EditText e1,e2,e3,e4,e5;
    SessionManager sessionManager;
    ConnectionDetector cd;
    JSONParser jsonParser = new JSONParser();
    private static String LOGIN_URL = "https://guruzeba.000webhostapp.com/zedg.php";
    EditText e;
    private ProgressDialog pDialog;
    public String token,zeba;
    GPSTracker gps;
    public double latitude, longitude,lat,log;
    String lt,lg;
String z,g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1=(Button)findViewById(R.id.button);
        b2=(Button)findViewById(R.id.button2);
        e1=(EditText)findViewById(R.id.editText);
        e2=(EditText)findViewById(R.id.editText2);
        e3=(EditText)findViewById(R.id.editText3);
        e4=(EditText)findViewById(R.id.editText4);
        e5=(EditText)findViewById(R.id.editText5);
        zeba=e1.getText().toString();

        cd = new ConnectionDetector(getApplicationContext());
        sessionManager=new SessionManager(getApplicationContext());
        if(sessionManager.isUserLoggedIn())
        {
            Intent intent=new Intent(this,Location.class);
            startActivity(intent);
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //acceswebservice();
                if ((e1.getText().toString()).length() > 0 && (e2.getText().toString()).length() > 0&& (e3.getText().toString()).length() > 0&& (e4.getText().toString()).length() > 0&& (e5.getText().toString()).length() > 0) {
                    Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false

                    if (isInternetPresent) {
                        //z = e1.getText().toString().toUpperCase();
                        //g = e2.getText().toString();

                        acceswebservice();

                    } else {
                        Toast.makeText(getApplicationContext(), "Connect to internet", Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (!((e1.getText().toString()).length() > 0)) {
                        e1.setError("enter username");
                    }
                    if (!((e2.getText().toString()).length() > 0)) {
                        e2.setError("enter phone");
                    }
                    if (!((e3.getText().toString()).length() > 0)) {
                        e3.setError("enter area");
                    }
                    if (!((e4.getText().toString()).length() > 0)) {
                        e4.setError("enter busno");
                    }
                    if (!((e5.getText().toString()).length() > 0)) {
                        e5.setError("enter password");
                    }

                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Login.class);
                startActivity(intent);
            }
        });
        /*gps = new GPSTracker(MainActivity.this);

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
                Toast.makeText(getApplicationContext(), "cannot get address", LENGTH_LONG).show();
            }

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }*/
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
    }
    private void acceswebservice() {
        JsonReadTask task = new JsonReadTask();
        task.execute();
    }


    private class JsonReadTask extends AsyncTask<String, String, String> {
        private final String z = e1.getText().toString();
        private final String g = e5.getText().toString();
        private final String gz=e2.getText().toString();
        private final String zg=e3.getText().toString();
        private final String gzz=e4.getText().toString();

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Attempting for login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String success;
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("username", z));
                param.add(new BasicNameValuePair("password", g));
                param.add(new BasicNameValuePair("phone",gz));
                param.add(new BasicNameValuePair("area",zg));
                param.add(new BasicNameValuePair("busno",gzz));
                param.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
                param.add(new BasicNameValuePair("longitude",String.valueOf(longitude)));
                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", param);
//                Log.d("Login attempt", json.toString());
                success = json.getString("emp_info");
                if (success.equals("guruzeba")) {

                    //Log.d("Successfully Login!", json.toString());
                    //Intent ii = new Intent(MainActivity.this, Login.class);
                    //ii.putExtra("username",z);
                    //ii.putExtra("password",g);
                    //finish();
                    //startActivity(ii);
                    return json.getString("emp_info");
                } else {
                    return json.getString("emp_info");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String String) {
            pDialog.dismiss();
            if (String.equals("guruzeba")) {
                Toast.makeText(getApplicationContext(),String,Toast.LENGTH_LONG).show();
                //sessionManager.createUserLoginSession(z, token);
                Intent intent = new Intent(MainActivity.this, Login.class);
                //intent.putExtra("guru", LOGIN_URL);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("guru", token);
                intent.putExtra("zeba", z);
                //intent.putExtra("guz", g);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"user already exists",LENGTH_LONG).show();
            }
        }
    }
}
