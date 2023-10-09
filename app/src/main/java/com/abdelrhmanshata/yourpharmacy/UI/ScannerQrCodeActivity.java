package com.abdelrhmanshata.yourpharmacy.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.annotations.NotNull;
import com.google.zxing.Result;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.UI.Docter.Add_Medicine_Activity;
import com.abdelrhmanshata.yourpharmacy.UI.Docter.ShowMedicineActivity;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerQrCodeActivity extends AppCompatActivity
        implements ZXingScannerView.ResultHandler , SwipeRefreshLayout.OnRefreshListener {

    // QRCode
    private ZXingScannerView mScannerView;
    SwipeRefreshLayout refreshLayout;
    ViewGroup contentFrame;
    ProgressBar loading;

    boolean ADD_Medicine=false;
    boolean Edite_Medicine=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_qr_code);

        // Permissions to open Camera
        ActivityCompat.requestPermissions(ScannerQrCodeActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        refreshLayout = findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(this);

        loading = findViewById(R.id.loading);

        // link View to AlertDialog in Activity
        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);

        ADD_Medicine = getIntent().getBooleanExtra("ADD_Medicine",false);
        Edite_Medicine = getIntent().getBooleanExtra("Edite_Medicine",false);

    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        mScannerView.removeAllViews();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toasty.normal(ScannerQrCodeActivity.this, "" + getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void handleResult(Result result) {
        String Code = result.getText().trim();
        if(ADD_Medicine)
        {
            Add_Medicine_Activity.QRCode=Code;
            onBackPressed();
        }
        if(Edite_Medicine)
        {
            ShowMedicineActivity.QRCode=Code;
            onBackPressed();
        }
    }
    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            refreshLayout.setRefreshing(false);
            onResume();
        }, 500);
    }

}