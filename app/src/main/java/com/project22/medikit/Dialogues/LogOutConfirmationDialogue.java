package com.project22.medikit.Dialogues;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.project22.medikit.R;

public class LogOutConfirmationDialogue extends AppCompatDialogFragment {

    private LogOutConfirmationDialogueListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_layout, null);

        builder.setView(view)
                .setTitle(R.string.logoutconfirmation)
                .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.logoutconfirmation(true);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.logoutconfirmation(false);
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (LogOutConfirmationDialogueListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement confirmation dialogue listener");
        }
    }

    public interface LogOutConfirmationDialogueListener{
        void logoutconfirmation(boolean state);

    }
}
