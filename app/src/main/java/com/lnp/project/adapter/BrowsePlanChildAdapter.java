package com.lnp.project.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.firebase.auth.FirebaseAuth;
import com.lnp.project.LoginActivity;
import com.lnp.project.R;
import com.lnp.project.activity.MainActivity;
import com.lnp.project.activity.MobileRecharge;
import com.lnp.project.activity.UserInformationActivity;
import com.lnp.project.common.JWTKey;
import com.lnp.project.dto.BrowsePlanDto;
import com.lnp.project.responseDto.operatorlist.OperatorListDataDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowsePlanChildAdapter extends RecyclerView
        .Adapter<BrowsePlanChildAdapter.ViewHolder>{

    private List<BrowsePlanDto> browsePlanDtoList;
    Integer operatorId, responseCode, random;
    Integer userIdInt;
    String mobileNumber, message, ackNo;
    Context cxt;

    private ProgressDialog progressBar;

    SharedPreferences sp;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    Integer wallet = 0;

    public BrowsePlanChildAdapter(List<BrowsePlanDto> browsePlanDtoList, Context cxt, Integer operatorId, String mobileNumber) {
        this.browsePlanDtoList = browsePlanDtoList;
        this.cxt = cxt;
        this.operatorId = operatorId;
        this.mobileNumber = mobileNumber;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Here we inflate the corresponding
        // layout of the child item
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.browse_plan_child_list,
                        parent, false);

        sp = cxt.getSharedPreferences("login",MODE_PRIVATE);
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

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        progressBar=new ProgressDialog(cxt);
        // Create an instance of the ChildItem
        // class for the given position
        BrowsePlanDto childItem
                = browsePlanDtoList.get(position);

        // For the created instance, set title.
        // No need to set the image for
        // the ImageViews because we have
        // provided the source for the images
        // in the layout file itself
        holder.browserPlanChildRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.recharge.setVisibility(View.VISIBLE);
            }
        });

        holder.recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Rupees: "+ childItem.getRupees() +" \nValidity: "+ childItem.getValidity());
                alertbox.setTitle("Recharge");
                alertbox.setCancelable(false);

                alertbox.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                progressBar.setTitle("Recharge in progress");
                                progressBar.setMessage("Wait For A While");
                                progressBar.show();
                                progressBar.setCanceledOnTouchOutside(true);
                                if(wallet >= Integer.parseInt(childItem.getRupees()))
                                    doRecharge(operatorId, Integer.parseInt(childItem.getRupees()), mobileNumber, userIdInt);
                                else
                                    Toast.makeText(cxt, "You have insufficient balance. Please contact admin for recharge.", Toast.LENGTH_SHORT).show();
                            }
                        });

                alertbox.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                alertbox.show();
            }
        });
        holder.rupeesBrowsePlan.setText("Rupees: "+ childItem.getRupees());
        holder.validityBrowsePlan.setText("Validity: "+ childItem.getValidity());
        holder.descriptionBrowsePlan.setText("Description: "+ childItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return browsePlanDtoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout browserPlanChildRelative;
        private TextView rupeesBrowsePlan, validityBrowsePlan, descriptionBrowsePlan;
        private Button recharge;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            browserPlanChildRelative = itemView.findViewById(R.id.browse_plan_child_relative);
            rupeesBrowsePlan = itemView.findViewById(R.id.browse_plan_child_rupees);
            validityBrowsePlan = itemView.findViewById(R.id.browse_plan_child_validity);
            descriptionBrowsePlan = itemView.findViewById(R.id.browse_plan_child_description);
            recharge = itemView.findViewById(R.id.recharge_button);

        }
    }

    private void doRecharge(Integer operatorId, Integer rupees, String mobileNumber, Integer userIdInt) {
        // url to post our data
        String url = "http://www.techfolkapi.in/Paysprint/DoRecharge";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(cxt);
        JSONObject params = new JSONObject();
        // on below line we are passing our key
        // and value pair to our parameters.
        try {
            params.put("userId", "TECHFOLK");
            params.put("operatorid", operatorId);
            params.put("canumber", Long.valueOf(mobileNumber));
            params.put("amount", rupees);
            random = (int) Math.round(Math.random() * 1000000000);
            params.put("transactionId", String.valueOf(random));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

//        '{"userId":"TECHFOLK","operatorid":"1",
//    "canumber":"8376852504","amount":"100","transactionId":"35788888888"}'

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                response[0] = respObj;
                try {
                    JSONObject respObj = response.getJSONObject("responseData");
                    responseCode = respObj.getInt("response_code");
                    message = respObj.getString("message");
                    ackNo = respObj.getString("ackno");

                    Toast.makeText(cxt.getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    if(responseCode.equals(1)) {
                        saveRechargeData(String.valueOf(operatorId), mobileNumber, String.valueOf(rupees),
                                String.valueOf(random), ackNo, userIdInt);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(cxt, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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

    public void saveRechargeData(String operatorId, String number, String amount, String refId, String ackNo, Integer userIdInt) {
        StringBuilder queryToCreate = new StringBuilder();
        queryToCreate.append("INSERT INTO lnp.recharge_info VALUES ("+null+", '"+ operatorId +"' , '"+ number
                +"', '"+ amount +"', '"+ refId +"', '"+ ackNo +"', "+ userIdInt +")");

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

                rechargeStatusEnquiry(Integer.parseInt(refId) , generatedKey, userIdInt);

            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
    }

    private void rechargeStatusEnquiry(Integer referenceId, Integer rechargeInfoId, Integer userIdInt) {
        // url to post our data
        String url = "http://www.techfolkapi.in/Paysprint/RechargeStatusEnquiry";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(cxt);
        JSONObject params = new JSONObject();
        // on below line we are passing our key
        // and value pair to our parameters.
        try {
            params.put("userId", "TECHFOLK");
            params.put("referenceid", referenceId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject respObj = response.getJSONObject("responseData");

//                    responseCode = respObj.getInt("response_code");
                    message = respObj.getString("message");
                    JSONObject jsonObject = respObj.getJSONObject("data");
                    String txnId = jsonObject.getString("txnid");
                    String operatorname = jsonObject.getString("operatorname");
                    Double comm = Double.parseDouble(jsonObject.getString("comm"));
                    String tds = jsonObject.getString("tds");
                    String dateadded = jsonObject.getString("dateadded");
                    String refunded = jsonObject.getString("refunded");
                    String refundtxnid = jsonObject.getString("refundtxnid");
                    String daterefunded = jsonObject.getString("daterefunded");
                    final Double[] amount = {Double.parseDouble(jsonObject.getString("amount"))};

                    StringBuilder queryToCreate = new StringBuilder();
                    queryToCreate.append("INSERT INTO lnp.recharge_info_details VALUES ("+null+", "+ rechargeInfoId +" , '"+ txnId
                            +"', '"+ operatorname +"', '"+ comm +"', '"+ tds +"', '"+ dateadded +"', '"+ refunded +"', '"
                            + refundtxnid +"')");
                    new Thread(() -> {
                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

                            String sql = "Select * from lnp.lnp_user where idlnp_user_id = '"+userIdInt;
                            Statement statement = connection.createStatement();
                            ResultSet rs = statement.executeQuery(sql);
                            Double rechargeCom = null;
                            while (rs.next()) {
                                rechargeCom = rs.getDouble("lnp_user_recharge_comm");

                            }
                            statement = connection.createStatement();
                            statement.executeUpdate(queryToCreate.toString());

                            Double commission = (Double) comm/100 * rechargeCom;
                            Double finalAmount = amount[0] - commission;

                            statement = connection.createStatement();
                            statement.executeUpdate(queryToCreate.toString());
                            sql = "UPDATE lnp.lnp_user SET lnp_user_debit_fund = lnp_user_debit_fund - "+ finalAmount +" where idlnp_user_id = "+ userIdInt;
                            Statement amountUpdate = connection.createStatement();
                            amountUpdate.executeUpdate(sql);

                        } catch (Exception e) {
                            Log.e("InfoAsyncTask", "Error reading school information", e);
                        }

                    }).start();

                    progressBar.hide();
                    Toast.makeText(cxt, "Recharge done successfully!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(cxt, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    cxt.startActivity(i);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(cxt, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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
