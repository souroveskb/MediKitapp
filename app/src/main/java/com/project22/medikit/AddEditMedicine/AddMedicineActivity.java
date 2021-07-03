package com.project22.medikit.AddEditMedicine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project22.medikit.AlarmReceiver.AlarmBroadcastReceiver;
import com.project22.medikit.R;
import com.project22.medikit.databinding.ActivityAddMedicineBinding;

import java.util.HashMap;
import java.util.Map;

import static com.project22.medikit.FirebaseAuth.LogInActivity.userID_email;
import static com.project22.medikit.FirebaseAuth.SignUpActivity.initialCollectionName;



public class AddMedicineActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    //view binding is used
    private ActivityAddMedicineBinding addMedicineBinding;

    //firebase
    private FirebaseFirestore firestore;
    private CollectionReference reference;

    //string for all the medicine type
    private String[] meditype = { "Tablet(s)", "Capsule(s)", "Syrup/Liquid", "Inject", "Inhale", "Vitamin(s)", "Dropper", "Suppositories", "Ointment", "Spray"};
    private String[] medicinetypeTV = { "piece(s)", "piece(s)", "spoon", "ml", "puff(s)", "pill(s)", "drop(s)", "pill(s)", "gm(s)", "puff(s)"};

    //Necessary medicine info to save in the firestore
    private String medicineName, medicineType, medicineDose, alarmTime, xtra;

    //alarm manager
    private AlarmManager alarmManager;
    private NotificationManagerCompat notificationManager;

    public static String medicineCollection = "Medicine";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMedicineBinding = ActivityAddMedicineBinding.inflate(getLayoutInflater());
        setContentView(addMedicineBinding.getRoot());
        setTitle("Add Medicine");


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);


        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection(initialCollectionName)
                .document(userID_email).collection(medicineCollection);

        //spinner for medicinetype
        Spinner medicinetypespinner = addMedicineBinding.addMedicinespinner;
        medicinetypespinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> spin = new ArrayAdapter(this, R.layout.spinner_item, meditype);
        spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicinetypespinner.setAdapter(spin);

        notificationManager = NotificationManagerCompat.from(this);

        addMedicineBinding.addMedicinesaveFB.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(addMedicineBinding.addMedicinesaveFB.equals(view)){
            medicineName = addMedicineBinding.addMedicinenameET.getText().toString().trim();
            medicineDose = addMedicineBinding.addMedicinedoseEt.getText().toString().trim() + " " + xtra;

            setAlarm();
            medicinedatasave();
        }
    }


    //spinner activity
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //Toast.makeText(this, meditype[position], Toast.LENGTH_SHORT).show();
        addMedicineBinding.addMedicinetypeET.setText(medicinetypeTV[position]);
        medicineType = meditype[position];
        xtra = medicinetypeTV[position];

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}



    //set alarm at the time from timepicker
    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        intent.putExtra("notification id", 0);
        intent.putExtra("medicineName", medicineName + "\n" + medicineDose);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,0,intent,0);

        Calendar calendar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    addMedicineBinding.addmedicinetimepicker.getHour(),
                    addMedicineBinding.addmedicinetimepicker.getMinute(),
                    0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,pendingIntent);

            Toast.makeText(this, "Alarm set!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Your device doesnot support alarm!", Toast.LENGTH_SHORT).show();


        }
    }



    //save data to firestore
    private void medicinedatasave() {

        picktime();
        if(medicineName.isEmpty() || medicineType.isEmpty() || medicineDose.isEmpty()){
           Toast.makeText(this, "error! " + "Please fill up the information!" , Toast.LENGTH_SHORT).show();
           return;
       } else {
            Map<String, Object> mediInfo = new HashMap<>();
            mediInfo.put("medicinename", medicineName);
            mediInfo.put("medicinetype", medicineType);
            mediInfo.put("medicinedose", medicineDose);
            mediInfo.put("time", alarmTime);

            reference.document().set(mediInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                       public void onSuccess(Void unused) {
                            Toast.makeText(AddMedicineActivity.this, "Saved medicine info!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddMedicineActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }

    }


    private void picktime() {
        Integer hour, minute;
        String am_pm;

        if (Build.VERSION.SDK_INT >= 23 ){
            hour = addMedicineBinding.addmedicinetimepicker.getHour();
            minute = addMedicineBinding.addmedicinetimepicker.getMinute();
        } else{
            hour = addMedicineBinding.addmedicinetimepicker.getCurrentHour();
            minute = addMedicineBinding.addmedicinetimepicker.getCurrentMinute();
        }

        if(hour > 12) {
            am_pm = "PM";
            hour = hour - 12;
        } else {
            am_pm="AM";
        }

        if(minute < 10 && hour < 10) {
            String time ="0"+ hour.toString() + ":" +"0"+ minute.toString() + am_pm;
            alarmTime = time;
        } else if(minute < 10 && hour >= 10){
            alarmTime = hour.toString() + ":" + "0" + minute.toString() + am_pm;
        } else if (minute >= 10 && hour < 10){
            alarmTime = "0" + hour.toString() + ":" + minute.toString() + am_pm;
        } else {
            alarmTime = hour.toString() + ":" + minute.toString() + am_pm;
        }

    }

    

}