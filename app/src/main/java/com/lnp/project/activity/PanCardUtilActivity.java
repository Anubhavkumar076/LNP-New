package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lnp.project.R;
import com.lnp.project.adapter.ImageAdapter;

public class PanCardUtilActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_card_util);
        GridView gridview = (GridView) findViewById(R.id.pan_card_util);

        String utilityName[]= {"Create PAN", "PAN Status"};

        int utilityImage[]={
                R.mipmap.pan, R.mipmap.view

        };

        ImageAdapter utilityAdapter = new ImageAdapter(PanCardUtilActivity.this, utilityImage, utilityName);
        gridview.setAdapter(utilityAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent(getApplicationContext(), PanCardActivity.class);
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getApplicationContext(), ViewPanCardInfo.class);
                    startActivity(i);
                }
            }
        });
    }
}