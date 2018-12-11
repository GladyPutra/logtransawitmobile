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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText txtuser,txtpass;
    private Button btn_login;
    private ProgressDialog progressDialog;

    public static final int MY_SOCKET_TIMEOUT_MS = 30000;

    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SharedPrefManager.getInstance(this).isLoggedin())
        {
            finish();
            startActivity(new Intent(this,ProfilActivity.class));
            return;
        }

        txtuser = (EditText) findViewById(R.id.txtuser);
        txtpass = (EditText) findViewById(R.id.txtpass);
        btn_login = (Button) findViewById(R.id.btn_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu ...");

        btn_login.setOnClickListener(this);
    }

    private void userLogin()
    {
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://api.gis-riset.online/api/auth/login";

        username = txtuser.getText().toString();
        password = txtpass.getText().toString();

        progressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        progressDialog.dismiss();
                        if(!password.isEmpty() && !username.isEmpty()) {
                            SharedPrefManager.getInstance(getApplicationContext())
                                    .setUsername(txtuser.getText().toString());
                            SharedPrefManager.getInstance(getApplicationContext())
                                    .setPassword(txtpass.getText().toString());
                            SharedPrefManager.getInstance(getApplicationContext())
                                    .setFlag("0");
                            Log.d("Response", response);
                            Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
                            intent.putExtra("token", response);
                            startActivity(intent);
                        }
                        else if(password.isEmpty() && username.isEmpty())
                        {
                            txtpass.setError("Kata Sandi Masih Kosong");
                            txtuser.setError("Nama Pengguna Masih Kosong");
                        }
                        else if(password.isEmpty() && !username.isEmpty())
                        {
                            txtpass.setError("Kata Sandi Masih Kosong");
                        }
                        else if(!password.isEmpty() && username.isEmpty())
                        {
                            txtuser.setError("Nama Pengguna Masih Kosong");
                        }
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
                params.put("username", txtuser.getText().toString());
                params.put("password", txtpass.getText().toString());

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    @Override
    public void onClick(View view) {
        if(view == btn_login)
        {
            userLogin();
        }
//        if(view==txtregister)
//            startActivity(new Intent(this,RegisterActivity.class));
//        if(view == txtlupa)
//        {
//            startActivity(new Intent(this,GeneratePassActivity.class));
//        }
    }
}
