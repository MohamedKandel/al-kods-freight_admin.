package com.mkandeel.kodsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mkandeel.kodsadmin.databinding.ActivityDisplayCertBinding;
import com.mkandeel.kodsadmin.models.Certificates;

import java.util.List;
import java.util.Objects;

public class DisplayCert extends AppCompatActivity {


    private ActivityDisplayCertBinding binding;
    private Certificates certificate;
    private String cert_num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayCertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Tools.isNetworkAvailable(this)) {
            cert_num = Objects.requireNonNull(getIntent().getExtras()).getString("txt");

            getCertDataFromRTDB();
        } else {
            Tools.showDialog(this);
        }
    }

    private void getCertDataFromRTDB() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Database")
                .child("Certificates");
        Query query = reference.orderByChild("cert_num")
                .equalTo(cert_num);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        certificate = ds.getValue(Certificates.class);
                    }
                    if (certificate!= null) {
                        binding.txtViewCertnum.setText(certificate.getCert_num());
                        binding.txtViewCertdate.setText(certificate.getCert_date());
                        binding.txtViewCompname.setText(certificate.getComp_name());
                        binding.txtViewCompnum.setText(certificate.getComp_num());
                        binding.txtViewCountry.setText(certificate.getCountry());
                        binding.txtViewTrans.setText(certificate.getTrans());
                        binding.txtViewOffers.setText(certificate.getOffers());
                        String model13 = (certificate.isModel_13()) ? "نعم" : "لا";
                        String fact = (certificate.isFact()) ? "نعم" : "لا";
                        binding.txtViewModel13.setText(model13);
                        binding.txtViewBuildfact.setText(fact);
                        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DisplayCert.this,DownloadFiles.class);
                                intent.putExtra("cert_num",cert_num);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                } else {
                    Toast.makeText(DisplayCert.this, "لا يوجد ملفات لهذه الشهادة", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DisplayCert.this,ViewCert.class);
        startActivity(intent);
        finish();
    }
}