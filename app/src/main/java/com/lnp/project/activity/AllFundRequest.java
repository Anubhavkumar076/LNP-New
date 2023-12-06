package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.util.CollectionUtils;
import com.lnp.project.R;
import com.lnp.project.adapter.AllFundRequestAdapter;
import com.lnp.project.adapter.FundRequestAdapter;
import com.lnp.project.dto.FundRequestDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AllFundRequest extends AppCompatActivity {

    RecyclerView mAllFundRequestRecycler;

    TextView mTextView;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_fund_request);
        mAllFundRequestRecycler= findViewById(R.id.view_all_fund_recycler);
        mTextView = findViewById(R.id.view_all_fund_request);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));
        Boolean isAdmin = sp.getBoolean("admin", false);

        List<FundRequestDto> fundRequestDtos = new ArrayList<>();

        AllFundRequestAdapter adapter = new AllFundRequestAdapter(fundRequestDtos, AllFundRequest.this);
        mAllFundRequestRecycler.setHasFixedSize(true);
        mAllFundRequestRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAllFundRequestRecycler.setAdapter(adapter);

        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Wait for a while");
        progressDialog.show();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT * FROM lnp.fund_request as fr inner join lnp.lnp_user_information as lui" +
                        " on fr.fund_request_user_id = lui.lnp_user_information_user_id ";

                if(userIdInt != null && !isAdmin) {
                    sql += " where fund_request_user_id = "+ userIdInt;
                }
                sql += " order by idfund_request_id desc";

                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    FundRequestDto fundRequestDto = new FundRequestDto();
                    String status = null;
                    if(rs.getInt("fund_request_is_approved") == 0)
                        status = "PENDING";
                    else if(rs.getInt("fund_request_is_approved") == 1)
                        status = "APPROVED";
                    else
                        status = "REJECTED";
                    fundRequestDto.setFundRequestUserName(rs.getString("lnp_user_information_first_name") + " "+ rs.getString("lnp_user_information_second_name"));
                    fundRequestDto.setFundRequestUserId(rs.getString("fund_request_user_id"));
                    fundRequestDto.setFundRequestRupees(rs.getString("fund_request_rupees"));
                    fundRequestDto.setFundRequestDateAdded(rs.getString("fund_request_date_added"));
                    fundRequestDto.setRequestId("FND00"+rs.getString("idfund_request_id"));
                    fundRequestDto.setFundRequestStatus(status);

                    fundRequestDtos.add(fundRequestDto);
                }

                runOnUiThread(() -> {
                    if(CollectionUtils.isEmpty(fundRequestDtos)) {
                        mTextView.setVisibility(View.VISIBLE);
                        mAllFundRequestRecycler.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    } else {
                        // passing this array list inside our adapter class.
                        mAllFundRequestRecycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
    }
}