package com.lnp.project.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.dto.FundRequestDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

public class AllFundRequestAdapter extends RecyclerView.Adapter<AllFundRequestAdapter.ViewHolder>{

    private List<FundRequestDto> listdata;

    Context cxt;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    public AllFundRequestAdapter(List<FundRequestDto> listdata, Context cxt) {
        this.listdata = listdata;
        this.cxt = cxt;
    }

    @NonNull
    @Override
    public AllFundRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.all_fund_request_list, parent, false);
        AllFundRequestAdapter.ViewHolder viewHolder = new AllFundRequestAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllFundRequestAdapter.ViewHolder holder, int position) {
        final FundRequestDto fundRequestDto = listdata.get(position);
        holder.fundRequestUserName.setText("Name: "+ fundRequestDto.getFundRequestUserName());
        holder.fundRequestRupees.setText("Amount(Rs): "+ fundRequestDto.getFundRequestRupees());
        holder.fundRequestUserId.setText("User ID: LNP-ID-"+ fundRequestDto.getFundRequestUserId());
        holder.fundRequestDateAdded.setText("Date: "+ fundRequestDto.getFundRequestDateAdded());
        holder.fundRequestId.setText("Request ID: "+ fundRequestDto.getRequestId());
        holder.fundRequestStatus.setText("Status: "+ fundRequestDto.getFundRequestStatus());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView fundRequestUserName, fundRequestUserId, fundRequestRupees,
                fundRequestDateAdded, fundRequestId, fundRequestStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.fundRequestUserName = (TextView) itemView.findViewById(R.id.all_fund_request_user_name);
            this.fundRequestUserId = (TextView) itemView.findViewById(R.id.all_fund_request_user_id);
            this.fundRequestRupees = (TextView) itemView.findViewById(R.id.all_fund_request_rupees);
            this.fundRequestDateAdded = (TextView) itemView.findViewById(R.id.all_fund_request_date_added);
            this.fundRequestId = (TextView) itemView.findViewById(R.id.all_fund_request_id);
            this.fundRequestStatus = (TextView) itemView.findViewById(R.id.all_fund_request_status);
        }
    }
}
