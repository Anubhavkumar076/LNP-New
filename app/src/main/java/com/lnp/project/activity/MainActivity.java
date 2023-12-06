package com.lnp.project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lnp.project.ContactUsFragment;
import com.lnp.project.HomeFragment;
import com.lnp.project.LedgerFragment;
import com.lnp.project.LoginActivity;
import com.lnp.project.ProfileFragment;
import com.lnp.project.R;
import com.lnp.project.adapter.ImageAdapter;
import com.lnp.project.adapter.MainAdapter;
import com.lnp.project.adapter.ViewBBPSAdapter;
import com.lnp.project.adapter.ViewRechargeAdapter;
import com.lnp.project.common.JWTKey;
import com.lnp.project.dto.MainAdapterDto;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

//    String creditWallet, debitWallet;
//
//    TextView creditWalletText, debitWalletText;
////    retailerVerifyText;
//
//    CardView cardView;
//
//    RecyclerView mRecycler;

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

        if (item.getTitle().equals("Youtube")) {
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
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("login",MODE_PRIVATE);
//        creditWalletText = findViewById(R.id.credit_balance_text);
//        debitWalletText = findViewById(R.id.debit_card_balance);
//        cardView = findViewById(R.id.main_card_view);
//        mRecycler = findViewById(R.id.main_recycler_list);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

//        getCreditBalance();
//        getDebitBalance();
        setTitle("LNP Mini Bank");
//        MainAdapterDto mainAdapterDto = new MainAdapterDto();
        isAdmin = sp.getBoolean("admin", false);
        isRetailer = sp.getBoolean("retailer", false);
        Integer userIdInt = Integer.parseInt(sp.getString("userId", ""));
//        List<MainAdapterDto> mainAdapterDtoList = new ArrayList<>();
//        if (isAdmin)
//            mainAdapterDto.setAdmin(true);
//        else if (isRetailer) {
//            mainAdapterDto.setRetailer(true);
//        } else {
//            mainAdapterDto.setUser(true);
//        }

//        if (!isRetailer && !isAdmin) {
//            cardView.setVisibility(View.GONE);
//        }


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

                                Bundle bundle = new Bundle();
                                bundle.putString("retailerCreditWallet", retailerCreditWallet);
                                bundle.putString("retailerDebitWallet", retailerDebitWallet);
                                HomeFragment homeFragment = new HomeFragment();
                                homeFragment.setArguments(bundle);
//                                runOnUiThread(() -> {
//                                    try {
//                                            creditWalletText.setText("Credit Wallet: \n"+ finalRetailerCreditWallet);
//                                            debitWalletText.setText("Debit Wallet: \n"+ finalRetailerDebitWallet);
//
//                                    } catch (Exception ex) {
//                                        Log.e("InfoAsyncTask", "Error reading school information", ex);
//                                    }
//
//                                });
                            } catch (Exception e) {
                                Log.e("InfoAsyncTask", "Error reading school information", e);
                            }

                        }).start();
                    } else {
//                        if(creditWallet != null && debitWallet != null) {
//                            walletText = true;
//                            creditWalletText.setText("Credit Wallet: \n"+creditWallet);
//                            debitWalletText.setText("Debit Wallet: \n"+debitWallet);
//                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

//                    while(walletText) {
////                        creditWalletText.setText("Credit Wallet: \n"+creditWallet);
////                        debitWalletText.setText("Debit Wallet: \n"+debitWallet);
//                        if (!mainAdapterDtoList.isEmpty()
//                                && mainAdapterDtoList.get(0).getDebitWallet() == null  && debitWallet != null) {
//                            mainAdapterDtoList.get(0).setDebitWallet(debitWallet);
//                        }
//                        break;
//                    }

                    builder.setView(imageLayoutView)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder.create();
                    builder.show();

                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();


        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getTitle().equals("Home")) {
                        selectedFragment = new HomeFragment();
                    } else if (item.getTitle().equals("Profile")) {
                        selectedFragment = new ProfileFragment();
                    } else if (item.getTitle().equals("Contact Us")) {
                        selectedFragment = new ContactUsFragment();
                    } else if (item.getTitle().equals("Ledger")) {
                        selectedFragment = new LedgerFragment();
                    } else if (item.getTitle().equals("Wallet")) {
                        selectedFragment = new ContactUsFragment();
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

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

}