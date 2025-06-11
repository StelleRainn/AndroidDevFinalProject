package com.example.myreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SalesRecordAdapter extends ArrayAdapter<SalesRecord> {
    public SalesRecordAdapter(Context context, List<SalesRecord> salesRecords) {
        super(context, 0, salesRecords);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sales_record_item, parent, false);
        }

        TextView bookNameTextView = convertView.findViewById(R.id.bookNameTextView);
        TextView priceTextView = convertView.findViewById(R.id.priceTextView);
        TextView buyerTextView = convertView.findViewById(R.id.buyerTextView);
        TextView timeTextView = convertView.findViewById(R.id.timeTextView);

        SalesRecord record = getItem(position);

        bookNameTextView.setText(record.getBookName());
        priceTextView.setText("¥" + record.getPrice());
        buyerTextView.setText("买家: " + record.getBuyerUsername());
        timeTextView.setText(record.getBuyTime());

        return convertView;
    }
}