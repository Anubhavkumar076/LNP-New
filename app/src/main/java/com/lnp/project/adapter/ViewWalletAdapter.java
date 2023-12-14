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
import com.lnp.project.dto.WalletFragmentListDto;

import java.util.List;

public class ViewWalletAdapter extends RecyclerView.Adapter<ViewWalletAdapter.ViewHolder> {

    private List<WalletFragmentListDto> listdata;

    Context cxt;

    public ViewWalletAdapter(List<WalletFragmentListDto> listdata, Context cxt) {
        this.listdata = listdata;
        this.cxt = cxt;
    }

    @NonNull
    @Override
    public ViewWalletAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.wallet_fragment_list, parent, false);
        ViewWalletAdapter.ViewHolder viewHolder = new ViewWalletAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewWalletAdapter.ViewHolder holder, int position) {
        final WalletFragmentListDto walletFragmentListDto = listdata.get(position);
        holder.transactionType.setText(walletFragmentListDto.getTransactionType());
        holder.creditWalletAdd.setText(walletFragmentListDto.getCreditAmount());
        holder.creditWalletTotal.setText(walletFragmentListDto.getCreditTotalAmount());
        holder.debitWalletAdd.setText(walletFragmentListDto.getDebitAmount());
        holder.debitWalletTotal.setText(walletFragmentListDto.getDebitTotalAmount());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView transactionType, creditWalletAdd, creditWalletTotal, debitWalletAdd, debitWalletTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.transactionType = itemView.findViewById(R.id.wallet_fragment_list_transaction_type);
            this.creditWalletAdd = itemView.findViewById(R.id.credit_wallet_add);
            this.creditWalletTotal = itemView.findViewById(R.id.credit_wallet_total);
            this.debitWalletAdd = itemView.findViewById(R.id.debit_wallet_add);
            this.debitWalletTotal = itemView.findViewById(R.id.debit_wallet_total);
        }
    }
}
