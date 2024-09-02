package com.example.mybambuhandy;

import static android.content.ContentValues.TAG;

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
import java.util.Date;
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

    public void onCclickBtnStart(View view) {

        String path = Environment.getExternalStorageDirectory().toString()+DIR_BAMBU_LOGS;
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d(LOG_TAG, "Size: "+ files.length);
        int countFiles = files.length;

        for (int i = 0; i < countFiles; i++) {

            if (files[i].exists()) {
                Log.d(LOG_TAG, "Name Files:" + files[i].getName());
            }
        }

        Date currentDate = new Date();
        // Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        try {


            int sizeFile = (int) files[0].length();
            byte[] bytes = new byte[sizeFile];
            int[] IndexsUnsignedBytes = new int[sizeFile];
            int sizeUnsignedSymbols = 0;

            File subFolder = new File(path);
            FileInputStream outputStream = new FileInputStream(new File(subFolder, "logger.0.log"));

            outputStream.read(bytes);
            outputStream.close();

            //Получаем размер  для создания массива без отрицат.значений и без символа '/n'
            for(int i =0; i<sizeFile;i++){
                if((bytes[i]>=0)&& (bytes[i] != 10)){
                    //IndexsUnsignedBytes - массив хранящий индексы массива bytes с полож.числами
                    IndexsUnsignedBytes[sizeUnsignedSymbols] = i;
                    sizeUnsignedSymbols++;
                }
            }

            byte[] bytesUnsigned = new byte[sizeUnsignedSymbols];

            for(int i = 0; i < sizeUnsignedSymbols; i++){
                bytesUnsigned[i] = bytes[IndexsUnsignedBytes[i]];
            }

            String string = new String(bytesUnsigned);
            //Убираем лишние пробелы
            while(string.contains("  ")) {
                String replace = string.replace("  ", " ");
                string=replace;
            }

          //  textView.setText(string);
            Log.d(LOG_TAG, "Size: "+ (int) files[0].length());
           // Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Log.e("ERROR", e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

    }

    public void onClickLastLog(View view) {

        String path = Environment.getExternalStorageDirectory().toString()+DIR_BAMBU_LOGS;
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d(LOG_TAG, "Size: "+ files.length);

        int indexFileMinSize = files.length-1;
        long size2;
        long min = files[indexFileMinSize].length();
        for (int i = indexFileMinSize; i > 0; i--) {

            size2 = files[i-1].length();

            if( min > size2 ){
                min = size2;
                indexFileMinSize = i-1;
            }
        }

        String fileMinSize = files[indexFileMinSize].getName();
        Log.d(LOG_TAG, "Name Files MinSize:" + fileMinSize);

        try {

            int sizeFile = (int) files[indexFileMinSize].length();
            byte[] bytes = new byte[sizeFile];
            int[] IndexsUnsignedBytes = new int[sizeFile];
            int sizeUnsignedSymbols = 0;

            File subFolder = new File(path);
            FileInputStream outputStream = new FileInputStream(new File(subFolder, fileMinSize));

            outputStream.read(bytes);
            outputStream.close();

            //Получаем размер  для создания массива без отрицат.значений и без символа '/n'
            for(int i =0; i<sizeFile;i++){
                if((bytes[i]>=0)&& (bytes[i] != 10)){
                    //IndexsUnsignedBytes - массив хранящий индексы массива bytes с полож.числами
                    IndexsUnsignedBytes[sizeUnsignedSymbols] = i;
                    sizeUnsignedSymbols++;
                }
            }

            byte[] bytesUnsigned = new byte[sizeUnsignedSymbols];

            for(int i = 0; i < sizeUnsignedSymbols; i++){
                bytesUnsigned[i] = bytes[IndexsUnsignedBytes[i]];
            }

            String string = new String(bytesUnsigned);
            //Убираем лишние пробелы
            while(string.contains("  ")) {
                String replace = string.replace("  ", " ");
                string=replace;
            }

            Log.d(LOG_TAG, "Size: "+ (int) files[indexFileMinSize].length());

        } catch (FileNotFoundException e) {
            Log.e("ERROR", e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }


    }
}