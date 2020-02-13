package com.gwinto.uzalama;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdaptor extends BaseAdapter {
    ArrayList<HistoryItem> historyItems;
    Context context;

    public HistoryAdaptor(Context context, ArrayList<HistoryItem> items){
        this.historyItems = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return historyItems.size();
    }

    @Override
    public HistoryItem getItem(int position) {
        return historyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.history_item,parent,false);

        TextView name_txt = convertView.findViewById(R.id.hist_fullname);
        TextView phone_txt = convertView.findViewById(R.id.hist_phone);
        TextView message_txt = convertView.findViewById(R.id.hist_message);
        TextView timestamp_txt = convertView.findViewById(R.id.hist_timestamp);

        HistoryItem histItem = getItem(position);
        String fullName = histItem.getFname()+ " "+ histItem.getOnames();
        name_txt.setText(fullName);
        message_txt.setText(histItem.getMessage());
        timestamp_txt.setText(histItem.getTimestamp());
        phone_txt.setText(histItem.getPhone());

        return  convertView;
    }
}
