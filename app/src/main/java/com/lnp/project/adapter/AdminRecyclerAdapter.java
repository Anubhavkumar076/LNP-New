package com.lnp.project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.activity.AdminDistrictActivity;
import com.lnp.project.activity.AdminRetailerVerification;
import com.lnp.project.activity.AllFundRequest;
import com.lnp.project.activity.BannerUpdateActivity;
import com.lnp.project.activity.FundRequestApproval;
import com.lnp.project.activity.LinkUpdateActivity;
import com.lnp.project.activity.QueryActivity;
import com.lnp.project.activity.RetailerConfigActivity;
import com.lnp.project.activity.UserSearchActivity;
import com.lnp.project.activity.WebsiteActivity;
import com.lnp.project.dto.MyListData;

import java.util.List;

public class AdminRecyclerAdapter extends RecyclerView.Adapter<AdminRecyclerAdapter.ViewHolder>{
    private List<String> listdata;

    private Context context;

    public AdminRecyclerAdapter(Context context, List<String> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View listItem= layoutInflater.inflate(R.layout.layout_admin_recycler, parent, false);
        AdminRecyclerAdapter.ViewHolder viewHolder = new AdminRecyclerAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.text.setText(listdata.get(position).toString());
        final Intent[] intent = new Intent[1];
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0) {
                    intent[0] =  new Intent(context, BannerUpdateActivity.class);
                    context.startActivity(intent[0]);
                } else if (position == 1) {
                    intent[0] =  new Intent(context, LinkUpdateActivity.class);
                    context.startActivity(intent[0]);
                }  else if (position == 2) {
                    intent[0] =  new Intent(context, AdminDistrictActivity.class);
                    context.startActivity(intent[0]);
                }   else if (position == 3) {
                    intent[0] =  new Intent(context, QueryActivity.class);
                    context.startActivity(intent[0]);
                } else if (position == 4) {
                    intent[0] =  new Intent(context, AdminRetailerVerification.class);
                    context.startActivity(intent[0]);
                } else if (position == 5) {
                    intent[0] =  new Intent(context, UserSearchActivity.class);
                    context.startActivity(intent[0]);
                } else if (position == 6) {
                    intent[0] =  new Intent(context, FundRequestApproval.class);
                    context.startActivity(intent[0]);
                } else if (position == 7) {
                    intent[0] =  new Intent(context, AllFundRequest.class);
                    context.startActivity(intent[0]);
                } else if (position == 8) {
                    intent[0] =  new Intent(context, RetailerConfigActivity.class);
                    context.startActivity(intent[0]);
                } else if (position == 9) {
                    intent[0] =  new Intent(context, WebsiteActivity.class);
                    context.startActivity(intent[0]);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text= (TextView) itemView.findViewById(R.id.text_id);
        }
    }
}

