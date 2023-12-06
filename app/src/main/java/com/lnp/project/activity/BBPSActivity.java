package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lnp.project.R;
import com.lnp.project.common.JWTKey;
import com.lnp.project.responseDto.bbps.BBPSItemResponseDto;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BBPSActivity extends AppCompatActivity {


    EditText caNumber;

    Button fetchBill, payBill;
    TextView nameTextView, amountTextView;

    RelativeLayout bbpsrelative;

    Spinner bbpsOperatorSpinner;

    RadioGroup radioGroup;
    SharedPreferences sp;
    Integer userIdInt;

    RadioButton modeButton;
    String operator;
    List<BBPSItemResponseDto> bbpsItemResponseDtoList;

    private ProgressDialog progressBar;
    Integer wallet = 0;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbpsactivity);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        userIdInt = Integer.parseInt(sp.getString("userId", ""));
        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "SELECT lnp_user_debit_fund FROM lnp.lnp_user where idlnp_user_id = "+ userIdInt;
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    wallet = rs.getInt("lnp_user_debit_fund");
                }

            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
        bbpsOperatorSpinner = findViewById(R.id.bbps_operator_spinner);
        caNumber = findViewById(R.id.bbps_number);
        radioGroup = findViewById(R.id.bbps_radio_group);
        fetchBill = findViewById(R.id.bbps_fetch_bill_button);
        nameTextView = findViewById(R.id.bbps_fetch_bill_name);
        amountTextView = findViewById(R.id.bbps_fetch_bill_amount);
        payBill = findViewById(R.id.bbps_pay_bill);
        bbpsrelative = findViewById(R.id.bbps_relative);
        progressBar=new ProgressDialog(this);

        Intent i = getIntent();
        bbpsItemResponseDtoList = (List<BBPSItemResponseDto>) i.getSerializableExtra("category");

        setTitle(bbpsItemResponseDtoList.get(0).getCategory());
        String[] categoryList = Arrays.copyOf(bbpsItemResponseDtoList.stream().map(BBPSItemResponseDto::getName)
                .collect(Collectors.toSet()).toArray(), bbpsItemResponseDtoList.stream().map(BBPSItemResponseDto::getName)
                .collect(Collectors.toSet()).size(), String[].class);
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryList);
        //set the spinners adapter to the previously created one.
        bbpsOperatorSpinner.setAdapter(adapter);

        bbpsOperatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                operator = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void onclickbuttonMethod(View v){
        int selectedId = radioGroup.getCheckedRadioButtonId();
        modeButton = (RadioButton) findViewById(selectedId);
        Map<String, String> requestBodyParams = new HashMap<>();
        if(selectedId==-1){
            Toast.makeText(BBPSActivity.this,"Nothing selected", Toast.LENGTH_SHORT).show();
        }
        else{
            String number = caNumber.getText().toString().trim();
            String mode = modeButton.getText().toString().trim();

            String operatorId = bbpsItemResponseDtoList.stream().filter(x -> x.getName().equals(operator)).map(BBPSItemResponseDto::getId).collect(Collectors.toList()).get(0);

            String url = "http://www.techfolkapi.in/Paysprint/BillFetch";
            JSONObject params = new JSONObject();
            try {
                params.put("userId", "TECHFOLK");
                params.put("operatorId", operatorId);
                params.put("caNumber", number);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // creating a new variable for our request queue
            RequestQueue queue = Volley.newRequestQueue(BBPSActivity.this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject respObj = response.getJSONObject("responseData");

//                        StringBuilder queryToCreate = new StringBuilder();
//                        Instant instant = Instant.now();
//                        long timeStampMillis = instant.toEpochMilli();
//                        ObjectMapper obj = new ObjectMapper();
//                        String json = obj.writeValueAsString(requestBodyParams);
//                        queryToCreate.append("INSERT INTO lnp.transaction_log VALUES ("+null+", '"+ url +"' , '"+ json
//                                +"', '"+ response +"', '"+ userIdInt +"', "+ timeStampMillis +")");
//
//                        new Thread(() -> {
//                            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//                                Statement statement = connection.createStatement();
//                                statement.executeUpdate(queryToCreate.toString());
//
//                            } catch (Exception e) {
//                                Log.e("InfoAsyncTask", "Error reading school information", e);
//                            }
//
//                        }).start();

                        String name = respObj.getString("name");
                        String amount = respObj.getString("amount");



                        bbpsrelative.setVisibility(View.VISIBLE);

                        nameTextView.setText("Name: "+ name);
                        amountTextView.setText("Bill Amount: "+ amount);

                        JSONObject billFetchObject = respObj.getJSONObject("bill_fetch");

                        payBill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                progressBar.setTitle("Bill payment in progress");
                                progressBar.setMessage("Wait For A While");
                                progressBar.show();
                                progressBar.setCanceledOnTouchOutside(false);
                                Double amountCheck = Double.parseDouble(amount);
                                if(wallet >= amountCheck)
                                    payBill(operatorId, amount, number, mode,
                                            billFetchObject, userIdInt);
                                else
                                    Toast.makeText(BBPSActivity.this, "You have insufficient balance. Please contact admin for recharge.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(BBPSActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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

    }

    private void payBill(String operatorId, String rupees, String mobileNumber, String mode,
                            JSONObject billFetch, Integer userIdInt) {
        // url to post our data
        String url = "http://www.techfolkapi.in/Paysprint/BillPayment";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(BBPSActivity.this);
        JSONObject params = new JSONObject();
        Integer random = (int) Math.round(Math.random() * 1000000000);
        // on below line we are passing our key
        // and value pair to our parameters.
        try {
            params.put("userId", "TECHFOLK");
            params.put("operatorId", operatorId);
            params.put("caNumber", mobileNumber);
            params.put("amount", rupees);
            params.put("transactionId", String.valueOf(random));
            params.put("billFetchData", billFetch);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                response[0] = respObj;
                try {

                    JSONObject respObj = response.getJSONObject("responseData");
//                    StringBuilder queryToCreate = new StringBuilder();
//                    Instant instant = Instant.now();
//                    long timeStampMillis = instant.toEpochMilli();
//                    ObjectMapper obj = new ObjectMapper();
//                    obj.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//                    obj.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//                    String requestjson = obj.writeValueAsString(params);
//                    String responsejson = obj.writeValueAsString(respObj);
//                    queryToCreate.append("INSERT INTO lnp.transaction_log VALUES ("+null+", '"+ url +"' , '"+ requestjson
//                            +"', '"+ responsejson +"', '"+ userIdInt +"', "+ timeStampMillis +")");
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

                    String message = respObj.getString("message");
                    saveBBPSData(operator, mobileNumber, rupees, random, mode, bbpsItemResponseDtoList.get(0).getCategory(), billFetch, userIdInt);
                    progressBar.hide();
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                    message = respObj.getString("message");
//                    ackNo = respObj.getString("ackno");

//                    saveRechargeData(String.valueOf(operatorId), mobileNumber, String.valueOf(rupees),
//                            String.valueOf(random), ackNo, userIdInt);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BBPSActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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

    private void saveBBPSData(String operator, String mobileNumber, String rupees, Integer random, String mode, String category, JSONObject billFetch, Integer userIdInt) {
        StringBuilder queryToCreate = new StringBuilder();
        queryToCreate.append("INSERT INTO lnp.bbps_info VALUES ("+null+", '"+ operator +"' , '"+ mobileNumber
                +"', '"+ rupees +"', '"+ random +"', '"+ mode +"', '"+ category +"', '"+ null +"', "+ userIdInt +")");

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

                PreparedStatement statement = connection.prepareStatement(queryToCreate.toString(), Statement.RETURN_GENERATED_KEYS);
                statement.execute();

                ResultSet resultSet = statement.getGeneratedKeys();
                int generatedKey = 0;
                if (resultSet.next()) {
                    generatedKey = resultSet.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                billPayment(random , generatedKey);

            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
    }

    private void billPayment(Integer random, int bbpsInfoId) {

        // url to post our data
        String url = "http://www.techfolkapi.in/Paysprint/BBPSStatusEnquiry";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(BBPSActivity.this);
        JSONObject params = new JSONObject();
        // on below line we are passing our key
        // and value pair to our parameters.
        try {
            params.put("userId", "TECHFOLK");
            params.put("referenceid", String.valueOf(random));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject respObj = response.getJSONObject("responseData");
                    JSONObject jsonObject = respObj.getJSONObject("data");
                    String txnId = jsonObject.getString("txnid");
                    String operatorname = jsonObject.getString("operatorname");
                    String comm = jsonObject.getString("comm");
                    String tds = jsonObject.getString("tds");
                    String dateadded = jsonObject.getString("dateadded");
                    String refunded = jsonObject.getString("refunded");
                    String refundtxnid = jsonObject.getString("refundtxnid");
                    String amount = jsonObject.getString("amount");

                    StringBuilder queryToCreate = new StringBuilder();
                    queryToCreate.append("INSERT INTO lnp.bbps_info_details VALUES ("+null+", "+ bbpsInfoId +" , '"+ txnId
                            +"', '"+ operatorname +"', '"+ comm +"', '"+ tds +"', '"+ dateadded +"', '"+ refunded +"', '"
                            + refundtxnid +"')");
                    new Thread(() -> {
                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

                            String sql = "Select * from lnp.lnp_user where idlnp_user_id = '"+userIdInt;
                            Statement statement = connection.createStatement();
                            ResultSet rs = statement.executeQuery(sql);
                            Double bbpsCom = null;
                            while (rs.next()) {
                                bbpsCom = rs.getDouble("lnp_user_bbps_comm");

                            }
                            statement = connection.createStatement();
                            statement.executeUpdate(queryToCreate.toString());

                            Double commission = (Double) (Double.parseDouble(comm)/100 * bbpsCom);
                            Double finalAmount = Double.parseDouble(amount) - commission;
                            sql = "UPDATE lnp.lnp_user SET lnp_user_debit_fund = lnp_user_debit_fund - "+ finalAmount +" where idlnp_user_id = "+ userIdInt;
                            Statement amountUpdate = connection.createStatement();
                            amountUpdate.executeUpdate(sql);

                        } catch (Exception e) {
                            Log.e("InfoAsyncTask", "Error reading school information", e);
                        }

                    }).start();

                    progressBar.hide();
                    Toast.makeText(BBPSActivity.this, "Recharge done successfully!", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(BBPSActivity.this, MainActivity.class);
                    startActivity(i);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BBPSActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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
}