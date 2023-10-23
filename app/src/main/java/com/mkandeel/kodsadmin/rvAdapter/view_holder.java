package com.mkandeel.kodsadmin.rvAdapter;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mkandeel.kodsadmin.R;

public class view_holder extends RecyclerView.ViewHolder{

    TextView txt;
    Switch aswitch;
    View view;

    public view_holder(@NonNull View itemView) {
        super(itemView);

        txt = itemView.findViewById(R.id.txt_username);
        aswitch = itemView.findViewById(R.id.swOnOff);

        view = itemView;
    }
}
