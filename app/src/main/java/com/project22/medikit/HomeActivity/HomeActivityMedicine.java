package com.project22.medikit.HomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project22.medikit.AddEditMedicine.AddMedicineActivity;
import com.project22.medikit.AddEditMedicine.EditMedicineActivity;
import com.project22.medikit.DataModels.Medicine;
import com.project22.medikit.Dialogues.LogOutConfirmationDialogue;
import com.project22.medikit.FirebaseAuth.LogInActivity;
import com.project22.medikit.R;
import com.project22.medikit.RecyclerViewAdapter.MedicineViewAdapter;
import com.project22.medikit.databinding.ActivityHomeMedicineBinding;

import static com.project22.medikit.FirebaseAuth.LogInActivity.userID_email;


public class HomeActivityMedicine extends AppCompatActivity
implements View.OnClickListener,
        LogOutConfirmationDialogue.LogOutConfirmationDialogueListener{

    //using viewbinding
    private ActivityHomeMedicineBinding homeMedicineBinding;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private CollectionReference reference;


    //Adapter
    private MedicineViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeMedicineBinding = ActivityHomeMedicineBinding.inflate(getLayoutInflater());
        setContentView(homeMedicineBinding.getRoot());
        setTitle("Home");


        //firebase initialize
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("Users");

        //check user
        checkuser();

        //setuprecyclerview
        setupRecyclerview();

        homeMedicineBinding.homemedicinefloatingbutton.setOnClickListener(this);

    }



    //if user is already logged in or not
    //and set the userID_email
    private void checkuser() {
        if (firebaseAuth.getCurrentUser() != null) {
            userID_email = firebaseAuth.getCurrentUser().getEmail();
            Toast.makeText(this, "Home: " + userID_email, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(HomeActivityMedicine.this, LogInActivity.class));
            finish();
        }
    }

    //On click listeners
    @Override
    public void onClick(View view) {
        if(homeMedicineBinding.homemedicinefloatingbutton.equals(view)){
            startActivity(new Intent(HomeActivityMedicine.this, AddMedicineActivity.class));
        } else{}
    }



    //toolbar menu inflate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        return true;
    }


    //menu item listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_itemLogout) {
            LogOutConfirmationDialogue dialogue = new LogOutConfirmationDialogue();
            dialogue.show(getSupportFragmentManager(), "LogOut Dialogue");
        } else {}
        return super.onOptionsItemSelected(item);
    }

    //logout function
    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(HomeActivityMedicine.this, LogInActivity.class));
        finish();
    }

    @Override
    public void logoutconfirmation(boolean state) {
        if(state){
            logout();
            Toast.makeText(this, "logout!", Toast.LENGTH_SHORT).show();
            state = false;
        }
    }



    private void setupRecyclerview() {
        Query query = reference.document(userID_email).collection("Medicine").orderBy("time", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Medicine> options = new FirestoreRecyclerOptions.Builder<Medicine>()
                .setQuery(query, Medicine.class)
                .build();

        adapter = new MedicineViewAdapter(options);
        RecyclerView recyclerView = homeMedicineBinding.homemedicineRecyclerview;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivityMedicine.this);

                builder.setTitle("Are you sure you want to delete this medicine?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.deleteItem(viewHolder.getAdapterPosition());
                                //adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                Toast.makeText(HomeActivityMedicine.this, "Deleted!", Toast.LENGTH_SHORT).show();;

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.notifyDataSetChanged();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        }).attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(new MedicineViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String id = documentSnapshot.getId();
                Medicine editItem = documentSnapshot.toObject(Medicine.class);
                Intent intent = new Intent(HomeActivityMedicine.this, EditMedicineActivity.class);
                intent.putExtra("medicineName", editItem.getMedicinename());
                intent.putExtra("medicineDose", editItem.getMedicinedose());
                intent.putExtra("medicineType", editItem.getMedicinetype());
                intent.putExtra("time", editItem.getTime());
                intent.putExtra("id", id);

                startActivity(intent);

                Toast.makeText(HomeActivityMedicine.this, "Position: " + position + " ID: " + id, Toast.LENGTH_SHORT).show();
            }
        });

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

}