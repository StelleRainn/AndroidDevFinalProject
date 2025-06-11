package com.example.myreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        myPublishedBooksListView = findViewById(R.id.myPublishedBooksListView);

        loadBooks();//初始加载数据

//        设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(()->{
            //重新加数据
            loadBooks();
        });

//        // 获取用户发布的书籍
//        List<Book> myPublishedBooks = DatabaseHelper.getInstance(getApplicationContext())
//                .getMyPublishedBooks(username);
//
//        // 设置适配器
//        adapter = new MyPublishedBooksAdapter(this, myPublishedBooks, username);
//        myPublishedBooksListView.setAdapter(adapter);

    }

    private void loadBooks(){
        myPublishedBooks=DatabaseHelper.getInstance(getApplicationContext())
                .getMyPublishedBooks(username);
        if(adapter==null){
            adapter=new MyPublishedBooksAdapter(this,myPublishedBooks,username);
            myPublishedBooksListView.setAdapter(adapter);
        }
        else {
            adapter.clear();
            adapter.addAll(myPublishedBooks);
            adapter.notifyDataSetChanged();
        }
        Toast.makeText(MyPublishedBooksActivity.this, "已刷新书籍列表", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);//结束刷新数据
    }
}