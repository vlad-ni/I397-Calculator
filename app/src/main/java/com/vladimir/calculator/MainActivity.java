package com.vladimir.calculator;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Integer firstNumber = null;
    String operation = null;
    Integer secondNumber = null;
    Boolean newNumber = false;
    Boolean numberClicked = false;
    TextView numberField;
    TextView historyField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberField = (TextView) findViewById(R.id.textNumberField);
        historyField = (TextView) findViewById(R.id.textHistoryField);

        if (savedInstanceState != null) {
            firstNumber = savedInstanceState.getIntegerArrayList("numbers").get(0);
            operation = savedInstanceState.getString("operation");
            newNumber = savedInstanceState.getBoolean("newNumber");
            numberClicked = savedInstanceState.getBoolean("numberClicked");
            numberField.setText(savedInstanceState.getString("numberField"));
            historyField.setText(savedInstanceState.getString("historyField"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        numbers.add(firstNumber);

        savedInstanceState.putIntegerArrayList("numbers", numbers);
        savedInstanceState.putString("operation", operation);
        savedInstanceState.putBoolean("newNumber", newNumber);
        savedInstanceState.putBoolean("numberClicked", numberClicked);
        savedInstanceState.putString("numberField", numberField.getText().toString());
        savedInstanceState.putString("historyField", historyField.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    public void buttonNumberClicked (View view) {
        Button button = (Button) view;
        String pressedButton = button.getText().toString();
        String numberOnScreen = numberField.getText().toString();

        if (historyField.getText().toString().contains("Error"))
            clear(true, false);

        if (newNumber) {
            numberOnScreen = "";
            newNumber = false;
        }

        if (numberOnScreen.equals("0") && pressedButton.equals("0")) {
            return;
        } else if (numberOnScreen.equals("0")) {
            numberOnScreen = pressedButton;
        } else {
            numberOnScreen += pressedButton;
        }
        numberField.setText(numberOnScreen);
        numberClicked = true;
    }

    public void buttonOperationClicked (View view) {
        Button button = (Button) view;
        String currentOperation = button.getText().toString();
        Integer num = Integer.parseInt(numberField.getText().toString());

        if (!numberClicked && !currentOperation.equals("C") && !currentOperation.equals("=")) {
            operation = currentOperation;
            writeHistory(null, currentOperation, true);
            return;
        }

        if (currentOperation.equals("C")){
            clear(true, true);
        } else if (currentOperation.equals("=") && firstNumber != null && operation != null) {
            secondNumber = Integer.parseInt(numberField.getText().toString());
            firstNumber = calculate();
            if (setResult())
                clear(true, false);
        } else {
            if (firstNumber != null && operation == null) {
                operation = currentOperation;
                secondNumber = num;
                writeHistory(firstNumber, currentOperation, false);
                numberClicked = true;
            } else if (firstNumber == null && operation == null) {
                operation = currentOperation;
                firstNumber = num;
                writeHistory(firstNumber, currentOperation, false);
                numberClicked = false;
            } else {
                secondNumber = num;
                firstNumber = calculate(); //calculate using 1st and 2nd numbers and rewrite 1st number with the result
                if (setResult()) {
                    writeHistory(secondNumber, currentOperation, false);
                    operation = currentOperation;
                }
                numberClicked = false;
            }
        }
        newNumber = true;
    }

    public Integer calculate() {
        Integer result = 0;
        switch (operation) {
            case "+":
                result = firstNumber + secondNumber;
                break;
            case "-":
                result = firstNumber - secondNumber;
                break;
            case "*":
                result = firstNumber * secondNumber;
                break;
            case "/":
                if (secondNumber == 0) {
                    result = -1;
                    operation = "Error";
                } else {
                    result = firstNumber / secondNumber;
                }
                break;
        }
        return result;
    }

    public void clear(boolean clearHistory, boolean clearResult) {
        firstNumber = null;
        secondNumber = null;
        operation = null;

        if (clearResult)
            numberField.setText("0");

        if (clearHistory)
            historyField.setText("");
    }

    public boolean setResult(){
        if (firstNumber == -1 && operation.equals("Error")) {
            historyField.setText("Error: division by zero!");
            clear(false, true);
            return false;
        } else {
            numberField.setText(firstNumber.toString());
            return true;
        }
    }

    public void writeHistory(Integer number, String operation, boolean changeOnlyOperation){
        String history = historyField.getText().toString();

        if (changeOnlyOperation) {
            history = history.substring(0, history.length() - 1);
            historyField.setText(history.concat(operation));
        }

        if (number != null)
            historyField.setText(history.concat(" " + number.toString().concat(" " + operation)));
    }
}
