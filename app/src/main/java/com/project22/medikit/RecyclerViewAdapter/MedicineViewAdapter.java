package com.project22.medikit.RecyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project22.medikit.DataModels.Medicine;
import com.project22.medikit.R;

public class MedicineViewAdapter extends FirestoreRecyclerAdapter <Medicine, MedicineViewAdapter.MedicineHolder> {

    private OnItemClickListener listener;
    String id;
    int pos;

    public MedicineViewAdapter(@NonNull FirestoreRecyclerOptions<Medicine> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MedicineViewAdapter.MedicineHolder holder, int position, @NonNull Medicine model) {
        holder.medicinename.setText(model.getMedicinename());
        holder.medicinedose.setText(model.getMedicinedose());
        holder.medicinetype.setText(model.getMedicinetype());
        holder.time.setText(model.getTime());
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_item, parent, false);
        return new MedicineHolder(view);
    }

    class MedicineHolder extends RecyclerView.ViewHolder{

        private TextView medicinename;
        private TextView medicinetype;
        private TextView medicinedose;
        private TextView time;
        private ImageView edit;


        public MedicineHolder(@NonNull View itemView) {
            super(itemView);
            medicinename = itemView.findViewById(R.id.itemmedicinenameTV);
            medicinetype = itemView.findViewById(R.id.itemmedicinetypeTV);
            medicinedose = itemView.findViewById(R.id.itemmedicinedoseTV);
            time = itemView.findViewById(R.id.time_alarm);
            edit = itemView.findViewById(R.id.ivitemedit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = getAdapterPosition();
                    id = getSnapshots().getSnapshot(pos).getId();
                    if(pos != RecyclerView.NO_POSITION && listener != null){
                        if(edit.getVisibility() == View.GONE) {
                            edit.setVisibility(View.VISIBLE);
                        } else {
                            edit.setVisibility(View.GONE);
                        }
                    }
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    Medicine medicine = getSnapshots().getSnapshot(pos).toObject(Medicine.class);
                    //Toast.makeText(itemView.getContext(), "Edit Data!" + medicine.getMedicinename(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
