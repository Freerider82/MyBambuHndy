package com.example.mybambuhandy;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final String DIR_BAMBU_LOGS = "/Android/data/bbl.intl.bambulab.com/files/logs/logger";
    final String LOG_TAG = "Files";
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        textView =findViewById(R.id.textView);
    }



    public String deleteLogMessage (String editStr, String nameLogMessage){

        int indexPushing = 0;
        int countScobkaStart = 0;
        int countScobkaEnd = 0;
        int fromIndex = 0;
        StringBuilder myString = new StringBuilder(editStr);
        boolean flagDoFor;
        while(true) {

            flagDoFor = true;
            indexPushing = myString.indexOf(nameLogMessage,fromIndex);

            if(indexPushing == -1) break;

            for(int i = indexPushing;(flagDoFor == true) && (i < editStr.length()-1) ; i++ ){

                switch (editStr.charAt(i)){
                    case '{':countScobkaStart++;break;
                    case '}':
                        countScobkaEnd++;
                        if(countScobkaEnd == countScobkaStart){
                            fromIndex = i;
                            flagDoFor = false;
                        }
                        break;
                }
                myString.setCharAt(i, ' ');

            }
        }


        editStr = myString.toString();

        //Убираем лишние пробелы
        while(editStr.contains("  ")) {
            String replace = editStr.replace("  ", " ");
            editStr=replace;
        }

        return editStr;
    }


    public void onClickLastLog(View view) {

            Intent intent = new Intent(MainActivity.this, ValueBambuActivity.class);

            startActivity(intent);

    }

}