package com.se.letscook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.se.letscook.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class ArrayAdapterAllRecipes extends ArrayAdapter<Recipe> {

    public List<Recipe> itemList = new ArrayList<>();

    // Enthält alle Elemente eines Listenelements
    static class ViewHolder {
        ImageView itemImg;
        TextView itemTitle;
        TextView itemDescription;
    }

    public ArrayAdapterAllRecipes(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void clear(){
        super.clear();
        itemList.clear();
    }

    @Override
    public void add(Recipe object) {
        itemList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.itemList.size();
    }

    @Override
    public Recipe getItem(int index) {
        return this.itemList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Layout eines Listenelements laden und erstellen
        View row = convertView;
        ViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_layout_all_recipes, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemImg = (ImageView) row.findViewById(R.id.image);
            viewHolder.itemTitle = (TextView) row.findViewById(R.id.title);
            viewHolder.itemDescription = (TextView) row.findViewById(R.id.description);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)row.getTag();
        }
        // Layout mit dem Inhalt "befüllen"
        Recipe item = getItem(position);
        if(item != null) {
            viewHolder.itemImg.setImageBitmap(item.getImage());
            viewHolder.itemTitle.setText(item.getTitle());
            viewHolder.itemDescription.setText(item.getDescription());
        }

        return row;
    }
}

