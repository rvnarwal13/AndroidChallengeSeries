package com.example.minicalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import java.util.regex.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView resultTv, solutionTv;
    MaterialButton buttonC,buttonBS;
    MaterialButton buttonDivide, buttonMultiply, buttonSubt, buttonAdd, buttonEquals, buttonMod;
    MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    MaterialButton buttonAC, buttonDot;
    static boolean checkDecimal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);
        assignId(buttonC, R.id.button_c);
        assignId(buttonBS, R.id.button_open_bracket);
        assignId(buttonMod, R.id.button_close_bracket);
        assignId(button0, R.id.button_zero);
        assignId(button1, R.id.button_one);
        assignId(button2, R.id.button_two);
        assignId(button3, R.id.button_three);
        assignId(button4, R.id.button_four);
        assignId(button5, R.id.button_five);
        assignId(button6, R.id.button_six);
        assignId(button7, R.id.button_seven);
        assignId(button8, R.id.button_eight);
        assignId(button9, R.id.button_nine);
        assignId(buttonDivide, R.id.button_divide);
        assignId(buttonMultiply, R.id.button_multiply);
        assignId(buttonSubt, R.id.button_subtract);
        assignId(buttonAdd, R.id.button_add);
        assignId(buttonEquals, R.id.button_equals);
        assignId(buttonAC, R.id.button_ac);
        assignId(buttonDot, R.id.button_decimal);
    }

    void assignId(MaterialButton btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    public static double evaluate(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else if (eat('%')) x %= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = expression.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    String checkRegex(String dataToCalculate) {
        char newOperator = dataToCalculate.charAt(dataToCalculate.length()-1);
        char lastChar = dataToCalculate.charAt(dataToCalculate.length()-2);
        String newDataToCalculate = dataToCalculate.substring(0, dataToCalculate.length()-2);
        if(lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/' || lastChar == '%' || lastChar == '.') {

            if(newOperator == lastChar) {
                dataToCalculate = newDataToCalculate + newOperator;
            } else if(newOperator == '+' || newOperator == '-' || newOperator == '*' || newOperator == '/' || newOperator == '%') {
                if(lastChar == '.') {
                    dataToCalculate = newDataToCalculate + lastChar;
                } else {
                    dataToCalculate = newDataToCalculate + newOperator;
                }
            } else if(newOperator == '.') {
                dataToCalculate = newDataToCalculate + lastChar + "0" + newOperator;
            }
        }  else {
            Pattern pattern = Pattern.compile("^(\\d+(\\.)?(\\d+)?)([\\+\\-\\*\\/\\%](\\d+(\\.)?(\\d+)?)?)*$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(dataToCalculate);
            if (!matcher.find()) {
                dataToCalculate = newDataToCalculate + lastChar;
            }
        }
        return dataToCalculate;
    }

    @Override
    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String dataToCalculate = solutionTv.getText().toString();

        if(buttonText.equals("AC")) {
            solutionTv.setText("0");
            resultTv.setText("0");
            return;
        }
        if(buttonText.equals("=")) {
            resultTv.setText(""+evaluate(dataToCalculate));
            return;
        }
        if(buttonText.equals("C")) {
            solutionTv.setText("0");
            return;
        }

        char firstChar = dataToCalculate.charAt(0);

        if((firstChar == '0' || firstChar == '+' || firstChar == '-' || firstChar == '*' || firstChar == '/' || firstChar == '%') && !(buttonText.equals("x") || buttonText.equals(".")) && dataToCalculate.length()<2) {
            dataToCalculate = dataToCalculate.substring(1, dataToCalculate.length());
        }

        if(buttonText.equals("x")) {
            if(dataToCalculate.length()==1) {
                dataToCalculate = "0";
            } else {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
            }
        } else {
            dataToCalculate = dataToCalculate + buttonText;
        }
        if(dataToCalculate.length()>2) {
            dataToCalculate = checkRegex(dataToCalculate);
        }

        solutionTv.setText(dataToCalculate);
    }

    String getResult(String data) {
        return "Calculated";
    }
}