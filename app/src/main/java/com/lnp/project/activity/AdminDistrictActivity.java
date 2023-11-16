package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.lnp.project.LoginActivity;
import com.lnp.project.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDistrictActivity extends AppCompatActivity {

    private Spinner dropdown, districtDropDown, addStateDropDown;
    private Button deleteDistrict, addDistrict;
    private EditText addDistrictEdit;
    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    List<String> states = new ArrayList<>();
    List<String> districts = new ArrayList<>();
    Map<String, Integer> districtMap = new HashMap<>();
    Map<String, Integer> stateMap = new HashMap<>();
    String banner = null, districtBanner = null;
    private ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_district);
        dropdown = findViewById(R.id.activity_admin_state_spinner);
        districtDropDown = findViewById(R.id.activity_admin_district_spinner);
        deleteDistrict = findViewById(R.id.district_delete);
        addStateDropDown = findViewById(R.id.activity_admin_state_add_spinner);
        addDistrict = findViewById(R.id.admin_district_add_button);
        addDistrictEdit = findViewById(R.id.admin_district_add_edit);

        progressBar=new ProgressDialog(this);
        //create a list of items for the spinner.
        states.add("Select State/UT");
        districts.add("Select District");
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states);
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districts);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        addStateDropDown.setAdapter(adapter);
        districtDropDown.setAdapter(districtAdapter);
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


        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        districtDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                districtBanner = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        deleteDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(!districtBanner.contains("Select")) {
                        try {
                        progressBar.setTitle("Deleting");
                        progressBar.setMessage("Wait For A While");
                        progressBar.show();
                        progressBar.setCanceledOnTouchOutside(false);
                        new Thread(() -> {
                            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                                Integer id = districtMap.get(districtBanner);
                                String sql = "Delete from lnp.lnp_district where idlnp_district_id = "+id;
                                PreparedStatement statement = connection.prepareStatement(sql);
                                Integer i = statement.executeUpdate();

                                if (i > 0) {
                                    System.out.println("Record deleted successfully.");
                                    districtMap.remove(districtBanner);
                                    districts.clear();
                                    districts.addAll(districtMap.keySet());
                                }
                                else
                                    System.out.println("Record not found.");

                                runOnUiThread(() -> {
                                    progressBar.dismiss();
                                    districtAdapter.notifyDataSetChanged();
                                });
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    progressBar.dismiss();
                                    Toast.makeText(getApplicationContext(), "This district is associated with few users. Please contact developer.", Toast.LENGTH_SHORT).show();
                                });
                            }

                        }).start();
                    } catch (Exception ex) {

                        Toast.makeText(getApplicationContext(), "This district is associated with few users. Please contact developer.", Toast.LENGTH_SHORT).show();
                    }
                    }
            }
        });

        addStateDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                banner = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String districtName = addDistrictEdit.getText().toString().trim();
                progressBar.setTitle("Adding...");
                progressBar.setMessage("Wait For A While");
                progressBar.show();
                progressBar.setCanceledOnTouchOutside(true);
                if(!banner.contains("Select")) {
                    Integer stateId = stateMap.get(banner);
                    new Thread(() -> {
                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            String sql = "INSERT into lnp.lnp_district(lnp_district_name, lnp_district_state_id) values ('"+districtName+"', "+stateId+")";
                            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                            statement.execute();

                            ResultSet resultSet = statement.getGeneratedKeys();
                            int generatedKey = 0;
                            if (resultSet.next()) {
                                generatedKey = resultSet.getInt(1);
                                districtMap.put(banner, generatedKey);
                            } else {
                                throw new SQLException("Creating user failed, no rows affected.");
                            }

                            districts.addAll(districtMap.keySet());
                            runOnUiThread(() -> {
                                progressBar.dismiss();
                                districtAdapter.notifyDataSetChanged();
                            });
                        } catch (Exception e) {
                            Log.e("InfoAsyncTask", "Error reading school information", e);
                        }

                    }).start();
                }
            }
        });

    }
}