package com.lnp.project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lnp.project.LoginActivity;
import com.lnp.project.R;
import com.lnp.project.adapter.ImageAdapter;
import com.lnp.project.adapter.ViewRechargeAdapter;
import com.lnp.project.common.JWTKey;
import com.lnp.project.dto.MyListData;
import com.lnp.project.dto.ViewRechargeInfoDto;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    String iconName[]= {"Loans", "CA", "Engineer", "Cibil", "Savings", "View Forms"};

    int mThumbIds[]={
            R.drawable.loans, R.drawable.caservice, R.drawable.engineerservice,
            R.drawable.cibilscore, R.drawable.supersaving, R.drawable.viewform

    };
    String utilityName[]= {"Recharge", "PAN Card", "BBPS", "Fund Request"};

    int utilityImage[]={
            R.mipmap.recharge, R.mipmap.pan, R.mipmap.bill, R.mipmap.fund

    };
    String iconNameAdmin[]= {"Loans", "CA", "Engineer", "Cibil", "Savings", "View Forms", "Admin"};

    int mThumbIdsAdmin[]={
            R.drawable.loans, R.drawable.caservice, R.drawable.engineerservice,
            R.drawable.cibilscore, R.drawable.supersaving, R.drawable.viewform, R.drawable.adminicon

    };

    String creditWallet, debitWallet;

    TextView creditWalletText, debitWalletText, retailerVerifyText;

    CardView cardView;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_contact_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Set Alert Title
        builder.setTitle("Alert !");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        if (item.getTitle().equals("My Profile")) {
            Intent i = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(i);
        } else if (item.getTitle().equals("Contact Us")) {
            Intent i = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(i);
        } else if (item.getTitle().equals("Youtube")) {
            new Thread(() -> {
                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String sql = "SELECT * FROM lnp.lnp_admin_key_value where lnp_admin_key_value_key like '%youtube%'";
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(sql);

                    String homeScreenBanner = null;
                    if(rs.next())
                        homeScreenBanner = rs.getString("lnp_admin_key_value_value");

                    String finalHomeScreenBanner = homeScreenBanner;
                    runOnUiThread(() -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalHomeScreenBanner));
                        startActivity(browserIntent);
                    });
                } catch (Exception e) {
                    Log.e("InfoAsyncTask", "Error reading school information", e);
                }

            }).start();

        } else if (item.getTitle().equals("Instagram")) {
            new Thread(() -> {
                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String sql = "SELECT * FROM lnp.lnp_admin_key_value where lnp_admin_key_value_key like '%instagram%'";
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(sql);

                    String homeScreenBanner = null;
                    if(rs.next())
                        homeScreenBanner = rs.getString("lnp_admin_key_value_value");

                    String finalHomeScreenBanner = homeScreenBanner;
                    runOnUiThread(() -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalHomeScreenBanner));
                        startActivity(browserIntent);
                    });
                } catch (Exception e) {
                    Log.e("InfoAsyncTask", "Error reading school information", e);
                }

            }).start();

        } else if (item.getTitle().equals("LNP eKart")) {
            new Thread(() -> {
                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String sql = "SELECT * FROM lnp.lnp_admin_key_value where lnp_admin_key_value_key like '%lnpekart%'";
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(sql);

                    String homeScreenBanner = null;
                    if(rs.next())
                        homeScreenBanner = rs.getString("lnp_admin_key_value_value");

                    String finalHomeScreenBanner = homeScreenBanner;
                    runOnUiThread(() -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalHomeScreenBanner));
                        startActivity(browserIntent);
                    });
                } catch (Exception e) {
                    Log.e("InfoAsyncTask", "Error reading school information", e);
                }

            }).start();
        } else if(item.getTitle().equals("Logout")){
            // Set the message show for the Alert time
            builder.setMessage("Do you want to logout?");

            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                // When the user click yes button then app will close
                FirebaseAuth.getInstance().signOut();
                sp.edit().remove("logged").apply();
                sp.edit().remove("userId").apply();
                sp.edit().remove("admin").apply();
                sp.edit().remove("retailer").apply();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            });

            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();
        } else if (item.getTitle().equals("Delete Account")) {
            builder.setMessage("Do you want to delete account?");

            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                // When the user click yes button then app will close
                Integer userId = Integer.parseInt(sp.getString("userId", ""));
                sp.edit().remove("logged").apply();
                sp.edit().remove("userId").apply();
                sp.edit().remove("admin").apply();
                sp.edit().remove("retailer").apply();
                deleteuser(userId);
            });

            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    Boolean isAdmin, isRetailer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView gridview = (GridView) findViewById(R.id.gridView);
        GridView utilityGridview = (GridView) findViewById(R.id.utility_main_grid);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        creditWalletText = findViewById(R.id.credit_balance_text);
        debitWalletText = findViewById(R.id.debit_card_balance);
        retailerVerifyText = findViewById(R.id.main_retailer_text);
        cardView = findViewById(R.id.main_card_view);
