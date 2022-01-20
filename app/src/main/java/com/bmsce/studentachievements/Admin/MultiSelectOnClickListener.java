package com.bmsce.studentachievements.Admin;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;


public class MultiSelectOnClickListener implements View.OnClickListener{

    private Context context;
    private String[] items;
    private boolean[] selectedItems;
    private String title;
    private TextView textView;
    private AlertDialog.Builder builder;

    public MultiSelectOnClickListener(String title, Context context, String[] items, TextView textView) {

        this.context = context;
        this.items = items;
        this.selectedItems = new boolean[items.length];
        this.title = title;
        this.textView = textView;

        builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);

        builder.setMultiChoiceItems(items, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    selectedItems[i] = true;
                } else {
                    selectedItems[i] = false;
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                StringBuilder stringBuilder = new StringBuilder();

                for (int j = 0; j < selectedItems.length; j++) {
                    if(selectedItems[j]) {
                        stringBuilder.append(items[j]);
                        stringBuilder.append(", ");
                    }
                }
                if(stringBuilder.length() > 2) {
                    stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length()-1); // removing last ", "
                }
                // set text on textView
                textView.setText(stringBuilder.toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("Select All", selectAllOnClickListener);
    }

    public boolean[] getSelectedItems() {
        return selectedItems;
    }

    public ArrayList<String> getSelectedItemNames() {
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < items.length; ++i) {
            if(selectedItems[i]) {
                list.add(items[i]);
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        builder.show();
    }


    private DialogInterface.OnClickListener selectAllOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            Arrays.fill(selectedItems, true);

            StringBuilder stringBuilder = new StringBuilder();
            for (int k = 0; k < items.length; k++) {
                stringBuilder.append(items[k]);
                stringBuilder.append(", ");
            }

            if(stringBuilder.length() > 2) {
                stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length()-1); // removing last ", "
            }

            textView.setText(stringBuilder);
            builder.setNeutralButton("Clear All", clearAllOnClickListener);
            builder.show();
        }
    };

    private DialogInterface.OnClickListener clearAllOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            Arrays.fill(selectedItems, false);
            textView.setText("");

            builder.setNeutralButton("Select All", selectAllOnClickListener);
            builder.show();
        }
    };
}
