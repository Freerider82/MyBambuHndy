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

    public void onClickBtnStart(View view) {

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


            int sizeFile = 20000;//(int) files[0].length();

            byte[] bytes = new byte[sizeFile];
            int[] IndexsUnsignedBytes = new int[sizeFile];
            int sizeUnsignedSymbols = 0;

            File subFolder = new File(path);
            FileInputStream outputStream = new FileInputStream(new File(subFolder, "logger.log"));

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

            string = deleteLogMessage(string,"Pushing  TUTK  Received");
            string = deleteLogMessage(string,"TUTK MediaStore Received");

            int stringSize= string.length();
            int j = 0;
            int index = 0;
            ArrayList<String> datesInLog = new ArrayList<>();

            ArrayList<StateBambu> stateBambus = getListStateBambu("2024-09-03",string);
/*
            while(j<stringSize){
                index = string.indexOf("2024-08-29",j);
                if(index !=-1){
                    String date = string.substring(index+10,index+23);
                    j = index+23;
                    datesInLog.add(date);
                }   else break;
            }
*/


            textView.setText(String.valueOf(index));
            Log.d(LOG_TAG, "Size: "+ (int) files[0].length());
            // Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Log.e("ERROR", e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

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

    public ArrayList<StateBambu> getListStateBambu(String date_y_m_d,String str){

        ArrayList<StateBambu> stateBambuArrayList = new ArrayList<>();

        int stringSize = str.length();
        int j = 0;
        int index = 0;

        while(j<stringSize){
            index = str.indexOf(date_y_m_d,j);
            if(index !=-1){
                StateBambu stateBambu = new StateBambu();

                String date = str.substring(index+10,index+23);
                j = index+23;

              //  stateBambu.setDateStr_h_m_s_ms(date);
                stateBambuArrayList.add(stateBambu);
            }   else break;
        }


        return stateBambuArrayList;
    }

    public ArrayList<StateBambu> getListStateBambuLastLog(String date_y_m_d,String str){

        ArrayList<StateBambu> stateBambuArrayList = new ArrayList<>();

        int index = str.indexOf(date_y_m_d);
        if(index !=-1){
            StateBambu stateBambu = new StateBambu();
            String temp = str.substring(index+10,index+23);

           // stateBambu.setDateStr_h_m_s_ms(temp);

            index = str.indexOf("nozzle_temper");

            if(index != -1){
                int indexComma = str.indexOf(",",index);
                temp = str.substring(index+16,indexComma);
                float f = Float.parseFloat(temp);
                int t = (int) f;
             //   stateBambu.setNozzle_temper(t);
            }
            stateBambuArrayList.add(stateBambu);

        }

        return stateBambuArrayList;
    }



    public void onClickLastLog(View view) {
/*
        long time = System.currentTimeMillis();
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
        Log.d(LOG_TAG, "Time search minFile:" + String.valueOf(System.currentTimeMillis()-time ) );

        try {

            time = System.currentTimeMillis();
            int sizeFile = (int) files[indexFileMinSize].length();
            byte[] bytes = new byte[sizeFile];
            int[] IndexsGoodBytes = new int[sizeFile];
            int sizeGoodSymbols = 0;

            File subFolder = new File(path);
            FileInputStream outputStream = new FileInputStream(new File(subFolder, fileMinSize));

            outputStream.read(bytes);
            outputStream.close();

            //Получаем размер  для создания массива без  не нужных значений  без символов '/n' '/r'
            for(int i =0; i<sizeFile;i++){
                if((bytes[i] != 63) && (bytes[i] != 10) && (bytes[i] != 13)){
                    //IndexsUnsignedBytes - массив хранящий индексы массива bytes с полож.числами
                    IndexsGoodBytes[sizeGoodSymbols] = i;
                    sizeGoodSymbols++;
                }
            }

            byte[] bytesGood = new byte[sizeGoodSymbols];

            for(int i = 0; i < sizeGoodSymbols; i++){
                bytesGood[i] = bytes[IndexsGoodBytes[i]];
            }

            String string = new String(bytesGood);
            //Убираем лишние пробелы
            while(string.contains("  ")) {
                String replace = string.replace("  ", " ");
                string=replace;
            }

            //Ищем с конца строку лога с полезной инфой
            int indexStartStrLastLog = string.lastIndexOf("Pushing Status");
            int indexEndStrLastLog = string.lastIndexOf("new_ver_list");
            String subStr = string.substring(indexStartStrLastLog,indexEndStrLastLog);

            //ArrayList<StateBambu> stateBambus = getListStateBambuLastLog("2024-09-03",subStr);
*/
            Intent intent = new Intent(MainActivity.this, ValueBambuActivity.class);

            startActivity(intent);

    }

    private HashMap<String, ValueBambu> getHashMapBanbuLastLog(String date_y_m_d, String subStr) {
        HashMap<String, ValueBambu> valueBambuHashMap = new HashMap<>();

        int index;
        //key местами не менять
        final int indexNozzleTemper = 4;

        String[] keys ={"hour_min_sec_ms",
                "gcode_state",
                "subtask_name",
                "wifi_signal",
                "nozzle_temper",
                "nozzle_target_temper",
                "bed_temper",
                "bed_target_temper",
                "mc_percent",
                "mc_remaining_time",
                "spd_mag",
                "print_error",
                "layer_num",
                "total_layer_num"
        };

        //Инициализация valueBambuHashMap
        for (String key:keys) {
            valueBambuHashMap.put(key,new ValueBambu(null,-1));
        }

        //Записываем дату в формате String например 14:59:40.748 и перевод в милисекунді
        index = subStr.indexOf(date_y_m_d);
        if(index !=-1) {
            String date = subStr.substring(index + 11, index + 23);
            //14:59:40.748
            int hour = Integer.parseInt(date.substring(0,2));
            int minute = Integer.parseInt(date.substring(3,5));
            int seconds = Integer.parseInt(date.substring(6,8));
            int mSeconds = Integer.parseInt(date.substring(9));
            int time_ms = mSeconds + seconds*1000 + minute*60000 + hour*3600000;
            valueBambuHashMap.put("hour_min_sec_ms",new ValueBambu(date,time_ms));
        }

        //Записываем gcode_state: NO_STATE 'N';RUNNING 'R'; PAUSE 'P'; FINISH 'F'
        index = subStr.indexOf("gcode_state");
        if(index != -1){
            int indexStart = subStr.indexOf(" ",index) +2;
            int indexEnd = subStr.indexOf('"',indexStart+1);
            String gcodeState = subStr.substring(indexStart,indexEnd);
            int temp = (int) gcodeState.charAt(0);
            valueBambuHashMap.put("gcode_state",new ValueBambu(gcodeState,temp));
        }
        //Записываем имя файла что идет на печать
        index = subStr.indexOf("subtask_name");
        if(index != -1){
            int indexStart = subStr.indexOf(" ",index) +2;
            int indexEnd = subStr.indexOf('"',indexStart+1) ;
            String subTaskName = subStr.substring(indexStart,indexEnd);
            int temp = (int) subTaskName.charAt(0);
            valueBambuHashMap.put("subtask_name",new ValueBambu(subTaskName,temp));
        }
        //Записывам уровень wi-fi сигнала
        index = subStr.indexOf("wifi_signal");
        if(index != -1){
            int indexStart = subStr.indexOf("-",index) +1;
            int indexEnd = subStr.indexOf("d",indexStart) ;
            String wifiSignal = subStr.substring(indexStart,indexEnd);
            int temp = Integer.parseInt(wifiSignal);
            valueBambuHashMap.put("wifi_signal",new ValueBambu(wifiSignal,temp));
        }

        //Запись остальных value

        for(int i = indexNozzleTemper; i<keys.length;i++){
            putValueToBambuHashMap(valueBambuHashMap,subStr,keys[i]);
        }


        return valueBambuHashMap;
    }

    private void putValueToBambuHashMap(HashMap<String, ValueBambu> valueBambuHashMap,String subStr, String key) {

        int index = subStr.indexOf(key);
        if(index != -1){
            int indexStart = subStr.indexOf(" ",index)+1;
            int indexEnd = subStr.indexOf(",",indexStart+1);
            String str = subStr.substring(indexStart,indexEnd);
            float f = Float.parseFloat(str);

            valueBambuHashMap.put(key, new ValueBambu(str,(int)f));
        }
    }
}