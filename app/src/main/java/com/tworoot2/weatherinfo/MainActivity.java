package com.tworoot2.weatherinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tworoot2.weatherinfo.Adapter.WeatherAdapter;
import com.tworoot2.weatherinfo.ModelClass.PrevWeatherModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LinearLayout search;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    TextView address, temperature, aqi, weatherStatus, updatedOrNot, dateDay, windSpeed;
    EditText searchCity;
    ImageView searchCityClick;
    ImageView weatherImage;
    RecyclerView recyclerView;
    public static final String url = "https://api.weatherapi.com/v1/forecast.json?key=c0ed4e2e0a4942f795574639222107&q=";
    private ArrayList<PrevWeatherModel> modelArrayList;
    private WeatherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        address = findViewById(R.id.address);
        temperature = findViewById(R.id.temperature);
        weatherStatus = findViewById(R.id.weatherStatus);
        windSpeed = findViewById(R.id.windSpeed);
        updatedOrNot = findViewById(R.id.updatedOrNot);
        aqi = findViewById(R.id.aqi);
        searchCity = findViewById(R.id.searchCity);
        weatherImage = findViewById(R.id.weatherImage);
        searchCityClick = findViewById(R.id.searchCityClick);
        dateDay = findViewById(R.id.dateDay);
        requestPermissions();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        searchCityClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchCity != null || !searchCity.equals("")) {
                    getWeatherInfo(searchCity.getText().toString(), "y");
                }
            }
        });
        searchCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchCity != null || !searchCity.equals("")) {
                        getWeatherInfo(searchCity.getText().toString(), "y");
                        return true;
                    }
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        modelArrayList = new ArrayList<>();
        adapter = new WeatherAdapter(MainActivity.this, modelArrayList);
        recyclerView.setAdapter(adapter);

//        Date date = Calendar.getInstance().getTime();
//        String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
//        dateDay.setText());

        int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String months = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        Date date = new Date();
        dateDay.setText(getDayName(weekday) + ", " + date.getDate() + " " + months);

    }

    public static String getDayName(int day) {
        switch (day) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
        }

        return "invalid Day";
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                locationD.setText("Lat: " + addresses.get(0).getLatitude() + "\n" + "Long: " + addresses.get(0).getLongitude());
//                                locationD.setText(addresses.get(0).getLocality());
//                                getWeatherInfo(String.valueOf(addresses.get(0).getLatitude()), String.valueOf(addresses.get(0).getLongitude()));
                                String lat = String.valueOf(location.getLatitude());
                                String lon = String.valueOf(location.getLongitude());
//                            latitudeTextView.setText(location.getLatitude() + "");
                                address.setText(addresses.get(0).getSubLocality() + "");
//                            Toast.makeText(MainActivity.this, lat + " " + lon, Toast.LENGTH_SHORT).show();
//                                Toast.makeText(MainActivity.this, addresses.get(0).getSubLocality(), Toast.LENGTH_SHORT).show();
                                getWeatherInfo(addresses.get(0).getLocality(), "n");
