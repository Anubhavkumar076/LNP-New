package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lnp.project.LoginActivity;
import com.lnp.project.R;
import com.lnp.project.common.JWTKey;
import com.lnp.project.responseDto.operatorlist.OperatorListDataDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanCardActivity extends AppCompatActivity {

    private EditText mFirstName, mLastName, mMiddleName, emailId;

    private Spinner mPanCardMode, mGender, mKycType;

    private Button submit;
    private String mode,gender, kycType;

    private ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_card);
        mFirstName = findViewById(R.id.pan_card_first_name);
        mLastName = findViewById(R.id.pan_card_last_name);
        mMiddleName = findViewById(R.id.pan_card_middle_name);
        emailId = findViewById(R.id.pan_card_email);
        mPanCardMode = findViewById(R.id.pan_card_mode);
        mGender = findViewById(R.id.pan_card_gender);
        mKycType = findViewById(R.id.pan_card_kyc_type);
        submit = findViewById(R.id.pan_card_submit);
        progressBar=new ProgressDialog(this);

        String[] modes = new String[]{"Select Mode","Physical Pan", "Electronic Pan"};
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, modes);
        mPanCardMode.setAdapter(modeAdapter);

        String[] genders = new String[]{"Select Gender","MALE", "FEMALE", "TRANSGENDER"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        mGender.setAdapter(genderAdapter);

        String[] kycTypes = new String[]{"Select KYC Type","eKYC", "eSign"};
        ArrayAdapter<String> kycTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kycTypes);
        mKycType.setAdapter(kycTypeAdapter);

        mPanCardMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mode = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mKycType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                kycType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = mFirstName.getText().toString().trim();
                String middleName = mMiddleName.getText().toString().trim();
                String lastName = mLastName.getText().toString().trim();
                String email = emailId.getText().toString().trim();
                progressBar.setTitle("Initiating Pan Card Creation");
                progressBar.setMessage("Wait For A While");
                progressBar.show();
                progressBar.setCanceledOnTouchOutside(true);

                Integer refid =  (int) Math.round(Math.random() * 1000000000);
                progressBar.hide();
                Intent i = new Intent(PanCardActivity.this, PanCardWebViewActivity.class);
                i.putExtra("refid", String.valueOf(refid));
                String title = null;
                if(gender.equals("MALE"))
                    title = "1";
                else if (gender.equals("FEMALE")) {
                    title = "2";
                } else {
                    title = "3";
                }
                i.putExtra("title", title);
                i.putExtra("firstname", firstName);
                i.putExtra("middlename", middleName);
                i.putExtra("lastName", lastName);
                i.putExtra("mode", mode.equals("Physical Pan") ? "P" : "E");
                i.putExtra("gender", gender);
                i.putExtra("redirect_url", "https://www.google.com");
                i.putExtra("email", email);
                i.putExtra("kyctype", kycType.equals("eKYC") ? "K" : "E");
                startActivity(i);

            }
        });

    }
}