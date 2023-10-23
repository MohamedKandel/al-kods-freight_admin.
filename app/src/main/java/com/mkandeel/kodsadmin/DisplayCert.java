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

    private static final int STORAGE_PERMISSION_CODE = 1;
    private ActivityDisplayCertBinding binding;
    private Certificates certificate;
    private String cert_num;
    private Intent myIntent;

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
                                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                                        PackageManager.PERMISSION_GRANTED) {
                                    myIntent = registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                    DownloadFiles(certificate.getList(), cert_num);
                                } else {
                                    requestStoragePermission();
                                }
                            }
                        });

                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DownloadFiles(List<String> list,String txt) {
        for (String url:list) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("جارِ تحميل ملفات الشهادة");
            request.setTitle("الشهادة رقم "+txt);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            }
            request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOCUMENTS+"/"+txt,
                    System.currentTimeMillis()+"."+getExtn(url));
            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            // your code
            Toast.makeText(ctxt, "تم تحميل الملف بنجاح", Toast.LENGTH_SHORT)
                    .show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myIntent != null) {
            unregisterReceiver(onComplete);
        }
    }

    private String getExtn(String url) {
        String extn = "";
        String[] arr = url.split("\\?");
        extn = arr[0].substring(arr[0].length()-3);
        return extn;
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("السماح مطلوب")
                    .setMessage("اذن الوصول لذاكرة التخزين مطلوب لتحميل الملفات الخاصة بالشهادات إلى هاتفك المحمول")
                    .setPositiveButton("حسنًا", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DisplayCert.this,
                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("تجاهل", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DisplayCert.this,ViewCert.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),"تم اعطاء السماح ... من فضلك اضغط على زر التحميل مرة اخرى لتحميل الملفات",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"لن تتمكن من تحميل الملفات على هاتفك إلا عن طريق السماح للتطبيق بالوصول للذاكرة",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}