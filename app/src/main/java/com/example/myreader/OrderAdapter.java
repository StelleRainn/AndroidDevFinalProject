package com.example.myreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {
    public OrderAdapter(Context context, List<Order> orders) {
        super(context, 0, orders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_item, parent, false);
        }

        TextView bookNameTextView = convertView.findViewById(R.id.bookNameTextView);
        TextView priceTextView = convertView.findViewById(R.id.priceTextView);
        TextView timeTextView = convertView.findViewById(R.id.timeTextView);
        Button viewBookButton = convertView.findViewById(R.id.viewBookButton);
        Button viewBuySellInfoButton = convertView.findViewById(R.id.viewBuySellInfoButton);




        final Order order = getItem(position);

        bookNameTextView.setText(order.getBookName());
        priceTextView.setText("¥" + order.getPrice());
        timeTextView.setText(order.getBuyTime());

        // hjf新加
        viewBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bookId = DatabaseHelper.getInstance(getContext()).getBookIdByName(order.getBookName());
                String username = ((OrderActivity) getContext()).getIntent().getStringExtra("username");
                Intent intent = new Intent(getContext(), ReadBookActivity.class);
                intent.putExtra("bookId", bookId);
                intent.putExtra("username", username);
                getContext().startActivity(intent);
            }
        });

        viewBuySellInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyerUsername = ((OrderActivity) getContext()).getIntent().getStringExtra("username");
                String sellerUsername = DatabaseHelper.getInstance(getContext()).getSellerByUsername(order.getBookName());
                if (sellerUsername != null) {
                    Toast.makeText(getContext(),
                            "卖家: " + sellerUsername + "\n" +
                                    "买家: " + buyerUsername,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "无法获取卖家信息", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }
}