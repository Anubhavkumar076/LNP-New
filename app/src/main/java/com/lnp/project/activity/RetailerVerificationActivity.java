package com.lnp.project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lnp.project.R;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RetailerVerificationActivity extends AppCompatActivity {

    RelativeLayout panRelativeLayout, aadhaarRelativeLayout;
    ImageView panImageView, aadhaarImageView;
    Button uploadButton;

    TextView mTextView;

    private String panImageUrl, aadhaarImageUrl;
    private Integer userId;

    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    private Uri panFilePath, aadhaarFilePath;
    private int PICK_PAN_IMAGE_REQUEST = 0;
    private int PICK_AADHAAR_IMAGE_REQUEST = 1;

    FirebaseStorage storage;
    StorageReference storageReference;

    SharedPreferences sp;

    Boolean panUpdate = false, aadhaarUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_verification);
        panImageView = findViewById(R.id.pan_image);
        aadhaarImageView = findViewById(R.id.aadhar_image);
        uploadButton = findViewById(R.id.upload_verification_button);
        mTextView = findViewById(R.id.retailer_verification);
        panRelativeLayout = findViewById(R.id.upload_pan_relative);
        aadhaarRelativeLayout = findViewById(R.id.upload_aadhar_relative);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        sp = getSharedPreferences("login",MODE_PRIVATE);
        userId = Integer.parseInt(sp.getString("userId", ""));

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT * FROM lnp.lnp_user_information where lnp_user_information_user_id = "+ userId;
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);
                String panUrl = null, aadhaarUrl = null;
                while (rs.next()) {
                    panUrl = rs.getString("lnp_user_information_pan_url");
                    aadhaarUrl = rs.getString("lnp_user_information_aadhaar_url");
                }
                String finalPanUrl = panUrl;
                String finalAadhaarUrl = aadhaarUrl;
                runOnUiThread(() -> {
                    if (finalPanUrl != null && !finalPanUrl.trim().isEmpty()) {
                        panRelativeLayout.setVisibility(View.GONE);
                        panUpdate = true;
                    }
                    if (finalAadhaarUrl != null && !finalAadhaarUrl.trim().isEmpty()) {
                        aadhaarRelativeLayout.setVisibility(View.GONE);
                        aadhaarUpdate = true;
                    }
                    if (aadhaarUpdate && panUpdate) {
                        mTextView.setVisibility(View.VISIBLE);
                        uploadButton.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

        panImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(PICK_PAN_IMAGE_REQUEST);
            }
        });

        aadhaarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(PICK_AADHAAR_IMAGE_REQUEST);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPanImage();
                uploadAadhaarImage();
            }
        });





    }

    private void uploadAadhaarImage() {
        if (aadhaarFilePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "aadhaarimages/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(aadhaarFilePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Uri downloadUrl = uri;

                                            aadhaarImageUrl = downloadUrl.toString();
                                            new AadhaarAsyncTask().execute();
                                            aadhaarUpdate = true;

                                            //Do what you want with the url
                                        }
                                    });
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(getApplicationContext(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    aadhaarRelativeLayout.setVisibility(View.GONE);
                                    aadhaarUpdate = true;
                                    if (panUpdate && aadhaarUpdate) {
                                        mTextView.setVisibility(View.VISIBLE);
                                        uploadButton.setVisibility(View.GONE);
                                    }
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        } else {
            Toast.makeText(RetailerVerificationActivity.this, "Please add Aadhaar Image to complete your verification", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPanImage() {
        if (panFilePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "panimages/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(panFilePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Uri downloadUrl = uri;

                                            panImageUrl = downloadUrl.toString();
                                            new PanAsyncTask().execute();

                                            //Do what you want with the url
                                        }
                                    });
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(getApplicationContext(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    panRelativeLayout.setVisibility(View.GONE);
                                    panUpdate = true;
                                    if (panUpdate && aadhaarUpdate) {
                                        mTextView.setVisibility(View.VISIBLE);
                                        uploadButton.setVisibility(View.GONE);
                                    }
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        } else {
            Toast.makeText(RetailerVerificationActivity.this, "Please add Pan Image to complete your verification", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage(Integer panImageRequest) {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                panImageRequest);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_PAN_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            panFilePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                panFilePath);
                panImageView.setBackground(null);
                panImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        } else if(requestCode == PICK_AADHAAR_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            // Get the Uri of data
            aadhaarFilePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                aadhaarFilePath);
                aadhaarImageView.setBackground(null);
                aadhaarImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class PanAsyncTask extends AsyncTask<Void, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            Map<String, String> info = new HashMap<>();

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "UPDATE lnp.lnp_user_information SET lnp_user_information_pan_url='"+ panImageUrl +"' WHERE lnp_user_information_user_id = "+ userId;
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

            return info;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class AadhaarAsyncTask extends AsyncTask<Void, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            Map<String, String> info = new HashMap<>();

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "UPDATE lnp.lnp_user_information SET lnp_user_information_aadhaar_url = '"+ aadhaarImageUrl +"' WHERE lnp_user_information_user_id = "+ userId;
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

            return info;
        }
    }
}