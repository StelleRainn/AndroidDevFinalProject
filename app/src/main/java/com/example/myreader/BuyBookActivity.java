package com.example.myreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BuyBookActivity extends AppCompatActivity {
    private TextView bookNameTextView;
    private TextView bookPriceTextView;
    private Button buyButton;
    private int bookId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_book);

        bookNameTextView = findViewById(R.id.bookNameTextView);
        bookPriceTextView = findViewById(R.id.bookPriceTextView);
        buyButton = findViewById(R.id.buyButton);

        bookId = getIntent().getIntExtra("bookId", 0);
        String bookName = getIntent().getStringExtra("bookName");
        double bookPrice = getIntent().getDoubleExtra("bookPrice", 0.0);
        username = getIntent().getStringExtra("username");

        bookNameTextView.setText(bookName);
        bookPriceTextView.setText("价格: " + bookPrice);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 购买图书
                if (DatabaseHelper.getInstance(getApplicationContext()).buyBook(username, bookId)) {
                    Toast.makeText(BuyBookActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BuyBookActivity.this, ReadBookActivity.class);
                    intent.putExtra("bookId", bookId);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BuyBookActivity.this, "购买失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}