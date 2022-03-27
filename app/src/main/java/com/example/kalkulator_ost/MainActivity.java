package com.example.kalkulator_ost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mariuszgromada.math.mxparser.Expression;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class MainActivity extends AppCompatActivity {

    boolean isError = false;
    boolean isNewOperator = false;
    boolean isComa = false;
    boolean isResult = false;
    boolean isFirstNumber = true;
    String newNumber = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView;

        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isError", isError);
        outState.putBoolean("isNewOperator", isNewOperator);
        outState.putBoolean("isComa", isComa);
        outState.putBoolean("isResult", isResult);
        outState.putString("newNumber", newNumber);
        outState.putBoolean("firstNumber", isFirstNumber);

        TextView screen = findViewById(R.id.textView);
        outState.putString("screenContent", screen.getText().toString());
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        isError = savedInstanceState.getBoolean("isError");
        isNewOperator = savedInstanceState.getBoolean("isNewOperator");
        isComa = savedInstanceState.getBoolean("isComa");
        isResult = savedInstanceState.getBoolean("isResult");
        newNumber = savedInstanceState.getString("newNumber");
        isFirstNumber = savedInstanceState.getBoolean("firstNumber");

        TextView screen = findViewById(R.id.textView);
        String screenContent = savedInstanceState.getString("screenContent");
        screen.setText(screenContent);
    }

    public void numberEvent(View v){
        if(!isError && !isResult) {
            TextView screen = findViewById(R.id.textView);
            Button button = (Button) v;

            String inputContent = screen.getText().toString();

            if(button.getText().toString().equals(".")){
                if(isNewOperator){
                    inputContent += "0.";
                    newNumber += "0.";
                    screen.setText(inputContent);
                    isComa = true;
                    isNewOperator = false;
                }
                else if(!isComa){
                    inputContent += button.getText().toString();
                    newNumber += button.getText().toString();
                    screen.setText(inputContent);
                    isComa = true;
                }
            } else if (isFirstNumber){
                inputContent = button.getText().toString();
                newNumber = button.getText().toString();
                screen.setText(inputContent);
            } else{
                inputContent += button.getText().toString();
                newNumber += button.getText().toString();
                screen.setText(inputContent);
                isNewOperator = false;
            }
            isFirstNumber = false;
        }
    }

    public void operatorEvent(View v){
        if(!isError && !isNewOperator) {
            TextView screen = findViewById(R.id.textView);
            Button button = (Button) v;

            String inputContent = screen.getText().toString();
            inputContent += " " + button.getText().toString() + " ";
            screen.setText(inputContent);

            isNewOperator = true;
            isComa = false;
            isResult = false;
            newNumber = "";
            isFirstNumber = false;
        }
    }

    public void operatorEventNow(View v) {

        if (equalEvent(findViewById(R.id.button_calculate))) {

            if (!isError) {
                Button button = (Button) v;
                TextView screen = findViewById(R.id.textView);
                String operator = button.getText().toString();

                double num = Double.parseDouble(screen.getText().toString());

                switch (operator) {
                    case "%":
                        num /= 100;
                        break;
                    case "x^2":
                        num = Math.pow(num, 2);
                        break;
                    case "x^3":
                        num = Math.pow(num, 3);
                        break;

                    case "log10":
                        num = Math.log10(num);
                        break;

                    case "x!":
                        double value = 1;
                        if (num >= 0.0) {
                            for (int i = 1; i <= num; i++) {
                                value *= i;
                            }
                            num = value;
                        } else
                            isError = true;
                        break;

                    case "SQRT(X)":
                        num = Math.sqrt(num);
                        break;

                }
                if (isError)
                    screen.setText(R.string.ERROR);
                else {
                    screen.setText(String.valueOf(num));

                    isResult = true;
                    isComa = true;

                    if (screen.getText().toString().equals("Infinity") || screen.getText().toString().equals("-Infinity") || screen.getText().toString().equals("NaN")) {
                        screen.setText(R.string.ERROR);
                        isError = true;
                    }
                }

            }
        }
    }

    public boolean equalEvent(View v) {
        if (!isError && !isNewOperator) {
            TextView screen = findViewById(R.id.textView);

            String inputContent = screen.getText().toString();

            try {

                Expression e = new Expression(inputContent);
                double calculate = e.calculate();

                String number = String.valueOf(calculate);

                screen.setText(number);

                if (number.equals("NaN") || number.equals("Infinity") || number.equals("-Infinity")) {
                    screen.setText(R.string.ERROR);
                    isError = true;
                }

            } catch (Exception e) {
                screen.setText(R.string.ERROR);
                isError = true;
            }
            isComa = true;
            isResult = true;
            return true;
        }
        return false;
    }

    public void signEvent(View v){
        TextView screen = findViewById(R.id.textView);
        if(!isError && !newNumber.equals("")){

            if(equalEvent(findViewById(R.id.button_calculate))) {

                double number = Double.parseDouble(screen.getText().toString());

                if (number != 0) {
                    number *= -1;
                    screen.setText(String.valueOf(number));
                    isComa = true;
                }
            }
        }
    }

    public void clearEvent(View v){
        TextView screen = findViewById(R.id.textView);
        screen.setText("0");

        isError = false;
        isNewOperator = false;
        isComa = false;
        isResult = false;
        isFirstNumber = true;
    }
    
}