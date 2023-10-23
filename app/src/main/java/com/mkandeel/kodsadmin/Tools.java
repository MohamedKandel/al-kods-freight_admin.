package com.mkandeel.kodsadmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

public class Tools {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor edit;

    public static boolean isEmpty(String ... strs) {
        boolean flag = false;
        for (String str:strs) {
            if (str.trim().isEmpty()) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static boolean BooleanParseBoolean(int value) {
        return value == 1;
    }

    public static int IntegerParseInt(boolean value) {
        return (value) ? 1 : 0;
    }

    private static void initSP(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sp.edit();
    }

    public static void setValueToSP(boolean value,Context context) {
        initSP(context);
        edit.putBoolean("subscribed",value);
        edit.apply();
    }

    public static boolean getValueFromSP(Context context) {
        initSP(context);
        return sp.getBoolean("subscribed",false);
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void showSearchDialog(Activity activity,onOptionSelected listener) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.search_dialog,null));
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        final int[] selectedItem = {-1};

        RadioButton cert_num = dialog.findViewById(R.id.cert_num);
        RadioButton country = dialog.findViewById(R.id.country);
        RadioButton trans = dialog.findViewById(R.id.trans);
        RadioButton comp_num = dialog.findViewById(R.id.comp_num);

        EditText txt_search = dialog.findViewById(R.id.txt_searched);

        Button btn_search = dialog.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cert_num.isChecked()) {
                    selectedItem[0] = 0;
                } else if (country.isChecked()) {
                    selectedItem[0] = 1;
                } else if (trans.isChecked()) {
                    selectedItem[0] = 2;
                } else if (comp_num.isChecked()) {
                    selectedItem[0] = 3;
                }

                if (selectedItem[0] == -1) {
                    Toast.makeText(activity, "يجب عليك اختيار طريقة البحث اولًا", Toast.LENGTH_SHORT).show();
                } else {
                    listener.onOptionSelectedListener(selectedItem[0],txt_search.getText().toString().trim());
                    dialog.dismiss();
                    dialog.cancel();
                }
            }
        });
    }

    public static void showDialog(Activity activity) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.internet_dialog,null));
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button btn = dialog.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialog.dismiss();
                activity.finish();
            }
        });
    }
}
