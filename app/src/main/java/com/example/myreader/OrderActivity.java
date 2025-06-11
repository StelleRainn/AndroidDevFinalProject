package com.example.myreader;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private ListView orderListView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        username = getIntent().getStringExtra("username");

        orderListView = findViewById(R.id.orderListView);

        orderList = DatabaseHelper.getInstance(getApplicationContext()).getOrders(username);
        orderAdapter = new OrderAdapter(this, orderList);
        orderListView.setAdapter(orderAdapter);
    }
}