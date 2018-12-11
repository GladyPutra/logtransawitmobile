package com.example.gladyputra.logtransawit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private  static final int LOCATION_REQUEST = 500;
    public static final int MY_SOCKET_TIMEOUT_MS = 30000;
    //String id_dist = getIntent().getStringExtra("id_dist");
    LocationManager locationManager;
    private double tempLatLine, tempLongLine;
    private double tempLat,tempLong;

    private Button btnYes,btnCancel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d("dist : ",getIntent().getStringExtra("id_dist"));
        final RequestQueue queue = Volley.newRequestQueue(this);
        final RequestQueue queue2 = Volley.newRequestQueue(this);
        final String url = "http://api.gis-riset.online/api/auth/addcoord";
        final String url2 = "http://api.gis-riset.online/api/auth/changestatus";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu ...");

        btnYes = (Button) findViewById(R.id.btnYes);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                finish();
                Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
                startActivity(intent);
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                progressDialog.dismiss();
                                Log.d("success : ","success");
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                                finish();
                                Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                progressDialog.dismiss();
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
                        params.put("id_dist",getIntent().getStringExtra("id_dist"));

                        return params;
                    }
                };
                postRequest.setRetryPolicy(new DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue2.add(postRequest);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//                    final double latitude = location.getLatitude();           // get latitude
//                    final double longitude = location.getLongitude();         // get longitude
//                    LatLng latLng = new LatLng(latitude, longitude);    // instantiate the LatLng class
//                    Geocoder geocoder = new Geocoder(getApplicationContext());
//
//                    tempLatLine = latitude;
//                    tempLongLine = longitude;
//
//
//                    try {
//                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
//                        String str = addressList.get(0).getLocality()+", ";
//                        str += addressList.get(0).getCountryName();
//                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
//
//
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
//
//                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                                new Response.Listener<String>()
//                                {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        Log.d("success : ","success");
//                                    }
//                                },
//                                new Response.ErrorListener()
//                                {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        // error
//                                        NetworkResponse networkResponse = error.networkResponse;
//                                        if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
//                                            Toast toast = Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT);
//                                            toast.show();
//                                        }
//                                    }
//                                }
//                        ) {
//                            @Override
//                            protected Map<String, String> getParams()
//                            {
//                                Map<String, String>  params = new HashMap<String, String>();
//                                params.put("id_dist",getIntent().getStringExtra("id_dist"));
//                                params.put("latitude", String.valueOf(latitude));
//                                params.put("longitude", String.valueOf(longitude));
//
//
//                                return params;
//                            }
//
//                        };
//                        postRequest.setRetryPolicy(new DefaultRetryPolicy(
//                                MY_SOCKET_TIMEOUT_MS,
//                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                        queue.add(postRequest);
//
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onStatusChanged(String s, int i, Bundle bundle) {
//
//                }
//
//                @Override
//                public void onProviderEnabled(String s) {
//
//                }
//
//                @Override
//                public void onProviderDisabled(String s) {
//
//                }
//            });
        }
//        else {
//            Toast.makeText(getApplicationContext(),"Tidak Terhubung",Toast.LENGTH_LONG).show();
//        }
        // check the network provider is Enabled
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    final double latitude = location.getLatitude();           // get latitude
                    final double longitude = location.getLongitude();         // get longitude
                    LatLng latLng = new LatLng(latitude, longitude);    // instantiate the LatLng class
                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    tempLatLine = latitude;
                    tempLongLine = longitude;


                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality()+", ";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));


                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));

                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
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
                                params.put("id_dist",getIntent().getStringExtra("id_dist"));
                                params.put("latitude", String.valueOf(latitude));
                                params.put("longitude", String.valueOf(longitude));


                                return params;
                            }

                        };
                        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                                MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(postRequest);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();           // get latitude
                    double longitude = location.getLongitude();         // get longitude
                    LatLng latLng = new LatLng(latitude, longitude);    // instantiate the LatLng class
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    tempLat = latitude;
                    tempLong = longitude;
                    //------------ini polyline---------

                    Log.d("lat : ",String.valueOf(tempLatLine));
                    Log.d("long", String.valueOf(tempLongLine));
                    mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(tempLatLine, tempLongLine))
                            .add(new LatLng(latitude, longitude))
                            .width(5)
                            .color(Color.RED));

                    //---------------------------------
                    tempLatLine = latitude;
                    tempLongLine = longitude;
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality()+", ";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));

                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
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
                                params.put("id_dist",getIntent().getStringExtra("id_dist"));
                                params.put("latitude", String.valueOf(tempLat));
                                params.put("longitude", String.valueOf(tempLong));


                                return params;
                            }

                        };
                        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                                MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(postRequest);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),"Tidak Terhubung",Toast.LENGTH_LONG).show();
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
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);


        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10.2f));
    }
}
