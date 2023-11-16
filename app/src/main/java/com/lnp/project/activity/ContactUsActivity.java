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
import java.sql.Statement;

public class ContactUsActivity extends AppCompatActivity {

    private Spinner dropdown;
    private Button sendQuery;
    private EditText query;

    private String banner, reviewer, updateQuery;
    private ProgressDialog progressDialog;
    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        dropdown = findViewById(R.id.contact_us_spinner);
        query = findViewById(R.id.contact_us_query);
        sendQuery = findViewById(R.id.send_query);
        progressDialog
                = new ProgressDialog(this);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        //create a list of items for the spinner.
        String[] items = new String[]{"Select Reviewer","CEO", "Complaints Team"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                banner = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sendQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (banner.equals("CEO")) {
                    reviewer = "CEO";
                } else if (banner.equals("Complaints Team")) {
                    reviewer = "Complaints";
                }
                updateQuery = query.getText().toString().trim();
                if(TextUtils.isEmpty(reviewer) || TextUtils.isEmpty(updateQuery)) {
                    Toast.makeText(getApplicationContext(),"Please fill all the required information!",Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setTitle("Updating...");
                progressDialog.setMessage("Wait for a while");
                progressDialog.show();

                sendQueryToDB();
            }
        });

    }

    private void sendQueryToDB() {

        Integer userId = Integer.parseInt(sp.getString("userId", ""));
        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "INSERT into lnp.lnp_contact_us_query(lnp_contact_us_query_query, lnp_contact_us_query_reviewer, lnp_contact_us_query_user_id) values ('"+updateQuery+"', '"+reviewer+"', '"+userId+"')";
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Query submitted successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
    }
}