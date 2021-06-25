package com.project22.medikit.DataModels;

import android.text.format.Time;

import java.util.ArrayList;

public class Medicine {
    private String medicinename, type, dose;
    private ArrayList<Time> alarmtime;

    public Medicine(String medicinename, String type, String dose, ArrayList<Time> alarmtime) {
        this.medicinename = medicinename;
        this.type = type;
        this.dose = dose;
        this.alarmtime = alarmtime;
    }

    public String getMedicinename() {
        return medicinename;
    }

    public String getType() {
        return type;
    }

    public String getDose() {
        return dose;
    }

    public ArrayList<Time> getAlarmtime() {
        return alarmtime;
    }
}
