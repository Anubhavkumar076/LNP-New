package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.lnp.project.R;
import com.lnp.project.adapter.UserSearchAdapter;
import com.lnp.project.adapter.WebsiteUrlFetchAdapter;
import com.lnp.project.adapter.WebsiteViewAdapter;
import com.lnp.project.dto.UserNameAndIdDto;
import com.lnp.project.dto.WebsitShowDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WebsiteViewActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website_view);

        mRecyclerView = findViewById(R.id.website_view_recycler);
        String categoryId = getIntent().getStringExtra("listFetch").toString();
        if (categoryId.equals("0"))
            setTitle("Government Websites");
        else if (categoryId.equals("1"))
            setTitle("e-Courses Websites");
        else if (categoryId.equals("2"))
            setTitle("e-Books Websites");
        else if (categoryId.equals("3"))
            setTitle("Other Websites");

        List<WebsitShowDto> websitShowDtos = new ArrayList<>();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String searchSql = "select * from lnp.website_url where website_url_category_key = "+ Integer.parseInt(categoryId);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(searchSql);
                while (rs.next()) {
                    WebsitShowDto websitShowDto = new WebsitShowDto();
                    websitShowDto.setWebsiteBannerUrl(rs.getString("website_url_banner"));
                    websitShowDto.setWebsiteBannerName(rs.getString("website_url_text"));
                    websitShowDto.setId(rs.getInt("idwebsite_url_id"));
                    websitShowDtos.add(websitShowDto);
                }

                runOnUiThread(() -> {
                    WebsiteViewAdapter adapter = new WebsiteViewAdapter(WebsiteViewActivity.this, websitShowDtos);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(WebsiteViewActivity.this));
                    mRecyclerView.setAdapter(adapter);
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
    }
}