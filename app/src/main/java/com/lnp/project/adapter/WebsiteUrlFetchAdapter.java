package com.lnp.project.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.lnp.project.activity.UserProfile;
import com.lnp.project.dto.UserNameAndIdDto;
import com.lnp.project.dto.WebsitShowDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class WebsiteUrlFetchAdapter extends RecyclerView.Adapter<WebsiteUrlFetchAdapter.ViewHolder>{

    private List<WebsitShowDto> listdata;

    private Context context;

    private ProgressDialog progressBar;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    public WebsiteUrlFetchAdapter(Context context, List<WebsitShowDto> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View listItem= layoutInflater.inflate(R.layout.website_show_list, parent, false);
        WebsiteUrlFetchAdapter.ViewHolder viewHolder = new WebsiteUrlFetchAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        progressBar=new ProgressDialog(context);
        String url = listdata.get(position).getWebsiteBannerUrl().toString();
        String name = listdata.get(position).getWebsiteBannerName().toString();

        holder.websiteFetchName.setText(name);
        holder.websiteFetchUrl.setText(url);

        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Are you sure you want to delete: "+ name);
                alertbox.setTitle("Delete Confirmation");
                alertbox.setCancelable(false);
                String id = listdata.get(position).getId().toString();

                alertbox.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                progressBar.setTitle("Deleting");
                                progressBar.setMessage("Wait For A While");
                                progressBar.show();
                                progressBar.setCanceledOnTouchOutside(true);

                                new Thread(() -> {
                                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                                        String sql = "Delete from lnp.website_url where idwebsite_url_id = "+id;
                                        PreparedStatement statement = connection.prepareStatement(sql);
                                        statement.executeUpdate();
                                    } catch (Exception e) {
                                        Log.e("InfoAsyncTask", "Error reading school information", e);
                                    }

                                }).start();
                                listdata.remove(position);
                                notifyItemRemoved(position);
                                progressBar.hide();
                                Toast.makeText(context, "Name Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        });

                alertbox.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                alertbox.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView websiteFetchName, websiteFetchUrl;
        ImageButton deleteImageButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            websiteFetchName = (TextView) itemView.findViewById(R.id.website_show_list_names);
            websiteFetchUrl = (TextView) itemView.findViewById(R.id.website_show_list_urls);
            deleteImageButton = (ImageButton) itemView.findViewById(R.id.website_show_delete);
        }
    }
}
