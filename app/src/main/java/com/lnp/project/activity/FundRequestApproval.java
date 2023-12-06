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
import com.lnp.project.adapter.FundRequestAdapter;
import com.lnp.project.adapter.ViewPanCardAdapter;
import com.lnp.project.dto.FundRequestDto;
import com.lnp.project.dto.ViewPanCardDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FundRequestApproval extends AppCompatActivity {

    RecyclerView mFundRequestRecycler;

    TextView mTextView;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_request_approval);
        mFundRequestRecycler= findViewById(R.id.fund_request_approval_recycler);
        mTextView = findViewById(R.id.fund_request_approval_text);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));
        Boolean isAdmin = sp.getBoolean("admin", false);

        List<FundRequestDto> fundRequestDtos = new ArrayList<>();

        FundRequestAdapter adapter = new FundRequestAdapter(fundRequestDtos, FundRequestApproval.this);
        mFundRequestRecycler.setHasFixedSize(true);
        mFundRequestRecycler.setLayoutManager(new LinearLayoutManager(this));
        mFundRequestRecycler.setAdapter(adapter);

        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Wait for a while");
        progressDialog.show();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT * FROM lnp.fund_request as fr inner join lnp.lnp_user_information as lui" +
                        " on fr.fund_request_user_id = lui.lnp_user_information_user_id where fund_request_is_approved = 0";

                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    FundRequestDto fundRequestDto = new FundRequestDto();
                    fundRequestDto.setFundRequestUserName(rs.getString("lnp_user_information_first_name") + " "+ rs.getString("lnp_user_information_second_name"));
                    fundRequestDto.setFundRequestUserId(rs.getString("fund_request_user_id"));
                    fundRequestDto.setFundRequestRupees(rs.getString("fund_request_rupees"));
                    fundRequestDto.setFundRequestDateAdded(rs.getString("fund_request_date_added"));
                    fundRequestDto.setRequestId("FND00"+rs.getString("idfund_request_id"));

                    fundRequestDtos.add(fundRequestDto);
                }

                runOnUiThread(() -> {
                    if(CollectionUtils.isEmpty(fundRequestDtos)) {
                        mTextView.setVisibility(View.VISIBLE);
                        mFundRequestRecycler.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    } else {
                        // passing this array list inside our adapter class.
                        mFundRequestRecycler.setAdapter(adapter);
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