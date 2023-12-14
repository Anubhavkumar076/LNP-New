package com.lnp.project;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lnp.project.activity.AllFundRequest;
import com.lnp.project.activity.CommisionUserSearch;
import com.lnp.project.activity.FundRequestActivity;
import com.lnp.project.activity.FundRequestApproval;
import com.lnp.project.activity.RetailerConfigActivity;
import com.lnp.project.activity.ViewBBPSActivity;
import com.lnp.project.adapter.ViewBBPSAdapter;
import com.lnp.project.adapter.ViewWalletAdapter;
import com.lnp.project.dto.WalletFragmentListDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class WalletFragment extends Fragment {

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    SharedPreferences sp;

    String retailerCreditWallet = "", retailerDebitWallet ="";

    RecyclerView mRecyclerView;

    TextView creditWalletText, debitWalletText, sendMoney, addMoney, viewRequest;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        creditWalletText = view.findViewById(R.id.fragment_wallet_credit);
        debitWalletText = view.findViewById(R.id.fragment_wallet_debit);
        mRecyclerView = view.findViewById(R.id.main_recycler_list);
        sendMoney = view.findViewById(R.id.fragment_wallet_send);
        addMoney = view.findViewById(R.id.fragment_wallet_receive);
        viewRequest = view.findViewById(R.id.fragment_wallet_view_request);

        sp = requireActivity().getSharedPreferences("login",MODE_PRIVATE);

        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));
        List<WalletFragmentListDto> walletFragmentListDtos = new ArrayList<>();
        Boolean isAdmin = sp.getBoolean("admin", false);

        if (isAdmin) {
            addMoney.setText("Approve Request");
        }

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAdmin) {
                    Intent intent = new Intent(getActivity(), FundRequestApproval.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), FundRequestActivity.class);
                    startActivity(intent);
                }
            }
        });
        viewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AllFundRequest.class);
                startActivity(intent);
            }
        });

        sendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAdmin) {
                    Intent intent = new Intent(getActivity(), CommisionUserSearch.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                }
            }
        });


        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String retailerWallet = "SELECT * FROM lnp.lnp_user where idlnp_user_id = "+ userIdInt;

                Statement retailerStatement = conn.createStatement();
                ResultSet resultSet = retailerStatement.executeQuery(retailerWallet);
                while (resultSet.next()) {
                    retailerCreditWallet = resultSet.getString("lnp_user_credit_fund");
                    retailerDebitWallet = resultSet.getString("lnp_user_debit_fund");
                }

                getActivity().runOnUiThread(() -> {
                    creditWalletText.setText("Credit Wallet: \n"+ retailerCreditWallet);
                    debitWalletText.setText("Debit Wallet: \n"+ retailerDebitWallet);

                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String retailerWallet = "SELECT * FROM lnp.all_transactions where all_transactions_user_id = "+ userIdInt;

                Statement retailerStatement = conn.createStatement();
                ResultSet resultSet = retailerStatement.executeQuery(retailerWallet);
                while (resultSet.next()) {
                    WalletFragmentListDto walletFragmentListDto = new WalletFragmentListDto();
                    walletFragmentListDto.setCreditAmount(resultSet.getString("all_transactions_credit"));
                    walletFragmentListDto.setDebitAmount(resultSet.getString("all_transactions_debit"));
                    walletFragmentListDto.setCreditTotalAmount(resultSet.getString("all_transactions_total_credit"));
                    walletFragmentListDto.setDebitTotalAmount(resultSet.getString("all_transactions_total_debit"));
                    walletFragmentListDto.setTransactionType(resultSet.getString("all_transactions_type"));
                    walletFragmentListDtos.add(walletFragmentListDto);
                }

                getActivity().runOnUiThread(() -> {
                    if(walletFragmentListDtos.size() != 0) {
                        ViewWalletAdapter adapter = new ViewWalletAdapter(walletFragmentListDtos, getContext());
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerView.setAdapter(adapter);
                    }
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
        return view;
    }
}