//                            Toast.makeText(Dashboard.this, getCityName(latitude, longitude), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
            if (location == null) {
                requestNewLocationData();
            } else {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                locationD.setText("Lat: " + addresses.get(0).getLatitude() + "\n" + "Long: " + addresses.get(0).getLongitude());
//                                locationD.setText(addresses.get(0).getLocality());
//                                getWeatherInfo(String.valueOf(addresses.get(0).getLatitude()), String.valueOf(addresses.get(0).getLongitude()));
                    String lat = String.valueOf(location.getLatitude());
                    String lon = String.valueOf(location.getLongitude());
//                            latitudeTextView.setText(location.getLatitude() + "");
                    address.setText(addresses.get(0).getSubLocality() + "");
//                            Toast.makeText(MainActivity.this, lat + " " + lon, Toast.LENGTH_SHORT).show();
//                                Toast.makeText(MainActivity.this, addresses.get(0).getSubLocality(), Toast.LENGTH_SHORT).show();
                    getWeatherInfo(addresses.get(0).getLocality(), "n");
//                            Toast.makeText(Dashboard.this, getCityName(latitude, longitude), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    private void requestPermissions() {
        // below line is use to request
        // permission in the current activity.
        Dexter.withActivity(this)
                // below line is use to request the number of
                // permissions which are required in our app.
                .withPermissions(
                        // below is the list of permissions
                        Manifest.permission.ACCESS_FINE_LOCATION)
                // after adding permissions we are
                // calling an with listener method.
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        // this method is called when all permissions are granted
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            // do you work now
                            Toast.makeText(MainActivity.this, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                        }
                        // check for permanent denial of any permission
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permanently,
                            // we will show user a dialog message.
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        // this method is called when user grants some
                        // permission and denies some of them.
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
                    // this method is use to handle error
                    // in runtime permissions
                    @Override
                    public void onError(DexterError error) {
                        // we are displaying a toast message for error message.
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                // below line is use to run the permissions
                // on same thread and to check the permissions
                .onSameThread().check();
    }

    // below is the shoe setting dialog
    // method which is use to display a
    // dialogue message.
    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called on click on positive
                // button and on clicking shit button we
                // are redirecting our user from our app to the
                // settings page of our app.
                dialog.cancel();
                // below is the intent from which we
                // are redirecting our user.
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called when
                // user click on negative button.
                dialog.cancel();
            }
        });
        // below line is used
        // to display our dialog
        builder.show();
    }

    private void getWeatherInfo(String address1, String check) {
        //    =28.48,77.31
        updatedOrNot.setText("searching....");
//        String localUrl = url + lat + "," + lon + "&days=1&aqi=no&alerts=no";
        String localUrl = url + address1 + "&days=1&aqi=yes&alerts=yes";
        Log.d("urlFor", localUrl);
//        Toast.makeText(this, localUrl, Toast.LENGTH_SHORT).show();

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, localUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String temperature1 = response.getJSONObject("current").getString("temp_c");
                    temperature.setText(temperature1.toString());
                    String airQuality = response.getJSONObject("current").getJSONObject("air_quality").getString("pm10");
                    float floatAQI = Float.parseFloat(airQuality);
                    aqi.setText("AQI " + String.format("%.2f", floatAQI).toString());

                    String wImage = "https:" + response.getJSONObject("current").getJSONObject("condition").getString("icon");


                    Glide.with(MainActivity.this).load(wImage).into(weatherImage);


                    String status = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    weatherStatus.setText(status);
                    updatedOrNot.setText(getString(R.string.updated));
                    updatedOrNot.setTextColor(getResources().getColor(R.color.locationUpdated));
                    String wind = response.getJSONObject("current").getString("wind_kph");
                    windSpeed.setText(wind + " km/h");
                    // previous weather
                    modelArrayList.clear();

                    JSONArray prev = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0)
                            .getJSONArray("hour");

                    for (int i = 0; i < prev.length(); i++) {
                        JSONObject jsonObject = prev.getJSONObject(i);
                        String time = jsonObject.getString("time");
                        String actualTime = time.substring(time.length() - 5, time.length());
                        String temp = jsonObject.getString("temp_c") + "°C";
                        String icons = "https:" + jsonObject.getJSONObject("condition").getString("icon");

                        PrevWeatherModel prevWeatherModel = new PrevWeatherModel(actualTime, icons, temp);
                        modelArrayList.add(prevWeatherModel);
                        adapter.notifyDataSetChanged();

                    }
                    if (check.equals("y")) {
                        String city = response.getJSONObject("location").getString("name");
                        address.setText(city);
                    }
                    // end

                } catch (JSONException e) {
                    e.printStackTrace();
                    updatedOrNot.setText(getString(R.string.location_not_found));
                    updatedOrNot.setTextColor(getResources().getColor(R.color.locationNotFound));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("InvalidCity", "Invalid City Name");
                updatedOrNot.setText(getString(R.string.location_not_found));
                updatedOrNot.setTextColor(getResources().getColor(R.color.locationNotFound));
//                Toast.makeText(MainActivity.this, "Invalid" + error, Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }


}