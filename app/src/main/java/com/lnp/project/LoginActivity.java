package com.lnp.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.lnp.project.activity.MainActivity;
import com.lnp.project.activity.RegisterActivity;
import com.lnp.project.activity.UserInformationActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {
    private EditText logusername,logpassword;
    private Button login;
    private TextView newAccountLink;
    private ProgressDialog progressBar;
    SharedPreferences sp;

    private FirebaseAuth mAuth;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logusername=findViewById(R.id.login_email);
        logpassword=findViewById(R.id.login_password);
        login=findViewById(R.id.login_sign);
        newAccountLink=findViewById(R.id.login_create);
        progressBar=new ProgressDialog(this);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("login",MODE_PRIVATE);

        newAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToRegisterActivity();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAuthentication();
            }
        });
    }

    //*******************************CONTAINS AUTHENTICATION PROCESS*********************************************
    private void loginAuthentication() {
        String name=logusername.getText().toString().trim();
        String pass=logpassword.getText().toString().trim();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(getApplicationContext(),"Name is empty",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(getApplicationContext(),"Password is empty",Toast.LENGTH_SHORT).show();
        }
        else {
            progressBar.setTitle("Signing in");
            progressBar.setMessage("Wait For A While");
            progressBar.show();
            progressBar.setCanceledOnTouchOutside(true);

            if (pass != null) {
                mAuth.signInWithEmailAndPassword(name, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    progressBar.hide();
                                    Log.d("TAG", "signInWithEmail:success");
                                    sp.edit().putBoolean("logged",true).apply();
                                    new Thread(() -> {
                                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                                            String sql = "Select * from lnp.lnp_user where lnp_user_email = '"+name+"' LIMIT 1";
                                            Statement statement = connection.createStatement();
                                            ResultSet rs = statement.executeQuery(sql);
                                            Integer userId = null;
                                            while (rs.next()) {
                                                userId = rs.getInt("idlnp_user_id");
                                                Integer admin = rs.getInt("lnp_user_admin");
                                                sp.edit().putString("userId",String.valueOf(userId)).apply();
                                                if(admin.equals(1))
                                                    sp.edit().putBoolean("admin",true).apply();
                                                else
                                                    sp.edit().putBoolean("admin",false).apply();

                                                if (admin.equals(2))
                                                    sp.edit().putBoolean("retailer",true).apply();
                                                else
                                                    sp.edit().putBoolean("retailer",false).apply();

                                            }

                                            String userInfoSql = "SELECT * FROM lnp.lnp_user_information where lnp_user_information_user_id = "+userId;
//                Toast.makeText(getApplicationContext(), "Message "+ sql , Toast.LENGTH_SHORT).show();
                                            statement = connection.createStatement();
                                            rs = statement.executeQuery(userInfoSql);

                                            if(!rs.next()) {
                                                sp.edit().putBoolean("userInfoAvail",false).apply();
                                                runOnUiThread(() -> {
                                                    Intent i=new Intent(LoginActivity.this, UserInformationActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    finish();
                                                });
                                            } else {
                                                runOnUiThread(() -> {
                                                    sendToMainActivity();
                                                });
                                            }

                                        } catch (Exception e) {
                                            Log.e("InfoAsyncTask", "Error reading school information", e);
                                        }

                                    }).start();
                                } else {
                                    progressBar.hide();
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed, please check your email and password.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
    }


    private void sendToMainActivity() {
        Intent i=new Intent(LoginActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    //********************************************FUNCTION USED IN TEXTVIEW*************************************************
    private void sendToRegisterActivity() {
        Intent i=new Intent(LoginActivity.this, RegisterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}