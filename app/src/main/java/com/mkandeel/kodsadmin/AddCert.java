package com.mkandeel.kodsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mkandeel.kodsadmin.NotificationHandler.ApiUtils;
import com.mkandeel.kodsadmin.NotificationHandler.NotificationData;
import com.mkandeel.kodsadmin.NotificationHandler.PushNotification;
import com.mkandeel.kodsadmin.databinding.ActivityAddCertBinding;
import com.mkandeel.kodsadmin.models.Cert;

import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCert extends AppCompatActivity {

    private ActivityAddCertBinding binding;
    private final String TOPIC = "adminsTopic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Tools.isNetworkAvailable(this)) {
            binding.layoutDate.setStartIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayDatePicker();
                }
            });

            binding.txtCertDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.txtCertDate.getWindowToken(), 0);
                    displayDatePicker();
                }
            });

            binding.btnUploadWithoutFiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cert_num = Objects.requireNonNull(binding.txtCertNum.getText()).toString();
                    String cert_date = Objects.requireNonNull(binding.txtCertDate.getText()).toString();
                    String comp_name = Objects.requireNonNull(binding.txtCertName.getText()).toString();
                    String comp_num = Objects.requireNonNull(binding.txtCompNum.getText()).toString();
                    String country = Objects.requireNonNull(binding.txtCountry.getText()).toString();
                    String trans = Objects.requireNonNull(binding.txtTrans.getText()).toString();
                    String offers = Objects.requireNonNull(binding.txtOffers.getText()).toString();
                    if (Tools.isEmpty(cert_num, cert_date, comp_name, comp_num, country,
                            trans, offers)) {
                        Toast.makeText(getApplicationContext(), "يجب عليك ملئ جميع الحفول", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        boolean model_13 = binding.chk13.isChecked();
                        boolean chk_fact = binding.chkFact.isChecked();
                        Cert cert = new Cert(cert_num, cert_date,
                                comp_name, comp_num, country, trans, offers, model_13, chk_fact);

                        uploadCertificate(cert);
                    }
                }
            });

            binding.btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cert_num = Objects.requireNonNull(binding.txtCertNum.getText()).toString();
                    String cert_date = Objects.requireNonNull(binding.txtCertDate.getText()).toString();
                    String cert_name = Objects.requireNonNull(binding.txtCertName.getText()).toString();
                    String comp_num = Objects.requireNonNull(binding.txtCompNum.getText()).toString();
                    String country = Objects.requireNonNull(binding.txtCountry.getText()).toString();
                    String trans = Objects.requireNonNull(binding.txtTrans.getText()).toString();
                    String offers = Objects.requireNonNull(binding.txtOffers.getText()).toString();
                    if (Tools.isEmpty(cert_num, cert_date, cert_name, comp_num, country,
                            trans, offers)) {
                        Toast.makeText(getApplicationContext(), "يجب عليك ملئ جميع الحفول", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        boolean model_13 = binding.chk13.isChecked();
                        boolean chk_fact = binding.chkFact.isChecked();
                        Cert cert = new Cert(cert_num, cert_date,
                                cert_name, comp_num, country, trans, offers, model_13, chk_fact);

                        Intent intent = new Intent(AddCert.this, BrowseFiles.class);
                        //pass values from this activity to next activity
                        /////////////////////////////////////////////////
                        intent.putExtra("cert_data", cert);
                        startActivity(intent);
                        finish();
                    }
                }
            });

            binding.txtCertNum.requestFocus();


        } else {
            Tools.showDialog(this);
        }

    }

    private void displayDatePicker() {
        final Calendar c = Calendar.getInstance();

        // on below line we are getting
        // our day, month and year.
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                AddCert.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        binding.txtCertDate.
                                setText(dayOfMonth + " - " + (monthOfYear + 1) + " - " + year);

                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void uploadCertificate(Cert cert) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Database").child("Certificates");
        reference.child(cert.getCert_num()).setValue(cert)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SendMsg();
                        }
                    }
                });
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
                                                public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                                                    if (response.isSuccessful()) {
                                                        Log.d("Notification", "Sent Notification");

                                                        //FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
                                                    } else {
                                                        Toast.makeText(AddCert.this, "فشل الارسال للمديرين", Toast.LENGTH_SHORT).show();
                                                        Log.e("Notification", "Failed Sent Notification");
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> call, Throwable t) {
                                                    Toast.makeText(AddCert.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                                                        public void onResponse(Call<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> call,
                                                                               Response<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> response) {
                                                            if (response.isSuccessful()) {
                                                                Log.d("Notification", "Notification send");
                                                            } else {
                                                                Log.d("Notification", "Notification failed");
                                                                Toast.makeText(AddCert.this, "فشل الارسال للمديرين", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<com.mkandeel.kodsadmin.NotificationHandler.PushNotification> call, Throwable t) {
                                                            Toast.makeText(AddCert.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(AddCert.this,adminOPT.class);
        startActivity(intent);
        finish();
    }
}