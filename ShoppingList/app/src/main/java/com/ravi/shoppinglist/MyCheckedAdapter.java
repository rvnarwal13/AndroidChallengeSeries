package com.ravi.shoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyCheckedAdapter extends RecyclerView.Adapter<MyCheckedAdapter.ViewHolder> {
    private ArrayList<DataItem> checkedDataItemList = new ArrayList<>();
    private Context context;

    public MyCheckedAdapter(Context context, ArrayList<DataItem> checkedDataItemList) {
        this.context = context;
        this.checkedDataItemList = checkedDataItemList;
    }

    public void setCheckedDataItemList(ArrayList<DataItem> checkedDataItemList) {
        this.checkedDataItemList = checkedDataItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyCheckedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataItem data = checkedDataItemList.get(position);
        data.setSerialNumber(holder.getAdapterPosition());
        holder.textViewData.setText(data.getData());
    }

    @Override
    public int getItemCount() {
        return checkedDataItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewData = itemView.findViewById(R.id.textViewDataChecked);
        }
    }
}
