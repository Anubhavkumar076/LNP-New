package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lnp.project.R;
import com.lnp.project.adapter.SliderAdapter;
import com.lnp.project.dto.SliderData;
import com.smarteist.autoimageslider.SliderView;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SingleViewActivity extends AppCompatActivity {
    private EditText fullName, address,mobileNumber, loanAmount, savingAmount;
    private Button submit;
    private TextView singleViewTextTerm, singleViewHeadingTextTerm;
    private Spinner loanCategory, caServiceType, engineerBuildingType, engineerServiceType, tenureSpinner, loanTypeSpinner;

    SharedPreferences sp;

    private ProgressDialog progressBar;
    private static final String URL = "jdbc:mysql://database-lnp.cz2mgaxvmcml.ap-south-1.rds.amazonaws.com:3306/lnp-schema";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminlnp";

    private String sqlQuery;

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    String loanTerms, otherLoanTerms, businessLoanTerms, housingLoanTerms, vehicleLoanTerms,
        goldLoanTerms, educationLoanTerms;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_view);
        loanTypeSpinner = findViewById(R.id.loan_type_edittext);
        fullName = findViewById(R.id.fullname_edittext);
        address = findViewById(R.id.address_edittext);
        mobileNumber = findViewById(R.id.mobile_number_edittext);
        submit = findViewById(R.id.submit);
        loanCategory = findViewById(R.id.loan_category_edittext);
        loanAmount = findViewById(R.id.loan_amount_edittext);
        savingAmount = findViewById(R.id.savings_amount_edittext);
        caServiceType = findViewById(R.id.service_edittext);
        engineerBuildingType = findViewById(R.id.engineer_building_type_edittext);
        engineerServiceType = findViewById(R.id.engineer_service_type_edittext);
        tenureSpinner = findViewById(R.id.tenure_edittext);
        singleViewTextTerm = findViewById(R.id.single_view_text_term);
        singleViewHeadingTextTerm = findViewById(R.id.single_view_text_heading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        savingAmount.addTextChangedListener(onTextChangedListener());
        loanAmount.addTextChangedListener(onTextChangedListener());

        progressBar=new ProgressDialog(this);

        String title = getIntent().getStringExtra("service").trim();
        setTitle(title);

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("LNP");
        // we are creating array list for storing our image urls.
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

        // initializing the slider view.
        SliderView sliderView = findViewById(R.id.slider);

        // passing this array list inside our adapter class.
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT * FROM lnp.lnp_admin_key_value where lnp_admin_key_value_key like '%sliderscreen%'";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    sliderDataArrayList.add(new SliderData(rs.getString("lnp_admin_key_value_value")));
                }

                runOnUiThread(() -> {
                    // passing this array list inside our adapter class.
                    sliderView.setSliderAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

        //create a list of items for the spinner.
//        String[] contactFormItems = new String[]{"Loans", "CA Services", "Engineer Services", "Cibil Score", "Super Savings Pack"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, contactFormItems);
        //set the spinners adapter to the previously created one.
//        contactFormSprinner.setAdapter(adapter);

        String formType = getIntent().getStringExtra("service").trim();
        loanTerms = "LNP offers financial guidance on a range of loans available in the Loan Category" +
                " option. Kindly choose a specific loan category to view detailed terms corresponding to your particular loan query.";

        //LOANS
        String[] loanItems = new String[]{"Select Loan Category", "Business Loan", "Housing Loan", "Vehicle Loan", "Gold Loan", "Education Loan", "Other Loan"};
        ArrayAdapter<String> loanItemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, loanItems);
        loanCategory.setAdapter(loanItemsAdapter);

        //CA Services
        String[] caItems = new String[]{"Select CA Service","IT Returns", "GST", "MSME", "Company Formation", "FASSAI Registration", "ISO Certificate", "Digitial Signature Certificate"};
        ArrayAdapter<String> caItemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, caItems);
        caServiceType.setAdapter(caItemsAdapter);

        //Engineer Services
        String[] engineerBuildingItems = new String[]{"Select Engineer Building Type","Residential", "Semi Commercial", "Commercial", "Industrial"};
        ArrayAdapter<String> engineerBuildingItemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, engineerBuildingItems);
        engineerBuildingType.setAdapter(engineerBuildingItemsAdapter);

        String[] engineerServiceTypeItems = new String[]{"Select Engineer Service Type","Planning", "Estimation", "Valuation"};
        ArrayAdapter<String> engineerServiceTypeItemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, engineerServiceTypeItems);
        engineerServiceType.setAdapter(engineerServiceTypeItemsAdapter);

        //Super Saving Packs
        String[] tenures = new String[]{"Select Tenure","3 Months", "6 Months", "12 Months", "18 Months", "24 Months"};
        ArrayAdapter<String> tenureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tenures);
        tenureSpinner.setAdapter(tenureAdapter);

        //Home loan

        String[] homeLoanItems = new String[]{"Select Loan Type","Construction Loan", "Extension/Renovation Loan", "Purchase Loan", "Balance Transfer Loan", "Mortgage(LAP)", "NRI"};
        ArrayAdapter<String> homeLoanItemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, homeLoanItems);
        loanTypeSpinner.setAdapter(homeLoanItemsAdapter);

        loanCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    loanTypeSpinner.setVisibility(View.GONE);
                    singleViewTextTerm.setText(loanTerms);
                } else if(position == 1) {
                    businessLoanTerms = "We offer financial guidance on securing business loans to address diverse " +
                            "expenses related to various business activities such as to setup new infrastructure, " +
                            "or to manage the operating costs and other allied activities." +
                            " Leveraging an extensive network of financial experts and NBFC Banks, LNP is well-equipped to address your queries and ensure the optimal solution for your needs.";
                    loanTypeSpinner.setVisibility(View.GONE);
                    singleViewTextTerm.setText(businessLoanTerms);
                } else if(position == 2) {
                    housingLoanTerms = "We offer financial guidance on securing Housing loans to address diverse " +
                            "expenses related to various Housing activities such as to make new house, " +
                            "or to have house renovation and other allied activities." +
                            " Leveraging an extensive network of financial experts and NBFC Banks, LNP is well-equipped to address your queries and ensure the optimal solution for your needs.";
                    loanTypeSpinner.setVisibility(View.VISIBLE);
                    singleViewTextTerm.setText(housingLoanTerms);
                } else if(position == 3) {
                    vehicleLoanTerms = "We offer financial guidance on securing Vehicle loans to address diverse " +
                            "expenses related to Vehicle operations such as to buy new vehicle, " +
                            "or to have vehicle renovation and other allied activities." +
                            " Leveraging an extensive network of financial experts and NBFC Banks, LNP is well-equipped to address your queries and ensure the optimal solution for your needs.";
                    loanTypeSpinner.setVisibility(View.GONE);
                    singleViewTextTerm.setText(vehicleLoanTerms);
                } else if(position == 4) {
                    goldLoanTerms = "We offer financial guidance on securing Gold loans as per your need." +
                        " Leveraging an extensive network of financial experts and NBFC Banks, LNP is well-equipped to address your queries and ensure the optimal solution for your needs.";
                    loanTypeSpinner.setVisibility(View.GONE);
                    singleViewTextTerm.setText(goldLoanTerms);
                }  else if(position == 5) {
                    educationLoanTerms = "We offer financial guidance on securing Education loans as per your need and courses." +
                            " Leveraging an extensive network of financial experts and NBFC Banks, LNP is well-equipped to address your queries and ensure the optimal solution for your needs.";
                    loanTypeSpinner.setVisibility(View.GONE);
                    singleViewTextTerm.setText(educationLoanTerms);
                }  else if(position == 6) {
                    otherLoanTerms = "We offer financial guidance on securing different other kind of other loans such as " +
                            " Microfinance Loans, LAP to address diverse " +
                            "expenses related to users need  and other allied activities." +
                            " Leveraging an extensive network of financial experts and NBFC Banks, " +
                            "LNP is well-equipped to address your queries and ensure the optimal solution " +
                            "for your needs.";
                    loanTypeSpinner.setVisibility(View.GONE);
                    singleViewTextTerm.setText(otherLoanTerms);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(formType.equals("Other Services")) {
            singleViewTextTerm.setText(loanTerms);
            loanAmount.setVisibility(View.VISIBLE);
            loanCategory.setVisibility(View.VISIBLE);
            loanTypeSpinner.setVisibility(View.GONE);
            engineerBuildingType.setVisibility(View.GONE);
            engineerServiceType.setVisibility(View.GONE);
            tenureSpinner.setVisibility(View.GONE);
            caServiceType.setVisibility(View.GONE);
            savingAmount.setVisibility(View.GONE);
        } else if (formType.equals("CA Services")) {
            singleViewHeadingTextTerm.setVisibility(View.GONE);
            singleViewTextTerm.setVisibility(View.GONE);
            loanAmount.setVisibility(View.GONE);
            loanCategory.setVisibility(View.GONE);
            loanTypeSpinner.setVisibility(View.GONE);
            engineerBuildingType.setVisibility(View.GONE);
            engineerServiceType.setVisibility(View.GONE);
            tenureSpinner.setVisibility(View.GONE);
            caServiceType.setVisibility(View.VISIBLE);
            savingAmount.setVisibility(View.GONE);
        } else if (formType.equals("Engineer Services")) {
            singleViewHeadingTextTerm.setVisibility(View.GONE);
            singleViewTextTerm.setVisibility(View.GONE);
            loanAmount.setVisibility(View.GONE);
            loanCategory.setVisibility(View.GONE);
            loanTypeSpinner.setVisibility(View.GONE);
            engineerBuildingType.setVisibility(View.VISIBLE);
            engineerServiceType.setVisibility(View.VISIBLE);
            tenureSpinner.setVisibility(View.GONE);
            caServiceType.setVisibility(View.GONE);
            savingAmount.setVisibility(View.GONE);
        } else if (formType.equals("CIBIL Score")) {
            singleViewHeadingTextTerm.setVisibility(View.GONE);
            singleViewTextTerm.setVisibility(View.GONE);
            loanAmount.setVisibility(View.GONE);
            loanCategory.setVisibility(View.GONE);
            loanTypeSpinner.setVisibility(View.GONE);
            engineerBuildingType.setVisibility(View.GONE);
            engineerServiceType.setVisibility(View.GONE);
            tenureSpinner.setVisibility(View.GONE);
            caServiceType.setVisibility(View.GONE);
            savingAmount.setVisibility(View.GONE);
        } else {
            singleViewHeadingTextTerm.setVisibility(View.GONE);
            singleViewTextTerm.setVisibility(View.GONE);
            loanAmount.setVisibility(View.GONE);
            loanCategory.setVisibility(View.GONE);
            loanTypeSpinner.setVisibility(View.GONE);
            engineerBuildingType.setVisibility(View.GONE);
            engineerServiceType.setVisibility(View.GONE);
            tenureSpinner.setVisibility(View.VISIBLE);
            caServiceType.setVisibility(View.GONE);
            savingAmount.setVisibility(View.VISIBLE);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistContactFormInfo(formType);
            }
        });
        // Get intent data
