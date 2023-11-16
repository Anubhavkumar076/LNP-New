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
import com.lnp.project.adapter.QueryListAdapter;
import com.lnp.project.dto.MyListData;
import com.lnp.project.dto.Query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QueryActivity extends AppCompatActivity {

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    private RecyclerView mRecyclerView;
    TextView textView;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        mRecyclerView = findViewById(R.id.query_recycler_view);

        textView = findViewById(R.id.view_text);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        Boolean admin = sp.getBoolean("admin", false);

        List<Query> queryList = new ArrayList<>();

        QueryListAdapter adapter = new QueryListAdapter(queryList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Wait for a while");
        progressDialog.show();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = null;
                if(admin)
                    sql = "SELECT lnp_contact_us_query_query, lnp_user_email, lnp_user_information_first_name, lnp_user_information_second_name,\n" +
                            "lnp_user_information_phone_number, lnp_user_information_city, lnp_district_name, lnp_state_name\n" +
                            " FROM lnp.lnp_contact_us_query as lcuq inner join lnp.lnp_user_information as lui \n" +
                            "on lcuq.lnp_contact_us_query_user_id = lui.lnp_user_information_user_id inner join lnp.lnp_district as ld \n" +
                            "on ld.idlnp_district_id = lui.lnp_user_information_district inner join \n" +
                            "lnp.lnp_state as ls on ls.idlnp_state_id = lui.lnp_user_information_state inner join lnp.lnp_user as lu on\n" +
                            "lu.idlnp_user_id = lui.lnp_user_information_user_id\n" +
                            " where lnp_contact_us_query_reviewer = 'CEO';";
                else
                    sql = "SELECT lnp_contact_us_query_query, lnp_user_email, lnp_user_information_first_name, lnp_user_information_second_name,\n" +
                            "lnp_user_information_phone_number, lnp_user_information_city, lnp_district_name, lnp_state_name\n" +
                            " FROM lnp.lnp_contact_us_query as lcuq inner join lnp.lnp_user_information as lui \n" +
                            "on lcuq.lnp_contact_us_query_user_id = lui.lnp_user_information_user_id inner join lnp.lnp_district as ld \n" +
                            "on ld.idlnp_district_id = lui.lnp_user_information_district inner join \n" +
                            "lnp.lnp_state as ls on ls.idlnp_state_id = lui.lnp_user_information_state inner join lnp.lnp_user as lu on\n" +
                            "lu.idlnp_user_id = lui.lnp_user_information_user_id\n" +
                            " where lnp_contact_us_query_reviewer = 'Complaints';";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    queryList.add(new Query("Full Name: "+ rs.getString("lnp_user_information_first_name") + rs.getString("lnp_user_information_second_name"),
                            "Mobile: "+rs.getString("lnp_user_information_phone_number"),
                            "Email: "+rs.getString("lnp_user_email"),
                            "Query: "+ rs.getString("lnp_contact_us_query_query"),
                            "Address: "+ rs.getString("lnp_user_information_city") +", "+ rs.getString("lnp_district_name") +", "+
                                    rs.getString("lnp_state_name")
                    ));
                }

                runOnUiThread(() -> {
                    if(CollectionUtils.isEmpty(queryList)) {
                        textView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    } else {
                        // passing this array list inside our adapter class.
                        mRecyclerView.setAdapter(adapter);
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