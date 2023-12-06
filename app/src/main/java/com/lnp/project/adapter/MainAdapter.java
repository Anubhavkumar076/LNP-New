package com.lnp.project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnp.project.R;
import com.lnp.project.activity.AdminPanelActivity;
import com.lnp.project.activity.BBPSUtilActivity;
import com.lnp.project.activity.FundRequestActivity;
import com.lnp.project.activity.MainActivity;
import com.lnp.project.activity.MobileRechargeUtilActivity;
import com.lnp.project.activity.PanCardUtilActivity;
import com.lnp.project.activity.RetailerVerificationActivity;
import com.lnp.project.activity.SingleViewActivity;
import com.lnp.project.activity.UserProfile;
import com.lnp.project.activity.ViewContactFormActivity;
import com.lnp.project.activity.WebsiteViewActivity;
import com.lnp.project.dto.MainAdapterDto;
import com.lnp.project.dto.UserNameAndIdDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{

    private List<MainAdapterDto> listdata;

    private Context context;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    String iconName[]= {"Loans", "CA", "Engineer", "Cibil", "Savings"};

    int mThumbIds[]={
            R.mipmap.loan, R.mipmap.ca, R.drawable.engineerservice,
            R.drawable.cibilscore, R.drawable.supersaving
    };

    String utilityName[]= {"Recharge", "PAN Card", "BBPS", "Fund Request"};

    int utilityImage[]={
            R.mipmap.recharge, R.mipmap.pan, R.mipmap.bill, R.mipmap.fund

    };

    String websiteUtilityName[]= {"Government", "e-Courses", "e-Books", "Others"};

    int websiteUtilityImage[]={
            R.mipmap.recharge, R.mipmap.pan, R.mipmap.bill, R.mipmap.fund

    };

    public MainAdapter(Context context, List<MainAdapterDto> listdata) {
        this.context = context;
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View listItem= layoutInflater.inflate(R.layout.main_recycler_view, parent, false);
        MainAdapter.ViewHolder viewHolder = new MainAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageAdapter adapter = new ImageAdapter(context, mThumbIds, iconName);
        holder.gridView.setAdapter(adapter);

        ImageAdapter utilityAdapter = new ImageAdapter(context, utilityImage, utilityName);
        holder.utilityGridview.setAdapter(utilityAdapter);

        ImageAdapter websiteUtilityAdapter = new ImageAdapter(context, websiteUtilityImage, websiteUtilityName);
        holder.websiteGridview.setAdapter(websiteUtilityAdapter);

        if (!listdata.get(0).getRetailer() && !listdata.get(0).getAdmin()) {
            holder.retailerVerifyText.setVisibility(View.VISIBLE);
        }

        if (listdata.get(0).getRetailer() || listdata.get(0).getAdmin()) {
            holder.retailerVerifyText.setVisibility(View.GONE);
        }

        holder.retailerVerifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, RetailerVerificationActivity.class);
                context.startActivity(i);
            }
        });

        holder.utilityGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(listdata.get(0).getAdmin() || listdata.get(0).getRetailer()) {
                    if (position == 0) {
                        Intent i = new Intent(context, MobileRechargeUtilActivity.class);
                        context.startActivity(i);
                    } else if (position == 1) {
                        Intent i = new Intent(context, PanCardUtilActivity.class);
                        context.startActivity(i);
                    } else if (position == 2) {
                        Intent i = new Intent(context, BBPSUtilActivity.class);
                        context.startActivity(i);
                    } else if (position == 3) {
                        Intent i = new Intent(context, FundRequestActivity.class);
                        i.putExtra("debitWallet", listdata.get(0).getDebitWallet());
                        context.startActivity(i);
                    }
                } else {
                    Toast.makeText(context, "You are not a retailer. Please click on verification banner above to become retailer!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.websiteGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent(context, WebsiteViewActivity.class);
                    i.putExtra("listFetch","0");
                    context.startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(context, WebsiteViewActivity.class);
                    i.putExtra("listFetch","1");
                    context.startActivity(i);
                } else if (position == 2) {
                    Intent i = new Intent(context, WebsiteViewActivity.class);
                    i.putExtra("listFetch","2");
                    context.startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(context, WebsiteViewActivity.class);
                    i.putExtra("listFetch","3");
                    context.startActivity(i);
                }
            }
        });

        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){

//                if (position == 6) {
//                    // Send intent to SingleViewActivity
//                    Intent i = new Intent(context, AdminPanelActivity.class);
//                    context.startActivity(i);
//                } else
                if (position == 0) {
                    Intent i = new Intent(context, SingleViewActivity.class);
                    i.putExtra("service","Other Services");
                    context.startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(context, SingleViewActivity.class);
                    i.putExtra("service","CA Services");
                    context.startActivity(i);
                } else if (position == 2) {
                    Intent i = new Intent(context, SingleViewActivity.class);
                    i.putExtra("service","Engineer Services");
                    context.startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(context, SingleViewActivity.class);
                    i.putExtra("service","CIBIL Score");
                    context.startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(context, SingleViewActivity.class);
                    i.putExtra("service","Super Savings");
                    context.startActivity(i);
                }
//                else if (position == 5) {
//                    Intent i = new Intent(context, ViewContactFormActivity.class);
//                    context.startActivity(i);
//                }

            }
        });

        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(() -> {
                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "SELECT * FROM lnp.lnp_admin_key_value where lnp_admin_key_value_key like '%phone%'";
                        Statement statement = connection.createStatement();
                        ResultSet rs = statement.executeQuery(sql);

                        String phoneNumber = null;
                        if(rs.next())
                            phoneNumber = rs.getString("lnp_admin_key_value_value");

                        String finalPhoneNumber = "tel:"+ phoneNumber;
                        Intent browserIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(finalPhoneNumber));
                        context.startActivity(browserIntent);
                    } catch (Exception e) {
                        Log.e("InfoAsyncTask", "Error reading school information", e);
                    }

                }).start();
            }
        });

        holder.mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(() -> {
                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "SELECT * FROM lnp.lnp_admin_key_value where lnp_admin_key_value_key like '%email%'";
                        Statement statement = connection.createStatement();
                        ResultSet rs = statement.executeQuery(sql);

                        String mailId = null;
                        if(rs.next())
                            mailId = rs.getString("lnp_admin_key_value_value");

                        String finalMailId = "mailto:"+ mailId;
                        Intent browserIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(finalMailId));
                        context.startActivity(browserIntent);
                    } catch (Exception e) {
                        Log.e("InfoAsyncTask", "Error reading school information", e);
                    }

                }).start();
            }
        });

        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(() -> {
                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "SELECT * FROM lnp.lnp_admin_key_value where lnp_admin_key_value_key like '%whatsapp%'";
                        Statement statement = connection.createStatement();
                        ResultSet rs = statement.executeQuery(sql);

                        String whatsappNumber = null;
                        if(rs.next())
                            whatsappNumber = rs.getString("lnp_admin_key_value_value");

                        String finalWhatsappNumber = "smsto:"+ whatsappNumber;
                        Intent browserIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(finalWhatsappNumber));
                        browserIntent.setPackage("com.whatsapp");
                        context.startActivity(browserIntent);
                    } catch (Exception e) {
                        Log.e("InfoAsyncTask", "Error reading school information", e);
                    }

                }).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        GridView gridView, utilityGridview, websiteGridview;
        TextView retailerVerifyText;

        ImageButton phone, mail, whatsapp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gridView = (GridView) itemView.findViewById(R.id.gridView);
            utilityGridview = (GridView) itemView.findViewById(R.id.utility_main_grid);
            websiteGridview = (GridView) itemView.findViewById(R.id.utility_websites);
            retailerVerifyText = (TextView) itemView.findViewById(R.id.main_retailer_text);
            phone = (ImageButton) itemView.findViewById(R.id.main_phone);
            mail = (ImageButton) itemView.findViewById(R.id.main_email);
            whatsapp = (ImageButton) itemView.findViewById(R.id.main_whatsapp);
        }
    }
}
