package com.example.myreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BookListActivity extends AppCompatActivity {
    private Button libraryButton;
    private Button uploadBookButton;
    private Button orderButton;
    private Button myBooksButton;
    private Button salesRecordButton;
    private Button switchAccountButton;
    private TextView welcomeTextView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        welcomeTextView = findViewById(R.id.welcomeTextView);
        libraryButton = findViewById(R.id.libraryButton);
        myBooksButton = findViewById(R.id.myBooksButton);
        uploadBookButton = findViewById(R.id.uploadBookButton);
        orderButton = findViewById(R.id.orderButton);
        salesRecordButton = findViewById(R.id.salesRecordButton);
        switchAccountButton = findViewById(R.id.switchAccountButton);

        welcomeTextView.setText("Welcome: " + username);

        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookListActivity.this, LibraryActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookListActivity.this, MyPublishedBooksActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        uploadBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookListActivity.this, UploadBookActivity.class);
                intent.putExtra("username", username);
                startActivityForResult(intent, 1);
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookListActivity.this, OrderActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        salesRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookListActivity.this, SalesRecordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        switchAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 上传图书后无需刷新列表，因为列表已迁移到 LibraryActivity
            Toast.makeText(this, "图书上传成功", Toast.LENGTH_SHORT).show();
        }
    }
}