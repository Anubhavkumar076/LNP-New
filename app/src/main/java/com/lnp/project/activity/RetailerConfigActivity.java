package com.lnp.project.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lnp.project.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RetailerConfigActivity extends AppCompatActivity {

    TextView retailerConfigName, retailerConfigId, retailerConfigRechargeComm,
            retailerConfigPanComm, retailerConfigBbpsComm;

    EditText retailerConfigRechargeCommEdit, retailerConfigPanCommEdit, retailerConfigBBPSCommEdit,
            retailerConfigFundEdit;

    Button retailerConfigCommBtn, retailerConfigAddFundBtn;
    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    SharedPreferences sp;
    Integer userIdInt = null;

    Double panCommVar, rechargeCommVar, bbpsComVar;
    private ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_config);
        retailerConfigName = findViewById(R.id.retailer_config_name);
        retailerConfigId = findViewById(R.id.retailer_config_id);
        retailerConfigRechargeComm = findViewById(R.id.retailer_config_recharge_comm);
        retailerConfigPanComm = findViewById(R.id.retailer_config_pan_comm);
        retailerConfigBbpsComm = findViewById(R.id.retailer_config_bbps_comm);
        retailerConfigPanCommEdit = findViewById(R.id.retailer_config_pan_comm_edit);
        retailerConfigRechargeCommEdit = findViewById(R.id.retailer_config_recharge_comm_edit);
        retailerConfigFundEdit = findViewById(R.id.retailer_config_fund_edit);
        retailerConfigBBPSCommEdit = findViewById(R.id.retailer_config_bbps_comm_edit);
        retailerConfigCommBtn = findViewById(R.id.retailer_config_comm_btn);
        retailerConfigAddFundBtn = findViewById(R.id.retailer_config_add_fund_btn);


        sp = getSharedPreferences("login",MODE_PRIVATE);
        progressBar=new ProgressDialog(this);

        userIdInt = getIntent().getIntExtra("usersearchid", 0);
        if (userIdInt == 0)
            userIdInt = Integer.parseInt(sp.getString("userId", ""));

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT idlnp_user_id, lnp_user_information_first_name, " +
                        "lnp_user_information_second_name, lnp_user_pan_comm, lnp_user_recharge_comm, lnp_user_bbps_comm \n" +
                        "FROM lnp.lnp_user as lu inner JOIN lnp.lnp_user_information as luf on \n" +
                        "lu.idlnp_user_id = luf.lnp_user_information_user_id \n" +
                        "where lu.idlnp_user_id = "+userIdInt;
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                String userIdVar, fullNameVar;
                if(rs.next()) {
                    userIdVar = "LNP-ID-"+ rs.getString("idlnp_user_id").trim();
                    fullNameVar = rs.getString("lnp_user_information_first_name").trim() + " " +
                            rs.getString("lnp_user_information_second_name").trim();
                    panCommVar = rs.getDouble("lnp_user_pan_comm");
                    rechargeCommVar = rs.getDouble("lnp_user_recharge_comm");
                    bbpsComVar = rs.getDouble("lnp_user_bbps_comm");
                } else {
                    fullNameVar = null;
                    userIdVar = null;
                    panCommVar = null;
                    rechargeCommVar = null;
                    bbpsComVar = null;
                }

                runOnUiThread(() -> {
                    retailerConfigId.setText(userIdVar);
                    retailerConfigName.setText(fullNameVar);
                    retailerConfigPanComm.setText(String.valueOf(panCommVar));
                    retailerConfigRechargeComm.setText(String.valueOf(rechargeCommVar));
                    retailerConfigBbpsComm.setText(String.valueOf(bbpsComVar));
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();


        retailerConfigAddFundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String retailerConfigFund = retailerConfigFundEdit.getText().toString().trim();

                if (!retailerConfigFund.isEmpty()) {
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                    StringBuilder sb = new StringBuilder();
                    sb.append("Amount: Rs. "+ retailerConfigFund);
                    alertbox.setTitle("Adding Fund");
                    alertbox.setMessage(sb.toString());
                    alertbox.setCancelable(false);

                    alertbox.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    progressBar.setTitle("Adding Fund");
                                    progressBar.setMessage("Wait For A While");
                                    progressBar.show();
                                    progressBar.setCanceledOnTouchOutside(false);

                                    new Thread(() -> {
                                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

                                            String sql = "UPDATE lnp.lnp_user SET lnp_user_debit_fund = lnp_user_debit_fund + "+ retailerConfigFund +" where idlnp_user_id = "+ userIdInt;
                                            Statement amountUpdate = connection.createStatement();
                                            amountUpdate.executeUpdate(sql);
                                            Toast.makeText(getApplicationContext(), "Fund Added for user LNP-ID-"+userIdInt, Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {
                                            Log.e("InfoAsyncTask", "Error reading school information", e);
                                        }

                                    }).start();
                                }
                            });

                    alertbox.show();
                }
            }
        });

        retailerConfigCommBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rechargeComm = retailerConfigRechargeCommEdit.getText().toString().trim();
                String panComm = retailerConfigPanCommEdit.getText().toString().trim();
                String bbpsComm = retailerConfigBBPSCommEdit.getText().toString().trim();

                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                StringBuilder sb = new StringBuilder();
                sb.append("Recharge: "+ (!rechargeComm.isEmpty() ? rechargeComm : rechargeCommVar) +"%");
                sb.append("\nPan: "+ (!panComm.isEmpty() ? panComm : panCommVar)+"%");
                sb.append("\nBBPS: "+ (!bbpsComm.isEmpty() ? bbpsComm : bbpsComVar)+"%");
                alertbox.setTitle("Commision Slab");
                alertbox.setMessage(sb.toString());
                alertbox.setCancelable(false);

                alertbox.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                progressBar.setTitle("Updating commission");
                                progressBar.setMessage("Wait For A While");
                                progressBar.show();
                                progressBar.setCanceledOnTouchOutside(false);

                                new Thread(() -> {
                                    //UPDATE USER TO RETAILER
                                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                                        String sql = "UPDATE lnp.lnp_user ";

                                        if (!rechargeComm.isEmpty())
                                            sql += "SET lnp_user_recharge_comm = "+ rechargeComm;
                                        if (!panComm.isEmpty())
                                            sql += ", lnp_user_pan_comm = "+ panComm;
                                        if (!bbpsComm.isEmpty())
                                            sql += ", lnp_user_bbps_comm = "+ bbpsComm;

                                        sql += " where idlnp_user_id = "+ userIdInt;
                                        Statement statement = connection.createStatement();
                                        statement.executeUpdate(sql);

                                        runOnUiThread(() -> {
                                            progressBar.hide();
                                            Toast.makeText(getApplicationContext(), "Commission slab updated!", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(RetailerConfigActivity.this, MainActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                            finish();
                                        });

                                    } catch (Exception e) {
                                        Log.e("InfoAsyncTask", "Error reading school information", e);
                                    }

                                }).start();
                            }
                });

                alertbox.show();
            }
        });

    }
}