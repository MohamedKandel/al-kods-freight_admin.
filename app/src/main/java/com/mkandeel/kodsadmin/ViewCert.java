package com.mkandeel.kodsadmin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mkandeel.kodsadmin.NotificationHandler.ApiUtils;
import com.mkandeel.kodsadmin.NotificationHandler.NotificationData;
import com.mkandeel.kodsadmin.NotificationHandler.PushNotification;
import com.mkandeel.kodsadmin.databinding.ActivityViewCertBinding;
import com.mkandeel.kodsadmin.models.customModel;
import com.mkandeel.kodsadmin.models.users;
import com.mkandeel.kodsadmin.rvAdapter.showAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCert extends AppCompatActivity {

    private ActivityViewCertBinding binding;
    private showAdapter adapter;
    private List<Uri> mlist;
    private boolean addModelOrFact;
    private String mystr;
    private boolean isModel13;
    private String TOPIC = "adminsTopic";
    private DatabaseReference reference;
    private List<customModel> list;
    private LoadingDialog dialog;
    private onOptionSelected listener;
    private String searchWith;
    private ActivityResultLauncher<Intent> arl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewCertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Tools.isNetworkAvailable(this)) {


            dialog = new LoadingDialog(this);

            getCertFromRTDB();
            //////////////////////////////////////////////////////////////////////
            mlist = new ArrayList<>();
            arl = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                if (result.getData() == null) {
                                    return;
                                }

                                if (result.getData().getClipData() != null) {
                                    for (int i = 0; i < result.getData().getClipData().getItemCount(); i++) {
                                        Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                                        mlist.add(uri);
                                    }
                                } else {
                                    Uri uri = result.getData().getData();
                                    mlist.add(uri);
                                }
                                Log.d("my list", mlist + "");
                                if (addModelOrFact) {
                                    AddDataToCert(mystr, isModel13, true);
                                } else {
                                    uploadExtrasToStorage(mystr,mlist);
                                }
                            }
                        }
                    }
            );
            //////////////////////////////////////////////////////////////////////

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
        ////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////
        adapter.setOnClickListener(new showAdapter.ItemClicked() {
            @Override
            public void onItemClickListener(String txt, int position) {
                Intent intent = new Intent(ViewCert.this,DisplayCert.class);
                //Toast.makeText(ViewCert.this, txt, Toast.LENGTH_SHORT).show();
                intent.putExtra("txt",txt);
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClickListener(String txt, int itemId, int position) {
                mystr = txt;
                if (itemId == R.id.add_13) {
                    Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    mIntent.setType("*/*");
                    mIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    arl.launch(mIntent);
                    addModelOrFact = true;
                    isModel13 = true;
                } else if (itemId == R.id.add_fact) {
                    Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    mIntent.setType("*/*");
                    mIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    arl.launch(mIntent);
                    addModelOrFact = true;
                    isModel13 = false;
                } else if (itemId == R.id.edit_files) {
                        /*Intent mIntent = new Intent(Show.this,BrowseFiles.class);
                        mIntent.putExtra("cert_num",str);
                        startActivity(mIntent);*/
                    //Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    //mIntent.setType("*/*");
                    //mIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    addModelOrFact = false;
                    //arl.launch(mIntent);
                } else {
                    //deleteCertByNum(str,index);
                }
            }
        });
    }

    private void AddDataToCert(String cert_num, boolean isModel13, boolean value) {
        String childName = isModel13 ? "model_13" : "fact";
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Database").child("Certificates")
                .child(cert_num);
        ref.child(childName).setValue(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //uploadExtrasToStorage(cert_num, mlist);
                        uploadExtrasToStorage(cert_num, mlist);
                    }
                });
    }

    private void uploadExtrasToStorage(String cert_num, List<Uri> list) {
        StorageReference mReference = FirebaseStorage.getInstance()
                .getReference("Certificates")
                .child(cert_num);
        final int[] k = {0};
        for (Uri uri : list) {
            StorageReference sr = mReference.child("Other")
                    .child(System.currentTimeMillis() + "."
                            + Tools.getFileExtn(ViewCert.this, uri));
            sr.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        k[0]++;
                        if (k[0] == list.size()) {
                            SendMsg();
                        }
                    }
                }
            });
        }
    }

    private void SendMsg() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance()
                    .signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseMessaging.getInstance()
                                    .subscribeToTopic(TOPIC).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TOPIC", "Subscribed");
                                            ApiUtils.getClient().sendNotification(new PushNotification(
                                                    new NotificationData("تم اضافة ملفات جديدة لشهادة", "تم اضافة ملفات جديدة لشهادة موجودة في التطبيق"),
                                                    "/topics/adminsTopic"
                                            )).enqueue(new Callback<PushNotification>() {
                                                @Override
                                                public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                                                    if (response.isSuccessful()) {
                                                        Log.d("Notification", "Sent Notification");
                                                    } else {
                                                        Toast.makeText(ViewCert.this, "فشل الارسال للمديرين", Toast.LENGTH_SHORT).show();
                                                        Log.e("Notification", "Failed Sent Notification");
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> call, Throwable t) {
                                                    Toast.makeText(ViewCert.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    Log.e("Notification", "Error sending");
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TOPIC", "Failed");
                                            Log.d("TOPIC", e.getMessage().toString());
                                        }
                                    });
                        }
                    });


        } else {
            FirebaseMessaging.getInstance()
                    .getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TOPIC", "mohamed Subscribed");
                                            ApiUtils.getClient().sendNotification(new PushNotification(
                                                            new NotificationData("تم اضافة ملفات جديدة لشهادة", "تم اضافة ملفات جديدة لشهادة موجودة في التطبيق")
                                                            , "/topics/adminsTopic"))
                                                    .enqueue(new Callback<PushNotification>() {
                                                        @Override
                                                        public void onResponse(Call<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> call,
                                                                               Response<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> response) {
                                                            if (response.isSuccessful()) {
                                                                Log.d("Notification", "Notification send");
                                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
                                                            } else {
                                                                Log.d("Notification", "Notification failed");
                                                                Toast.makeText(ViewCert.this, "فشل الارسال للمديرين", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> call, Throwable t) {
                                                            Toast.makeText(ViewCert.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TOPIC", "failed");
                                            Log.d("TOPIC", e.getMessage().toString());
                                        }
                                    });

                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,adminOPT.class);
        startActivity(intent);
        finish();
    }
}