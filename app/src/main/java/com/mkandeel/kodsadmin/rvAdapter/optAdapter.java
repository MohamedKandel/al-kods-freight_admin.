package com.mkandeel.kodsadmin.rvAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mkandeel.kodsadmin.ClickListener;
import com.mkandeel.kodsadmin.R;
import com.mkandeel.kodsadmin.models.Model;

import java.util.Collections;
import java.util.List;

public class optAdapter extends RecyclerView.Adapter<cardHolder> {

    private List<Model> list = Collections.emptyList();

    private Context context;
    private ClickListener listener;


    public optAdapter(List<Model> list, Context context, ClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public cardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout

        View photoView = inflater.inflate(R.layout.card_item, parent, false);

        return new cardHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull cardHolder cardHolder, int position) {
        final int index = cardHolder.getAdapterPosition();
        cardHolder.txt.setText(list.get(position).getTxt());
        cardHolder.img.setImageResource(list.get(position).getImg());

        cardHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                listener.click(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
