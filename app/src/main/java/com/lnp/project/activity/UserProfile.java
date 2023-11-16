package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.lnp.project.R;
import com.lnp.project.dto.SliderData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserProfile extends AppCompatActivity {

    TextView userId, userName, emailId, phoneNumber, town, district, state;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));

        userId = findViewById(R.id.user_profile_user_id);
        userName = findViewById(R.id.user_profile_name);
        emailId = findViewById(R.id.user_profile_email);
        phoneNumber = findViewById(R.id.user_profile_phone);
        town = findViewById(R.id.user_profile_town);
        district = findViewById(R.id.user_profile_district);
        state = findViewById(R.id.user_profile_state);

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT idlnp_user_id, lnp_user_email, lnp_user_information_first_name, lnp_user_information_second_name,\n" +
                        "lnp_user_information_phone_number, lnp_user_information_city, lnp_district_name, lnp_state_name FROM lnp.lnp_user as lu inner JOIN lnp.lnp_user_information as luf on \n" +
                        "lu.idlnp_user_id = luf.lnp_user_information_user_id \n" +
                        "inner join lnp.lnp_district as ld on ld.idlnp_district_id = luf.lnp_user_information_district\n" +
                        "inner join lnp.lnp_state as ls on ls.idlnp_state_id = luf.lnp_user_information_state where lu.idlnp_user_id = "+userIdInt;
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                String userIdVar, fullNameVar, emailVar, phoneVar, cityVar, districtVar, stateVar;
                if(rs.next()) {
                    userIdVar = "LNP-ID-"+ rs.getString("idlnp_user_id").trim();
                    fullNameVar = rs.getString("lnp_user_information_first_name").trim() + " " +
                            rs.getString("lnp_user_information_second_name").trim();
                    emailVar = rs.getString("lnp_user_email").trim().toString();
                    phoneVar = rs.getString("lnp_user_information_phone_number").trim().toString();
                    cityVar = rs.getString("lnp_user_information_city").trim().toString();
                    districtVar = rs.getString("lnp_district_name").trim().toString();
                    stateVar = rs.getString("lnp_state_name").trim().toString();
                } else {
                    fullNameVar = null;
                    emailVar = null;
                    phoneVar = null;
                    cityVar = null;
                    districtVar = null;
                    stateVar = null;
                    userIdVar = null;
                }

                runOnUiThread(() -> {
                    userId.setText(userIdVar);
                    userName.setText(fullNameVar);
                    emailId.setText(emailVar);
                    phoneNumber.setText(phoneVar);
                    town.setText(cityVar);
                    district.setText(districtVar);
                    state.setText(stateVar);
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
    }
}