package com.mkandeel.kodsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mkandeel.kodsadmin.Fragments.*;
import com.mkandeel.kodsadmin.NotificationHandler.ApiUtils;
import com.mkandeel.kodsadmin.NotificationHandler.NotificationData;
import com.mkandeel.kodsadmin.NotificationHandler.PushNotification;
import com.mkandeel.kodsadmin.databinding.ActivityBrowseFilesBinding;
import com.mkandeel.kodsadmin.models.Cert;
import com.mkandeel.kodsadmin.models.Modal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseFiles extends AppCompatActivity implements Frag_one.FragmentInterActionListener,
        Frag_two.FragmentInterActionListener, Frag_three.FragmentInterActionListener,
        Frag_four.FragmentInterActionListener, Frag_five.FragmentInterActionListener,
        Frag_six.FragmentInterActionListener, Frag_seven.FragmentInterActionListener,
        Frag_eight.FragmentInterActionListener, Frag_nine.FragmentInterActionListener,
        Frag_ten.FragmentInterActionListener, Frag_eleven.FragmentInterActionListener,
        Frag_twelve.FragmentInterActionListener, onFileChoose {

    private ActivityBrowseFilesBinding binding;
    private final String TOPIC = "adminsTopic";
    private int fragment_index;
    private StorageReference sReference;
    private boolean upload;
    private Cert mycertificate;
    private Map<String, List<Uri>> listMap;
    private Map<Integer, String> fragmentsIndx;
    private List<Uri> listGomrok;
    private List<Uri> listFloor;
    private List<Uri> listHayaa;
    private List<Uri> listFood;
    private List<Uri> listAgri;
    private List<Uri> listFact;
    private List<Uri> listRelease;
    private List<Uri> listComp;
    private List<Uri> listBill;
    private List<Uri> listFoodHealth;
    private List<Uri> listAgriOffers;
    private List<Uri> listHealth;
    private List<List<Uri>> mylists;
    private List<String> urls1;
    private List<String> urls2;
    private List<String> urls3;
    private List<String> urls4;
    private List<String> urls5;
    private List<String> urls6;
    private List<String> urls7;
    private List<String> urls8;
    private List<String> urls9;
    private List<String> urls10;
    private List<String> urls11;
    private List<String> urls12;

    private DBConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrowseFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Tools.isNetworkAvailable(this)) {
            upload = false;
            sReference = FirebaseStorage.getInstance().getReference("Certificates");
            fragment_index = 1;
            listMap = new LinkedHashMap<>();
            listGomrok = new ArrayList<>();
            listAgri = new ArrayList<>();
            listFact = new ArrayList<>();
            listFloor = new ArrayList<>();
            listFood = new ArrayList<>();
            listHayaa = new ArrayList<>();

            listRelease = new ArrayList<>();
            listComp = new ArrayList<>();
            listAgriOffers = new ArrayList<>();
            listBill = new ArrayList<>();
            listHealth = new ArrayList<>();
            listFoodHealth = new ArrayList<>();
            fragmentsIndx = new LinkedHashMap<>();

            mylists = new ArrayList<>();

            urls1 = new ArrayList<>();
            urls2 = new ArrayList<>();
            urls3 = new ArrayList<>();
            urls4 = new ArrayList<>();
            urls5 = new ArrayList<>();
            urls6 = new ArrayList<>();
            urls7 = new ArrayList<>();
            urls8 = new ArrayList<>();
            urls9 = new ArrayList<>();
            urls10 = new ArrayList<>();
            urls11 = new ArrayList<>();
            urls12 = new ArrayList<>();

            FillMap();

            mycertificate = getIntent().getParcelableExtra("cert_data");

            showFragment();

            connection = DBConnection.getInstance(this);

            binding.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.btnNext.getText().equals("رفع الملفات")) {
                        ///////////////////upload files one by one//////////////////
                        List<String> names = new ArrayList<>();
                        for (int i = 1; i < 13; i++) {
                            names.add(fragmentsIndx.get(i));
                        }
                        String UUID = connection.getUserID();
                        uploadFilesToStorage(UUID,names, listGomrok, listFloor,
                                listHayaa, listFood, listAgri,
                                listFact, listRelease, listComp, listBill,
                                listFoodHealth, listAgriOffers, listHealth);
                    } else {
                        if (fragment_index <= 11) {
                            fragment_index++;
                            String keyForAnother = fragmentsIndx.get(fragment_index);
                            if (listMap.get(keyForAnother) != null) {
                                int count = Objects.requireNonNull(listMap.get(keyForAnother)).size();
                                binding.txtChoosen.setText("تم اختيار " + count + " ملفات");
                            } else {
                                binding.txtChoosen.setText("");
                            }
                            showFragment();
                            upload = false;
                        } else {
                            binding.btnNext.setText("رفع الملفات");
                            upload = true;
                        }
                    }
                }
            });

            binding.btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment_index > 1) {
                        fragment_index--;
                        String keyForAnother = fragmentsIndx.get(fragment_index);
                        int count = listMap.get(keyForAnother).size();
                        //Toast.makeText(BrowseFiles.this, count+"", Toast.LENGTH_SHORT).show();
                        binding.txtChoosen.setText("تم اختيار " + count + " ملفات");
                        showFragment();
                        upload = false;
                    }
                }
            });

            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (fragment_index) {
                        case 1:
                            listGomrok.clear();
                            listGomrok = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 2:
                            listFloor.clear();
                            listFloor = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 3:
                            listHayaa.clear();
                            listHayaa = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 4:
                            listFood.clear();
                            listFood = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 5:
                            listAgri.clear();
                            listAgri = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 6:
                            listFact.clear();
                            listFact = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 7:
                            listRelease.clear();
                            listRelease = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 8:
                            listComp.clear();
                            listComp = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 9:
                            listBill.clear();
                            listBill = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 10:
                            listFoodHealth.clear();
                            listFoodHealth = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 11:
                            listAgriOffers.clear();
                            listAgriOffers = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                        case 12:
                            listHealth.clear();
                            listHealth = new ArrayList<>();
                            Toast.makeText(BrowseFiles.this, "تم المسح بنجاح", Toast.LENGTH_SHORT).show();
                            binding.txtChoosen.setText("");
                            break;
                    }
                    showFragment();
                }
            });


        } else {
            Tools.showDialog(this);
        }

    }

    private void FillMap() {
        fragmentsIndx.put(1, "Gomrok");
        fragmentsIndx.put(2, "Floor");
        fragmentsIndx.put(3, "Hayaa");
        fragmentsIndx.put(4, "Food");
        fragmentsIndx.put(5, "Agri");
        fragmentsIndx.put(6, "Fact");
        fragmentsIndx.put(7, "Release");
        fragmentsIndx.put(8, "Comp");
        fragmentsIndx.put(9, "Bill");
        fragmentsIndx.put(10, "FoodHealth");
        fragmentsIndx.put(11, "AgriOffers");
        fragmentsIndx.put(12, "Health");
    }

    private void showFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = null;
        switch (fragment_index) {
            case 1:
                frag = new Frag_one();
                break;
            case 2:
                frag = new Frag_two();
                break;
            case 3:
                frag = new Frag_three();
                break;
            case 4:
                frag = new Frag_four();
                break;
            case 5:
                frag = new Frag_five();
                break;
            case 6:
                frag = new Frag_six();
                break;
            case 7:
                frag = new Frag_seven();
                break;
            case 8:
                frag = new Frag_eight();
                break;
            case 9:
                frag = new Frag_nine();
                break;
            case 10:
                frag = new Frag_ten();
                break;
            case 11:
                frag = new Frag_eleven();
                break;
            case 12:
                frag = new Frag_twelve();
                break;
        }
        if (frag != null) {
            ft.replace(R.id.frm, frag);
            ft.commit();
        } else {
            binding.btnNext.setText("رفع الملفات");
            upload = true;
        }
    }

    @Override
    public void OnFragmentInterAction(List<Uri> list) {
        listGomrok = list;
        listMap.put("Gomrok", listGomrok);
        mylists.add(listGomrok);
    }

    @Override
    public void OnFragmentTwoInterAction(List<Uri> list) {
        listFloor = list;
        listMap.put("Floor", listFloor);
        mylists.add(listFloor);
    }

    @Override
    public void OnFragmentThreeInterAction(List<Uri> list) {
        listHayaa = list;
        listMap.put("Hayaa", listHayaa);
        mylists.add(listHayaa);
    }

    @Override
    public void OnFragmentFourInterAction(List<Uri> list) {
        listFood = list;
        listMap.put("Food", listFood);
        mylists.add(listFood);
    }

    @Override
    public void OnFragmentFiveInterAction(List<Uri> list) {
        listAgri = list;
        listMap.put("Agri", listAgri);
        mylists.add(listAgri);
    }

    @Override
    public void OnFragmentSixInterAction(List<Uri> list) {
        listFact = list;
        listMap.put("Fact", listFact);
        mylists.add(listFact);
    }

    @Override
    public void OnFragmentSevenInterAction(List<Uri> list) {
        listRelease = list;
        listMap.put("Release", listRelease);
        mylists.add(listRelease);
    }

    @Override
    public void OnFragmentEightInterAction(List<Uri> list) {
        listComp = list;
        listMap.put("Comp", listComp);
        mylists.add(listComp);
    }

    @Override
    public void OnFragmentNineInterAction(List<Uri> list) {
        listBill = list;
        listMap.put("Bill", listBill);
        mylists.add(listBill);
    }

    @Override
    public void OnFragmentTenInterAction(List<Uri> list) {
        listFoodHealth = list;
        listMap.put("FoodHealth", listFoodHealth);
        mylists.add(listFoodHealth);
    }

    @Override
    public void OnFragmentElevenInterAction(List<Uri> list) {
        listAgriOffers = list;
        listMap.put("AgriOffers", listAgriOffers);
        mylists.add(listAgriOffers);
    }

    @Override
    public void OnFragmentTwelveInterAction(List<Uri> list) {
        listHealth = list;
        listMap.put("Health", listHealth);
        mylists.add(listHealth);
    }

    @Override
    public void onFileChooseListener(int count) {
        binding.txtChoosen.setText("تم اختيار " + count + " ملفات");
    }

    private int j = 1;

    private List<Uri> mergeLists(List<Uri>... lists) {
        List<Uri> newList = new ArrayList<>();
        for (List<Uri> list : lists) {
            newList.addAll(list);
        }
        return newList;
    }

    int i;

    private void uploadFilesToStorage(String userKey,List<String> listName, List<Uri>... lists) {
        i = 0;
        j = 0;
        StorageReference mReference = sReference.child(mycertificate.getCert_num()
                + "/");
        for (List<Uri> list : lists) {

            for (Uri uri : list) {
                StorageReference sr = mReference.child(listName.get(i) + "/"
                        +
                        System.currentTimeMillis() + "." +
                        Tools.getFileExtn(BrowseFiles.this, uri));
                sr.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        j++;
                        if (i == lists.length && j == list.size()) {
                            Modal model = new
                                    Modal(userKey,mycertificate.getCert_num(),
                                    mycertificate.getCert_date(),mycertificate.getComp_name(),
                                    mycertificate.getComp_num(),mycertificate.getCountry(),
                                    mycertificate.getTrans(),mycertificate.isModel_13(),
                                    mycertificate.isChk_fact(),mycertificate.getOffers());
                            uploadDataToRTDB(model);
                            SendMsg();
                        }
                    }
                });
            }
            i++;
        }
    }

    private void uploadDataToRTDB(Modal modal) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Database");
        reference.child("Certificates")
                .child(modal.getCert_num()).setValue(modal);
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
                                                    new NotificationData("تم اضافة شهادة", "تم اضافة شهادة جديدة في التطبيق"),
                                                    "/topics/adminsTopic"
                                            )).enqueue(new Callback<PushNotification>() {
                                                @Override
                                                public void onResponse(Call<PushNotification> call, Response<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> response) {
                                                    if (response.isSuccessful()) {
                                                        Log.d("Notification", "Sent Notification");

                                                        //FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
                                                    } else {
                                                        Toast.makeText(BrowseFiles.this, "فشل الارسال للمديرين", Toast.LENGTH_SHORT).show();
                                                        Log.e("Notification", "Failed Sent Notification");
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> call,
                                                                      Throwable t) {
                                                    Toast.makeText(BrowseFiles.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                                                            new NotificationData("تم اضافة شهادة", "تم اضافة شهادة جديدة في التطبيق")
                                                            , "/topics/adminsTopic"))
                                                    .enqueue(new Callback<PushNotification>() {
                                                        @Override
                                                        public void onResponse(Call<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> call, Response<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> response) {
                                                            if (response.isSuccessful()) {
                                                                Log.d("Notification", "Notification send");
                                                                //FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
                                                            } else {
                                                                Log.d("Notification", "Notification failed");
                                                                Toast.makeText(BrowseFiles.this, "فشل الارسال للمديرين", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> call, Throwable t) {
                                                            Toast.makeText(BrowseFiles.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
}