package com.lnp.project.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.lnp.project.activity.FundRequestActivity;
import com.lnp.project.activity.MainActivity;
import com.lnp.project.dto.FundRequestDto;
import com.lnp.project.dto.ViewPanCardDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class FundRequestAdapter  extends RecyclerView.Adapter<FundRequestAdapter.ViewHolder>{

    private List<FundRequestDto> listdata;

    Context cxt;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    public FundRequestAdapter(List<FundRequestDto> listdata, Context cxt) {
        this.listdata = listdata;
        this.cxt = cxt;
    }

    @NonNull
    @Override
    public FundRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.fund_request_approval_list, parent, false);
        FundRequestAdapter.ViewHolder viewHolder = new FundRequestAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FundRequestAdapter.ViewHolder holder, int position) {
        final FundRequestDto fundRequestDto = listdata.get(position);
        holder.fundRequestUserName.setText("Name: "+ fundRequestDto.getFundRequestUserName());
        holder.fundRequestRupees.setText("Amount(Rs): "+ fundRequestDto.getFundRequestRupees());
        holder.fundRequestUserId.setText("User ID: LNP-ID-"+ fundRequestDto.getFundRequestUserId());
        holder.fundRequestDateAdded.setText("Date: "+ fundRequestDto.getFundRequestDateAdded());
        holder.fundRequestId.setText("Request ID: "+ fundRequestDto.getRequestId());

        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Please click Yes to approve the fund request.");
                alertbox.setTitle("Approve Confirmation");
                alertbox.setCancelable(false);

                alertbox.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                approveFundRequest(fundRequestDto.getFundRequestUserId(), fundRequestDto.getFundRequestRupees());
                                listdata.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(cxt, "Request Approved!", Toast.LENGTH_SHORT).show();
                            }
                        });

                alertbox.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                alertbox.show();
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Do you want to reject this request?");
                alertbox.setTitle("Reject Confirmation");
                alertbox.setCancelable(false);

                alertbox.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                rejectFundRequest(fundRequestDto.getFundRequestUserId());
                                listdata.remove(position);
                                Toast.makeText(cxt, "Request Rejected!", Toast.LENGTH_SHORT).show();
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

        TextView fundRequestUserName, fundRequestUserId, fundRequestRupees,
                fundRequestDateAdded, fundRequestId;

        Button approve, reject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.fundRequestUserName = (TextView) itemView.findViewById(R.id.fund_request_user_name);
            this.fundRequestUserId = (TextView) itemView.findViewById(R.id.fund_request_user_id);
            this.fundRequestRupees = (TextView) itemView.findViewById(R.id.fund_request_rupees);
            this.fundRequestDateAdded = (TextView) itemView.findViewById(R.id.fund_request_date_added);
            this.fundRequestId = (TextView) itemView.findViewById(R.id.fund_request_id);
            this.approve = (Button) itemView.findViewById(R.id.fund_request_approve_button);
            this.reject = (Button) itemView.findViewById(R.id.fund_request_reject_button);
        }
    }

    public void approveFundRequest(String userId, String amount) {
        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "UPDATE lnp.lnp_user SET lnp_user_debit_fund = lnp_user_debit_fund + "+ amount +" where idlnp_user_id = "+ userId;
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);

                String fundRequestStatusUpdate = "UPDATE lnp.fund_request SET fund_request_is_approved = 1 where fund_request_user_id = "+ userId;
                statement.executeUpdate(fundRequestStatusUpdate);
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }
        }).start();
    }

    public void rejectFundRequest(String userId) {
        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                Statement statement = connection.createStatement();
                String fundRequestStatusUpdate = "UPDATE lnp.fund_request SET fund_request_is_approved = 2 where fund_request_user_id = "+ userId;
                statement.executeUpdate(fundRequestStatusUpdate);
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }
        }).start();
    }
}
