package com.lnp.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.lnp.project.dto.MyListData;
import com.lnp.project.R;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private List<MyListData> listdata;

    // RecyclerView recyclerView;
    public MyListAdapter(List<MyListData> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = listdata.get(position);
        holder.formType.setText(listdata.get(position).getFormType());
        holder.fullName.setText(listdata.get(position).getFullName());
        holder.address.setText(listdata.get(position).getAddress());
        holder.mobileNumber.setText(listdata.get(position).getMobileNumber());
        holder.addedBy.setText(listdata.get(position).getAddedBy());
        holder.addedUser.setText(listdata.get(position).getUserAddedBy());
        holder.addedDate.setText(listdata.get(position).getAddedDate());

        if(listdata.get(position).getTenure() != null) {
            holder.tenure.setVisibility(View.VISIBLE);
            holder.tenure.setText(listdata.get(position).getTenure());
        } else {
            holder.tenure.setVisibility(View.GONE);
        }

        if(listdata.get(position).getSavingAmount()!= null) {
            holder.savingAmount.setVisibility(View.VISIBLE);
            holder.savingAmount.setText(listdata.get(position).getSavingAmount());
        } else {
            holder.savingAmount.setVisibility(View.GONE);
        }

        if(listdata.get(position).getLoanType() != null) {
            holder.loanType.setVisibility(View.VISIBLE);
            holder.loanType.setText(listdata.get(position).getLoanType());
        } else {
            holder.loanType.setVisibility(View.GONE);
        }

        if(listdata.get(position).getLoanCategory() != null) {
//            holder.loanCategory.setVisibility(View.VISIBLE);
            holder.loanCategory.setText(listdata.get(position).getLoanCategory());
        } else {
            holder.loanCategory.setVisibility(View.GONE);
        }

        if(listdata.get(position).getLoanAmount() != null) {
            holder.loanAmount.setVisibility(View.VISIBLE);
            holder.loanAmount.setText(listdata.get(position).getLoanAmount());
        } else {
            holder.loanAmount.setVisibility(View.GONE);
        }
        if(listdata.get(position).getCaService() != null) {
            holder.caService.setVisibility(View.VISIBLE);
            holder.caService.setText(listdata.get(position).getCaService());
        } else {
            holder.caService.setVisibility(View.GONE);
        }
        if(listdata.get(position).getEngineerBuilding() != null) {
            holder.engineerBuilding.setVisibility(View.VISIBLE);
            holder.engineerBuilding.setText(listdata.get(position).getEngineerBuilding());
        } else {
            holder.engineerBuilding.setVisibility(View.GONE);
        }
        if(listdata.get(position).getEngineerService() != null) {
            holder.engineerService.setVisibility(View.VISIBLE);
            holder.engineerService.setText(listdata.get(position).getEngineerService());
        } else {
            holder.engineerService.setVisibility(View.GONE);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(),"click on item: "+myListData.getLoanCategory(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView formType, fullName, mobileNumber, address, loanCategory, loanAmount, loanType,
                caService, engineerBuilding, engineerService, tenure, savingAmount, addedBy, addedUser, addedDate;

        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.formType = (TextView) itemView.findViewById(R.id.form_type);
            this.fullName = (TextView) itemView.findViewById(R.id.fullname_text);
            this.mobileNumber = (TextView) itemView.findViewById(R.id.mobile_number_text);
            this.address = (TextView) itemView.findViewById(R.id.address_text);
            this.loanCategory = (TextView) itemView.findViewById(R.id.loan_category_text);
            this.loanAmount = (TextView) itemView.findViewById(R.id.loan_amount_text);
            this.loanType = (TextView) itemView.findViewById(R.id.loan_type_text);
            this.caService = (TextView) itemView.findViewById(R.id.ca_service_text);
            this.engineerBuilding = (TextView) itemView.findViewById(R.id.engineer_building_text);
            this.engineerService = (TextView) itemView.findViewById(R.id.engineer_service_text);
            this.tenure = (TextView) itemView.findViewById(R.id.tenure_text);
            this.savingAmount = (TextView) itemView.findViewById(R.id.saving_amount_text);
            this.addedBy = (TextView) itemView.findViewById(R.id.addedBy);
            this.addedUser = (TextView) itemView.findViewById(R.id.addedUser);
            this.addedDate = (TextView) itemView.findViewById(R.id.addedDate);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}

