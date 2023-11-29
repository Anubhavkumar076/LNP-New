package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lnp.project.R;
import com.lnp.project.adapter.ImageAdapter;

public class MobileRechargeUtilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge_util);
        GridView gridview = (GridView) findViewById(R.id.mobile_recharge_util);

        String utilityName[]= {"Recharge", "View Recharge"};

        int utilityImage[]={
                R.mipmap.recharge, R.mipmap.view
        };

        ImageAdapter utilityAdapter = new ImageAdapter(MobileRechargeUtilActivity.this, utilityImage, utilityName);
        gridview.setAdapter(utilityAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent(getApplicationContext(), MobileRecharge.class);
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getApplicationContext(), ViewRechargeDetailsInfo.class);
                    startActivity(i);
                }
            }
        });
    }
}