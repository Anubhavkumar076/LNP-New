package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lnp.project.LoginActivity;
import com.lnp.project.R;
import com.lnp.project.adapter.ImageAdapter;
import com.lnp.project.common.JWTKey;
import com.lnp.project.responseDto.bbps.BBPSItemResponseDto;
import com.lnp.project.responseDto.hlrcheck.HlrCheckDto;
import com.lnp.project.responseDto.hlrcheck.HlrCheckInfoDto;
import com.lnp.project.responseDto.operatorlist.OperatorListDataDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BBPSUtilActivity extends AppCompatActivity {

    GridView bbpsGridView;


    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbpsutil);
        bbpsGridView = findViewById(R.id.bbps_util);

        String utilityName[]= {"EMI", "Broadband", "Bill Payment", "Insurance", "Water", "DTH",
                    "Landline", "Electricity", "Cable", "Fastag", "LPG", "View Entries"};

        int utilityImage[]={
                R.drawable.loans, R.drawable.caservice, R.drawable.caservice, R.drawable.caservice,
                R.drawable.caservice, R.drawable.caservice, R.drawable.caservice, R.drawable.caservice,
                R.drawable.caservice, R.drawable.caservice, R.drawable.caservice, R.drawable.caservice
        };

        ImageAdapter utilityAdapter = new ImageAdapter(BBPSUtilActivity.this, utilityImage, utilityName);
        bbpsGridView.setAdapter(utilityAdapter);

        bbpsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (position == 11) {
                    Intent i = new Intent(getApplicationContext(), ViewBBPSActivity.class);
                    startActivity(i);
                    return;
                }

                String url = "https://paysprint.in/service-api/api/v1/service/bill-payment/bill/getoperator";

                // creating a new variable for our request queue
                RequestQueue queue = Volley.newRequestQueue(BBPSUtilActivity.this);
                List<BBPSItemResponseDto> bbpsItemResponseDtoList = new ArrayList<>();

                StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // on below line we are displaying a success toast message.
                        try {
                            JSONObject respObj = new JSONObject(response);
                            JSONArray respArray = respObj.getJSONArray("data");
                            if (respArray != null) {
                                for (int i=0;i<respArray.length();i++){
                                    JSONObject object=respArray.getJSONObject(i);
                                    BBPSItemResponseDto bbpsItemResponseDto = new BBPSItemResponseDto();
                                    bbpsItemResponseDto.setCategory(object.getString("category"));
                                    bbpsItemResponseDto.setId(object.getString("id"));
                                    bbpsItemResponseDto.setName(object.getString("name"));
                                    bbpsItemResponseDtoList.add(bbpsItemResponseDto);
                                }
                            }

                            Intent i = new Intent(getApplicationContext(), BBPSActivity.class);
                            i.putExtra("category", (Serializable) bbpsItemResponseDtoList.stream()
                                    .filter(x -> x.getCategory().equals(utilityName[position])).collect(Collectors.toList()));
                            startActivity(i);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BBPSUtilActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }

                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        JWTKey jwtKey = new JWTKey();
                        String jwtKeyString = jwtKey.getToken();
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Authorisedkey", "NjAxMjlhZGQ5MjMwODNiZTMwYzFjNGQwYWRlM2QwNmU=");
                        params.put("Token", jwtKeyString);

                        return params;
                    }
                };
                queue.add(request);

            }
        });
    }
}