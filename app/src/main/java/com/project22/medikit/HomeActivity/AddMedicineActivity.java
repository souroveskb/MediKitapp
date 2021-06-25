package com.project22.medikit.HomeActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.project22.medikit.R;
import com.project22.medikit.databinding.ActivityAddMedicineBinding;

import static com.project22.medikit.FirebaseAuth.LogInActivity.userID_email;

public class AddMedicineActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    //view binding is used
    private ActivityAddMedicineBinding addMedicineBinding;

    //string for all the medicine type
    private String[] medicinetype = { "Tablet(s)", "Capsule(s)", "Syrup/Liquid", "Inject", "Inhale", "Vitamin(s)", "Drops", "Suppositories", "Ointment"};
    private String[] medicinetypeTV = { "Tablet(s)", "Capsule(s)", "Spoon", "Injection", "Inhaler", "Vitamin(s)", "Drops", "times", "times"};

    //Necessary medicine info to save in the firestore
    private String medicineName, medicineType, medicineDose, alarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMedicineBinding = ActivityAddMedicineBinding.inflate(getLayoutInflater());
        setContentView(addMedicineBinding.getRoot());
        setTitle("Add Medicine");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //spinner for medicinetype
        Spinner medicinetypespinner = addMedicineBinding.addMedicinespinner;
        ArrayAdapter<String> spin = new ArrayAdapter(this, R.layout.spinner_item, medicinetype);
        spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicinetypespinner.setAdapter(spin);
        medicinetypespinner.setOnItemSelectedListener(this);
        
        addMedicineBinding.addMedicinesaveFB.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(addMedicineBinding.addMedicinesaveFB.equals(view)){
            medicinedatasave();
            Toast.makeText(this, "Saved!"+ "\n" + userID_email + "\n" + medicineName + "\n"+  medicineType + "\n" + medicineDose, Toast.LENGTH_SHORT).show();
        }

    }

    private void medicinedatasave() {
        medicineName = addMedicineBinding.addMedicinenameET.getText().toString().trim();
        medicineDose = addMedicineBinding.addMedicinedoseEt.getText().toString().trim();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(this, medicinetype[position], Toast.LENGTH_SHORT).show();
        addMedicineBinding.addMedicinetypeTV.setText(medicinetypeTV[position]);
        medicineType = medicinetype[position];

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}