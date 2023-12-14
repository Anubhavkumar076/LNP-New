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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lnp.project.R;
import com.lnp.project.activity.AdminDistrictActivity;
import com.lnp.project.activity.AdminPanelActivity;
import com.lnp.project.activity.AdminRetailerVerification;
import com.lnp.project.activity.AllFundRequest;
import com.lnp.project.activity.BBPSActivity;
import com.lnp.project.activity.BBPSUtilActivity;
import com.lnp.project.activity.BannerUpdateActivity;
import com.lnp.project.activity.CommisionUserSearch;
import com.lnp.project.activity.FundRequestActivity;
import com.lnp.project.activity.FundRequestApproval;
import com.lnp.project.activity.LinkUpdateActivity;
import com.lnp.project.activity.MainActivity;
import com.lnp.project.activity.MobileRecharge;
import com.lnp.project.activity.MobileRechargeUtilActivity;
import com.lnp.project.activity.PanCardActivity;
import com.lnp.project.activity.PanCardUtilActivity;
import com.lnp.project.activity.QueryActivity;
import com.lnp.project.activity.RetailerVerificationActivity;
import com.lnp.project.activity.SingleViewActivity;
import com.lnp.project.activity.UserProfile;
import com.lnp.project.activity.UserSearchActivity;
import com.lnp.project.activity.ViewContactFormActivity;
import com.lnp.project.activity.WebsiteActivity;
import com.lnp.project.activity.WebsiteViewActivity;
import com.lnp.project.dto.MainAdapterDto;
import com.lnp.project.dto.UserNameAndIdDto;
import com.lnp.project.responseDto.bbps.BBPSItemResponseDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{

    private List<MainAdapterDto> listdata;

    private Context context;

    List<BBPSItemResponseDto> bbpsItemResponseDtoList = new ArrayList<>();

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    String iconName[]= {"Loans", "CA", "Engineer", "Cibil", "Savings"};

    int mThumbIds[]={
            R.mipmap.loan, R.mipmap.ca, R.mipmap.engineer,
            R.mipmap.cibil, R.mipmap.saving
    };

    String utilityName[]= {
            "Recharge", "PAN Card", "EMI", "Broadband", "Bill Payment", "Insurance",
            "Water", "DTH", "Landline", "Electricity", "Cable", "Fastag", "LPG"
    };

    int utilityImage[]={
            R.mipmap.rechargeicon, R.mipmap.pancard, R.mipmap.emiicon, R.mipmap.broadbandicon,
            R.mipmap.bbps, R.mipmap.insuranceicon, R.mipmap.watericon, R.mipmap.dthicon,
            R.mipmap.landlineicon, R.mipmap.electricityicon, R.mipmap.cableicon, R.mipmap.fastag,
            R.mipmap.lpgicon
    };

    String adminUtilityName[]= {
            "Banner", "Links", "District Update", "Queries", "Verification", "User Search",
            "Fund Request Approval", "All Fund Request", "Comm Config", "Websites Config"
    };

    int adminUtilityImage[]={
            R.mipmap.banner, R.mipmap.link, R.mipmap.district, R.mipmap.query,
            R.mipmap.verification, R.mipmap.search, R.mipmap.fund_request, R.mipmap.view_fund,
            R.mipmap.commission, R.mipmap.webiste
    };

    String websiteUtilityName[]= {"Government", "e-Courses", "e-Books", "Others"};

    int websiteUtilityImage[]={
            R.mipmap.goverment, R.mipmap.ecourses, R.mipmap.ebooks, R.mipmap.other

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
        fetchBBPSCategory();
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

        ImageAdapter adminUtilityAdapter = new ImageAdapter(context, adminUtilityImage, adminUtilityName);
        holder.adminGridView.setAdapter(adminUtilityAdapter);

        if (listdata.get(0).getAdmin()) {
            holder.retailerVerifyText.setVisibility(View.GONE);
            holder.gridViewRelative.setVisibility(View.GONE);
            holder.utilityGridViewRelative.setVisibility(View.GONE);
            holder.adminGridViewRelative.setVisibility(View.VISIBLE);
        } else if (listdata.get(0).getRetailer()) {
            holder.retailerVerifyText.setVisibility(View.GONE);
            holder.adminGridViewRelative.setVisibility(View.GONE);
            holder.gridViewRelative.setVisibility(View.VISIBLE);
            holder.utilityGridViewRelative.setVisibility(View.VISIBLE);
        } else {
            holder.utilityGridViewRelative.setVisibility(View.VISIBLE);
            holder.retailerVerifyText.setVisibility(View.VISIBLE);
            holder.gridViewRelative.setVisibility(View.VISIBLE);
        }

        if (!listdata.get(0).getRetailer() && !listdata.get(0).getAdmin()) {
            holder.retailerVerifyText.setVisibility(View.VISIBLE);
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
                        Intent i = new Intent(context, MobileRecharge.class);
                        context.startActivity(i);
                    } else if (position == 1) {
                        Intent i = new Intent(context, PanCardActivity.class);
                        context.startActivity(i);
                    } else if (bbpsItemResponseDtoList != null && !bbpsItemResponseDtoList.isEmpty()){
                        Intent i = new Intent(context, BBPSActivity.class);
                        List<BBPSItemResponseDto> itemResponseDtos = bbpsItemResponseDtoList.stream()
                                .filter(x -> x.getCategory().equals(utilityName[position])).collect(Collectors.toList());
                        if (!itemResponseDtos.isEmpty()) {
                            i.putExtra("category", (Serializable) itemResponseDtos);
                            context.startActivity(i);
                        } else {
                            Toast.makeText(context, "This payment system is not available now. Please contact admin!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "This payment system is not available now. Please contact admin!", Toast.LENGTH_SHORT).show();
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

        holder.adminGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent(context, BannerUpdateActivity.class);
                    context.startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(context, LinkUpdateActivity.class);
                    context.startActivity(i);
                } else if (position == 2) {
                    Intent i = new Intent(context, AdminDistrictActivity.class);
                    context.startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(context, QueryActivity.class);
                    context.startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(context, AdminRetailerVerification.class);
                    context.startActivity(i);
                } else if (position == 5) {
                    Intent i = new Intent(context, UserSearchActivity.class);
                    context.startActivity(i);
                } else if (position == 6) {
                    Intent i = new Intent(context, FundRequestApproval.class);
                    context.startActivity(i);
                } else if (position == 7) {
                    Intent i = new Intent(context, AllFundRequest.class);
                    context.startActivity(i);
                } else if (position == 8) {
                    Intent i = new Intent(context, CommisionUserSearch.class);
                    context.startActivity(i);
                } else if (position == 9) {
                    Intent i = new Intent(context, WebsiteActivity.class);
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
        GridView gridView, utilityGridview, websiteGridview, adminGridView;

        RelativeLayout gridViewRelative, utilityGridViewRelative, adminGridViewRelative;
        TextView retailerVerifyText;

        ImageButton phone, mail, whatsapp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gridViewRelative = (RelativeLayout) itemView.findViewById(R.id.grid_view_relative);
            utilityGridViewRelative = (RelativeLayout) itemView.findViewById(R.id.utility_grid_view_relative);
            adminGridViewRelative = (RelativeLayout) itemView.findViewById(R.id.admin_grid_view_relative);
            gridView = (GridView) itemView.findViewById(R.id.gridView);
            adminGridView = (GridView) itemView.findViewById(R.id.admin_main_grid);
            utilityGridview = (GridView) itemView.findViewById(R.id.utility_main_grid);
            websiteGridview = (GridView) itemView.findViewById(R.id.utility_websites);
            retailerVerifyText = (TextView) itemView.findViewById(R.id.main_retailer_text);
            phone = (ImageButton) itemView.findViewById(R.id.main_phone);
            mail = (ImageButton) itemView.findViewById(R.id.main_email);
            whatsapp = (ImageButton) itemView.findViewById(R.id.main_whatsapp);
        }
    }

    private void fetchBBPSCategory() {
        String url = "http://www.techfolkapi.in/Paysprint/OperatorList";
        JSONObject params = new JSONObject();
        // on below line we are passing our key
        // and value pair to our parameters.
        try {
            params.put("userId", "TECHFOLK");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // on below line we are displaying a success toast message.
                try {
                    JSONObject respObj = response.getJSONObject("responseData");
                    JSONArray respArray = respObj.getJSONArray("data");
                    if (respArray != null) {
                        for (int i=0;i<respArray.length();i++){
                            JSONObject object=respArray.getJSONObject(i);
                            BBPSItemResponseDto bbpsItemResponseDto = new BBPSItemResponseDto();
                            bbpsItemResponseDto.setCategory(object.getString("category"));
                            bbpsItemResponseDto.setId(object.getString("id"));
                            bbpsItemResponseDto.setName(object.getString("name"));
                            bbpsItemResponseDtoList.add(bbpsItemResponseDto);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error occurred -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("partnerKey", "TechFolk@2023@#8376852504");

                return params;
            }
        };
        queue.add(request);
    }
}