//        ImageView imageView = (ImageView) findViewById(R.id.recharge_icon);
        getCreditBalance();
        getDebitBalance();
        setTitle("LNP Mini Bank");
        isAdmin = sp.getBoolean("admin", false);
        isRetailer = sp.getBoolean("retailer", true);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));
        if (isAdmin) {
            ImageAdapter adapter = new ImageAdapter(MainActivity.this, mThumbIdsAdmin, iconNameAdmin);
            gridview.setAdapter(adapter);

        } else {
            ImageAdapter adapter = new ImageAdapter(MainActivity.this, mThumbIds, iconName);
            gridview.setAdapter(adapter);
        }

        if (!isRetailer && !isAdmin) {
            cardView.setVisibility(View.GONE);
            retailerVerifyText.setVisibility(View.VISIBLE);
        }

        if (isRetailer || isAdmin) {
            retailerVerifyText.setVisibility(View.GONE);
        }

        retailerVerifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RetailerVerificationActivity.class);
                startActivity(i);
            }
        });

        ImageAdapter utilityAdapter = new ImageAdapter(MainActivity.this, utilityImage, utilityName);
        utilityGridview.setAdapter(utilityAdapter);

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT * FROM lnp.lnp_admin_key_value where lnp_admin_key_value_key like '%homescreenbanner%'";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                String homeScreenBanner = null;
                if(rs.next())
                    homeScreenBanner = rs.getString("lnp_admin_key_value_value");

                String finalHomeScreenBanner = homeScreenBanner;
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    LayoutInflater inflater = getLayoutInflater();
                    View imageLayoutView = inflater.inflate(R.layout.home_page_banner, null);
                    ImageView image = (ImageView) imageLayoutView.findViewById(R.id.goProDialogImage);
                    //an URI example
                    Uri uri = Uri.parse(finalHomeScreenBanner);

                    Picasso.get().load(uri.toString()).into(image);
                    Boolean walletText = false;

                     if(isRetailer) {
                        new Thread(() -> {
                            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                                String retailerWallet = "SELECT * FROM lnp.lnp_user where idlnp_user_id = "+ userIdInt;

                                Statement retailerStatement = conn.createStatement();
                                ResultSet resultSet = retailerStatement.executeQuery(retailerWallet);
                                String retailerCreditWallet = null, retailerDebitWallet = null;
                                while (resultSet.next()) {
                                    retailerCreditWallet = resultSet.getString("lnp_user_credit_fund");
                                    retailerDebitWallet = resultSet.getString("lnp_user_debit_fund");
                                }
                                String finalRetailerCreditWallet = retailerCreditWallet;
                                String finalRetailerDebitWallet = retailerDebitWallet;
                                runOnUiThread(() -> {
                                    try {
                                            creditWalletText.setText("Credit Wallet: \n"+ finalRetailerCreditWallet);
                                            debitWalletText.setText("Debit Wallet: \n"+ finalRetailerDebitWallet);

                                    } catch (Exception ex) {
                                        Log.e("InfoAsyncTask", "Error reading school information", ex);
                                    }

                                });
                            } catch (Exception e) {
                                Log.e("InfoAsyncTask", "Error reading school information", e);
                            }

                        }).start();
                    } else {
                        if(creditWallet != null && debitWallet != null) {
                            walletText = true;
                            creditWalletText.setText("Credit Wallet: \n"+creditWallet);
                            debitWalletText.setText("Debit Wallet: \n"+debitWallet);
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    while(walletText) {
                        creditWalletText.setText("Credit Wallet: \n"+creditWallet);
                        debitWalletText.setText("Debit Wallet: \n"+debitWallet);
                        break;
                    }

                    builder.setView(imageLayoutView)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder.create();
                    builder.show();

                    if (isAdmin) {
                        new Thread(() -> {
                            try (Connection updateCon = DriverManager.getConnection(URL, USER, PASSWORD)) {
                                String updatesql = "UPDATE lnp.lnp_user SET lnp_user_debit_fund = "+ debitWallet +" where idlnp_user_id = "+ userIdInt;
                                Statement amountUpdate = updateCon.createStatement();
                                amountUpdate.executeUpdate(updatesql);

                            } catch (Exception e) {
                                Log.e("InfoAsyncTask", "Error reading school information", e);
                            }

                        }).start();
                    }

                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

        utilityGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(isAdmin || isRetailer) {
                    if (position == 0) {
                        Intent i = new Intent(getApplicationContext(), MobileRechargeUtilActivity.class);
                        startActivity(i);
                    } else if (position == 1) {
                        Intent i = new Intent(getApplicationContext(), PanCardUtilActivity.class);
                        startActivity(i);
                    } else if (position == 2) {
                        Intent i = new Intent(getApplicationContext(), BBPSUtilActivity.class);
                        startActivity(i);
                    } else if (position == 3) {
                        Intent i = new Intent(getApplicationContext(), FundRequestActivity.class);
                        i.putExtra("debitWallet", debitWallet);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "You are not a retailer. Please click on verification banner above to become retailer!", Toast.LENGTH_SHORT).show();
                }

            }
        });



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){

                if (position == 6) {
                    // Send intent to SingleViewActivity
                    Intent i = new Intent(getApplicationContext(), AdminPanelActivity.class);
                    startActivity(i);
                } else if (position == 0) {
                    Intent i = new Intent(getApplicationContext(), SingleViewActivity.class);
                    i.putExtra("service","Other Services");
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getApplicationContext(), SingleViewActivity.class);
                    i.putExtra("service","CA Services");
                    startActivity(i);
                } else if (position == 2) {
                    Intent i = new Intent(getApplicationContext(), SingleViewActivity.class);
                    i.putExtra("service","Engineer Services");
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(getApplicationContext(), SingleViewActivity.class);
                    i.putExtra("service","CIBIL Score");
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(getApplicationContext(), SingleViewActivity.class);
                    i.putExtra("service","Super Savings");
                    startActivity(i);
                } else if (position == 5) {
                    Intent i = new Intent(getApplicationContext(), ViewContactFormActivity.class);
                    startActivity(i);
                }

            }
        });
    }

    private void deleteuser(Integer userId) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "Delete from lnp.lnp_user where idlnp_user_id = "+userId;
                PreparedStatement statement = connection.prepareStatement(sql);
                Integer i = statement.executeUpdate();

                if (i > 0)
                    System.out.println("Record deleted successfully.");
                else
                    System.out.println("Record not found.");

                runOnUiThread(() -> {
                    sp = getSharedPreferences("login",MODE_PRIVATE);
                    sp.edit().remove("logged").apply();
                    sp.edit().remove("userId").apply();
                    sp.edit().remove("admin").apply();
                    sp.edit().remove("retailer").apply();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Hello", "User account deleted.");
                        }
                    }
                });

    }

    public void getDebitBalance() {

        JWTKey jwtKey = new JWTKey();
        String token = jwtKey.getToken();
        String url = "https://paysprint.in/service-api/api/v1/service/balance/balance/cashbalance";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    debitWallet = jsonObject.getString("cdwallet");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorisedkey", "NjAxMjlhZGQ5MjMwODNiZTMwYzFjNGQwYWRlM2QwNmU=");
                params.put("Token", token);
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    public void getCreditBalance() {

        JWTKey jwtKey = new JWTKey();
        String token = jwtKey.getToken();
        String url = "https://paysprint.in/service-api/api/v1/service/balance/balance/mainbalance";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    creditWallet = jsonObject.getString("wallet");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorisedkey", "NjAxMjlhZGQ5MjMwODNiZTMwYzFjNGQwYWRlM2QwNmU=");
                params.put("Token", token);
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

}