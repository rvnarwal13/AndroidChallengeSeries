package com.ravi.shoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<DataItem> dataItemList = new ArrayList<>();
    private Context context;
    private OnCheckedChangeListener onCheckedChangeListener;

    public MyAdapter(Context context, ArrayList<DataItem> dataItemList, OnCheckedChangeListener onCheckedChangeListener) {
        this.context = context;
        this.dataItemList = dataItemList;
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setDataList(ArrayList<DataItem> dataItemList) {
        this.dataItemList = dataItemList;
        notifyDataSetChanged();
    }

    public interface OnCheckedChangeListener {
        void onCheckedChange(DataItem item, boolean isChecked);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataItem data = dataItemList.get(position);
        data.setSerialNumber(holder.getAdapterPosition());
        holder.textView.setText(data.getData());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(data.getChecked());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onCheckedChangeListener.onCheckedChange(data, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewData);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
