package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lnp.project.R;
import com.lnp.project.adapter.UserSearchAdapter;
import com.lnp.project.dto.UserNameAndIdDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserSearchActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    EditText mEditText;
    ImageButton searchButton;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        mEditText = findViewById(R.id.user_search_search);
        searchButton = findViewById(R.id.user_search_button);

        mRecyclerView = findViewById(R.id.user_search_recycler);
        setTitle("User Search");

        List<UserNameAndIdDto> myListData = new ArrayList<>();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "\n" +
                        "select * from lnp.lnp_user as lu inner join lnp.lnp_user_information as lui on \n" +
                        "lu.idlnp_user_id = lui.lnp_user_information_user_id";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);
                while (rs.next()) {
                    UserNameAndIdDto userNameAndIdDto = new UserNameAndIdDto();
                    userNameAndIdDto.setFirstName(rs.getString("lnp_user_information_first_name"));
                    userNameAndIdDto.setSecondName(rs.getString("lnp_user_information_second_name"));
                    userNameAndIdDto.setId(rs.getInt("idlnp_user_id"));
                    myListData.add(userNameAndIdDto);
                }

                runOnUiThread(() -> {
                    UserSearchAdapter adapter = new UserSearchAdapter(UserSearchActivity.this, myListData);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    mRecyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myListData.clear();
                String name = mEditText.getText().toString().trim();
                String searchSql = "select * from lnp.lnp_user as lu inner join lnp.lnp_user_information as lui on \n" +
                        "lu.idlnp_user_id = lui.lnp_user_information_user_id where";
                if(!TextUtils.isEmpty(name.trim())) {
                    if(name.trim().contains(" ")) {
                        String firstName = name.split(" ")[0];
                        String secondName = name.split(" ")[name.split(" ").length-1];
                        searchSql += " lui.lnp_user_information_first_name like '%"+ firstName +"%' ";
                        searchSql += " and lui.lnp_user_information_second_name like '%"+ secondName +"%' ";
                    } else {
                        searchSql += " lui.lnp_user_information_first_name like '%"+ name.trim() +"%' ";
                    }
                }

                String finalSearchSql = searchSql;
                new Thread(() -> {
                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        Statement statement = connection.createStatement();
                        ResultSet rs = statement.executeQuery(finalSearchSql);
                        while (rs.next()) {
                            UserNameAndIdDto userNameAndIdDto = new UserNameAndIdDto();
                            userNameAndIdDto.setFirstName(rs.getString("lnp_user_information_first_name"));
                            userNameAndIdDto.setSecondName(rs.getString("lnp_user_information_second_name"));
                            userNameAndIdDto.setId(rs.getInt("idlnp_user_id"));
                            myListData.add(userNameAndIdDto);
                        }

                        runOnUiThread(() -> {
                            UserSearchAdapter adapter = new UserSearchAdapter(UserSearchActivity.this, myListData);
                            mRecyclerView.setHasFixedSize(true);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(UserSearchActivity.this));
                            mRecyclerView.setAdapter(adapter);
                        });
                    } catch (Exception e) {
                        Log.e("InfoAsyncTask", "Error reading school information", e);
                    }

                }).start();

            }
        });

    }
}