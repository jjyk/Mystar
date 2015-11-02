package com.example.kmit.mystartest;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by evgenn on 12.03.2015.
 */
public class MyDialog extends DialogFragment implements View.OnClickListener {
    Button ok, cancell;
    EditText editText;
    String number;
    Communicator communicator;


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator= (Communicator) activity;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_dialog, null);
        ok = (Button) view.findViewById(R.id.okDialogModule);
        cancell = (Button) view.findViewById(R.id.cancellDialogModule);
        ok.setOnClickListener(this);
        cancell.setOnClickListener(this);

        setCancelable(false);
        getDialog().setTitle("Введите номер места");
        editText = (EditText) view.findViewById(R.id.editTextWindowModule);

        return view;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.okDialogModule)
        {
            if (editText.getText().toString() != null)
                number = editText.getText().toString();
            else
                number = "0";

            communicator.onDialogMessage(true, number);
            dismiss();
        }
        else
        {
            if (editText.getText().toString() != null)
                number = editText.getText().toString();
            else
                number = "0";
            communicator.onDialogMessage(false, number);
            dismiss();
        }
    }

    interface Communicator {
        public void onDialogMessage(boolean param, String mess);
    }


}
