package com.lnp.project;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProfileFragment extends Fragment {
    TextView userId, userName, emailId, phoneNumber, town, district, state;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    SharedPreferences sp;
    Integer userIdInt = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("User Profile");

        sp = requireActivity().getSharedPreferences("login",MODE_PRIVATE);

        userIdInt = getActivity().getIntent().getIntExtra("usersearchid", 0);
        if (userIdInt == 0)
            userIdInt = Integer.parseInt(sp.getString("userId", ""));

        userId = view.findViewById(R.id.user_profile_user_id);
        userName = view.findViewById(R.id.user_profile_name);
        emailId = view.findViewById(R.id.user_profile_email);
        phoneNumber = view.findViewById(R.id.user_profile_phone);
        town = view.findViewById(R.id.user_profile_town);
        district = view.findViewById(R.id.user_profile_district);
        state = view.findViewById(R.id.user_profile_state);

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

                getActivity().runOnUiThread(() -> {
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


        return view;
    }
}