package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lnp.project.R;
import com.lnp.project.common.JWTKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;

public class PanCardWebViewActivity extends AppCompatActivity {


    private WebView webView;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    SharedPreferences sp;
    Integer userId;

    JSONObject requestJson;

    Integer wallet = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_card_web_view);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        userId = Integer.parseInt(sp.getString("userId", "").trim());
        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "SELECT lnp_user_debit_fund FROM lnp.lnp_user where idlnp_user_id = "+ userId;
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);

                while (rs.next()) {
                    wallet = rs.getInt("lnp_user_debit_fund");
                }
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

        webView = findViewById(R.id.pan_card_web);

        JSONObject jsonObject = new JSONObject();
//        String jsonString = getIntent().getStringExtra("jsonString").toString();
        try {
            String refId = getIntent().getStringExtra("refid").toString();
            jsonObject.put("refid", refId);
            String title = getIntent().getStringExtra("title").toString();
            jsonObject.put("title", title);
            String firstName = getIntent().getStringExtra("firstname").toString();
            if (!firstName.isEmpty())
                jsonObject.put("firstname", getIntent().getStringExtra("firstname").toString());
            String middleName = getIntent().getStringExtra("middlename").toString();
            if (!middleName.isEmpty())
                jsonObject.put("middlename", getIntent().getStringExtra("middlename").toString());
            String lastName = getIntent().getStringExtra("lastName").toString();
            jsonObject.put("lastname", lastName);
            String mode = getIntent().getStringExtra("mode").toString();
            jsonObject.put("mode", mode);
            String gender = getIntent().getStringExtra("gender").toString();
            jsonObject.put("gender", gender);
            jsonObject.put("redirect_url", "https://www.google.com");
            String email = getIntent().getStringExtra("email").toString();
            if (!email.isEmpty())
                jsonObject.put("email", email);
            String kyctype = getIntent().getStringExtra("kyctype").toString();
            jsonObject.put("kyctype", kyctype);
            String jsonString = jsonObject.toString();
            if(wallet >= 107l)
                savePANData(refId, firstName.isEmpty()? " " : firstName, middleName.isEmpty() ? " " : middleName, lastName, mode, gender, email.isEmpty() ? " " : email , kyctype);
            else
                Toast.makeText(PanCardWebViewActivity.this, "You have insufficient balance. Please contact admin for recharge.", Toast.LENGTH_SHORT).show();
            requestJson = jsonObject;
            new PostData().execute(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public void savePANData(String refId, String firstName, String middleName, String lastName,
                            String mode, String gender, String email, String kycType) {
        StringBuilder queryToCreate = new StringBuilder();
        queryToCreate.append("INSERT INTO lnp.pan_card_info VALUES ("+null+", '"+ refId +"' , '"+ firstName
                +"', '"+ middleName +"', '"+ lastName +"', '"+ mode +"', '"+ gender +"', '"+ email +"', '"+ kycType +"', "+null+","+null+", "+userId+")");

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                Statement statement = connection.createStatement();
                statement.executeUpdate(queryToCreate.toString());

                String sql = "UPDATE lnp.lnp_user SET lnp_user_debit_fund = lnp_user_debit_fund - "+ 107 +" where idlnp_user_id = "+ userId;
                Statement amountUpdate = connection.createStatement();
                amountUpdate.executeUpdate(sql);

            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();
    }

    // on below line creating a class to post the data.
    class PostData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {

                // on below line creating a url to post the data.
                URL url = new URL("https://paysprint.in/service-api/api/v1/service/pan/V2/generateurl");

                // on below line opening the connection.
                HttpURLConnection client = (HttpURLConnection) url.openConnection();

                // on below line setting method as post.
                client.setRequestMethod("POST");

                JWTKey jwtKey = new JWTKey();
                String jwtKeyString = jwtKey.getToken();
                // on below line setting content type and accept type.
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestProperty("Accept", "application/json");
                client.setRequestProperty("Authorisedkey", "NjAxMjlhZGQ5MjMwODNiZTMwYzFjNGQwYWRlM2QwNmU=");
                client.setRequestProperty("Token", jwtKeyString);

//                // on below line setting client.
                client.setDoOutput(true);

                // on below line we are creating an output stream and posting the data.
                try (OutputStream os = client.getOutputStream()) {
                    byte[] input = strings[0].getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // on below line creating and initializing buffer reader.
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), "utf-8"))) {

                    // on below line creating a string builder.
                    StringBuilder response = new StringBuilder();

                    // on below line creating a variable for response line.
                    String responseLine = null;

                    // on below line writing the response
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    Instant instant = Instant.now();
                    long timeStampMillis = instant.toEpochMilli();
                    ObjectMapper obj = new ObjectMapper();
                    obj.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                    obj.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                    String requestjson = obj.writeValueAsString(requestJson);
//                    String responseJson = obj.writeValueAsString(response);
                    StringBuilder query = new StringBuilder();
                    query.append("INSERT INTO lnp.transaction_log VALUES ("+null+", '"+ url +"' , '"+ requestjson
                            +"', '"+ response +"', '"+ userId +"', "+ timeStampMillis +")");

                    new Thread(() -> {
                        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            Statement statement = connection.createStatement();
                            statement.executeUpdate(query.toString());

                        } catch (Exception e) {
                            Log.e("InfoAsyncTask", "Error reading school information", e);
                        }

                    }).start();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject resObj = jsonObject.getJSONObject("data");
                    String responseURL = resObj.getString("url");
                    String responseEncdata = resObj.getString("encdata");
                    runOnUiThread(() -> {
                        WebSettings settings = webView.getSettings();
                        settings.setJavaScriptEnabled(true);
                        settings.setDomStorageEnabled(true);
                        webView.setWebChromeClient(new WebChromeClient());
                        webView.loadData(getHtml(responseURL, responseEncdata), "text/html", "UTF-8");
                        // on below line displaying a toast message.
                        Toast.makeText(PanCardWebViewActivity.this, "Data has been posted to the API.", Toast.LENGTH_SHORT).show();
                    });



                }

            } catch (Exception e) {

                runOnUiThread(() -> {
                    // on below line handling the exception.
                    e.printStackTrace();
                    Toast.makeText(PanCardWebViewActivity.this, "Fail to post the data : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
            return null;
        }
    }

    private String getHtml(String url, String encData) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>\n" +
                "  <form method=\"post\" action=\""+url+"\">\n" +
                "  <textarea name=\"encdata\" style=\"display:none\">"+encData+"</textarea>\n" +
                "  <input type=\"submit\" value=\"Submit\" onChange=\"form.submit()\">\n" +
                "</form>\n" +
                "</html>");
        return stringBuilder.toString();
    }
}