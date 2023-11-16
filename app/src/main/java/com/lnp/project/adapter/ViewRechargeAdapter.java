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
import com.lnp.project.dto.ViewPanCardDto;
import com.lnp.project.dto.ViewRechargeInfoDto;

import java.util.List;

public class ViewRechargeAdapter extends RecyclerView.Adapter<ViewRechargeAdapter.ViewHolder>{

    private List<ViewRechargeInfoDto> listdata;

    Context cxt;

    public ViewRechargeAdapter(List<ViewRechargeInfoDto> listdata, Context cxt) {
        this.listdata = listdata;
        this.cxt = cxt;
    }

    @NonNull
    @Override
    public ViewRechargeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.view_recharge_list, parent, false);
        ViewRechargeAdapter.ViewHolder viewHolder = new ViewRechargeAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewRechargeAdapter.ViewHolder holder, int position) {
        final ViewRechargeInfoDto viewRechargeInfoDto = listdata.get(position);
        holder.rechargeId.setText(viewRechargeInfoDto.getId());
        holder.caNumberText.setText(viewRechargeInfoDto.getCaNumber());
        holder.amountText.setText(viewRechargeInfoDto.getAmount());
        holder.refIdText.setText(viewRechargeInfoDto.getRefId());
        holder.operatorText.setText(viewRechargeInfoDto.getOperator());
        holder.addedBy.setText(viewRechargeInfoDto.getAddedBy());
        holder.userName.setText(viewRechargeInfoDto.getUserName());
        holder.date.setText(viewRechargeInfoDto.getDate());

        holder.rechargeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Ref ID: "+ viewRechargeInfoDto.getRefId() +"\nAck No: "+viewRechargeInfoDto.getAckNo()+"\nCommission: "+viewRechargeInfoDto.getCommision() +"\nTDS: "+viewRechargeInfoDto.getTds() +"\nRefunded: "+ viewRechargeInfoDto.getRefunded());
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

        TextView rechargeId, caNumberText, amountText, refIdText, operatorText, addedBy, userName, date;

        Button rechargeStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rechargeId = itemView.findViewById(R.id.view_recharge_id);
            this.caNumberText = itemView.findViewById(R.id.view_recharge_number);
            this.amountText = itemView.findViewById(R.id.view_recharge_amount);
            this.refIdText = itemView.findViewById(R.id.view_recharge_ref_id);
            this.operatorText = itemView.findViewById(R.id.view_recharge_operator);
            this.addedBy = itemView.findViewById(R.id.view_recharge_added_by);
            this.rechargeStatus = itemView.findViewById(R.id.view_recharge_status);
            this.userName = itemView.findViewById(R.id.view_recharge_user_name);
            this.date = itemView.findViewById(R.id.view_recharge_date);
        }
    }
}