//        Intent i = getIntent();

//        // Selected image id
//        int position = i.getExtras().getInt("id");
//        if (position == 0) {
//
//        }

    }

    public void persistContactFormInfo(String formType) {

        String fullNameText = fullName.getText().toString().trim();
        String addressText = address.getText().toString().trim();
        String mobileNumberText = mobileNumber.getText().toString().trim();
        // Loan
        String loanType = loanCategory.getSelectedItem().toString();
        String loanSubType = loanTypeSpinner.getSelectedItem().toString();
        String loanAmountText = loanAmount.getText().toString().trim();
        // engineer
        String engineerBuildingTypeText = engineerBuildingType.getSelectedItem().toString();
        String engineerServiceTypeText = engineerServiceType.getSelectedItem().toString();
        //ca
        String caServiceTypeText = caServiceType.getSelectedItem().toString();
        //tenure
        String tenureText = tenureSpinner.getSelectedItem().toString();
        String savingAmountText = savingAmount.getText().toString().trim();

        if(TextUtils.isEmpty(fullNameText) || TextUtils.isEmpty(addressText) || TextUtils.isEmpty(mobileNumberText))
        {
            Toast.makeText(getApplicationContext(),"Name, Address and Number can not be empty",Toast.LENGTH_SHORT).show();
            return;
        } else if(mobileNumberText.trim().length() != 10){
            Toast.makeText(getApplicationContext(),"Incorrect Mobile Number",Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder queryToCreate = new StringBuilder();
        queryToCreate.append("INSERT INTO lnp.lnp_contact_forms VALUES ("+null+", '"+ formType +"' , '"+ fullNameText
                +"', '"+ addressText +"', '"+ mobileNumberText +"'");

        if(formType.equals("Loan Services")) {
            if(!TextUtils.isEmpty(loanType) && !loanType.contains("Select") && !loanAmountText.trim().isEmpty())
                queryToCreate.append(", ' "+ loanType +"', '"+ loanAmountText +"'");
            else {
                Toast.makeText(getApplicationContext(),"Please fill all the required information!",Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            queryToCreate.append(", "+ null +", "+ null);
        }

        if(loanType.contains("Housing")) {
            if(!TextUtils.isEmpty(loanSubType) && !loanSubType.contains("Select"))
                queryToCreate.append(",' "+loanSubType+"'");
            else {
                Toast.makeText(getApplicationContext(),"Please fill all the required information!",Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            queryToCreate.append(", "+ null);
        }

        if(formType.contains("Engineer Services")) {
            if(!TextUtils.isEmpty(engineerBuildingTypeText) && !TextUtils.isEmpty(engineerServiceTypeText)
                    && !engineerBuildingTypeText.contains("Select") && !engineerServiceTypeText.contains("Select")) {
                queryToCreate.append(", ' " + engineerBuildingTypeText + "', '" + engineerServiceTypeText + "'");
            } else {
                Toast.makeText(getApplicationContext(),"Please fill all the required information!",Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            queryToCreate.append(", "+ null +", "+ null);
        }

        if(formType.contains("CA Services")) {
            if(!TextUtils.isEmpty(caServiceTypeText) && !caServiceTypeText.contains("Select"))
                queryToCreate.append(", ' "+caServiceTypeText+"'");
            else {
                Toast.makeText(getApplicationContext(),"Please fill all the required information!",Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            queryToCreate.append(", "+ null);
        }

        if(formType.equals("Super Savings")) {
            if(!TextUtils.isEmpty(tenureText) && !tenureText.contains("Select")
                && !TextUtils.isEmpty(savingAmountText)) {
                queryToCreate.append(", ' "+tenureText+"', '"+ savingAmountText +"'");
            } else {
                Toast.makeText(getApplicationContext(),"Please fill all the required information!",Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            queryToCreate.append(", " + null + ", " + null);
        }

        progressBar.setTitle("Data Saving");
        progressBar.setMessage("Wait For A While");
        progressBar.show();
        progressBar.setCanceledOnTouchOutside(true);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        String userId = sp.getString("userId", "").trim();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        queryToCreate.append(", "+ Integer.parseInt(userId) +", '"+ dtf.format(now) +"')");
        sqlQuery = queryToCreate.toString();

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                Statement statement = connection.createStatement();
                statement.executeUpdate(sqlQuery);

                runOnUiThread(() -> {
                    progressBar.hide();
                    Toast.makeText(getApplicationContext(), "Data saved successfully, Thank You for Choosing LNP Mini Bank!", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(SingleViewActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                });
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

        }).start();

    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                loanAmount.removeTextChangedListener(this);
                savingAmount.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    loanAmount.setText(formattedString);
                    loanAmount.setSelection(loanAmount.getText().length());
                    savingAmount.setText(formattedString);
                    savingAmount.setSelection(loanAmount.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                loanAmount.addTextChangedListener(this);
                savingAmount.addTextChangedListener(this);
            }
        };
    }
}