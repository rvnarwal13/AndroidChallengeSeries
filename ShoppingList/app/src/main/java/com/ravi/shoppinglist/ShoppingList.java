package com.ravi.shoppinglist;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ShoppingList extends Fragment {
    private ShoppingListViewModel mViewModel;
    private EditText addItemEditText;
    private Button addItemButton;
    private RecyclerView recyclerView, checkedRecyclerView;
    private MyAdapter myAdapter;
    private MyCheckedAdapter myCheckedAdapter;

    public static ShoppingList newInstance() {
        return new ShoppingList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(requireContext())).get(ShoppingListViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        addItemEditText = view.findViewById(R.id.add_item_text);
        addItemButton = view.findViewById(R.id.add_item_button);
        recyclerView = view.findViewById(R.id.list_items_to_buy);
        checkedRecyclerView = view.findViewById(R.id.list_items_bought);
        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.button_click_animation);

        myAdapter = new MyAdapter(requireContext(), mViewModel.getDataList(), new MyAdapter.OnCheckedChangeListener(){
            @Override
            public void onCheckedChange(DataItem item, boolean isChecked) {
                mViewModel.updateData(item, isChecked);
            }
        });

        myCheckedAdapter = new MyCheckedAdapter(requireContext(), mViewModel.getCheckedDataList());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        checkedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerView.setAdapter(myAdapter);
        checkedRecyclerView.setAdapter(myCheckedAdapter);

        mViewModel.getDataListLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<DataItem>>() {
            @Override
            public void onChanged(ArrayList<DataItem> dataItems) {
                myAdapter.setDataList(dataItems);
            }
        });

        mViewModel.getCheckedListLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<DataItem>>() {
            @Override
            public void onChanged(ArrayList<DataItem> dataItems) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myCheckedAdapter.setCheckedDataItemList(dataItems);
            }
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                String data = addItemEditText.getText().toString().trim();
                if(!data.isEmpty()) {
                    DataItem newData = new DataItem();
                    newData.setData(data);
                    newData.setChecked(false);
                    mViewModel.insertData(newData);
                    myAdapter.notifyItemInserted(myAdapter.getItemCount()-1);
                    addItemEditText.setText("");
                }
            }
        });

        return view;
    }
}