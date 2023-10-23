package com.mkandeel.kodsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mkandeel.kodsadmin.databinding.ActivityAdminOptBinding;
import com.mkandeel.kodsadmin.models.Model;
import com.mkandeel.kodsadmin.rvAdapter.optAdapter;

import java.util.ArrayList;
import java.util.List;

public class adminOPT extends AppCompatActivity {

    private ActivityAdminOptBinding binding;
    private optAdapter adapter;
    private ClickListener listener;
    private List<Model> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Tools.isNetworkAvailable(this)) {
            binding.textViewAccess.setPaintFlags(
                    binding.textViewAccess.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            binding.textViewAccess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog(adminOPT.this);
                }
            });
            /////////////////////subscribe to topic///////////////////////
            boolean isSubscribed = Tools.getValueFromSP(this);
            if (!isSubscribed) {
                // subscribe to topic
                FirebaseMessaging.getInstance().subscribeToTopic("/topics/adminsTopic")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Tools.setValueToSP(true, adminOPT.this);
                                } else {
                                    Toast.makeText(adminOPT.this, "حدث خطأ ما... تواصل مع الدعم لحل المشكلة", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            //////////////////////////////////////////////////////////////
            list = new ArrayList<>();
            list.add(new Model("مستخدمين التطبيق", R.drawable.people));
            list.add(new Model("جميع الشهادات", R.drawable.view_all));

            listener = new ClickListener() {
                @Override
                public void click(int index) {
                    switch (index) {
                        case 0:
                            Intent intent = new Intent(adminOPT.this, ViewUsers.class);
                            startActivity(intent);
                            finish();
                            break;
                        case 1:
                            intent = new Intent(adminOPT.this, ViewCert.class);
                            startActivity(intent);
                            finish();
                            break;
                    }
                }
            };

            adapter = new optAdapter(list, this, listener);

            binding.rv.setAdapter(adapter);

            binding.rv.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Tools.showDialog(this);
        }
    }

    private void customDialog(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        EditText txt = dialog.findViewById(R.id.txtAccessCode);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Database").child("accessCode");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String access = snapshot.getValue(String.class);
                    txt.setText(access);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tools.isEmpty(txt.getText().toString())) {
                    Toast.makeText(getApplicationContext(),"لا يمكن حذف رمز الوصول",
                            Toast.LENGTH_SHORT).show();
                } else {
                    ref.setValue(txt.getText().toString());
                    dialog.dismiss();
                    dialog.cancel();
                }
            }
        });

        dialog.findViewById(R.id.img_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt.setEnabled(true);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}