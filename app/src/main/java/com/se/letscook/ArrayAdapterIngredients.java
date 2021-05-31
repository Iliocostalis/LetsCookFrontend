package com.se.letscook;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.se.letscook.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class ArrayAdapterIngredients extends ArrayAdapter<Ingredient> {

    private ListView listView;
    public List<Ingredient> itemList = new ArrayList<>();
    private Context context;

    // Enthält alle Elemente eines Listenelements
    static class ViewHolder {
        Button itemButton;
        TextView itemTextView;
    }

    public ArrayAdapterIngredients(Context context, int textViewResourceId, ListView listView) {
        super(context, textViewResourceId);
        this.context = context;
        this.listView = listView;
    }

    @Override
    public void clear(){
        super.clear();
        itemList.clear();
    }

    @Override
    public void add(Ingredient object) {
        itemList.add(object);
        super.add(object);
        setListViewHeight(listView);
    }

    @Override
    public int getCount() {
        return this.itemList.size();
    }

    @Override
    public Ingredient getItem(int index) {
        return this.itemList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Layout eines Listenelements laden und erstellen
        View row = convertView;
        ViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_layout_ingredients, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemButton = (Button) row.findViewById(R.id.button);
            viewHolder.itemTextView = (TextView) row.findViewById(R.id.textView);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)row.getTag();
        }

        Ingredient item = getItem(position);

        // Layout mit dem Inhalt "befüllen" und dem button eine Funktionalität geben

        viewHolder.itemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {
                        remove(position);
                    }
                });

        viewHolder.itemTextView.setText(item.getAmount() + " " + item.getUnit() + " " + item.getName());
        return row;
    }

    private void remove(int pos){
        // Modifikationen an der Liste, müssen im Main Thread erfolgen
        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                itemList.remove(pos);
                notifyDataSetChanged();
                setListViewHeight(listView);
            }
        };
        mainHandler.post(myRunnable);
    }

    // Die höhe der Liste muss manuell berechnet werden, da die automatisch gewählte Höhe nicht unseren
    // vorstellungen entsprach
    public void setListViewHeight(ListView myListView) {
        ListAdapter adapter = myListView.getAdapter();

        ViewGroup.LayoutParams params = myListView.getLayoutParams();

        int amount = adapter.getCount();
        if(amount != 0){
            View item = adapter.getView(0, null, myListView);
            item.measure(0, 0);

            params.height = item.getMeasuredHeight() * amount + myListView.getDividerHeight() * (amount - 1);
        }else{
            params.height = 0;
        }

        myListView.setLayoutParams(params);
    }
}

