package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lnp.project.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FundRequestActivity extends AppCompatActivity {

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    EditText fundRequestEditText;
    Button requestFund;
    SharedPreferences sp;

    Integer userIdInt, fundRequest;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_request);
        fundRequestEditText = findViewById(R.id.fund_request_edittext);
        requestFund = findViewById(R.id.fund_request_button);

        progressBar=new ProgressDialog(this);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        userIdInt = Integer.parseInt(sp.getString("userId", ""));

        requestFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setTitle("Raising Fund Request");
                progressBar.setMessage("Wait For A While");
                progressBar.show();
                progressBar.setCanceledOnTouchOutside(false);
                fundRequest = Integer.parseInt(fundRequestEditText.getText().toString().trim());
                new InfoAsyncTask().execute();
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    public class InfoAsyncTask extends AsyncTask<Void, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            Map<String, String> info = new HashMap<>();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "INSERT INTO lnp.fund_request VALUES ("+null+", '"+fundRequest +"', "+
                        userIdInt +", 0, '"+ dtf.format(now) +"')";
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
                runOnUiThread(() -> {
                    Toast.makeText(FundRequestActivity.this, "Fund Request Added!", Toast.LENGTH_SHORT).show();
                    progressBar.hide();
                    Intent i = new Intent(FundRequestActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

            return info;
        }
    }
}