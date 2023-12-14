package com.lnp.project;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lnp.project.activity.MainActivity;
import com.lnp.project.adapter.MainAdapter;
import com.lnp.project.dto.MainAdapterDto;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

//    String creditWallet, debitWallet;

//    TextView creditWalletText, debitWalletText;
//    retailerVerifyText;

//    CardView cardView;

    RecyclerView mRecycler;
    SharedPreferences sp;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";
    Boolean isAdmin, isRetailer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);
        mRecycler = view.findViewById(R.id.main_recycler_list);
        getActivity().setTitle("LNP");


        sp = requireActivity().getSharedPreferences("login",MODE_PRIVATE);

        MainAdapterDto mainAdapterDto = new MainAdapterDto();
        isAdmin = sp.getBoolean("admin", false);
        isRetailer = sp.getBoolean("retailer", false);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));
        List<MainAdapterDto> mainAdapterDtoList = new ArrayList<>();
        if (isAdmin)
            mainAdapterDto.setAdmin(true);
        else if (isRetailer) {
            mainAdapterDto.setRetailer(true);
        } else {
            mainAdapterDto.setUser(true);
        }


        mainAdapterDtoList.add(mainAdapterDto);
//        Bundle bundle = getArguments();
//        if(bundle != null) {
//            debitWallet = bundle.getString("retailerDebitWallet");
//            creditWallet = bundle.getString("retailerCreditWallet");
//        }
//        if (!mainAdapterDtoList.isEmpty()
//                && mainAdapterDtoList.get(0).getDebitWallet() == null && debitWallet != null) {
//            mainAdapterDtoList.get(0).setDebitWallet(debitWallet);
//        }

        MainAdapter adapter = new MainAdapter(requireContext(), mainAdapterDtoList);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRecycler.setAdapter(adapter);

        return view;
    }
}
