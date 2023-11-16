package com.lnp.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.activity.AdminDistrictActivity;
import com.lnp.project.activity.AdminRetailerVerification;
import com.lnp.project.activity.AdminUserRetailerVerificationDetails;
import com.lnp.project.activity.BannerUpdateActivity;
import com.lnp.project.activity.LinkUpdateActivity;
import com.lnp.project.activity.QueryActivity;
import com.lnp.project.dto.UserNameAndIdDto;

import java.util.List;

public class AdminRetailerVerificationAdapter extends RecyclerView.Adapter<AdminRetailerVerificationAdapter.ViewHolder>{

    private List<UserNameAndIdDto> listdata;

    private Context context;

    public AdminRetailerVerificationAdapter(Context context, List<UserNameAndIdDto> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View listItem= layoutInflater.inflate(R.layout.layout_admin_recycler, parent, false);
        AdminRetailerVerificationAdapter.ViewHolder viewHolder = new AdminRetailerVerificationAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String firstName = listdata.get(position).getFirstName().toString();
        String secondName = listdata.get(position).getSecondName().toString();

        holder.text.setText(firstName +" "+ secondName);
        final Intent[] intent = new Intent[1];
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0) {
                    intent[0] =  new Intent(context, AdminUserRetailerVerificationDetails.class);
                    intent[0].putExtra("UserVerificationId", listdata.get(position).getId());
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
