package com.doozycod.fleetoptics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.doozycod.fleetoptics.Model.ResultModel;
import com.doozycod.fleetoptics.R;
import com.doozycod.fleetoptics.Service.ApiService;
import com.doozycod.fleetoptics.Service.ApiUtils;
import com.doozycod.fleetoptics.Utils.CustomProgressBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignatureActivity extends AppCompatActivity {
    RadioButton yesRadioBtn, noRadioBtn;
    RadioGroup radioGroup;
    Button backSignature;
    CustomProgressBar customProgressBar;
    ApiService apiService;

    //    typecasting method
    private void initUI() {
        yesRadioBtn = findViewById(R.id.yes_sign);
        noRadioBtn = findViewById(R.id.no_sign);
        radioGroup = findViewById(R.id.signRadioGroup);
        backSignature = findViewById(R.id.backSignature);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        set Activity full Screen / hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signature);
//        hide action bar
        getSupportActionBar().hide();

//        progress bar
        customProgressBar = new CustomProgressBar(this);

//        api service
        apiService = ApiUtils.getAPIService();
//        typecasting
        initUI();
//        set Click Listeners
        onClickListener();
    }

    private void onClickListener() {
//          set radio button on check change listener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (yesRadioBtn.isChecked()) {
                    startActivity(new Intent(SignatureActivity.this, SpecificRecipientActivity.class));
                }
                if (noRadioBtn.isChecked()) {
                    getPackageDelivered("Package Delivery", "2", "Yes", "No");
                }
            }
        });
//        finish Activity on back press
        backSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //    Package Delivery Api
    void getPackageDelivered(String CheckinType, String deliverToWhom, String isSignReq, String isSpecificPerson) {
        customProgressBar.showProgress();
        apiService.packageDelivery(CheckinType, deliverToWhom, isSignReq, isSpecificPerson).enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                customProgressBar.hideProgress();

                if (response.isSuccessful()) {
                    if (response.body().getType().equals("success")) {
                        Toast.makeText(SignatureActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignatureActivity.this, NotifyActivity.class);
                        intent.putExtra("signature", "signature");
                        intent.putExtra("empPhoneNo",response.body().getEmployee_contact());
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignatureActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Toast.makeText(SignatureActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                customProgressBar.hideProgress();

            }
        });
    }
}
