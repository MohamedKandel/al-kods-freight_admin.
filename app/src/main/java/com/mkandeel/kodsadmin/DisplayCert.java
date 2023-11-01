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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    myIntent = registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                    DownloadFiles(certificate.getList(), cert_num);
                                } else {
                                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                            PackageManager.PERMISSION_GRANTED) {
                                        myIntent = registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                        DownloadFiles(certificate.getList(), cert_num);
                                    } else {
                                        requestStoragePermission();
                                    }
                                }
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

    private void DownloadFiles(List<String> list,String txt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("مسار التحميل");
        String path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        builder.setMessage("سيتم تحميل الملفات في المسار التالي\n"+ path);
        builder.setPositiveButton("حسنًا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        builder.create().show();
        for (String url:list) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("جارِ تحميل ملفات الشهادة");
            request.setTitle("الشهادة رقم "+txt);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
                    if (Tools.getNotificationValueFromSP(this)) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                    }
                } else {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                }
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
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                Toast.makeText(ctxt, "تم تحميل الملفات بنجاح", Toast.LENGTH_SHORT)
                        .show();
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                Intent dm = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dm);
                //Toast.makeText(ctxt, "clicked", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(ctxt, intent.getAction(), Toast.LENGTH_SHORT).show();
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