package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lnp.project.R;
import com.lnp.project.common.JWTKey;
import com.lnp.project.dto.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PanStatusActivity extends AppCompatActivity {

    TextView panMessage, panCardInfoDetailsUtrNo, panCardInfoDetailsAckNo, panCardInfoDetailsAmount,
            panCardInfoDetailsStatus, panCardInfoDetailsAddedDate;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    String panCardInfoDetailsUtrNoString = null, panCardInfoDetailsAckNoString = null,
            panCardInfoDetailsAmountString = null, message= null,
            panCardInfoDetailsStatusString = null, panCardInfoDetailsAddedDateString = null;

    SharedPreferences sp;
    Boolean statusUpdated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_status);
        panMessage = findViewById(R.id.pan_message);
        panCardInfoDetailsUtrNo = findViewById(R.id.pan_card_info_details_utr_no);
        panCardInfoDetailsAckNo = findViewById(R.id.pan_card_info_details_ack_no);
        panCardInfoDetailsAmount = findViewById(R.id.pan_card_info_details_amount);
        panCardInfoDetailsStatus = findViewById(R.id.pan_card_info_details_status);
        panCardInfoDetailsAddedDate = findViewById(R.id.pan_card_info_details_added_date);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));

        String refId = getIntent().getStringExtra("refId").toString();
        String panCardInfoId = getIntent().getStringExtra("panCardInfoId").toString();

        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Wait for a while");
        progressDialog.show();

        String url = "https://paysprint.in/service-api/api/v1/service/pan/V2/pan_status";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(PanStatusActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    // on below line we are parsing the response
                    // to json object to extract data from it.
                    JSONObject respObj = new JSONObject(response);

                    message = respObj.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (message != null) {
                    panMessage.setText("PAN Status: "+ message);
                } else {
                    panMessage.setText("NA");
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PanStatusActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("refid", refId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                JWTKey jwtKey = new JWTKey();
                String jwtKeyString = jwtKey.getToken();
                params.put("Authorisedkey", "NjAxMjlhZGQ5MjMwODNiZTMwYzFjNGQwYWRlM2QwNmU=");
                params.put("Token", jwtKeyString);

                return params;
            }
        };
        queue.add(request);


        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                Statement statement = connection.createStatement();
                String sql = "SELECT * FROM lnp.pan_card_info_details where pan_card_info_details_info_id = "+panCardInfoId;
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    statusUpdated = true;
                    panCardInfoDetailsAckNoString = rs.getString("pan_card_info_details_ack_no");
                    panCardInfoDetailsUtrNoString = rs.getString("pan_card_info_details_utr_no");
                    panCardInfoDetailsAmountString = rs.getString("pan_card_info_details_amount");
                    panCardInfoDetailsStatusString = rs.getString("pan_card_info_details_status");
                    panCardInfoDetailsAddedDateString = rs.getString("pan_card_info_details_added_date");

                    runOnUiThread(() -> {
                        if (panCardInfoDetailsUtrNoString != null)
                            panCardInfoDetailsUtrNo.setText("UTR: "+ panCardInfoDetailsUtrNoString);
                        if (panCardInfoDetailsAckNoString != null)
                            panCardInfoDetailsAckNo.setText("Ack No: "+ panCardInfoDetailsAckNoString);
                        if (panCardInfoDetailsAmountString != null)
                            panCardInfoDetailsAmount.setText("Amount: "+ panCardInfoDetailsAmountString);
                        if (panCardInfoDetailsStatusString != null)
                            panCardInfoDetailsStatus.setText("Status: "+ panCardInfoDetailsStatusString);
                        if (panCardInfoDetailsAddedDateString != null)
                            panCardInfoDetailsAddedDate.setText("Added Date: "+ panCardInfoDetailsAddedDateString);
                        progressDialog.hide();
                    });
                }

                if (!statusUpdated) {
                    String txnUrl = "https://paysprint.in/service-api/api/v1/service/pan/V2/txn_status";

                    // creating a new variable for our request queue
                    RequestQueue txnQueue = Volley.newRequestQueue(PanStatusActivity.this);

                    StringRequest txnRequest = new StringRequest(Request.Method.POST, txnUrl, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject respObj = new JSONObject(response);

                                statusUpdated = true;
                                JSONObject jsonObject = respObj.getJSONObject("data");
                                panCardInfoDetailsUtrNoString = jsonObject.getString("utr_no");
                                panCardInfoDetailsAckNoString = jsonObject.getString("ack_no");
                                panCardInfoDetailsAmountString = jsonObject.getString("amount");
                                panCardInfoDetailsStatusString = jsonObject.getString("status");
                                panCardInfoDetailsAddedDateString = jsonObject.getString("addeddate");

                                StringBuilder queryToCreate = new StringBuilder();
                                queryToCreate.append("INSERT INTO lnp.pan_card_info_details VALUES ("+null+", "+ panCardInfoId
                                        +" , '"+ panCardInfoDetailsUtrNoString
                                        +"', '"+ panCardInfoDetailsAckNoString +"', '"+ panCardInfoDetailsAmountString
                                        +"', '"+ panCardInfoDetailsStatusString +"', '"+ panCardInfoDetailsAddedDateString +"')");
                                new Thread(() -> {
                                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                                        Statement statement = connection.createStatement();
                                        statement.executeUpdate(queryToCreate.toString());

                                    } catch (Exception e) {
                                        Log.e("InfoAsyncTask", "Error reading school information", e);
                                    }

                                    runOnUiThread(() -> {
                                        if (statusUpdated) {
                                            if (panCardInfoDetailsUtrNoString != null)
                                                panCardInfoDetailsUtrNo.setText("UTR: "+ panCardInfoDetailsUtrNoString);
                                            if (panCardInfoDetailsAckNoString != null)
                                                panCardInfoDetailsAckNo.setText("Ack No: "+ panCardInfoDetailsAckNoString);
                                            if (panCardInfoDetailsAmountString != null)
                                                panCardInfoDetailsAmount.setText("Amount: "+ panCardInfoDetailsAmountString);
                                            if (panCardInfoDetailsStatusString != null)
                                                panCardInfoDetailsStatus.setText("Status: "+ panCardInfoDetailsStatusString);
                                            if (panCardInfoDetailsAddedDateString != null)
                                                panCardInfoDetailsAddedDate.setText("Added Date: "+ panCardInfoDetailsAddedDateString);
                                            progressDialog.hide();
                                        } else {
                                            panCardInfoDetailsUtrNo.setText("UTR: NA");
                                            panCardInfoDetailsAckNo.setText("Ack No: NA");
                                            panCardInfoDetailsAmount.setText("Amount: NA");
                                            panCardInfoDetailsStatus.setText("Status: NA");
                                            panCardInfoDetailsAddedDate.setText("Added Date: NA");
                                            progressDialog.hide();
                                        }
                                    });

                                }).start();

                                Toast.makeText(PanStatusActivity.this, "Data fetch successfully!", Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            panCardInfoDetailsUtrNo.setText("UTR: NA");
                            panCardInfoDetailsAckNo.setText("Ack No: NA");
                            panCardInfoDetailsAmount.setText("Amount: NA");
                            panCardInfoDetailsStatus.setText("Status: NA");
                            panCardInfoDetailsAddedDate.setText("Added Date: NA");
                            progressDialog.hide();
                            Toast.makeText(PanStatusActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                        }

                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("refid", refId);
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<String, String>();
                            JWTKey jwtKey = new JWTKey();
                            String jwtKeyString = jwtKey.getToken();
                            params.put("Authorisedkey", "NjAxMjlhZGQ5MjMwODNiZTMwYzFjNGQwYWRlM2QwNmU=");
                            params.put("Token", jwtKeyString);

                            return params;
                        }
                    };
                    txnQueue.add(txnRequest);
                }
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

    }
}