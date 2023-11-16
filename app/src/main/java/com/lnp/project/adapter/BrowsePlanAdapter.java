package com.lnp.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.dto.BrowsePlanDto;
import com.lnp.project.dto.BrowsePlanParentDto;
import com.lnp.project.dto.MyListData;

import java.util.List;

public class BrowsePlanAdapter extends RecyclerView.Adapter<BrowsePlanAdapter.ViewHolder>{

    private List<BrowsePlanParentDto> listdata;
    public Context cxt;

    private Integer operatorId;
    private String mobileNumber;

    public BrowsePlanAdapter(List<BrowsePlanParentDto> listdata, Context cxt, Integer operatorId, String mobileNumber) {
        this.listdata = listdata;
        this.cxt = cxt;
        this.operatorId = operatorId;
        this.mobileNumber = mobileNumber;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Here we inflate the corresponding
        // layout of the parent item
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.browse_plan_list,
                        parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BrowsePlanParentDto currentItem = listdata.get(position);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(cxt, LinearLayoutManager.VERTICAL, false);
        holder.childRecyclerView.setLayoutManager(layoutManager);
        holder.childRecyclerView.setHasFixedSize(true);

        holder.parentItemTitle.setText(currentItem.getBrowsePlanHeading());

        // added the first child row

        BrowsePlanChildAdapter browsePlanChildAdapter = new BrowsePlanChildAdapter(currentItem.getBrowsePlanDto(), holder.childRecyclerView.getContext(), operatorId, mobileNumber);
        holder.childRecyclerView.setAdapter(browsePlanChildAdapter);
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView childRecyclerView;
        private TextView parentItemTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            childRecyclerView = itemView.findViewById(R.id.child_recyclerview);
            parentItemTitle = itemView.findViewById(R.id.parent_item_title);
        }
    }
}

