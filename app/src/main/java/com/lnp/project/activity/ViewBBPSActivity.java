package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lnp.project.R;
import com.lnp.project.adapter.ViewBBPSAdapter;
import com.lnp.project.adapter.ViewRechargeAdapter;
import com.lnp.project.dto.ViewBBPSInfoDto;
import com.lnp.project.dto.ViewRechargeInfoDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ViewBBPSActivity extends AppCompatActivity {

    RecyclerView mViewBBPSRecycler;

    TextView mBBPSTextView;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bbpsactivity);

        mViewBBPSRecycler = findViewById(R.id.bbps_recycler);
        mBBPSTextView = findViewById(R.id.view_bbps_text);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));
        Boolean isAdmin = sp.getBoolean("admin", false);
        String category = getIntent().getStringExtra("category");

        List<ViewBBPSInfoDto> viewBBPSInfoDtos = new ArrayList<>();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT * FROM lnp.bbps_info as bi inner join lnp.bbps_info_details" +
                        " as bid on bi.idbbps_info_id = bid.bbps_info_details_info_id  inner join" +
                        " lnp.lnp_user_information" +
                        " as lui on bi.bbps_info_userId = lui.lnp_user_information_user_id where bi.bbps_info_category = '"+category +"'";

                if(userIdInt != null && !isAdmin) {
                    sql += " and bi.bbps_info_userId = "+ userIdInt;
                }
                sql += " order by bi.idbbps_info_id desc";

                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);
                while (rs.next()) {
                    ViewBBPSInfoDto viewBBPSInfoDto = new ViewBBPSInfoDto();
                    viewBBPSInfoDto.setId("ID: "+ rs.getInt("idbbps_info_id"));
                    viewBBPSInfoDto.setOperator("Operator: "+ rs.getString("bbps_info_operator"));
                    viewBBPSInfoDto.setNumber("CA Number: "+ rs.getString("bbps_info_number"));
                    viewBBPSInfoDto.setAmount("Amount: "+ rs.getString("bbps_info_amount"));
                    viewBBPSInfoDto.setRefId("Ref ID: "+ rs.getString("bbps_info_refId"));
                    viewBBPSInfoDto.setMode("Mode: "+ rs.getString("bbps_info_mode"));
                    viewBBPSInfoDto.setCategory("Category: "+ rs.getString("bbps_info_category"));
                    viewBBPSInfoDto.setUserId("Added By: "+ rs.getString("bbps_info_userId"));
                    viewBBPSInfoDto.setTxnId("Txn Id: "+ rs.getString("bbps_info_details_txnid"));
                    viewBBPSInfoDto.setCommission("Commission: "+ rs.getString("bbps_info_details_comm"));
                    viewBBPSInfoDto.setTds("TDS: "+ rs.getString("bbps_info_details_tds"));
                    viewBBPSInfoDto.setRefunded("Refund: "+ rs.getString("bbps_info_details_refunded"));
                    viewBBPSInfoDto.setUserName("Username: "+ rs.getString("lnp_user_information_first_name") +" "+ rs.getString("lnp_user_information_second_name"));
                    viewBBPSInfoDto.setDateAdded("Date: "+ rs.getString("bbps_info_details_dateadded"));
                    viewBBPSInfoDtos.add(viewBBPSInfoDto);
                }
                runOnUiThread(() -> {
                    if(viewBBPSInfoDtos.size() != 0) {
                        ViewBBPSAdapter adapter = new ViewBBPSAdapter(viewBBPSInfoDtos, ViewBBPSActivity.this);
                        mViewBBPSRecycler.setHasFixedSize(true);
                        mViewBBPSRecycler.setLayoutManager(new LinearLayoutManager(this));
                        mViewBBPSRecycler.setAdapter(adapter);
                    } else {
                        mViewBBPSRecycler.setVisibility(View.GONE);
                        mBBPSTextView.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
    }
}