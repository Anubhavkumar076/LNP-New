package com.lnp.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.dto.MyListData;
import com.lnp.project.dto.Query;

import java.util.List;

public class QueryListAdapter extends RecyclerView.Adapter<QueryListAdapter.ViewHolder>{
    private List<Query> listdata;

    // RecyclerView recyclerView;
    public QueryListAdapter(List<Query> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.query_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Query queryData = listdata.get(position);
        holder.fullName.setText(queryData.getUserName());
        holder.address.setText(listdata.get(position).getAddress());
        holder.mobileNumber.setText(queryData.getPhone());
        holder.queryText.setText(queryData.getQuery());
        holder.emailText.setText(queryData.getEmail());
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fullName, mobileNumber, address, queryText, emailText;
        public ViewHolder(View itemView) {
            super(itemView);
            this.fullName = (TextView) itemView.findViewById(R.id.fullname_text);
            this.mobileNumber = (TextView) itemView.findViewById(R.id.mobile_number_text);
            this.address = (TextView) itemView.findViewById(R.id.address_text);
            this.queryText = (TextView) itemView.findViewById(R.id.query_text);
            this.emailText = (TextView) itemView.findViewById(R.id.email_text);
        }
    }
}

