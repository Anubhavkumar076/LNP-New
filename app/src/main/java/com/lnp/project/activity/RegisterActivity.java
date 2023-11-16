package com.lnp.project.activity;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.lnp.project.LoginActivity;
import com.lnp.project.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegisterActivity extends AppCompatActivity {
    private EditText email,password,confirmPassword;
    private Button sign;
    private ProgressDialog progressBar;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    private FirebaseAuth mAuth;

    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirmPassword=findViewById(R.id.confirm);
        sign=findViewById(R.id.sign);
        progressBar=new ProgressDialog(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",false)){
            sendToMainActivity();
        }

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }
    private void createNewAccount() {
        String name=email.getText().toString().trim();
        String pass=password.getText().toString().trim();
        String confirmpass=confirmPassword.getText().toString().trim();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(getApplicationContext(),"Name is empty",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(getApplicationContext(),"Password is empty",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmpass))
        {
            Toast.makeText(getApplicationContext(),"confirm Password is empty",Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equalsIgnoreCase(confirmpass))
        {
            Toast.makeText(getApplicationContext(),"confirm Password does not match with password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressBar.setTitle("Creating Account");
            progressBar.setMessage("Wait For A While");
            progressBar.show();
            progressBar.setCanceledOnTouchOutside(true);

            if (pass.equals(confirmpass)) {
                mAuth.createUserWithEmailAndPassword(name, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");
                                    sp.edit().putBoolean("logged",true).apply();

                                    new Thread(() -> {
                                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                                            String sql = "INSERT into lnp.lnp_user(lnp_user_email, lnp_user_admin) values ('"+name+"', 0)";
                                            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                                            statement.execute();

                                            ResultSet resultSet = statement.getGeneratedKeys();
                                            int generatedKey = 0;
                                            if (resultSet.next()) {
                                                generatedKey = resultSet.getInt(1);
                                                sp.edit().putString("userId",String.valueOf(generatedKey)).apply();
                                            } else {
                                                throw new SQLException("Creating user failed, no rows affected.");
                                            }

                                            String userInfoSql = "SELECT * FROM lnp.lnp_user_information where lnp_user_information_user_id = "+generatedKey;
//                Toast.makeText(getApplicationContext(), "Message "+ sql , Toast.LENGTH_SHORT).show();
                                            Statement statement1 = connection.createStatement();
                                            ResultSet rs = statement1.executeQuery(userInfoSql);

                                            if(!rs.next()) {
                                                sp.edit().putBoolean("userInfoAvail",false).apply();
                                                runOnUiThread(() -> {
                                                    Intent i=new Intent(RegisterActivity.this, UserInformationActivity.class);
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

//                                    FirebaseUser user = mAuth.getCurrentUser();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed, please try again after sometime.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
    }

    private void sendToMainActivity() {
        Intent i=new Intent(RegisterActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}