package com.example.myreader;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

public class BookListActivity extends AppCompatActivity {
    private ListView bookListView;
    private Button searchButton;
    private Button uploadBookButton;
    private Button orderButton;
    private Button myBooksButton;
    private Button salesRecordButton;
    private Button switchAccountButton;
    private EditText searchEditText;
    private ImageView clearSearchButton;
    private TextView welcomeTextView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private String username;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        searchEditText = findViewById(R.id.searchEditText);
        clearSearchButton = findViewById(R.id.clearSearchButton);
        bookListView = findViewById(R.id.bookListView);
        searchButton = findViewById(R.id.searchButton);
        myBooksButton = findViewById(R.id.myBooksButton);
        uploadBookButton = findViewById(R.id.uploadBookButton);
        orderButton = findViewById(R.id.orderButton);
        salesRecordButton = findViewById(R.id.salesRecordButton);
        switchAccountButton = findViewById(R.id.switchAccountButton);

        welcomeTextView.setText("Welcome: " + username);

        bookList = DatabaseHelper.getInstance(getApplicationContext()).getAllBooks();
        bookAdapter = new BookAdapter(this, bookList, username);
        bookListView.setAdapter(bookAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString();
                if (!searchText.isEmpty()) {
                    List<Book> searchResult = DatabaseHelper.getInstance(getApplicationContext()).searchBooks(searchText);
                    bookAdapter.updateBooks(searchResult);
                    clearSearchButton.setVisibility(View.VISIBLE);
                } else {
                    bookAdapter.updateBooks(DatabaseHelper.getInstance(getApplicationContext()).getAllBooks());
                    clearSearchButton.setVisibility(View.GONE);
                }
            }
        });

        clearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                bookAdapter.updateBooks(DatabaseHelper.getInstance(getApplicationContext()).getAllBooks());
                clearSearchButton.setVisibility(View.GONE);
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

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = bookList.get(position);
                if (book.isFree() || DatabaseHelper.getInstance(getApplicationContext()).hasBoughtBook(username, book.getId())) {
                    Intent intent = new Intent(BookListActivity.this, ReadBookActivity.class);
                    intent.putExtra("bookId", book.getId());
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(BookListActivity.this, BuyBookActivity.class);
                    intent.putExtra("bookId", book.getId());
                    intent.putExtra("bookName", book.getName());
                    intent.putExtra("bookPrice", book.getPrice());
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearSearchButton.setVisibility(View.VISIBLE);
                } else {
                    clearSearchButton.setVisibility(View.GONE);
                    bookAdapter.updateBooks(DatabaseHelper.getInstance(getApplicationContext()).getAllBooks());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookList = DatabaseHelper.getInstance(getApplicationContext()).getAllBooks();
                bookAdapter.updateBooks(bookList);
                Toast.makeText(BookListActivity.this, "成功刷新", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            bookList = DatabaseHelper.getInstance(getApplicationContext()).getAllBooks();
            bookAdapter.updateBooks(bookList);
        }
    }
}