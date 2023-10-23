package com.mkandeel.kodsadmin.rvAdapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mkandeel.kodsadmin.R;


public class certHolder extends RecyclerView.ViewHolder {
    TextView txt_num;
    TextView txt_date;
    TextView txt_trans;
    View view;

    public certHolder(@NonNull View itemView) {
        super(itemView);
        txt_num = itemView.findViewById(R.id.txt_num);
        txt_date = itemView.findViewById(R.id.txt_date);
        txt_trans = itemView.findViewById(R.id.txtView_trans);
        view = itemView;
    }
}
