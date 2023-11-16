package com.lnp.project.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.dto.ViewBBPSInfoDto;

import java.util.List;

public class ViewBBPSAdapter  extends RecyclerView.Adapter<ViewBBPSAdapter.ViewHolder> {

    private List<ViewBBPSInfoDto> listdata;

    Context cxt;

    public ViewBBPSAdapter(List<ViewBBPSInfoDto> listdata, Context cxt) {
        this.listdata = listdata;
        this.cxt = cxt;
    }

    @NonNull
    @Override
    public ViewBBPSAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.view_bbps_list, parent, false);
        ViewBBPSAdapter.ViewHolder viewHolder = new ViewBBPSAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewBBPSAdapter.ViewHolder holder, int position) {
        final ViewBBPSInfoDto viewBBPSInfoDto = listdata.get(position);
        holder.viewBBPSId.setText(viewBBPSInfoDto.getId());
        holder.viewBBPSOperator.setText(viewBBPSInfoDto.getOperator());
        holder.viewBBPSNumber.setText(viewBBPSInfoDto.getNumber());
        holder.viewBBPSAmount.setText(viewBBPSInfoDto.getAmount());
        holder.viewBBPSRefId.setText(viewBBPSInfoDto.getRefId());
        holder.viewBBPSMode.setText(viewBBPSInfoDto.getMode());
        holder.viewBBPSCategory.setText(viewBBPSInfoDto.getCategory());
        holder.viewBBPSUserId.setText(viewBBPSInfoDto.getUserId());
        holder.userName.setText(viewBBPSInfoDto.getUserName());
        holder.addedDate.setText(viewBBPSInfoDto.getDateAdded());

        holder.bbpsStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage(viewBBPSInfoDto.getRefId() +"\n"+viewBBPSInfoDto.getTxnId()+"\n"+viewBBPSInfoDto.getCommission() +"\n"+viewBBPSInfoDto.getTds() +"\n"+ viewBBPSInfoDto.getRefunded());
                alertbox.setTitle("Recharge Details");
                alertbox.setCancelable(false);

                alertbox.setNegativeButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
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

        TextView viewBBPSId, viewBBPSOperator, viewBBPSNumber, viewBBPSAmount, viewBBPSRefId,
                viewBBPSMode, viewBBPSCategory, viewBBPSUserId, userName, addedDate;

        Button bbpsStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.viewBBPSId = itemView.findViewById(R.id.view_bbps_id);
            this.viewBBPSOperator = itemView.findViewById(R.id.view_bbps_operator);
            this.viewBBPSNumber = itemView.findViewById(R.id.view_bbps_number);
            this.viewBBPSAmount = itemView.findViewById(R.id.view_bbps_amount);
            this.viewBBPSRefId = itemView.findViewById(R.id.view_bbps_ref_id);
            this.viewBBPSMode = itemView.findViewById(R.id.view_bbps_mode);
            this.viewBBPSCategory = itemView.findViewById(R.id.view_bbps_category);
            this.viewBBPSUserId = itemView.findViewById(R.id.view_bbps_user_id);
            this.bbpsStatus = itemView.findViewById(R.id.view_bbps_status);
            this.userName = itemView.findViewById(R.id.view_bbps_user_name);
            this.addedDate = itemView.findViewById(R.id.view_bbps_date_added);
        }
    }
}
