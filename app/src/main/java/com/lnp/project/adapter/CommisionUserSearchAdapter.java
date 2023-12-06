package com.lnp.project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.activity.RetailerConfigActivity;
import com.lnp.project.activity.UserProfile;
import com.lnp.project.dto.UserNameAndIdDto;

import java.util.List;

public class CommisionUserSearchAdapter extends RecyclerView.Adapter<CommisionUserSearchAdapter.ViewHolder>{

    private List<UserNameAndIdDto> listdata;

    private Context context;

    public CommisionUserSearchAdapter(Context context, List<UserNameAndIdDto> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View listItem= layoutInflater.inflate(R.layout.layout_admin_recycler, parent, false);
        CommisionUserSearchAdapter.ViewHolder viewHolder = new CommisionUserSearchAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String firstName = listdata.get(position).getFirstName().toString();
        String secondName = listdata.get(position).getSecondName().toString();

        holder.text.setText(firstName +" "+ secondName);
        final Intent[] intent = new Intent[1];
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent[0] =  new Intent(context, RetailerConfigActivity.class);
                intent[0].putExtra("usersearchid", listdata.get(position).getId());
                context.startActivity(intent[0]);
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
