package com.example.mybambuhandy;

import android.os.Bundle;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.parceler.Parcels;

import java.util.HashMap;

public class ValueBambuActivity extends AppCompatActivity {

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);

        for (int i = 0; i < r_ids.length; i++){
            textViews[i] = findViewById(r_ids[i]);
        }


        //textViewTime = findViewById(R.id.textViewTime);
       // textViewBambuWork =findViewById(R.id.textViewBambuWork);


        String subStr = getIntent().getStringExtra("SubString");
        HashMap<String,ValueBambu> bambuHashMap = getHashMapBanbuLastLog("2024-09-03",subStr);

        for (int i = 0; i < keys.length; i++) {

            TextView textView = textViews[i];
            String s = textView.getText().toString();
            textView.setText(s + " " + bambuHashMap.get(keys[i]).getString());
        }

        int x =0;
/*
        String s = textViewTime.getText().toString();
        textViewTime.setText(bambuHashMap.get("hour_min_sec_ms").getString());
        textViewBambuWork.setText(bambuHashMap.get("gcode_state").getString());
*/
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
}
