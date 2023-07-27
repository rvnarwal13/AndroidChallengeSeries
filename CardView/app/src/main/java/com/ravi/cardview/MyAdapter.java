package com.ravi.cardview;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<DataModel> dataModelList;
    private DeleteItemListener deleteItemListener;

    public MyAdapter(List<DataModel> dataModelList, DeleteItemListener deleteItemListener) {
        this.dataModelList = dataModelList;
        this.deleteItemListener = deleteItemListener;
    }

    public interface DeleteItemListener {
        void onDeleteItem(int position);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel dataModel = dataModelList.get(position);
//        holder.textViewHeader.setText(dataModel.getId());
        holder.textViewName.setText(dataModel.getName());
        holder.textViewGender.setText(dataModel.getGender());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteItemListener!=null) {
                    deleteItemListener.onDeleteItem(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHeader;
        TextView textViewName;
        TextView textViewGender;
        ImageButton deleteButton;

        ViewHolder(View view) {
            super(view);
            textViewHeader = view.findViewById(R.id.database_index);
            textViewName = view.findViewById(R.id.textViewName);
            textViewGender = view.findViewById(R.id.textViewGender);
            deleteButton = view.findViewById(R.id.delete_item);
        }
    }
}
