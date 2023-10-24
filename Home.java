package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private LinearLayout homeRl;
    private ProgressBar loadingPB;
    private TextView cityNametV, temperatureTV, ConditionTV;

    private TextInputEditText cityEdit;
    private ImageView backIV, iconIV, searchIV, getBackIV;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;
    private RecyclerView weatherRV;
    private ArrayList<RVModel> RVModelArraylist;
    private RVAdapter weatherRVAdapter;
    private ImageView kebabMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_home);
        homeRl = findViewById(R.id.idHomeLL);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNametV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemprature);
        ConditionTV = findViewById(R.id.idTVCondition);
        cityEdit = findViewById(R.id.idEditCity);
        backIV = findViewById(R.id.idBackIV);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        getBackIV= findViewById(R.id.idGetBackIV);
        kebabMenu=findViewById(R.id.idOptionIV);

        weatherRV= findViewById(R.id.idRVWeather);


        RVModelArraylist= new ArrayList<>();
        weatherRVAdapter= new RVAdapter(this,RVModelArraylist);
        weatherRV.setAdapter(weatherRVAdapter);



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //if (ActivityCompat.checkSelfPermission(this<Manifest.permission.))
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //cityName = getCityName(location.getLongitude(),location.getLatitude());
        ;

        getWeatherInfo(cityName);

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city=cityEdit.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(Home.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                }else {
                    cityNametV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

        getBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "plrase provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /*private String getCityName(double longitude, double latitude){
        String cityName="Not Found";
        Geocoder gcd= new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses=gcd.getFromLocation(latitude,longitude,10);

            for (Address adr: addresses){
                if (adr!=null){
                    String city=adr.getLocality();
                    if (city!=null && !city.equals("")){
                        cityName=city;
                    }else {
                        Log.d("Tag","city not found");
                        Toast.makeText(this, "User City Not Found", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }catch (IOException e){
            e.printStackTrace();

        }
        return cityName;

    }*/
    private void getWeatherInfo(String cityName){
        //FetchData fetchData = new FetchData();
        String url= "https://api.weatherapi.com/v1/forecast.json?key=6ecf8262d0f44543bb391631233108&q="+cityName+"&days=7&aqi=no&alerts=no";
        cityNametV.setText(cityName);
        RequestQueue requestQueue= Volley.newRequestQueue(Home.this);
        loadingPB.setVisibility(View.GONE);
        homeRl.setVisibility(View.VISIBLE);

        //fetchData.execute();

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the successful response here

                        RVModelArraylist.clear();

                        try {
                            String temperature = response.getJSONObject("current").getString("temp_c");
                            temperatureTV.setText(temperature + "°C ");
                            int isDay=response.getJSONObject("current").getInt("is_day");
                            String condition= response.getJSONObject("current").getJSONObject("condition").getString("text");
                            String conditionIcon= response.getJSONObject("current").getJSONObject("condition").getString("icon");
                            Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                            ConditionTV.setText(condition);
                            /*if (isDay==1) {
                                Picasso.get().load("https://img.freepik.com/free-vector/realistic-blue-sky-background_1048-6707.jpg?w=740&t=st=1693640967~exp=1693641567~hmac=170349659f91776d7ee708ce93f2e2c861fde4f67e664c372386baa318b02a6a").into(backIV);
                            }
                            else {
                                Picasso.get().load("https://img.freepik.com/free-photo/full-moon-with-dark-cloudscapes-night-halloween-concept_9083-8075.jpg?w=1060&t=st=1693641022~exp=1693641622~hmac=adae5a9aae1cd1c6c520f974dd2d1463361a6b321f053cf1382bc60a1999dee4").into(backIV);
                            }*/

                            JSONObject forecastObj = response.getJSONObject("forecast");
                            JSONArray forecastdayArray = forecastObj.getJSONArray("forecastday");

                            for (int i = 0; i < forecastdayArray.length(); i++) {
                                JSONObject dayobj = forecastdayArray.getJSONObject(i);

                                String date = dayobj.getString("date");
                                String condi = dayobj.getJSONObject("day").getJSONObject("condition").getString("text");
                                String temp = dayobj.getJSONObject("day").getString("avgtemp_c");
                                String icon = dayobj.getJSONObject("day").getJSONObject("condition").getString("icon");
                                RVModelArraylist.add(new RVModel(date,temp,icon,condi));



                        }

                            weatherRVAdapter.notifyDataSetChanged();





                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Home.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors here
                        Toast.makeText(Home.this, "Please enter a valid city", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });

        requestQueue.add(jsonObjectRequest);


    }


}