package com.lnp.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lnp.project.activity.AllFundRequest;
import com.lnp.project.activity.BBPSUtilActivity;
import com.lnp.project.activity.MobileRechargeUtilActivity;
import com.lnp.project.activity.PanCardUtilActivity;
import com.lnp.project.activity.ViewBBPSActivity;
import com.lnp.project.activity.ViewContactFormActivity;
import com.lnp.project.activity.ViewPanCardInfo;
import com.lnp.project.activity.ViewRechargeDetailsInfo;
import com.lnp.project.adapter.ImageAdapter;

public class LedgerFragment extends Fragment {

    String utilityName[]= {"Forms", "Recharge", "Pan Status", "BBPS", "Funds"};

    int utilityImage[]={
            R.mipmap.view, R.mipmap.view, R.mipmap.view, R.mipmap.view, R.mipmap.view

    };

    GridView ledgerFragmentGrid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger, container, false);
        getActivity().setTitle("Ledgers");

        ledgerFragmentGrid = view.findViewById(R.id.fragment_ledger_gridview);

        ImageAdapter adapter = new ImageAdapter(getContext(), utilityImage, utilityName);
        ledgerFragmentGrid.setAdapter(adapter);

        ledgerFragmentGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent(getContext(), ViewContactFormActivity.class);
                    getContext().startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getContext(), ViewRechargeDetailsInfo.class);
                    getContext().startActivity(i);
                } else if (position == 2) {
                    Intent i = new Intent(getContext(), ViewPanCardInfo.class);
                    getContext().startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(getContext(), ViewBBPSActivity.class);
                    getContext().startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(getContext(), AllFundRequest.class);
                    getContext().startActivity(i);
                }
            }
        });

        return view;
    }
}