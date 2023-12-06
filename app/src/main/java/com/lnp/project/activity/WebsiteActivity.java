package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import com.lnp.project.adapter.UserSearchAdapter;
import com.lnp.project.adapter.WebsiteUrlFetchAdapter;
import com.lnp.project.common.WebisteBannerKey;
import com.lnp.project.dto.UserNameAndIdDto;
import com.lnp.project.dto.WebsitShowDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WebsiteActivity extends AppCompatActivity {

    Spinner websiteSpinner, websiteFetchSpinner;
    EditText enterLinkEditText, enterBannerName;
    Button addLinkBtn;

    RecyclerView websiteRecycler;

    String websiteBanner, websiteFetchBanner;

    Integer websiteSaveBannerKey, websiteFetchBannerKey = 0;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        websiteSpinner = findViewById(R.id.website_spinner);
        websiteFetchSpinner = findViewById(R.id.website_fetch_spinner);
        enterLinkEditText = findViewById(R.id.website_url);
        enterBannerName = findViewById(R.id.website_banner);
        addLinkBtn = findViewById(R.id.website_button);
        websiteRecycler = findViewById(R.id.website_recycler);
        ProgressDialog progressDialog
                = new ProgressDialog(this);

        String[] items = new String[]{"Select Banner","Government", "e-Courses", "e-Books", "Others"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        ArrayAdapter<String> websiteFetchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        websiteSpinner.setAdapter(adapter);
        websiteFetchSpinner.setAdapter(websiteFetchAdapter);


        websiteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                websiteBanner = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Adding...");
                progressDialog.setMessage("Wait for a while");
                progressDialog.show();
                Boolean updateValue = true;
                if (websiteBanner.equals("Government")) {
                    websiteSaveBannerKey = WebisteBannerKey.GOVERNMENT;
                } else if (websiteBanner.equals("e-Courses")) {
                    websiteSaveBannerKey = WebisteBannerKey.ECOURSES;
                } else if (websiteBanner.equals("e-Books")) {
                    websiteSaveBannerKey = WebisteBannerKey.EBOOKS;
                } else if (websiteBanner.equals("Others")) {
                    websiteSaveBannerKey = WebisteBannerKey.OTHERS;
                } else {
                    Toast.makeText(WebsiteActivity.this,
                            "No banner selected.",
                            Toast.LENGTH_SHORT).show();
                    updateValue = false;
                }
                String editLinkUrl = enterLinkEditText.getText().toString().trim();
                String bannerName = enterBannerName.getText().toString().trim();
                if (TextUtils.isEmpty(editLinkUrl) || TextUtils.isEmpty(bannerName)) {
                    Toast.makeText(WebsiteActivity.this,
                            "Please enter Link and its Banner!",
                            Toast.LENGTH_SHORT).show();
                    updateValue = false;
                }

                if (updateValue) {
                    new Thread(() -> {
                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            String sql = "INSERT into lnp.website_url(website_url_text, website_url_category_key," +
                                    " website_url_banner) values ('"+editLinkUrl+"', "+ websiteSaveBannerKey +", '"+bannerName+"')";
                            Statement statement = connection.createStatement();
                            statement.executeUpdate(sql);
                            runOnUiThread(() -> {
                                progressDialog.hide();
                                Toast.makeText(WebsiteActivity.this, "Website Url Added. Please refresh Banner below!",
                                        Toast.LENGTH_SHORT).show();
                                enterLinkEditText.getText().clear();
                                enterBannerName.getText().clear();
                            });

                        } catch (Exception e) {
                            Log.e("InfoAsyncTask", "Error reading school information", e);
                        }

                    }).start();
                }
            }
        });

        websiteFetchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                websiteFetchBanner = adapterView.getItemAtPosition(i).toString();
                if (websiteFetchBanner.equals("Government")) {
                    websiteFetchBannerKey = WebisteBannerKey.GOVERNMENT;
                } else if (websiteFetchBanner.equals("e-Courses")) {
                    websiteFetchBannerKey = WebisteBannerKey.ECOURSES;
                } else if (websiteFetchBanner.equals("e-Books")) {
                    websiteFetchBannerKey = WebisteBannerKey.EBOOKS;
                } else if (websiteFetchBanner.equals("Others")) {
                    websiteFetchBannerKey = WebisteBannerKey.OTHERS;
                } else {
                    websiteFetchBannerKey = 0;
                }

                if (websiteFetchBannerKey != 0) {
                    List<WebsitShowDto> websitShowDtos = new ArrayList<>();
                    new Thread(() -> {
                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            String searchSql = "select * from lnp.website_url where website_url_category_key = "+ websiteFetchBannerKey;
                            Statement statement = connection.createStatement();
                            ResultSet rs = statement.executeQuery(searchSql);
                            while (rs.next()) {
                                WebsitShowDto websitShowDto = new WebsitShowDto();
                                websitShowDto.setWebsiteBannerUrl(rs.getString("website_url_text"));
                                websitShowDto.setWebsiteBannerName(rs.getString("website_url_banner"));
                                websitShowDto.setId(rs.getInt("idwebsite_url_id"));
                                websitShowDtos.add(websitShowDto);
                            }

                            runOnUiThread(() -> {
                                WebsiteUrlFetchAdapter adapter = new WebsiteUrlFetchAdapter(WebsiteActivity.this, websitShowDtos);
                                websiteRecycler.setHasFixedSize(true);
                                websiteRecycler.setLayoutManager(new LinearLayoutManager(WebsiteActivity.this));
                                websiteRecycler.setAdapter(adapter);
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


    }
}