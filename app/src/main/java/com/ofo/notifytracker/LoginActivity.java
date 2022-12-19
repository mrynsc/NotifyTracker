package com.ofo.notifytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs= getSharedPreferences("isLogin", MODE_PRIVATE);



        if (prefs.getBoolean("login",false)){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        EditText username = findViewById(R.id.usernameEt);
        EditText password = findViewById(R.id.passwordEt);
        Button button = findViewById(R.id.loginBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String deviceId ="123456789";
                String deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if (!TextUtils.isEmpty(username.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())){
                    loginUser(username.getText().toString().trim(),password.getText().toString().trim()
                            ,deviceId);
                }

            }
        });
    }

    private void loginUser(String username,String password,String deviceId) {
        String url = "example.com";
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response: ", response);
                        //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();

                        if (response.contains("Success.")){
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            SharedPreferences prefs= getSharedPreferences("isLogin", MODE_PRIVATE);
                            SharedPreferences.Editor editor= prefs.edit();
                            editor.putBoolean("login", true);
                            editor.apply();
                        }else {
                            Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_LONG).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("imei",deviceId);

                return params;
            }
        };

        queue.add(stringRequest);
    }



}