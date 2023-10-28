package com.mkandeel.kodsadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mkandeel.kodsadmin.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private LoadingDialog dialog;
    private DBConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Tools.isNetworkAvailable(this)) {
            mAuth = FirebaseAuth.getInstance();
            dialog = new LoadingDialog(this);
            connection = DBConnection.getInstance(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!Tools.getNotificationValueFromSP(this)) {
                    requestStoragePermission();
                } else {
                    MainFn();
                }
            } else {
                MainFn();
            }
        } else {
            Tools.showDialog(this);
        }

    }

    private void MainFn() {
        binding.txtMail.setText(connection.getMail());
        binding.txtPass.requestFocus();

        binding.btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.startDialog();

                String mail = String.valueOf(binding.txtMail.getText());
                String pass = String.valueOf(binding.txtPass.getText());
                if (Tools.isEmpty(mail, pass)) {
                    Toast.makeText(getApplicationContext(), "برجاء ملء جميع الحقول المطلوبة",
                            Toast.LENGTH_SHORT).show();
                }
                mAuth.signInWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Database")
                                            .child("admins");
                                    Query query = reference.orderByChild("uuid")
                                            .equalTo(mAuth.getUid());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                dialog.closeDialog();
                                                Intent intent = new Intent(Login.this, adminOPT.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                dialog.closeDialog();
                                                Toast.makeText(getApplicationContext(), "خطأ في كلمة المرور او البريد",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            dialog.closeDialog();
                                        }
                                    });

                                } else {
                                    dialog.closeDialog();
                                    Toast.makeText(Login.this, "خطأ في اسم المستخدم او كلمة المرور", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.POST_NOTIFICATIONS)) {
            new AlertDialog.Builder(this)
                    .setTitle("السماح مطلوب")
                    .setMessage("يتطلب تطبيق شهادات القدس فريت السماح بارسال اشعارات")
                    .setPositiveButton("حسنًا", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Login.this,
                                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                    306);
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
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    306);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 306: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                    Tools.setNotificationValueToSP(true,this);
                    MainFn();
                } else {
                    Tools.showNotificationDialog(this);
                    //Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}