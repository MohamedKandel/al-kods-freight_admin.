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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mkandeel.kodsadmin.databinding.ActivityMainBinding;
import com.mkandeel.kodsadmin.models.adminModel;
import com.mkandeel.kodsadmin.models.usersModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LoadingDialog dialog;
    private FirebaseAuth mAuth;
    private String UUID;
    private DBConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Tools.isNetworkAvailable(this)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Database");
            reference.child("available").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.getValue(String.class);
                    if (value.trim().equals("A1")) {
                        mainFun();
                    } else {
                        startActivity(new Intent(MainActivity.this, ClosedApp.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Tools.showDialog(this);
        }
    }

    private void mainFun() {
        connection = DBConnection.getInstance(this);
        String user = connection.getUserID();
        //FirebaseUser user = mAuth.getCurrentUser();
        if (!user.equals("")) {
            Intent intent = new Intent(MainActivity.this,Login.class);
            startActivity(intent);
            finish();
        } else {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestStoragePermission();
            }

            dialog = new LoadingDialog(this);
            mAuth = FirebaseAuth.getInstance();

            binding.btnReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.startDialog();

                    String mail = String.valueOf(binding.txtMail.getText());
                    String pass = String.valueOf(binding.txtPass.getText());
                    String name = String.valueOf(binding.txtName.getText());

                    if (Tools.isEmpty(mail, pass, name)) {
                        Toast.makeText(getApplicationContext(), "برجاء ملء جميع الحقول المطلوبة",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        RegisterAdmin(mail, pass, name);
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.POST_NOTIFICATIONS)) {
            new AlertDialog.Builder(this)
                    .setTitle("السماح مطلوب")
                    .setMessage("يتطلب تطبيق شهادات القدس فريت السماح بارسال اشعارات")
                    .setPositiveButton("حسنًا", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
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

    private void RegisterAdmin(String mail,String pass, String userName) {
        mAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.closeDialog();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName).build();
                            if (user!=null) {
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("username", "username setted");
                                            }
                                        });
                            }
                            UUID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            AddAdminToRTDB(UUID,mail,pass,userName);
                            connection.insertIntoUsers(UUID,mail,pass,userName);

                            Intent intent = new Intent(MainActivity.this,Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"فشل التسجيل حاول مرة اخرى في وقت لاحق",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void AddAdminToRTDB(String userKey,String mail,String pass,String name) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Database").child("admins");
        adminModel model = adminModel.getInstance(mail,pass,name,userKey);
        reference.child(userKey).setValue(model);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 306: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                    Tools.setNotificationValueToSP(true,this);
                } else {
                    Tools.showNotificationDialog(this);
                    //Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}