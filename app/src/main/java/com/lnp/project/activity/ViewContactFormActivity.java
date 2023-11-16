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
import com.lnp.project.dto.MyListData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ViewContactFormActivity extends AppCompatActivity {

    //    Toolbar mToolbar;
    SharedPreferences sp;
    RecyclerView mRecyclerView;
    TextView textView;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact_form);
//        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.contact_form_layout_Recycler);
        textView = findViewById(R.id.view_text);
        setTitle("Contact Forms");
        sp = getSharedPreferences("login",MODE_PRIVATE);
        String userId = sp.getString("userId", "").trim();
        Boolean admin = sp.getBoolean("admin", false);


        List<MyListData> myListData = new ArrayList<>();

        MyListAdapter adapter = new MyListAdapter(myListData);
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
                String sql = "SELECT * FROM lnp.lnp_contact_forms as lcf inner join lnp.lnp_user_information" +
                        " as lui on lcf.lnp_contact_forms_user_id = lui.lnp_user_information_user_id";
                if(userId != null && !admin) {
                    sql += " where lnp_contact_forms_user_id = "+ Integer.parseInt(userId);
                }
                sql += " order by idlnp_contact_form_id desc";
//                Toast.makeText(getApplicationContext(), "Message "+ sql , Toast.LENGTH_SHORT).show();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    myListData.add(new MyListData(rs.getString("lnp_contact_forms_form_type"),
                            "Full Name: "+rs.getString("lnp_contact_forms_full_name"),
                            "Address: "+ rs.getString("lnp_contact_forms_address"),
                            "Mobile: "+ rs.getString("lnp_contact_forms_mobile"),
                            rs.getString("lnp_contact_forms_loan_category") == null ? null : "Loan Category: "+ rs.getString("lnp_contact_forms_loan_category"),
                            rs.getString("lnp_contact_forms_loan_amount") == null ? null : "Loan Amount: "+ rs.getString("lnp_contact_forms_loan_amount"),
                            rs.getString("lnp_contact_forms_loan_type") == null ? null : "Loan Type: "+ rs.getString("lnp_contact_forms_loan_type"),
                            rs.getString("lnp_contact_forms_engineer_building") == null ? null : "Engineer Building: "+ rs.getString("lnp_contact_forms_engineer_building"),
                            rs.getString("lnp_contact_forms_engineer_service") == null ? null : "Engineer Service: "+ rs.getString("lnp_contact_forms_engineer_service"),
                            rs.getString("lnp_contact_forms_ca_service") == null ? null : "CA Service: "+ rs.getString("lnp_contact_forms_ca_service"),
                            rs.getString("lnp_contact_forms_tenure") == null ? null : "Tenure: "+ rs.getString("lnp_contact_forms_tenure"),
                            rs.getString("lnp_contact_forms_saving_amount") == null ? null : "Savings Amount: "+ rs.getString("lnp_contact_forms_saving_amount"),
                            rs.getString("lnp_contact_forms_user_id") == null ? null : "Added By: LNP-ID-"+ rs.getString("lnp_contact_forms_user_id"),
                            rs.getString("lnp_user_information_first_name") == null && rs.getString("lnp_user_information_second_name") == null? null :
                                    "Added User: "+ rs.getString("lnp_user_information_first_name") +" "+rs.getString("lnp_user_information_second_name"),
                            rs.getString("lnp_contact_forms_insert_date") == null ? null : "Date: "+rs.getString("lnp_contact_forms_insert_date")
                    ));
                }

                runOnUiThread(() -> {
                    if(CollectionUtils.isEmpty(myListData)) {
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