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

public class MainActivity extends AppCompatActivity {

    final String DIR_BAMBU_LOGS = "/Android/data/bbl.intl.bambulab.com/files/logger/logs";
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

        try {

            //byte[] bytes = new byte[(int) files[0].length()];
            byte[] bytes = new byte[1024];
            int[] IndexsUnsignedBytes = new int[1024];
            File subFolder = new File(path);

            FileInputStream outputStream = new FileInputStream(new File(subFolder, "logger.log"));

            outputStream.read(bytes);
            outputStream.close();

            int sizeUnsignedSymbols = 0;





            for(int i =0; i<1024;i++){
                if((bytes[i]>=0)&& (bytes[i] != 10)){
                    IndexsUnsignedBytes[sizeUnsignedSymbols] = i;
                    sizeUnsignedSymbols++;
                }
            }

            byte[] bytesUnsigned = new byte[sizeUnsignedSymbols];

            for(int i = 0; i<sizeUnsignedSymbols;i++){
                bytesUnsigned[i] = bytes[IndexsUnsignedBytes[i]];
            }

            String string = new String(bytesUnsigned);

            while(string.contains("  ")) {
                String replace = string.replace("  ", " ");
                string=replace;
            }

            textView.setText(string);
            Log.d(LOG_TAG, "Size: "+ (int) files[0].length());
           // Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Log.e("ERROR", e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

    }
}