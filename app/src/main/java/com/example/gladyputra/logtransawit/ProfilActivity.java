package com.example.gladyputra.logtransawit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class ProfilActivity extends AppCompatActivity {

    private TextView txtuser;

    private ProgressDialog progressDialog;

    public static final int MY_SOCKET_TIMEOUT_MS = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        if(!SharedPrefManager.getInstance(this).isLoggedin()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

            txtuser = (TextView) findViewById(R.id.txtberat);

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Mohon Tunggu ...");

            txtuser.setText(SharedPrefManager.getInstance(this).getUserName());

//            if(txtrole.getText().toString().equalsIgnoreCase("General Manager") || txtrole.getText().toString().equalsIgnoreCase("Owner"))
//            {
//                finish();
//                startActivity(new Intent(this, Profile2Activity.class));
//            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String username = SharedPrefManager.getInstance(this).getUserName();
        final String password = SharedPrefManager.getInstance(this).getPassword();
        final String flag = SharedPrefManager.getInstance(this).getFlag();

        switch (item.getItemId())
        {
            case R.id.menuLogout:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.menuMap:
//                finish();

                //untuk parse username & password
                final RequestQueue queue = Volley.newRequestQueue(this);
                final String url = "http://api.gis-riset.online/api/auth/login";

                progressDialog.show();
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
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
                                        .setFlag(flag);
                                    Log.d("Response", response);
                                    Intent intent = new Intent(getApplicationContext(), MapsActivity2.class);
                                    intent.putExtra("token", response);
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
                        params.put("username", username);
                        params.put("password", password);

                        return params;
                    }
                };
                postRequest.setRetryPolicy(new DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(postRequest);
                break;
        }
        return true;
    }
}
