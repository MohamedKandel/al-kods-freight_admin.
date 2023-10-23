package com.mkandeel.kodsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new LoadingDialog(this);
        mAuth = FirebaseAuth.getInstance();
        connection = DBConnection.getInstance(this);

        String user = connection.getUserID();
        //FirebaseUser user = mAuth.getCurrentUser();
        if (!user.equals("")) {
            Intent intent = new Intent(MainActivity.this,Login.class);
            startActivity(intent);
            finish();
        } else {

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
}