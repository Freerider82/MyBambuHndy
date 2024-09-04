package com.example.mybambuhandy;


public class StateBambu {

    private String gcode_state;
    private int mc_percent;
    private int mc_remaining_time;
    private int nozzle_temper;
    private int nozzle_target_temper;
    private int bed_temper;
    private int bed_target_temper;
    private int print_error;
    private int wifi_signal;
    private String subtask_name;
    private int layer_num;
    private int total_layer_num;
    private String dateStr_h_m_s_ms;


    public String getDateStr_h_m_s_ms() {
        return dateStr_h_m_s_ms;
    }

    public void setDateStr_h_m_s_ms(String dateStr_h_m_s_ms) {
        this.dateStr_h_m_s_ms = dateStr_h_m_s_ms;
    }

    public String getGcode_state() {
        return gcode_state;
    }

    public void setGcode_state(String gcode_state) {
        this.gcode_state = gcode_state;
    }

    public int getMc_percent() {
        return mc_percent;
    }

    public void setMc_percent(int mc_percent) {
        this.mc_percent = mc_percent;
    }

    public int getMc_remaining_time() {
        return mc_remaining_time;
    }

    public void setMc_remaining_time(int mc_remaining_time) {
        this.mc_remaining_time = mc_remaining_time;
    }

    public int getNozzle_temper() {
        return nozzle_temper;
    }

    public void setNozzle_temper(int nozzle_temper) {
        this.nozzle_temper = nozzle_temper;
    }

    public int getNozzle_target_temper() {
        return nozzle_target_temper;
    }

    public void setNozzle_target_temper(int nozzle_target_temper) {
        this.nozzle_target_temper = nozzle_target_temper;
    }

    public int getBed_temper() {
        return bed_temper;
    }

    public void setBed_temper(int bed_temper) {
        this.bed_temper = bed_temper;
    }

    public int getBed_target_temper() {
        return bed_target_temper;
    }

    public void setBed_target_temper(int bed_target_temper) {
        this.bed_target_temper = bed_target_temper;
    }

    public int getPrint_error() {
        return print_error;
    }

    public void setPrint_error(int print_error) {
        this.print_error = print_error;
    }

    public int getWifi_signal() {
        return wifi_signal;
    }

    public void setWifi_signal(int wifi_signal) {
        this.wifi_signal = wifi_signal;
    }

    public String getSubtask_name() {
        return subtask_name;
    }

    public void setSubtask_name(String subtask_name) {
        this.subtask_name = subtask_name;
    }

    public int getLayer_num() {
        return layer_num;
    }

    public void setLayer_num(int layer_num) {
        this.layer_num = layer_num;
    }

    public int getTotal_layer_num() {
        return total_layer_num;
    }

    public void setTotal_layer_num(int total_layer_num) {
        this.total_layer_num = total_layer_num;
    }




}
