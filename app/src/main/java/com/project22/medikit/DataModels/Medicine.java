package com.project22.medikit.DataModels;


public class Medicine {
    private String medicinename, medicinetype, medicinedose;
    private String time;

    public Medicine() {
    }


    public Medicine(String medicinename, String medicinetype, String medicinedose, String time) {
        this.medicinename = medicinename;
        this.medicinetype = medicinetype;
        this.medicinedose = medicinedose;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getMedicinename() {
        return medicinename;
    }

    public String getMedicinetype() {
        return medicinetype;
    }

    public String getMedicinedose() {
        return medicinedose;
    }

}
