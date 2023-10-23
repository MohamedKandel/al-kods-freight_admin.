package com.mkandeel.kodsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mkandeel.kodsadmin.databinding.ActivityViewUsersBinding;
import com.mkandeel.kodsadmin.models.usersModel;
import com.mkandeel.kodsadmin.rvAdapter.ViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewUsers extends AppCompatActivity {

    private ActivityViewUsersBinding binding;
    private ViewAdapter adapter;
    private List<usersModel> list;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Tools.isNetworkAvailable(this)) {

            dialog = new LoadingDialog(this);
            list = new ArrayList<>();
            getUsersFromRTDB();
        } else {
            Tools.showDialog(this);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewUsers.this,adminOPT.class);
        startActivity(intent);
        finish();
    }

    private void getUsersFromRTDB() {
        dialog.startDialog();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Database")
                .child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        usersModel model = ds.getValue(usersModel.class);
                        list.add(model);
                    }

                    RecyclerView.LayoutManager manager =
                            new LinearLayoutManager(ViewUsers.this);
                    binding.viewRv.setLayoutManager(manager);

                    adapter = new ViewAdapter(ViewUsers.this,list);

                    dialog.closeDialog();

                    binding.viewRv.setAdapter(adapter);

                    adapter.setOnSwitchChangeListener(new ViewAdapter.SwitchChange() {
                        @Override
                        public void onSwitchChangedListener(boolean isChecked, int position) {
                            String status = (isChecked) ? "Active" : "InActive";
                            if (isChecked) {
                                Toast.makeText(getApplicationContext(),"الحساب مُفعل الآن",Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(),"الحساب معطل الآن",Toast.LENGTH_SHORT)
                                        .show();
                            }
                            reference.child(list.get(position).getUserKey()).child("status")
                                    .setValue(status);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.closeDialog();
            }
        });
    }
}