package com.ravi.tambola;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private List<AppCompatButton> buttonList1 = new ArrayList<>();
    private List<AppCompatButton> buttonList2 = new ArrayList<>();
    private List<AppCompatButton> buttonList3 = new ArrayList<>();
    private TableLayout tableLayout;
    private Set<Integer> numbersUsed;
    private Set<Integer> randomNumbersGet;
    private Set<Integer> randomButtonsInAList;
    private AppCompatButton restart, getRandomNumberBtn;
    private TextView randomNumberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = decorView.getWindowInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.activity_main);

        tableLayout = findViewById(R.id.tambola_card);
        restart = findViewById(R.id.restart_game);
        getRandomNumberBtn = findViewById(R.id.get_random_number);
        randomNumberView = findViewById(R.id.random_number);
        setList();
        fillLists();
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillLists();
                randomNumberView.setText("00");
            }
        });
        getRandomNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numberToShow = getRandomNumber(90);
                while(true) {
                    if(numbersUsed.contains(numberToShow)) {
                        numberToShow = getRandomNumber(90);
                    } else {
                        break;
                    }
                }
                randomNumberView.setText(String.valueOf(numberToShow));
            }
        });
    }

    private void fillLists() {
        setDefaultColorOfListButtons();
        numbersUsed = new HashSet<>();
        randomNumbersGet = new HashSet<>();
        fillList1();
        fillList2();
        fillList3();
    }

    private void fillList1() {
        randomButtonsInAList = new HashSet<>();
        fillRandomButtonsInAList(buttonList1);
    }

    private void fillList2() {
        randomButtonsInAList = new HashSet<>();
        fillRandomButtonsInAList(buttonList2);
    }

    private void fillList3() {
        randomButtonsInAList = new HashSet<>();
        fillRandomButtonsInAList(buttonList3);
    }

    private void fillRandomButtonsInAList(List<AppCompatButton> list) {
        int buttonsToGet = getRandomNumber(9);
        if (buttonsToGet > 7) {
            buttonsToGet = 7;
        } else if (buttonsToGet < 3) {
            buttonsToGet = 3;
        }
        for (int i = 0; i < buttonsToGet; i++) {
            int randomButton = getRandomNumber(9);
            while (true) {
                if (randomButtonsInAList.contains(randomButton)) {
                    randomButton = getRandomNumber(9);
                } else {
                    break;
                }
            }
            randomButtonsInAList.add(randomButton);
            AppCompatButton button = list.get(randomButton - 1);
            button.setBackgroundColor(getColor(R.color.used_card_item));
            button.setClickable(true);
            int randomNumber = getRandomNumber(90);
            while (true) {
                if (randomNumbersGet.contains(randomNumber)) {
                    randomNumber = getRandomNumber(90);
                } else {
                    break;
                }
            }
            randomNumbersGet.add(randomNumber);
            button.setText(String.valueOf(randomNumber));
        }
    }

    private int getRandomNumber(int maxNum) {
        Random random = new Random();
        return random.nextInt(maxNum) + 1;
    }

    private void setDefaultColorOfListButtons() {
        for (AppCompatButton button : buttonList1) {
            button.setBackgroundColor(getColor(R.color.default_card_item));
            button.setClickable(false);
            button.setText("");
        }
        for (AppCompatButton button : buttonList2) {
            button.setBackgroundColor(getColor(R.color.default_card_item));
            button.setClickable(false);
            button.setText("");
        }
        for (AppCompatButton button : buttonList3) {
            button.setBackgroundColor(getColor(R.color.default_card_item));
            button.setClickable(false);
            button.setText("");
        }
    }

    private void setList() {
        int numRows = tableLayout.getChildCount();
        for (int i = 0; i < numRows; i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            int numButtons = row.getChildCount();
            for (int j = 0; j < numButtons; j++) {
                AppCompatButton button = (AppCompatButton) row.getChildAt(j);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(button.isClickable()) {
                            button.setBackgroundColor(getColor(R.color.clicked_card_item));
                            button.setClickable(false);
                        }
                    }
                });
                if (i == 0) {
                    buttonList1.add(button);
                }
                if (i == 1) {
                    buttonList2.add(button);
                }
                if (i == 2) {
                    buttonList3.add(button);
                }
            }
        }
    }
}