package com.project22.medikit.HomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project22.medikit.DataModels.User;
import com.project22.medikit.FirebaseAuth.LogInActivity;
import com.project22.medikit.R;
import com.project22.medikit.RecyclerViewAdapter.UserAdapter;
import com.project22.medikit.databinding.ActivityHomeBinding;

import java.util.Queue;

import static com.project22.medikit.FirebaseAuth.LogInActivity.userID_email;
import static com.project22.medikit.FirebaseAuth.SignUpActivity.collectionName;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    //view binding to bind the views
    private ActivityHomeBinding homeBinding;

    //firebase
    private FirebaseAuth firebaseAuth ;
    private FirebaseFirestore firestore;
    private CollectionReference reference;

    //Adapter
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());
        setTitle("Home");

        //firebase initialize
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection(collectionName);

        //check user
        checkuser();

        //setuprecyclerview
        setupRecyclerview();

        homeBinding.homefloatingbutton.setOnClickListener(this);

    }

    private void setupRecyclerview() {
        Query query = reference.orderBy("age", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        adapter = new UserAdapter(options);
        RecyclerView recyclerView = homeBinding.homeRecyclerview;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    private void checkuser() {
        if(firebaseAuth.getCurrentUser() != null){
            Toast.makeText(this, "Home: " + userID_email, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(HomeActivity.this, LogInActivity.class));
            finish();
        }
    }

    //onclick listener method
    @Override
    public void onClick(View view) {
        if(homeBinding.homefloatingbutton.equals(view)){
            startActivity(new Intent(HomeActivity.this, AddMedicineActivity.class));
        }else {}

    }





    //toolbar menu onitem select
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        return true;
    }

    //menu item listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.menu_itemAdd){
            Toast.makeText(this, "addmenuclicked!", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.menu_itemSettings){
            Toast.makeText(this, "settingsclicked!", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.menu_itemLogout){
            Toast.makeText(this, "logoutmenu!", Toast.LENGTH_SHORT).show();
            logout();
        } else if(id == R.id.menu_itemGenerate){
            Toast.makeText(this, "generatemenuclicked!", Toast.LENGTH_SHORT).show();
        } else{}
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(HomeActivity.this, LogInActivity.class));
        finish();
    }

}