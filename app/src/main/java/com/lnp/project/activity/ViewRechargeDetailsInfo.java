package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.lnp.project.R;
import com.lnp.project.adapter.ViewPanCardAdapter;
import com.lnp.project.adapter.ViewRechargeAdapter;
import com.lnp.project.dto.ViewPanCardDto;
import com.lnp.project.dto.ViewRechargeInfoDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ViewRechargeDetailsInfo extends AppCompatActivity {


    RecyclerView mViewRechargeDetailsRecycler;

    TextView mTextView;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recharge_details_info);
        mViewRechargeDetailsRecycler = findViewById(R.id.view_recharge_recycler);
        mTextView = findViewById(R.id.view_recharge_details_text);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));
        Boolean isAdmin = sp.getBoolean("admin", false);

        List<ViewRechargeInfoDto> viewRechargeDtoArrayList = new ArrayList<>();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT * FROM lnp.recharge_info as ri inner join lnp.recharge_info_details as rid " +
                        "on ri.idrecharge_info_id = rid.recharge_info_details_info_id inner join lnp.lnp_user_information" +
                        " as lui on ri.recharge_info_user_id = lui.lnp_user_information_user_id";

                if(userIdInt != null && !isAdmin) {
                    sql += " where ri.recharge_info_user_id = "+ userIdInt;
                }
                sql += " order by ri.idrecharge_info_id desc";

                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);
                while (rs.next()) {
                    ViewRechargeInfoDto viewRechargeInfoDto = new ViewRechargeInfoDto();
                    viewRechargeInfoDto.setAckNo(rs.getString("recharge_info_ackno"));
                    viewRechargeInfoDto.setId("ID: "+ rs.getInt("idrecharge_info_id"));
                    viewRechargeInfoDto.setAmount("Amount: "+ rs.getString("recharge_info_amount"));
                    viewRechargeInfoDto.setAddedBy("AddedBy: LNP-ID-"+ rs.getInt("recharge_info_user_id"));
                    viewRechargeInfoDto.setCommision(rs.getString("recharge_info_details_comm"));
                    viewRechargeInfoDto.setCaNumber("Number: "+ rs.getString("recharge_info_number"));
                    viewRechargeInfoDto.setTds(rs.getString("recharge_info_details_tds"));
                    viewRechargeInfoDto.setRefunded(rs.getString("recharge_info_details_refunded"));
                    viewRechargeInfoDto.setRefId("Ref ID: "+ rs.getString("recharge_info_ref_id"));
                    viewRechargeInfoDto.setOperator("Operator: "+ rs.getString("recharge_info_details_operator_name"));
                    viewRechargeInfoDto.setUserName("Username: "+ rs.getString("lnp_user_information_first_name") +" "+ rs.getString("lnp_user_information_second_name"));
                    viewRechargeInfoDto.setDate("Date: "+ rs.getString("bbps_info_details_dateadded"));
                    viewRechargeDtoArrayList.add(viewRechargeInfoDto);
                }
                runOnUiThread(() -> {
                    ViewRechargeAdapter adapter = new ViewRechargeAdapter(viewRechargeDtoArrayList, ViewRechargeDetailsInfo.this);
                    mViewRechargeDetailsRecycler.setHasFixedSize(true);
                    mViewRechargeDetailsRecycler.setLayoutManager(new LinearLayoutManager(this));
                    mViewRechargeDetailsRecycler.setAdapter(adapter);
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
    }
}