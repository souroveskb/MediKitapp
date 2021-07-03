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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project22.medikit.AlarmReceiver.AlarmBroadcastReceiver;
import com.project22.medikit.R;
import com.project22.medikit.databinding.ActivityEditMedicineBinding;

import java.util.HashMap;
import java.util.Map;

import static com.project22.medikit.AddEditMedicine.AddMedicineActivity.medicineCollection;
import static com.project22.medikit.FirebaseAuth.LogInActivity.userID_email;
import static com.project22.medikit.FirebaseAuth.SignUpActivity.initialCollectionName;

public class EditMedicineActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener{

    //view bindind
    private ActivityEditMedicineBinding editMedicineBinding;

    //firebase
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;

    //string for all the medicine type
    private String[] meditype = { "Tablet(s)", "Capsule(s)", "Syrup/Liquid", "Inject", "Inhale", "Vitamin(s)", "Dropper", "Suppositories", "Ointment", "Spray"};
    private String[] medicinetypeTV = { "piece(s)", "piece(s)", "spoon", "ml", "puff(s)", "pill(s)", "drop(s)", "pill(s)", "gm(s)", "puff(s)"};

    //alarm manager
    private AlarmManager alarmManager;
    private NotificationManagerCompat notificationManager;

    //information from intent
    private String medicineName = "", medicineDose = "", medicineType = "", time = "", id = "", xtra = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editMedicineBinding = ActivityEditMedicineBinding.inflate(getLayoutInflater());
        setContentView(editMedicineBinding.getRoot());
        setTitle("Edit Medicine");

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);


        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection(initialCollectionName)
                .document(userID_email);

        Intent intent = getIntent();
        medicineName = intent.getStringExtra("medicineName");
        medicineDose = intent.getStringExtra("medicineDose");
        medicineType = intent.getStringExtra("medicineType");
        time = intent.getStringExtra("time");
        id = intent.getStringExtra("id");
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();

        setpreviousinfo();

        //spinner for medicinetype
        Spinner medicinetypespinner = editMedicineBinding.editMedicinespinner;
        medicinetypespinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> spin = new ArrayAdapter(this, R.layout.spinner_item, meditype);
        spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicinetypespinner.setAdapter(spin);

        editMedicineBinding.editMedicinesaveFB.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        medicineName = editMedicineBinding.editMedicinenameET.getText().toString().trim();
        medicineDose = editMedicineBinding.editMedicinedoseEt.getText().toString().trim() + " " + xtra;

        setAlarm();
        updateMedicine();
        Toast.makeText(this, "Data " + medicineName+ medicineDose + medicineType, Toast.LENGTH_SHORT).show();
    }

    private void setpreviousinfo() {
        editMedicineBinding.editMedicinenameET.setText(medicineName);
        editMedicineBinding.editMedicinetypeET.setText(medicineType);
        medicineDose = medicineDose.replaceAll("\\s.*", "");
        editMedicineBinding.editMedicinedoseEt.setText(medicineDose);
    }

    private void updateMedicine() {
        picktime();
        if(medicineName.isEmpty() || medicineType.isEmpty() || medicineDose.isEmpty()){
            Toast.makeText(this, "error! " + "Please fill up the information!" , Toast.LENGTH_SHORT).show();
            return;
        } else {
            Map<String, Object> mediInfo = new HashMap<>();
            mediInfo.put("medicinename", medicineName);
            mediInfo.put("medicinetype", medicineType);
            mediInfo.put("medicinedose", medicineDose);
            mediInfo.put("time", time);

            documentReference.collection(medicineCollection).document(id).update(mediInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditMedicineActivity.this, "Updated medicine info!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditMedicineActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    private void picktime() {
        Integer hour, minute;
        String am_pm;

        if (Build.VERSION.SDK_INT >= 23 ){
            hour = editMedicineBinding.editmedicinetimepicker.getHour();
            minute = editMedicineBinding.editmedicinetimepicker.getMinute();
        } else{
            hour = editMedicineBinding.editmedicinetimepicker.getCurrentHour();
                minute = editMedicineBinding.editmedicinetimepicker.getCurrentMinute();
        }

        if(hour > 12) {
            am_pm = "PM";
            hour = hour - 12;
        } else {
            am_pm="AM";
        }

        if(minute < 10 && hour < 10) {
            time ="0"+ hour.toString() + ":" +"0"+ minute.toString() + am_pm;
        } else if(minute < 10 && hour >= 10){
            time = hour.toString() + ":" + "0" + minute.toString() + am_pm;
        } else if (minute >= 10 && hour < 10){
            time = "0" + hour.toString() + ":" + minute.toString() + am_pm;
        } else {
            time = hour.toString() + ":" + minute.toString() + am_pm;
        }

    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        intent.putExtra("notification id", 0);
        intent.putExtra("medicineName", medicineName + " ->" + medicineDose);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,0,intent,0);

        Calendar calendar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    editMedicineBinding.editmedicinetimepicker.getHour(),
                    editMedicineBinding.editmedicinetimepicker.getMinute(),
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this, meditype[position], Toast.LENGTH_SHORT).show();
        editMedicineBinding.editMedicinetypeET.setText(medicinetypeTV[position]);
        medicineType = meditype[position];
        xtra = medicinetypeTV[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}