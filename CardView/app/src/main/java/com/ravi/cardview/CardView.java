package com.ravi.cardview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardView extends Fragment {

    private EditText editTextName;
    private RadioGroup radioGroupGender;
    private RecyclerView recyclerCardView;
    private List<DataModel> dataModelList;
    private MyAdapter myAdapter;
    private AppDatabase appDatabase;

    public CardView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_view, container, false);

        editTextName = view.findViewById(R.id.name);
        radioGroupGender = view.findViewById(R.id.radio_group_gender);
        recyclerCardView = view.findViewById(R.id.card_view);

        dataModelList = new ArrayList<>();
        myAdapter = new MyAdapter(dataModelList, new MyAdapter.DeleteItemListener() {
            @Override
            public void onDeleteItem(int position) {

                DataModel dataModel = dataModelList.get(position);
                long itemId = dataModel.getId();
                Runnable myRunnable3 = new Runnable() {
                    @Override
                    public void run() {
                        appDatabase.itemDao().deleteItem(dataModel);
                        updateArrayList();
                    }
                };
                Thread thread3 = new Thread(myRunnable3);
                thread3.start();
                myAdapter.notifyDataSetChanged();
            }
        });

        recyclerCardView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerCardView.setAdapter(myAdapter);

        Button buttonAddItem = view.findViewById(R.id.button_add_item);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        ImageButton buttonDeleteAll = view.findViewById(R.id.delete_all);
        buttonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable myRunnable4 = new Runnable() {
                    @Override
                    public void run() {
                        appDatabase.itemDao().deleteAllItems();
                        updateArrayList();
                    }
                };
                Thread thread4 = new Thread(myRunnable4);
                thread4.start();
                myAdapter.notifyDataSetChanged();
            }
        });

        appDatabase = Room.databaseBuilder(requireContext(), AppDatabase.class, "item_db")
                .build();

        Runnable myRunnable1 = new Runnable() {
            @Override
            public void run() {
                updateArrayList();
            }
        };

        Thread thread1 = new Thread(myRunnable1);
        thread1.start();


        myAdapter.notifyDataSetChanged();

        return view;
    }

    private void addItem() {
        String name = editTextName.getText().toString().trim();
        if(!name.equals("")) {
            int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
            if (selectedGenderId != -1) {
                RadioButton radioButton = requireView().findViewById(selectedGenderId);
                String gender = radioButton.getText().toString();

                DataModel dataModel = new DataModel(name, gender);

                Runnable myRunnable2 = new Runnable() {
                    @Override
                    public void run() {
                        appDatabase.itemDao().insertItem(dataModel);
                        updateArrayList();
                    }
                };

                Thread thread2 = new Thread(myRunnable2);
                thread2.start();
                myAdapter.notifyDataSetChanged();

                editTextName.setText("");
                radioGroupGender.clearCheck();
            } else {
                Toast.makeText(getContext(), "Please select a gender.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please enter a name.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateArrayList() {
        dataModelList.clear();
        dataModelList.addAll(appDatabase.itemDao().getAllItemsInReverseOrder());
    }
}