package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnp.project.R;
import com.lnp.project.adapter.BrowsePlanAdapter;
import com.lnp.project.common.JWTKey;
import com.lnp.project.dto.BrowsePlanDto;
import com.lnp.project.dto.BrowsePlanParentDto;
import com.lnp.project.responseDto.browseplan.BrowsePlan;
import com.lnp.project.responseDto.browseplan.BrowsePlanInfoDto;
import com.lnp.project.responseDto.hlrcheck.HlrCheckDto;
import com.lnp.project.responseDto.hlrcheck.HlrCheckInfoDto;
import com.lnp.project.responseDto.operatorlist.OperatorListDataDto;
import com.lnp.project.responseDto.operatorlist.OperatorListDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MobileRecharge extends AppCompatActivity {

    EditText mobileRechargeEdit;

    Button getOperatorButton, browsePlans;
    private ProgressDialog progressBar;
    private RecyclerView mRecyclerView;
    private RelativeLayout browsePlanRelativeLayout;
    BrowsePlanAdapter adapter;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    OperatorListDto operatorListDto = new OperatorListDto();

    BrowsePlan browsePlan = new BrowsePlan();

    Spinner spinnerOperatorList;

    Integer userIdInt;

    SharedPreferences sp;

    private TextView allTextView, fullTTextView, topupTextView, threeGTextView, twoGTextView,
            smsTextview, roamingTextView, comboTextView;

    String mCircle, mOperatorName, number, mOperatorId;
    List<BrowsePlanParentDto> myBrowsePlanList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge);
        mobileRechargeEdit = findViewById(R.id.mobile_number_edittext);
        getOperatorButton = findViewById(R.id.get_operator);
        browsePlans = findViewById(R.id.browse_plans);
        spinnerOperatorList = findViewById(R.id.spinner_mobile_recharge);
        mRecyclerView = findViewById(R.id.mobile_recharge_recyclerview);
        browsePlanRelativeLayout = findViewById(R.id.operator_list_relative);
        allTextView = findViewById(R.id.mobile_recharge_all);
        fullTTextView = findViewById(R.id.mobile_recharge_full_tt);
        topupTextView = findViewById(R.id.mobile_recharge_full_top_up);
        threeGTextView = findViewById(R.id.mobile_recharge_3g);
        twoGTextView = findViewById(R.id.mobile_recharge_2g);
        smsTextview = findViewById(R.id.mobile_recharge_SMS);
        roamingTextView = findViewById(R.id.mobile_recharge_roaming);
        comboTextView = findViewById(R.id.mobile_recharge_combo);
        progressBar=new ProgressDialog(this);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        userIdInt = Integer.parseInt(sp.getString("userId", ""));

        JWTKey jwtKey = new JWTKey();
        String jwtKeyString = jwtKey.getToken();
        getOperatorList(jwtKeyString);
        getOperatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setTitle("Getting Operators");
                progressBar.setMessage("Wait For A While");
                progressBar.show();
                progressBar.setCanceledOnTouchOutside(false);
                String jwtKeyString = jwtKey.getToken();
                number = mobileRechargeEdit.getText().toString().trim();
                hlrCheck(number, jwtKeyString);

            }
        });

        allTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTextView.setBackgroundColor(getResources().getColor(R.color.light_blue_A400));
                fullTTextView.setBackgroundColor(getResources().getColor(R.color.white));
                topupTextView.setBackgroundColor(getResources().getColor(R.color.white));
                threeGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                twoGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                smsTextview.setBackgroundColor(getResources().getColor(R.color.white));
                roamingTextView.setBackgroundColor(getResources().getColor(R.color.white));
                comboTextView.setBackgroundColor(getResources().getColor(R.color.white));
                adapter = new BrowsePlanAdapter(myBrowsePlanList, MobileRecharge.this, Integer.parseInt(mOperatorId), number);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MobileRecharge.this));
                mRecyclerView.setAdapter(adapter);
            }
        });

        fullTTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTTextView.setBackgroundColor(getResources().getColor(R.color.light_blue_A400));

                allTextView.setBackgroundColor(getResources().getColor(R.color.white));
                topupTextView.setBackgroundColor(getResources().getColor(R.color.white));
                threeGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                twoGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                smsTextview.setBackgroundColor(getResources().getColor(R.color.white));
                roamingTextView.setBackgroundColor(getResources().getColor(R.color.white));
                comboTextView.setBackgroundColor(getResources().getColor(R.color.white));
                adapter = new BrowsePlanAdapter(myBrowsePlanList.stream().filter(x -> x.getBrowsePlanHeading().equals("FULLTT")).collect(Collectors.toList()), MobileRecharge.this, Integer.parseInt(mOperatorId), number);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MobileRecharge.this));
                mRecyclerView.setAdapter(adapter);
            }
        });

        threeGTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTTextView.setBackgroundColor(getResources().getColor(R.color.white));
                allTextView.setBackgroundColor(getResources().getColor(R.color.white));
                topupTextView.setBackgroundColor(getResources().getColor(R.color.white));
                threeGTextView.setBackgroundColor(getResources().getColor(R.color.light_blue_A400));
                twoGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                smsTextview.setBackgroundColor(getResources().getColor(R.color.white));
                roamingTextView.setBackgroundColor(getResources().getColor(R.color.white));
                comboTextView.setBackgroundColor(getResources().getColor(R.color.white));
                adapter = new BrowsePlanAdapter(myBrowsePlanList.stream().filter(x -> x.getBrowsePlanHeading().equals("3G/4G")).collect(Collectors.toList()), MobileRecharge.this, Integer.parseInt(mOperatorId), number);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MobileRecharge.this));
                mRecyclerView.setAdapter(adapter);
            }
        });

        topupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTTextView.setBackgroundColor(getResources().getColor(R.color.white));
                allTextView.setBackgroundColor(getResources().getColor(R.color.white));
                topupTextView.setBackgroundColor(getResources().getColor(R.color.light_blue_A400));
                threeGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                twoGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                smsTextview.setBackgroundColor(getResources().getColor(R.color.white));
                roamingTextView.setBackgroundColor(getResources().getColor(R.color.white));
                comboTextView.setBackgroundColor(getResources().getColor(R.color.white));
                adapter = new BrowsePlanAdapter(myBrowsePlanList.stream().filter(x -> x.getBrowsePlanHeading().equals("TOPUP")).collect(Collectors.toList()), MobileRecharge.this, Integer.parseInt(mOperatorId), number);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MobileRecharge.this));
                mRecyclerView.setAdapter(adapter);
            }
        });

        twoGTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTTextView.setBackgroundColor(getResources().getColor(R.color.white));
                allTextView.setBackgroundColor(getResources().getColor(R.color.white));
                topupTextView.setBackgroundColor(getResources().getColor(R.color.white));
                threeGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                twoGTextView.setBackgroundColor(getResources().getColor(R.color.light_blue_A400));
                smsTextview.setBackgroundColor(getResources().getColor(R.color.white));
                roamingTextView.setBackgroundColor(getResources().getColor(R.color.white));
                comboTextView.setBackgroundColor(getResources().getColor(R.color.white));
                adapter = new BrowsePlanAdapter(myBrowsePlanList.stream().filter(x -> x.getBrowsePlanHeading().equals("2G")).collect(Collectors.toList()), MobileRecharge.this, Integer.parseInt(mOperatorId), number);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MobileRecharge.this));
                mRecyclerView.setAdapter(adapter);
            }
        });

        smsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTTextView.setBackgroundColor(getResources().getColor(R.color.white));
                allTextView.setBackgroundColor(getResources().getColor(R.color.white));
                topupTextView.setBackgroundColor(getResources().getColor(R.color.white));
                threeGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                twoGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                smsTextview.setBackgroundColor(getResources().getColor(R.color.light_blue_A400));
                roamingTextView.setBackgroundColor(getResources().getColor(R.color.white));
                comboTextView.setBackgroundColor(getResources().getColor(R.color.white));
                adapter = new BrowsePlanAdapter(myBrowsePlanList.stream().filter(x -> x.getBrowsePlanHeading().equals("SMS")).collect(Collectors.toList()), MobileRecharge.this, Integer.parseInt(mOperatorId), number);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MobileRecharge.this));
                mRecyclerView.setAdapter(adapter);
            }
        });

        roamingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTTextView.setBackgroundColor(getResources().getColor(R.color.white));
                allTextView.setBackgroundColor(getResources().getColor(R.color.white));
                topupTextView.setBackgroundColor(getResources().getColor(R.color.white));
                threeGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                twoGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                smsTextview.setBackgroundColor(getResources().getColor(R.color.white));
                roamingTextView.setBackgroundColor(getResources().getColor(R.color.light_blue_A400));
                comboTextView.setBackgroundColor(getResources().getColor(R.color.white));
                adapter = new BrowsePlanAdapter(myBrowsePlanList.stream().filter(x -> x.getBrowsePlanHeading().equals("Romaing")).collect(Collectors.toList()), MobileRecharge.this, Integer.parseInt(mOperatorId), number);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MobileRecharge.this));
                mRecyclerView.setAdapter(adapter);
            }
        });

        comboTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTTextView.setBackgroundColor(getResources().getColor(R.color.white));
                allTextView.setBackgroundColor(getResources().getColor(R.color.white));
                topupTextView.setBackgroundColor(getResources().getColor(R.color.white));
                threeGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                twoGTextView.setBackgroundColor(getResources().getColor(R.color.white));
                comboTextView.setBackgroundColor(getResources().getColor(R.color.light_blue_A400));
                smsTextview.setBackgroundColor(getResources().getColor(R.color.white));
                roamingTextView.setBackgroundColor(getResources().getColor(R.color.white));
                adapter = new BrowsePlanAdapter(myBrowsePlanList.stream().filter(x -> x.getBrowsePlanHeading().equals("COMBO")).collect(Collectors.toList()), MobileRecharge.this, Integer.parseInt(mOperatorId), number);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MobileRecharge.this));
                mRecyclerView.setAdapter(adapter);
            }
        });

        spinnerOperatorList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mOperatorName = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        browsePlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                browsePlan(mCircle, mOperatorName);
            }
        });
    }

    private void getOperatorList(String token) {
        // url to post our data
        String url = "http://www.techfolkapi.in/Paysprint/RechargeOperatorList";
        JSONObject params = new JSONObject();
        try {
            params.put("userId", "TECHFOLK");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MobileRecharge.this);

        OperatorListDto operatorListDtoLocal = new OperatorListDto();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    // on below line we are parsing the response
                    // to json object to extract data from it.
                    JSONObject respObj = response.getJSONObject("responseData");
//                    StringBuilder queryToCreate = new StringBuilder();
//                    Instant instant = Instant.now();
//                    long timeStampMillis = instant.toEpochMilli();
//                    queryToCreate.append("INSERT INTO lnp.transaction_log VALUES ("+null+", '"+ url +"' , "+ null
//                            +", '"+ response +"', '"+ userIdInt +"', "+ timeStampMillis +")");
//
//                    new Thread(() -> {
//                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//                            Statement statement = connection.createStatement();
//                            statement.executeUpdate(queryToCreate.toString());
//
//                        } catch (Exception e) {
//                            Log.e("InfoAsyncTask", "Error reading school information", e);
//                        }
//
//                    }).start();
                    operatorListDtoLocal.setStatus(respObj.getBoolean("status"));

                    List<OperatorListDataDto> operatorListDataDtos = new ArrayList<>();

                    JSONArray jsonArray = respObj.getJSONArray("data");
                    if (jsonArray != null) {
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject object=jsonArray.getJSONObject(i);
                            OperatorListDataDto operatorListDataDto = new OperatorListDataDto();
                            operatorListDataDto.setCategory(object.getString("category"));
                            operatorListDataDto.setId(object.getString("id"));
                            operatorListDataDto.setName(object.getString("name"));
                            operatorListDataDtos.add(operatorListDataDto);
                        }
                    }
                    operatorListDtoLocal.setOperatorListDataDtos(operatorListDataDtos);
                    operatorListDto = operatorListDtoLocal;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MobileRecharge.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("partnerKey", "TechFolk@2023@#8376852504");

                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    private void hlrCheck(String number, String token) {
        // url to post our data
        String url = "http://www.techfolkapi.in/Paysprint/HLRCheck";
        JSONObject params = new JSONObject();
        try {
            params.put("userId", "TECHFOLK");
            params.put("number", number);
            params.put("type", "mobile");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MobileRecharge.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject respObj = response.getJSONObject("responseData");

//                    StringBuilder queryToCreate = new StringBuilder();
//                    Instant instant = Instant.now();
//                    long timeStampMillis = instant.toEpochMilli();
//                    ObjectMapper obj = new ObjectMapper();
//                    String json = obj.writeValueAsString(requestBodyParams);
//                    queryToCreate.append("INSERT INTO lnp.transaction_log VALUES ("+null+", '"+ url +"' , '"+ json
//                            +"', '"+ response +"', '"+ userIdInt +"', "+ timeStampMillis +")");

//                    new Thread(() -> {
//                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//                            Statement statement = connection.createStatement();
//                            statement.executeUpdate(queryToCreate.toString());
//
//                        } catch (Exception e) {
//                            Log.e("InfoAsyncTask", "Error reading school information", e);
//                        }
//
//                    }).start();

                    HlrCheckDto hlrCheckDto = new HlrCheckDto();
                    hlrCheckDto.setMessage(respObj.getString("message"));
                    hlrCheckDto.setResponseCode(respObj.getInt("response_code"));
                    hlrCheckDto.setStatus(respObj.getBoolean("status"));

                    HlrCheckInfoDto hlrCheckInfoDto = new HlrCheckInfoDto();
                    hlrCheckInfoDto.setOperator(respObj.getJSONObject("info").getString("operator"));
                    hlrCheckInfoDto.setCircle(respObj.getJSONObject("info").getString("circle"));
                    hlrCheckDto.setInfo(hlrCheckInfoDto);
                    // below are the strings which we
                    // extract from our json object.
                    List<String> operatorList = operatorListDto.getOperatorListDataDtos().stream()
                            .map(OperatorListDataDto::getName).collect(Collectors.toList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MobileRecharge.this, android.R.layout.simple_spinner_dropdown_item, operatorList.stream().toArray(String[] ::new));

                    //set the spinners adapter to the previously created one.
                    spinnerOperatorList.setAdapter(adapter);

                    mOperatorName =  operatorListDto.getOperatorListDataDtos().stream()
                            .filter(x -> x.getName().equals(hlrCheckDto.getInfo().getOperator())).collect(Collectors.toList()).get(0).getName();

                    mOperatorId = operatorListDto.getOperatorListDataDtos().stream()
                            .filter(x -> x.getName().equals(hlrCheckDto.getInfo().getOperator())).collect(Collectors.toList()).get(0).getId();

                    mCircle = hlrCheckDto.getInfo().getCircle();
                    spinnerOperatorList.setVisibility(View.VISIBLE);
                    browsePlans.setVisibility(View.VISIBLE);
                    getOperatorButton.setVisibility(View.GONE);
                    progressBar.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MobileRecharge.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("partnerKey", "TechFolk@2023@#8376852504");

                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    private void browsePlan(String circle, String operatorId) {
        // url to post our data
        String url = "http://www.techfolkapi.in/Paysprint/BrowsePlan";
        JSONObject params = new JSONObject();
        try {
            params.put("userId", "TECHFOLK");
            params.put("operatorId", operatorId);
            params.put("circle", circle);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MobileRecharge.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // on below line we are displaying a success toast message.
                try {
                    JSONObject respObj = response.getJSONObject("responseData");
//                    StringBuilder queryToCreate = new StringBuilder();
//                    Instant instant = Instant.now();
//                    long timeStampMillis = instant.toEpochMilli();
//                    ObjectMapper obj = new ObjectMapper();
//                    String json = obj.writeValueAsString(requestBodyParams);
//                    queryToCreate.append("INSERT INTO lnp.transaction_log VALUES ("+null+", '"+ url +"' , '"+ json
//                            +"', '"+ response +"', '"+ userIdInt +"', "+ timeStampMillis +")");
//
//                    new Thread(() -> {
//                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//                            Statement statement = connection.createStatement();
//                            statement.executeUpdate(queryToCreate.toString());
//
//                        } catch (Exception e) {
//                            Log.e("InfoAsyncTask", "Error reading school information", e);
//                        }
//
//                    }).start();
                    browsePlan.setStatus(respObj.getBoolean("status"));
                    browsePlan.setMessage(respObj.getString("message"));
                    browsePlan.setResponseCode(respObj.getInt("response_code"));
                    JSONObject browsePlanInfoObject = respObj.getJSONObject("info");
                    Iterator<String> keys = browsePlanInfoObject.keys();
                    List<BrowsePlanInfoDto> browsePlanInfoDtoList = null;
                    Map<String, List<BrowsePlanInfoDto>> browsePlanInfoDtoMap = new HashMap<>();

                    while (keys.hasNext()) {
                        browsePlanInfoDtoList = new ArrayList<>();
                        String infoKey = keys.next();
                        JSONArray jsonArray = browsePlanInfoObject.get(infoKey) instanceof JSONArray ? browsePlanInfoObject.getJSONArray(infoKey) : new JSONArray();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            BrowsePlanInfoDto browsePlanInfoDto = new BrowsePlanInfoDto();
                            browsePlanInfoDto.setDescription(object.getString("desc"));
                            browsePlanInfoDto.setRupees(object.getString("rs"));
                            browsePlanInfoDto.setValidity(object.getString("validity"));
                            browsePlanInfoDto.setLastUpdate(object.getString("last_update"));
                            browsePlanInfoDtoList.add(browsePlanInfoDto);
                        }
                        browsePlanInfoDtoMap.put(infoKey, browsePlanInfoDtoList);
                    }

                    browsePlan.setBrowsePlanInfoDtoMap(browsePlanInfoDtoMap);
                    browsePlanRelativeLayout.setVisibility(View.VISIBLE);
                    allTextView.setBackgroundColor(getResources().getColor(R.color.light_blue_A400));

                    browsePlanInfoDtoMap.forEach((s, browsePlanInfoDtos) ->
                    {
                        BrowsePlanParentDto browsePlanParentDto = new BrowsePlanParentDto();
                        browsePlanParentDto.setBrowsePlanHeading(s);
                        List<BrowsePlanDto> browsePlanDtoList = new ArrayList<>();

                        browsePlanInfoDtos.stream().forEach(browsePlanInfoDto ->
                        {
                            BrowsePlanDto browsePlanDto = new BrowsePlanDto();
                            browsePlanDto.setRupees(browsePlanInfoDto.getRupees());
                            browsePlanDto.setValidity(browsePlanInfoDto.getValidity());
                            browsePlanDto.setDescription(browsePlanInfoDto.getDescription());
                            browsePlanDtoList.add(browsePlanDto);
                        });
                        browsePlanParentDto.setBrowsePlanDto(browsePlanDtoList);
                        myBrowsePlanList.add(browsePlanParentDto);
                    });

                    mRecyclerView.setVisibility(View.VISIBLE);
                    adapter = new BrowsePlanAdapter(myBrowsePlanList, MobileRecharge.this, Integer.parseInt(mOperatorId), number);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(MobileRecharge.this));
                    mRecyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MobileRecharge.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("partnerKey", "TechFolk@2023@#8376852504");

                return params;
            }
        };
        queue.add(request);
    }
}