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
import com.lnp.project.adapter.MyListAdapter;
import com.lnp.project.adapter.ViewPanCardAdapter;
import com.lnp.project.dto.MyListData;
import com.lnp.project.dto.ViewPanCardDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ViewPanCardInfo extends AppCompatActivity {

    RecyclerView mViewPanCardRecycler;

    TextView mTextView;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pan_card_info);
        mViewPanCardRecycler= findViewById(R.id.view_pan_card_recycler);
        mTextView = findViewById(R.id.view_pan_card_text);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));
        Boolean isAdmin = sp.getBoolean("admin", false);

        List<ViewPanCardDto> viewPanCardDtoArrayList = new ArrayList<>();

        ViewPanCardAdapter adapter = new ViewPanCardAdapter(viewPanCardDtoArrayList, ViewPanCardInfo.this);
        mViewPanCardRecycler.setHasFixedSize(true);
        mViewPanCardRecycler.setLayoutManager(new LinearLayoutManager(this));
        mViewPanCardRecycler.setAdapter(adapter);

        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Wait for a while");
        progressDialog.show();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT * FROM lnp.pan_card_info as pci inner join lnp.lnp_user_information as lui" +
                        " on pci.pan_card_info_user_id = lui.lnp_user_information_user_id";

                if(userIdInt != null && !isAdmin) {
                    sql += " where pan_card_info_user_id = "+ userIdInt;
                }
                sql += " order by idpan_card_info_id desc";

                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {

                    ViewPanCardDto viewPanCardDto = new ViewPanCardDto();
                    viewPanCardDto.setId(String.valueOf(rs.getInt("idpan_card_info_id")));
                    viewPanCardDto.setFirstName("Name: "+ rs.getString("pan_card_info_first_name"));
                    viewPanCardDto.setLastName(rs.getString("pan_card_info_last_name"));
                    viewPanCardDto.setMiddleName(rs.getString("pan_card_info_middle_name"));
                    viewPanCardDto.setRefId(rs.getString("pan_card_info_ref_id"));
                    viewPanCardDto.setGender("Gender: "+ rs.getString("pan_card_info_gender"));
                    viewPanCardDto.setEmail("Email: "+ rs.getString("pan_card_info_email"));
                    viewPanCardDto.setKycType("KYC Type: "+ (rs.getString("pan_card_info_kyctype").equals("K") ? "eKYC" : "eSign"));
                    viewPanCardDto.setMode("Mode: "+ (rs.getString("pan_card_info_mode").equals("P") ? "Physical PAN" : "Electronic PAN"));
                    viewPanCardDto.setUserId("Added By: LNP-ID-"+ (rs.getInt("pan_card_info_user_id")));
                    viewPanCardDto.setUserName("Username: "+ rs.getString("lnp_user_information_first_name") +" "+ rs.getString("lnp_user_information_second_name"));
//                    viewPanCardDto.setDateAdded("Date: "+ rs.getString("pan_card_info_details_added_date"));

                    viewPanCardDtoArrayList.add(viewPanCardDto);

                }

                runOnUiThread(() -> {
                    if(CollectionUtils.isEmpty(viewPanCardDtoArrayList)) {
                        mTextView.setVisibility(View.VISIBLE);
                        mViewPanCardRecycler.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    } else {
                        // passing this array list inside our adapter class.
                        mViewPanCardRecycler.setAdapter(adapter);
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