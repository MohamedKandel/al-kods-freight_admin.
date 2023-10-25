package com.mkandeel.kodsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mkandeel.kodsadmin.databinding.ActivityViewCertBinding;
import com.mkandeel.kodsadmin.models.customModel;
import com.mkandeel.kodsadmin.models.users;
import com.mkandeel.kodsadmin.rvAdapter.showAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewCert extends AppCompatActivity {

    private ActivityViewCertBinding binding;
    private showAdapter adapter;
    private DatabaseReference reference;
    private List<customModel> list;
    private LoadingDialog dialog;
    private onOptionSelected listener;
    private String searchWith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewCertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Tools.isNetworkAvailable(this)) {


            dialog = new LoadingDialog(this);

            getCertFromRTDB();


            binding.imgSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(ViewCert.this,
                            binding.imgSearch);
                    menu.getMenuInflater().inflate(R.menu.menu,menu.getMenu());
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.searchBy) {
                                //Toast.makeText(ViewCert.this, "Search", Toast.LENGTH_SHORT).show();
                                Tools.showSearchDialog(ViewCert.this,listener);
                            }
                            return true;
                        }
                    });
                    menu.show();
                }
            });

            listener = new onOptionSelected() {
                @Override
                public void onOptionSelectedListener(int method, String searchKeyWord) {
                    switch (method) {
                        case 0:
                            searchWith = "cert_num";
                            break;
                        case 1:
                            searchWith = "country";
                            break;
                        case 2:
                            searchWith = "trans";
                            break;
                        case 3:
                            searchWith = "comp_num";
                            break;
                    }
                    searchIntoRTDB(searchKeyWord,searchWith);
                }
            };

        } else {
            Tools.showDialog(this);
        }
    }

    private void searchIntoRTDB(String keyWord,String method) {
        dialog.startDialog();
        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Database")
                .child("Certificates");
        Query query = reference.orderByChild(method).equalTo(keyWord);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        customModel model = ds.getValue(customModel.class);
                        list.add(model);
                    }
                    setAdapter();
                } else {
                    dialog.closeDialog();
                    Toast.makeText(ViewCert.this, "لا يوجد بيانات", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewCert.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCertFromRTDB() {
        dialog.startDialog();

        list = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Database")
                .child("Certificates");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        customModel model = ds.getValue(customModel.class);
                        list.add(model);
                    }
                    setAdapter();
                } else {
                    dialog.closeDialog();
                    Toast.makeText(getApplicationContext(),"لا يوجد اية شهادات حتى الآن",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.closeDialog();
                Toast.makeText(getApplicationContext(),"حدث خطأ ما",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewCert.this);
        binding.parentRv.setLayoutManager(layoutManager);

        dialog.closeDialog();

        adapter = new showAdapter(list,ViewCert.this);
        binding.parentRv.setAdapter(adapter);

        adapter.setOnClickListener(new showAdapter.ItemClicked() {
            @Override
            public void onItemClickListener(String txt, int position) {
                Intent intent = new Intent(ViewCert.this,DisplayCert.class);
                //Toast.makeText(ViewCert.this, txt, Toast.LENGTH_SHORT).show();
                intent.putExtra("txt",txt);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,adminOPT.class);
        startActivity(intent);
        finish();
    }
}