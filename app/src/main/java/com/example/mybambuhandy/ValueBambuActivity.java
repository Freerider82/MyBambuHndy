package com.example.mybambuhandy;

import static android.content.ContentValues.TAG;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class ValueBambuActivity extends AppCompatActivity {

    final String LOG_TAG = "ValueBambu";
    private boolean is_started = false;
    private int counter = 0;
    TextView textViewTest,textViewTemp,textViewTemp2;
    final String DIR_BAMBU_LOGS = "/Android/data/bbl.intl.bambulab.com/files/logs/logger";
    HashMap<String,ValueBambu> bambuHashMap;
    int indexFileMinSize,sizeFile;
    String  fileMinSize;
    boolean subStringIsNormal = true;


    final String[] keys ={"hour_min_sec_ms",
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


    TextView []textViews = new TextView[keys.length];
    int []r_ids ={R.id.textViewTime,
            R.id.textViewBambuWork,
            R.id.textViewFileName,
            R.id.textViewWIFI,
            R.id.textViewNozzle_temper,
            R.id.textViewNozzle_target_temper,
            R.id.textViewBed_temper,
            R.id.textViewBed_target_temper,
            R.id.textViewMc_percent,
            R.id.textViewMc_remaining_time,
            R.id.textViewSpd_mag,
            R.id.textViewPrint_error,
            R.id.textViewLayer_num,
            R.id.textViewTotal_layer_num};
    
    private int[] IndexsGoodBytes;
    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);


        textViewTest = findViewById(R.id.textViewTest);
        textViewTemp = findViewById(R.id.textViewTemp);
        textViewTemp2 = findViewById(R.id.textViewTemp2);



        for (int i = 0; i < r_ids.length; i++){
            textViews[i] = findViewById(r_ids[i]);
        }

        String path = Environment.getExternalStorageDirectory().toString()+DIR_BAMBU_LOGS;
        scanFilesBambuLogs(new File(path));


    }

    private HashMap<String, ValueBambu> getHashMapBanbuLastLog(String date_y_m_d, String subStr) {

        HashMap<String, ValueBambu> valueBambuHashMap = new HashMap<>();

        int index;
        //key местами не менять
        final int indexNozzleTemper = 4;



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

    public void scanFilesBambuLogs(File  directory) {
        is_started = true;
        new Thread(new Runnable() {
            @Override
            public void run() {

                File[] files = directory.listFiles();
                int sizeGoodSymbols = 0;
                long size2,min;
                while (is_started){
                    counter++;
                    Log.d(LOG_TAG, "Counter: "+  counter);
                    //Поиск индекса файла с минимальным размером будем проходит с конца массива файлов

                     indexFileMinSize = files.length-1;

                     min = files[indexFileMinSize].length();
                    for (int i = indexFileMinSize; i > 0; i--) {

                        size2 = files[i-1].length();
                        if( min > size2 ){
                            min = size2;
                            indexFileMinSize = i-1;
                        }
                    }


                     sizeFile = (int) files[indexFileMinSize].length();
                     bytes = new byte[sizeFile];
                     IndexsGoodBytes = new int[sizeFile];//Индексы хороших байтов массива bytes(хорошие байты это символы без отриц.значений и '/n' '/r')



                    try {
                        fileMinSize = files[indexFileMinSize].getName();

                        FileInputStream outputStream = new FileInputStream(new File(directory, fileMinSize));
                        outputStream.read(bytes);
                        outputStream.close();
                        sizeGoodSymbols = 0;
                        //Получаем размер  для создания массива без  не нужных значений  без символов '/n' '/r'
                        for(int i =0; i<sizeFile;i++){
                            if((bytes[i] >= 0) && (bytes[i] != 63) && (bytes[i] != 10) && (bytes[i] != 13)){
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
                        int indexEndStrLastLog = string.indexOf("s_obj",indexStartStrLastLog);
                        Log.d(LOG_TAG, "indexStartStrLastLog: "+  indexStartStrLastLog);
                        Log.d(LOG_TAG, "indexEndStrLastLog: "+  indexEndStrLastLog);

                        if((indexStartStrLastLog !=-1) && (indexEndStrLastLog !=-1)){
                            String subStr = string.substring(indexStartStrLastLog,indexEndStrLastLog);
                            Log.d(LOG_TAG, "subStr: "+  subStr);
                            bambuHashMap = getHashMapBanbuLastLog("2024-09-07",subStr);
                        } else subStringIsNormal = false;





                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(subStringIsNormal){
                                //Запуск на основном потоке
                                for (int i = 0; i < keys.length; i++) {

                                    //String s = textView.getText().toString();
                                    textViews[i].setText(keys[i] + ": " + bambuHashMap.get(keys[i]).getString());
                                }

                                textViewTest.setText(String.valueOf(counter));
                                textViewTemp.setText((String.valueOf(sizeFile)));
                                textViewTemp2.setText(fileMinSize);
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_started = false;
    }

    private void addNotification() {
        // Builds your notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("My Bambu Notification")
                .setContentText("RepeatBambu");

        // Creates the intent needed to show the notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public void onClickButtonNotification(View view) throws InterruptedException {

        Intent intentBambu = getPackageManager().getLaunchIntentForPackage("bbl.intl.bambulab.com");
        Intent intentMain = getPackageManager().getLaunchIntentForPackage("com.example.mybambuhandy");

        PendingIntent pendingIntentBambu = PendingIntent.getActivity(ValueBambuActivity.this,0,intentBambu, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntentMain = PendingIntent.getActivity(ValueBambuActivity.this,0,intentMain, PendingIntent.FLAG_IMMUTABLE);

        try{
            pendingIntentBambu.send();
        }catch (Exception e){
            e.printStackTrace();
        }

        addNotification();

        //Thread.sleep(26000);
        try{
            //pendingIntentMain.send();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
