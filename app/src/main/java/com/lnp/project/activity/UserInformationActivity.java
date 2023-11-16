package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.lnp.project.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInformationActivity extends AppCompatActivity {

    SharedPreferences sp;
    private ProgressDialog progressBar;
    private Button submit;
    private EditText firstName, lastName, phoneNumber, cityName;

    private Spinner districtName, stateName;
    private String banner, districtBanner;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    List<String> states = new ArrayList<>();
    List<String> districts = new ArrayList<>();
    Map<String, Integer> districtMap = new HashMap<>();
    Map<String, Integer> stateMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.email_id);
        cityName = findViewById(R.id.city_name);
        districtName = findViewById(R.id.district_name);
        stateName = findViewById(R.id.state_name);
        submit = findViewById(R.id.submit_button);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        progressBar=new ProgressDialog(this);

        states.add("Select State/UT");
        districts.add("Select District");
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states);
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districts);
        //set the spinners adapter to the previously created one.
        stateName.setAdapter(adapter);
        districtName.setAdapter(districtAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
            }
        });

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "Select * from lnp.lnp_state";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);
                while (rs.next()) {
                    stateMap.put(rs.getString("lnp_state_name"), rs.getInt("idlnp_state_id"));

                }
                states.addAll(stateMap.keySet());
                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

        stateName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                banner = adapterView.getItemAtPosition(i).toString();

                if(!banner.contains("Select")) {
                    districtMap.clear();
                    districts.clear();
                    new Thread(() -> {
                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            String sql = "SELECT ld.idlnp_district_id, ld.lnp_district_name FROM lnp.lnp_district as ld inner join lnp.lnp_state as ls\n" +
                                    " on ld.lnp_district_state_id = ls.idlnp_state_id\n" +
                                    "where ls.lnp_state_name = '"+ banner +"'";
                            Statement statement = connection.createStatement();
                            ResultSet rs = statement.executeQuery(sql);
                            while (rs.next()) {
                                districtMap.put(rs.getString("lnp_district_name"), rs.getInt("idlnp_district_id"));
                            }
                            districts.add(0, "Select District");
                            districts.addAll(districtMap.keySet());
                            runOnUiThread(() -> {
                                districtAdapter.notifyDataSetChanged();
                            });
                        } catch (Exception e) {
                            Log.e("InfoAsyncTask", "Error reading school information", e);
                        }

                    }).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        districtName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                districtBanner = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void submitData() {

        String mFirstName = firstName.getText().toString().trim();
        String mLastName = lastName.getText().toString().trim();
        String mPhoneNumber = phoneNumber.getText().toString().trim();
        String mCity = cityName.getText().toString().trim();

        if(TextUtils.isEmpty(mFirstName) || TextUtils.isEmpty(mLastName) || TextUtils.isEmpty(mPhoneNumber)
            || TextUtils.isEmpty(mCity) || TextUtils.isEmpty(districtBanner) || TextUtils.isEmpty(banner))
        {
            Toast.makeText(getApplicationContext(),"Please fill all the details!",Toast.LENGTH_SHORT).show();
            return;
        }

        sp = getSharedPreferences("login",MODE_PRIVATE);
        Integer userId = Integer.parseInt(sp.getString("userId", ""));

        String insertUserInformation = "INSERT INTO lnp.lnp_user_information VALUES ("+null+", '"+mFirstName+"', '"+mLastName+"', '"+ mPhoneNumber +"', '"+
                mCity +"', "+ districtMap.get(districtBanner)+ ", "+ stateMap.get(banner) +", " +userId +", "+ null + ", "+ null +")";


        progressBar.setTitle("Data Saving");
        progressBar.setMessage("Wait For A While");
        progressBar.show();
        progressBar.setCanceledOnTouchOutside(true);

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                Statement statement = connection.createStatement();
                statement.executeUpdate(insertUserInformation);
                sp.edit().putBoolean("userInfoAvail",true).apply();
                sp.edit().putBoolean("retailer",false).apply();

                sp.edit().putBoolean("admin",false).apply();
                runOnUiThread(() -> {
                    progressBar.hide();
                    Toast.makeText(getApplicationContext(), "Data saved successfully, Thank You for Choosing LNP Mini Bank!", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(UserInformationActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

    }
}