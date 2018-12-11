package com.example.gladyputra.logtransawit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.android.gms.location.LocationServices;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private  static final int LOCATION_REQUEST = 500;
    public static final int MY_SOCKET_TIMEOUT_MS = 30000;
    ArrayList<LatLng> listPoints;
    double lat,lng;
    private String new_dist_id;
    String id_dist;
    String token = "";
    String id_user = "";
    Context c;
    Location location;
    FusedLocationProviderClient mFusedLocationClient;
    final String username = SharedPrefManager.getInstance(this).getUserName();
    final String password = SharedPrefManager.getInstance(this).getPassword();
    final String flag = SharedPrefManager.getInstance(this).getFlag();

    private Button btnYes,btnCancel,btnMulai;
    private ProgressDialog progressDialog;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    RequestQueue queue2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        btnMulai = (Button) findViewById(R.id.btnMulai);

        btnYes = (Button) findViewById(R.id.btnYes);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        if(flag.equalsIgnoreCase("1"))
        {
            btnYes.setEnabled(true);
            btnCancel.setEnabled(true);
            btnMulai.setEnabled(false);
        }
        else
        {
            btnYes.setEnabled(false);
            btnCancel.setEnabled(false);
            btnMulai.setEnabled(true);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        token = getIntent().getStringExtra("token");
        c = this;

        queue2 = Volley.newRequestQueue(this);

        btnMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Construct a FusedLocationProviderClient.
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://api.gis-riset.online/api/auth/user",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                progressDialog.dismiss();
                                id_user = response;
                                StringRequest postRequest = new StringRequest(Request.Method.POST,"http://api.gis-riset.online/api/auth/create-distribusi",
                                        new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse(String response) {
                                                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, "http://api.gis-riset.online/api/auth/newid",
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                id_dist = response;
                                                                SharedPrefManager.getInstance(getApplicationContext())
                                                                        .setDistribusi(id_dist);

                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                    }


                                                })
                                                {
                                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                                        Map<String, String> headers = new HashMap<>();
                                                        // Basic Authentication
                                                        //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                                                        headers.put("Authorization", "Bearer " + token);
                                                        return headers;
                                                    }
                                                };
                                                stringRequest2.setRetryPolicy(new DefaultRetryPolicy(
                                                        MY_SOCKET_TIMEOUT_MS,
                                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                queue2.add(stringRequest2);
                                            }
                                        },
                                        new Response.ErrorListener()
                                        {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // error

                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams()
                                    {
                                        Map<String, String>  params = new HashMap<String, String>();
                                        params.put("id_user",id_user);

                                        return params;
                                    }
                                };
                                postRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        MY_SOCKET_TIMEOUT_MS,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                queue2.add(postRequest);


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }


                })
                {
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        // Basic Authentication
                        //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue2.add(stringRequest);

                SharedPrefManager.getInstance(getApplicationContext())
                        .setFlag("1");
                btnYes.setEnabled(true);
                btnCancel.setEnabled(true);
                btnMulai.setEnabled(false);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        listPoints = new ArrayList<>();
        Log.d("fewewefwfewfefwe","fwefewweffwe");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu ...");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Code untuk Selesai rekam koordinat map

//                final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                final String url = "http://api.gis-riset.online/api/auth/deletedistribusi";
//                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                        new Response.Listener<String>()
//                        {
//                            @Override
//                            public void onResponse(String response) {
//                                // response
//                                progressDialog.dismiss();
//                                android.os.Process.killProcess(android.os.Process.myPid());
//                                System.exit(1);
//                                finish();
//                                Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
//                                intent.putExtra("token", token);
//                                startActivity(intent);
//
//                            }
//                        },
//                        new Response.ErrorListener()
//                        {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                // error
//                                progressDialog.dismiss();
//                                NetworkResponse networkResponse = error.networkResponse;
//                                if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                                    Toast toast = Toast.makeText(getApplicationContext(), "Nama Pengguna / Kata Sandi Salah ! ", Toast.LENGTH_SHORT);
//                                    toast.show();
//                                }
//                            }
//                        }
//                ) {
//                    @Override
//                    protected Map<String, String> getParams()
//                    {
//                        Map<String, String>  params = new HashMap<String, String>();
//                        params.put("id_dist", id_dist);
//
//                        return params;
//                    }
//                };
//                postRequest.setRetryPolicy(new DefaultRetryPolicy(
//                        MY_SOCKET_TIMEOUT_MS,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                queue.add(postRequest);

                SharedPrefManager.getInstance(getApplicationContext())
                        .setFlag("0");
                id_dist = SharedPrefManager.getInstance(MapsActivity2.this).getDistribusi();
                final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                progressDialog.show();
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://api.gis-riset.online/api/auth/changestatus",
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                progressDialog.dismiss();
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .setUsername(username);
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .setPassword(password);
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .setDistribusi(null);
                                Log.d("Response", response);

                                Intent intent = new Intent(getApplicationContext(), InputBeratActivity.class);
                                intent.putExtra("token", token);
                                intent.putExtra("id_dist", id_dist);
                                startActivity(intent);


                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                                finish();


                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                progressDialog.dismiss();
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Nama Pengguna / Kata Sandi Salah ! ", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("id_dist", id_dist);

                        return params;
                    }
                };
                postRequest.setRetryPolicy(new DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(postRequest);

            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_dist = SharedPrefManager.getInstance(MapsActivity2.this).getDistribusi();
                Intent intent = new Intent(getApplicationContext(), OptionActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("id_dist", id_dist);
                startActivity(intent);
            }
        });
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
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
//        }
//        else
//        {
//            LocationManager locationManager = (LocationManager)
//                    getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//
//            Location location = locationManager.getLastKnownLocation(locationManager
//                    .getBestProvider(criteria, false));
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//
//        }
//        mMap.setMyLocationEnabled(true);
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(2000);
//        mLocationRequest.setFastestInterval(2000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                //Reset Marker when already 2
//
//                if(listPoints.size() == 2)
//                {
//                    listPoints.clear();
//                    mMap.clear();
//                }
//                //Save First Point Select
//                listPoints.add(latLng);
//                //Create Marker
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//
//                if(listPoints.size() == 1){
//                    //Add First Marker to the Map
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                } else {
//                    //Add Second Marker to the Map
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                }
//                mMap.addMarker(markerOptions);
//
//                if(listPoints.size() == 2){
//                    //Create The URL to Get Request from First Marker to Second Marker
//                    String url = getRequestUrl(listPoints.get(0),listPoints.get(1));
//                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//                    taskRequestDirections.execute(url);
//                }
//            }
//        });
//    }
//
//    private void getDeviceLocation() {
//
//    }
//
//
//    LocationCallback mLocationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            List<Location> locationList = locationResult.getLocations();
//            if (locationList.size() > 0) {
//                //The last location in the list is the newest
//                Location location = locationList.get(locationList.size() - 1);
//                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
//                mLastLocation = location;
//                if (mCurrLocationMarker != null) {
//                    mCurrLocationMarker.remove();
//                }
//
//                //Place current location marker
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//                mCurrLocationMarker = mMap.addMarker(markerOptions);
//
//                //move map camera
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
//            }
//        }
//    };
//
//
////    private void getDeviceLocation() {
////        /*
////         * Get the best and most recent location of the device, which may be null in rare
////         * cases when a location is not available.
////         */
////        try {
////                Task locationResult = mFusedLocationProviderClient.getLastLocation();
////                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
////                    @Override
////                    public void onComplete(@NonNull Task task) {
////                        if (task.isSuccessful()) {
////                            // Set the map's camera position to the current location of the device.
////                            mLastKnownLocation = task.getResult();
////                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
////                                    new LatLng(mLastKnownLocation.getLatitude(),
////                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
////                        } else {
//////                            Log.d(TAG, "Current location is null. Using defaults.");
//////                            Log.e(TAG, "Exception: %s", task.getException());
//////                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//////                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
////                        }
////                    }
////                });
////        } catch(SecurityException e)  {
////            Log.e("Exception: %s", e.getMessage());
////        }
////    }
//
//
//
//    private String getRequestUrl(LatLng origin, LatLng dest) {
//        //Value of Origin
//        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
//        //Value of Destination
//        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        //Set Value Enable the Sensor
//        String sensor = "sensor=false";
//        //Mode for Find Direction
//        String mode = "mode=driving";
//        //Build the full param
//        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
//        //Output format
//        String output = "json";
//        //Create URL to Request
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
//        return url;
//
//    }
//
//    private String requestDirection(String reqURL) throws IOException {
//        String responseString = "";
//        InputStream inputStream = null;
//        HttpURLConnection httpURLConnection=null;
//        try{
//            URL url = new URL(reqURL);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.connect();
//
//            //Get the response result
//            inputStream = httpURLConnection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//            StringBuffer stringBuffer = new StringBuffer();
//            String line = "";
//            while((line = bufferedReader.readLine()) != null){
//                stringBuffer.append(line);
//            }
//
//            responseString = stringBuffer.toString();
//            bufferedReader.close();
//            inputStreamReader.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            if(inputStream != null){
//                inputStream.close();
//            }
//            httpURLConnection.disconnect();
//        }
//        return responseString;
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode)
//        {
//            case LOCATION_REQUEST:
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//                    mMap.setMyLocationEnabled(true);
////                    Log.d("ggggggggggggggggggg","gggggggggggggg");
////                    Toast.makeText(getApplicationContext(),"dwedewd",1000);
//                }
//                break;
//        }
//    }
//
//    public class TaskRequestDirections extends AsyncTask<String, Void, String>{
//        @Override
//        protected String doInBackground(String... strings) {
//            String responseString = "";
//            try {
//                responseString = requestDirection(strings[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return responseString;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            //Parse Json Here
//            TaskParser taskParser = new TaskParser();
//            taskParser.execute(s);
//        }
//    }
//
//    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{
//
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
//            JSONObject jsonObject = null;
//            List<List<HashMap<String,String>>> routes = null;
//            try {
//                jsonObject = new JSONObject(strings[0]);
//                DirectionsParser directionsParser = new DirectionsParser();
//                routes = directionsParser.parse(jsonObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
//            //Get List Route and Display it into the Map
//            ArrayList points = null;
//            PolylineOptions polylineOptions = null;
//
//            for(List<HashMap<String, String>> path : lists){
//                points = new ArrayList();
//                polylineOptions = new PolylineOptions();
//
//                for(HashMap<String, String> point : path){
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lon = Double.parseDouble(point.get("lon"));
//
//                    points.add(new LatLng(lat,lon));
//
//                }
//
//                polylineOptions.addAll(points);
//                polylineOptions.width(15);
//                polylineOptions.color(Color.BLUE);
//                polylineOptions.geodesic(true);
//            }
//
//            if(polylineOptions != null){
//                mMap.addPolyline(polylineOptions);
//            }else {
//                Toast.makeText(getApplicationContext(),"Direction not Found!",Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//
//        if (mFusedLocationClient != null) {
//            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2500);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                final double lat = location.getLatitude();
                final double lng = location.getLongitude();
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://api.gis-riset.online/api/auth/addcoord",
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                Log.d("success : ","success");
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("id_dist",id_dist);
                        params.put("latitude", String.valueOf(lat));
                        params.put("longitude", String.valueOf(lng));


                        return params;
                    }

                };
                postRequest.setRetryPolicy(new DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue2.add(postRequest);



                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Lokasi Awal: "+username.toString());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.5f));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity2.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
