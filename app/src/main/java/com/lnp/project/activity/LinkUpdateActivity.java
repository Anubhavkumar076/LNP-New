package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.lnp.project.LoginActivity;
import com.lnp.project.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class LinkUpdateActivity extends AppCompatActivity {

    private Button updateLinkButton;
    private EditText linkEditText;
    private Spinner dropdown;
    ProgressDialog progressDialog;
    private String banner;
    private String updateKey, updateValue;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_update);

        linkEditText = findViewById(R.id.link_edittext);
        updateLinkButton = findViewById(R.id.update_link);
        dropdown = findViewById(R.id.link_update_spinner);

        progressDialog
                = new ProgressDialog(this);

        //create a list of items for the spinner.
        String[] items = new String[]{"Select Banner","Youtube", "Instagram", "LNP eKart", "phone", "email", "whatsapp"};
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

        updateLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (banner.equals("Youtube")) {
                    updateKey = "Youtube";
                } else if (banner.equals("Instagram")) {
                    updateKey = "Instagram";
                } else if (banner.equals("LNP eKart")) {
                    updateKey = "lnpekart";
                } else if (banner.equals("phone")) {
                    updateKey = "phone";
                } else if (banner.equals("email")) {
                    updateKey = "email";
                } else if (banner.equals("whatsapp")) {
                    updateKey = "whatsapp";
                }
                updateValue = linkEditText.getText().toString().trim();
                if(TextUtils.isEmpty(updateValue) || TextUtils.isEmpty(updateKey)) {
                    Toast.makeText(getApplicationContext(),"Please fill all the required information!",Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setTitle("Updating...");
                progressDialog.setMessage("Wait for a while");
                progressDialog.show();

                uploadLink();
            }
        });
    }

    private void uploadLink() {
        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "UPDATE lnp.lnp_admin_key_value SET lnp_admin_key_value_value = '"+ updateValue +"' where lnp_admin_key_value_key = '"+ updateKey +"'";
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
                runOnUiThread(() -> {

                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), AdminPanelActivity.class);
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