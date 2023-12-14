package com.lnp.project;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.lnp.project.activity.AllFundRequest;
import com.lnp.project.activity.BBPSUtilActivity;
import com.lnp.project.activity.MobileRechargeUtilActivity;
import com.lnp.project.activity.PanCardUtilActivity;
import com.lnp.project.activity.ViewBBPSActivity;
import com.lnp.project.activity.ViewContactFormActivity;
import com.lnp.project.activity.ViewPanCardInfo;
import com.lnp.project.activity.ViewRechargeDetailsInfo;
import com.lnp.project.adapter.ImageAdapter;
import com.lnp.project.dto.MainAdapterDto;

import java.util.ArrayList;
import java.util.List;

public class LedgerFragment extends Fragment {

    String utilityName[]= {
            "Forms", "Recharge", "PAN Card", "EMI", "Broadband", "Bill Payment", "Insurance",
            "Water", "DTH", "Landline", "Electricity", "Cable", "Fastag", "LPG"
    };

    int utilityImage[]={
            R.mipmap.home, R.mipmap.rechargeicon, R.mipmap.pancard, R.mipmap.emiicon, R.mipmap.broadbandicon,
            R.mipmap.bbps, R.mipmap.insuranceicon, R.mipmap.watericon, R.mipmap.dthicon,
            R.mipmap.landlineicon, R.mipmap.electricityicon, R.mipmap.cableicon, R.mipmap.fastag,
            R.mipmap.lpgicon
    };

    GridView ledgerFragmentGrid;

    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger, container, false);
        getActivity().setTitle("Ledgers");

        sp = requireActivity().getSharedPreferences("login",MODE_PRIVATE);
        Boolean isAdmin = sp.getBoolean("admin", false);
        Boolean isRetailer = sp.getBoolean("retailer", false);

        ledgerFragmentGrid = view.findViewById(R.id.fragment_ledger_gridview);

        ImageAdapter adapter = new ImageAdapter(getContext(), utilityImage, utilityName);
        ledgerFragmentGrid.setAdapter(adapter);

        ledgerFragmentGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent(getContext(), ViewContactFormActivity.class);
                    getContext().startActivity(i);
                } else if (isAdmin || isRetailer){
                    if (position == 1) {
                        Intent i = new Intent(getContext(), ViewRechargeDetailsInfo.class);
                        getContext().startActivity(i);
                    } else if (position == 2) {
                        Intent i = new Intent(getContext(), ViewPanCardInfo.class);
                        getContext().startActivity(i);
                    } else {
                        Intent i = new Intent(getContext(), ViewBBPSActivity.class);
                        i.putExtra("category", utilityName[position]);
                        getContext().startActivity(i);
                    }
                } else {
                    Toast.makeText(getContext(), "You are not a retailer. Please click on verification banner above to become retailer!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}