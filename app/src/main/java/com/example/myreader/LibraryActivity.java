package com.example.myreader;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class LibraryActivity extends AppCompatActivity {
    private ListView bookListView;
    private Button searchButton;
    private EditText searchEditText;
    private ImageView clearSearchButton;
    private TextView titleTextView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private String username;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        titleTextView = findViewById(R.id.titleTextView);
        searchEditText = findViewById(R.id.searchEditText);
        clearSearchButton = findViewById(R.id.clearSearchButton);
        bookListView = findViewById(R.id.bookListView);
        searchButton = findViewById(R.id.searchButton);

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

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = bookList.get(position);
                if (book.isFree() || DatabaseHelper.getInstance(getApplicationContext()).hasBoughtBook(username, book.getId())) {
                    Intent intent = new Intent(LibraryActivity.this, ReadBookActivity.class);
                    intent.putExtra("bookId", book.getId());
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LibraryActivity.this, BuyBookActivity.class);
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
                Toast.makeText(LibraryActivity.this, "成功刷新", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}