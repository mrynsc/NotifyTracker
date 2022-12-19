package com.ofo.notifytracker;

import static com.android.volley.VolleyLog.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity  {

    private Button workBtn;
    private Button endBtn;

    private SharedPreferences prefs;

    private RequestQueue queue;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workBtn= findViewById(R.id.workBtn);
        endBtn= findViewById(R.id.endBtn);

        queue = getRequestQueue();

        prefs= getSharedPreferences("isWorking", MODE_PRIVATE);




        workBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs= getSharedPreferences("isWorking", MODE_PRIVATE);
                SharedPreferences.Editor editor= prefs.edit();
                editor.putBoolean("work", true);
                editor.apply();

            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs= getSharedPreferences("isWorking", MODE_PRIVATE);
                SharedPreferences.Editor editor= prefs.edit();
                editor.putBoolean("work", false);
                editor.apply();

            }
        });



        NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(n.isNotificationPolicyAccessGranted()) {
            }else{
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            String packageName = intent.getStringExtra("package");
            String titleData = intent.getStringExtra("title");
            String textData = intent.getStringExtra("text");


            if (prefs.getBoolean("work",true)){
                postData(packageName,titleData,textData, deviceId);
            }else {
                //Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }

        }
    };


    private void postData(String packageName, String title,String message,String deviceId) {
        String url = "www.example.com";


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respObj = new JSONObject(response);

//                    String packageName = respObj.getString("packageName");
//                    String title = respObj.getString("title");
//                    String message = respObj.getString("message");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<String, String>();

                map.put("packageName", packageName);
                map.put("title", title);
                map.put("message", message);
                map.put("deviceId", deviceId);

                return map;
            }
        };

//        request.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {

            queue = Volley.newRequestQueue(getApplicationContext());
        }
        return queue;
    }


}