package com.mkandeel.kodsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

                                            }
                                        });

                                    }
                                }
                            });
                }
            });
        } else {
            Tools.showDialog(this);
        }

    }
}