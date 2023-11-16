package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lnp.project.R;
import com.lnp.project.adapter.AdminRetailerVerificationAdapter;
import com.lnp.project.dto.UserNameAndIdDto;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdminUserRetailerVerificationDetails extends AppCompatActivity {

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    private ProgressDialog progressBar;
    ImageView panImage, aadhaarImage;
    Button approve, reject;
    String panUrl, aadhaarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_retailer_verification_details);
        panImage = findViewById(R.id.user_retailer_verification_details_pan);
        aadhaarImage = findViewById(R.id.user_retailer_verification_details_aadhaar);
        approve = findViewById(R.id.user_retailer_verification_details_approve_button);
        reject = findViewById(R.id.user_retailer_verification_details_reject_button);

        progressBar=new ProgressDialog(this);

        progressBar.setTitle("Fetching user info");
        progressBar.setMessage("Wait For A While");
        progressBar.show();
        progressBar.setCanceledOnTouchOutside(false);

        Integer userId = getIntent().getIntExtra("UserVerificationId", 0);

        if (userId.equals(0)) {
            Toast.makeText(AdminUserRetailerVerificationDetails.this, "There is some issue while fetching the data. Please contact Tech Support.", Toast.LENGTH_SHORT).show();
        } else {
            String searchSql = "select * from  lnp.lnp_user_information where lnp_user_information_user_id = "+ userId;

            String finalSearchSql = searchSql;
            new Thread(() -> {
                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(finalSearchSql);
                    while (rs.next()) {
                        panUrl = rs.getString("lnp_user_information_pan_url");
                        aadhaarUrl = rs.getString("lnp_user_information_aadhaar_url");
                    }

                    runOnUiThread(() -> {
                        Picasso.get().load(panUrl.toString()).resize(400,300).into(panImage);
                        Picasso.get().load(aadhaarUrl.toString()).resize(400,300).into(aadhaarImage);
                        progressBar.hide();
                    });
                } catch (Exception e) {
                    Log.e("InfoAsyncTask", "Error reading school information", e);
                }

            }).start();

            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setTitle("Approving Retailer");
                    progressBar.setMessage("Wait For A While");
                    progressBar.show();
                    progressBar.setCanceledOnTouchOutside(false);

                    new Thread(() -> {
                        //UPDATE USER TO RETAILER
                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

                            String sql = "UPDATE lnp.lnp_user SET lnp_user_admin = 2 where idlnp_user_id = "+ userId;
                            Statement statement = connection.createStatement();
                            statement.executeUpdate(sql);

                            runOnUiThread(() -> {
                                progressBar.hide();
                                Intent i = new Intent(AdminUserRetailerVerificationDetails.this, AdminRetailerVerification.class);
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

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setTitle("Rejecting Retailer");
                    progressBar.setMessage("Wait For A While");
                    progressBar.show();
                    progressBar.setCanceledOnTouchOutside(false);

                    new Thread(() -> {
                        //UPDATE USER TO RETAILER
                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

                            String sql = "UPDATE lnp.lnp_user_information SET lnp_user_information_pan_url = null, lnp_user_information_aadhaar_url = null where lnp_user_information_user_id = "+ userId;
                            Statement statement = connection.createStatement();
                            statement.executeUpdate(sql);

                            runOnUiThread(() -> {
                                progressBar.hide();
                                Intent i = new Intent(AdminUserRetailerVerificationDetails.this, AdminRetailerVerification.class);
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
        }
    }
}