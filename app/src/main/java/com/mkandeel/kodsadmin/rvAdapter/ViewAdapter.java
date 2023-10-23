package com.mkandeel.kodsadmin.rvAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mkandeel.kodsadmin.R;
import com.mkandeel.kodsadmin.models.usersModel;

import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<view_holder> {
    private List<usersModel> list;
    private Context context;
    private SwitchChange listener;

    public ViewAdapter(Context context,List<usersModel> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout

        View view = inflater.inflate(R.layout.users_view, parent, false);

        return new view_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull view_holder holder, int position) {

        int index = position;

        holder.txt.setText(list.get(position).getUsername());
        boolean isActive = list.get(position).getStatus().equals("Active");
        holder.aswitch.setChecked(isActive);
        holder.aswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onSwitchChangedListener(isChecked,index);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnSwitchChangeListener(SwitchChange listener) {
        this.listener = listener;
    }

    public interface SwitchChange{
        void onSwitchChangedListener(boolean isChecked,int position);
    }
}
