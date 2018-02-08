package com.engatway.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.engatway.classes.Utilizador;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diogo on 07/02/2018.
 */

public class MyClassAdapter extends ArrayAdapter<Utilizador> {


    private TextView itemView;

    private List<Utilizador> suggestions;
    private List<Utilizador> itemsAll;
    private List<Utilizador> items;

    public MyClassAdapter(Context context, int textViewResourceId, List<Utilizador> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.itemsAll = new ArrayList<>(items);
        this.suggestions = new ArrayList<>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);

        }

        Utilizador item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            itemView = (TextView)convertView.findViewById(android.R.id.text1);
            itemView.setText(item.getName());
        }


        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Utilizador)(resultValue)).getName();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Utilizador utilizador : itemsAll) {
                    if(utilizador.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(utilizador);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Utilizador> filteredList = (ArrayList<Utilizador>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Utilizador c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };


}
