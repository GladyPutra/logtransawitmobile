package com.example.gladyputra.logtransawit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class InputBeratActivity extends AppCompatActivity {

    String id_dist= "";
    String id_user= "";
    String token = "";

    private TextView jmlhBuah;

    public static final int MY_SOCKET_TIMEOUT_MS = 30000;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_berat);
        token = getIntent().getStringExtra("token");
        id_dist = getIntent().getStringExtra("id_dist");
        final EditText berat = (EditText) findViewById(R.id.txtberat);
        jmlhBuah = (TextView) findViewById(R.id.txtjmlhbuah);
        Button btn_tambah = (Button) findViewById(R.id.btn_input);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu ...");

        final RequestQueue queue = Volley.newRequestQueue(this);
        final RequestQueue queue2 = Volley.newRequestQueue(this);
        final String url = "http://api.gis-riset.online/api/auth/inputberat"; //URLnya diganti untuk Tambah Berat...
        final String url2 = "http://api.gis-riset.online/api/auth/getjumlahangkut?id_distribusi="+id_dist; //URLnya diganti untuk Tampil Jumlah Tandan...

        StringRequest postRequest2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        progressDialog.dismiss();
                        Log.d("ini : ", response);
                        jmlhBuah.setText(response + " Buah"); //Untuk set Jumlah Tandan...

                        Toast.makeText(getApplicationContext(),"Tampil Data Sukses",Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progressDialog.dismiss();
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }
        ) {
                };
        postRequest2.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue2.add(postRequest2);

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Input Data Sukses",Toast.LENGTH_SHORT);

                                finish();
                                Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
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
                        params.put("id_distribusi", id_dist);
                        //Atributnya apakah sudah sesuai?

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

    }
}