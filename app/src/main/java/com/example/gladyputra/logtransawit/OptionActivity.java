package com.example.gladyputra.logtransawit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OptionActivity extends AppCompatActivity {
    ArrayList<String> listItems=new ArrayList<>();
    ArrayList<String> listItems2=new ArrayList<>();

    ArrayList<String>list = new ArrayList<>();
    ArrayList<String>list2 = new ArrayList<>();

    ArrayAdapter<String> adapter;

    String id_dist= "";
    String id_user= "";
    String token = "";
    String id_tph = "";

    public static final int MY_SOCKET_TIMEOUT_MS = 30000;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        token = getIntent().getStringExtra("token");
        id_dist = getIntent().getStringExtra("id_dist");
        final EditText berat = (EditText) findViewById(R.id.txtberat);
        final EditText sisa = (EditText) findViewById(R.id.txtsisa);
        Button go = (Button) findViewById(R.id.btn_input);
        Button batal = (Button) findViewById(R.id.btn_batal);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu ...");

        final Spinner spin_tph =(Spinner) findViewById(R.id.spin_tph);

        adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,listItems);
        spin_tph.setAdapter(adapter);

        final RequestQueue queue = Volley.newRequestQueue(this);
        final RequestQueue queue1 = Volley.newRequestQueue(this);
        final RequestQueue queue2 = Volley.newRequestQueue(this);
        final RequestQueue queue4 = Volley.newRequestQueue(this);
        final RequestQueue queue5 = Volley.newRequestQueue(this);
        final String url = "http://api.gis-riset.online/api/auth/create-distribusi";
        final String url2 = "http://api.gis-riset.online/api/auth/editdistribusi";
        final String url3 = "http://api.gis-riset.online/api/auth/user";
        final String url4 = "http://api.gis-riset.online/api/auth/gettph";

        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        id_user = response;
                        Log.d("id_a : ",id_user);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                id_user = "";
                Log.d("id_a : ",id_user);
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

        //Get TPH
        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, url4, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(response);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonObject = null;
                        jsonObject = jArray.getJSONObject(i);
                        // add interviewee name to arraylist
                        list.add(jsonObject.getString("deskripsi"));
                    }
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
                listItems.addAll(list);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                id_tph = "";
                Log.d("id_a : ",id_tph);
            }
        })
        {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        stringRequest3.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue4.add(stringRequest3);

        //Get ID TPH
        StringRequest stringRequest4 = new StringRequest(Request.Method.GET, url4, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(response);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonObject = null;
                        jsonObject = jArray.getJSONObject(i);
                        // add interviewee name to arraylist
                        list2.add(jsonObject.getString("id_tph"));
                    }
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
                listItems2.addAll(list2);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                id_tph = "";
                Log.d("id_a : ",id_tph);
            }
        })
        {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        stringRequest4.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue5.add(stringRequest4);

        spin_tph.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_tph=listItems2.get(i).toString();
//                Toast.makeText(getApplicationContext(),id_tph.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

                go.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //Input id distribusi, id tph, berat sama sisa (di tabel distibusi_panen_tph) ....
                        progressDialog.show();
                        StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // response

                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Input Data Sukses",Toast.LENGTH_SHORT);
                                        Intent intent = new Intent(getApplicationContext(), MapsActivity2.class);
                                        intent.putExtra("token", token);
                                        startActivity(intent);

                                    }
                                },
                                new Response.ErrorListener() {
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
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("berat", berat.getText().toString());
                                params.put("sisa",sisa.getText().toString());
                                params.put("id_dist", id_dist);
                                params.put("id_tph",id_tph);


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
                batal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Code Buat Hapus ID yang untuk input TPH...

                        berat.setText("");
                        sisa.setText("");

                        finish();
                        Intent intent = new Intent(getApplicationContext(), MapsActivity2.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    }
                });
    }
}
