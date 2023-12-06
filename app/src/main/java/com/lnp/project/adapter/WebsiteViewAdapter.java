package com.lnp.project.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.activity.LinkUpdateActivity;
import com.lnp.project.activity.WebsiteViewActivity;
import com.lnp.project.activity.WebsiteWebViewActivity;
import com.lnp.project.dto.WebsitShowDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class WebsiteViewAdapter extends RecyclerView.Adapter<WebsiteViewAdapter.ViewHolder>{

    private List<WebsitShowDto> listdata;

    private Context context;

    public WebsiteViewAdapter(Context context, List<WebsitShowDto> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View listItem= layoutInflater.inflate(R.layout.website_view_list_layout, parent, false);
        WebsiteViewAdapter.ViewHolder viewHolder = new WebsiteViewAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String url = listdata.get(position).getWebsiteBannerUrl().toString();
        String name = listdata.get(position).getWebsiteBannerName().toString();

        holder.websiteFetchName.setText(name);
        holder.websiteFetchUrl.setText(url);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(context, WebsiteWebViewActivity.class);
                browserIntent.putExtra("website_url", listdata.get(position).getWebsiteBannerUrl());
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView websiteFetchName, websiteFetchUrl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            websiteFetchName = (TextView) itemView.findViewById(R.id.website_show_list_names);
            websiteFetchUrl = (TextView) itemView.findViewById(R.id.website_show_list_urls);
        }
    }
}
