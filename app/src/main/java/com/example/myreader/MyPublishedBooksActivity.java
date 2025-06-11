package com.example.myreader;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

public class MyPublishedBooksActivity extends AppCompatActivity {
    private ListView myPublishedBooksListView;
    private MyPublishedBooksAdapter adapter;
    private String username;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Book> myPublishedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_published_books);

        username = getIntent().getStringExtra("username");
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        myPublishedBooksListView = findViewById(R.id.myPublishedBooksListView);

        loadBooks();

        // 添加滚动监听，控制下拉刷新
        myPublishedBooksListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = firstVisibleItem == 0 && view.getChildAt(0) != null && view.getChildAt(0).getTop() == 0;
                swipeRefreshLayout.setEnabled(enable);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadBooks();
        });
    }

    private void loadBooks() {
        myPublishedBooks = DatabaseHelper.getInstance(getApplicationContext()).getMyPublishedBooks(username);
        if (adapter == null) {
            adapter = new MyPublishedBooksAdapter(this, myPublishedBooks, username);
            myPublishedBooksListView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(myPublishedBooks);
            adapter.notifyDataSetChanged();
        }
        Toast.makeText(MyPublishedBooksActivity.this, "已刷新书籍列表", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }
}