package com.project22.medikit.RecyclerViewAdapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.project22.medikit.DataModels.User;
import com.project22.medikit.R;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.UserHolder> {


    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull  UserAdapter.UserHolder holder, int position, @NonNull User model) {
        holder.nameTV.setText(model.getName());
        holder.emailTV.setText(model.getEmail());
        holder.ageTV.setText(model.getAge());
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserHolder(view);
    }

    class UserHolder extends RecyclerView.ViewHolder{

        private TextView nameTV;
        private TextView ageTV;
        private TextView emailTV;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.name_item);
            ageTV = itemView.findViewById(R.id.age_item);
            emailTV = itemView.findViewById(R.id.email_item);
        }
    }
}
