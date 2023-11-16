package com.lnp.project.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.activity.PanStatusActivity;
import com.lnp.project.dto.ViewPanCardDto;
import java.util.List;

public class ViewPanCardAdapter  extends RecyclerView.Adapter<ViewPanCardAdapter.ViewHolder> {

    private List<ViewPanCardDto> listdata;

    Context cxt;

    public ViewPanCardAdapter(List<ViewPanCardDto> listdata, Context cxt) {
        this.listdata = listdata;
        this.cxt = cxt;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.view_pan_card_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPanCardAdapter.ViewHolder holder, int position) {
        final ViewPanCardDto viewPanDto = listdata.get(position);
        holder.mPanCardId.setText("ID: "+ viewPanDto.getId());
        holder.mPanCardRefId.setText("Ref ID: "+ viewPanDto.getRefId());
        String name = viewPanDto.getFirstName().trim() + " "+ viewPanDto.getMiddleName().trim() + " "+ viewPanDto.getLastName().trim();
        holder.mPanCardName.setText(name);
        holder.mPanCardMode.setText(viewPanDto.getMode());
        holder.mPanCardGender.setText(viewPanDto.getGender());
        holder.mPanCardEmail.setText(viewPanDto.getEmail());
        holder.mPanCardKycType.setText(viewPanDto.getKycType());
        holder.mPanCardAddedBy.setText(viewPanDto.getUserId());
        holder.userName.setText(viewPanDto.getUserName());
        holder.date.setText(viewPanDto.getDateAdded());

        holder.panStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(cxt, PanStatusActivity.class);
                intent.putExtra("refId", viewPanDto.getRefId());
                intent.putExtra("panCardInfoId", viewPanDto.getId());
                cxt.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mPanCardId, mPanCardRefId, mPanCardName, userName, date,
            mPanCardMode, mPanCardGender, mPanCardEmail, mPanCardKycType, mPanCardAddedBy;

        Button panStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mPanCardId = (TextView) itemView.findViewById(R.id.view_pan_card_id);
            this.mPanCardRefId = (TextView) itemView.findViewById(R.id.view_pan_card_ref_id);
            this.mPanCardName = (TextView) itemView.findViewById(R.id.view_pan_card_name);
            this.mPanCardMode = (TextView) itemView.findViewById(R.id.view_pan_card_mode);
            this.mPanCardGender = (TextView) itemView.findViewById(R.id.view_pan_card_gender);
            this.mPanCardEmail = (TextView) itemView.findViewById(R.id.view_pan_card_email);
            this.mPanCardKycType = (TextView) itemView.findViewById(R.id.view_pan_card_kyc);
            this.mPanCardAddedBy = (TextView) itemView.findViewById(R.id.view_pan_card_added_by);
            this.userName = (TextView) itemView.findViewById(R.id.view_pan_card_user_name);
            this.date = (TextView) itemView.findViewById(R.id.view_pan_card_date);
            this.panStatus = (Button) itemView.findViewById(R.id.pan_status_button);
        }
    }
}
