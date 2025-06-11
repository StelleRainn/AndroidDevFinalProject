package com.example.myreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private EditText commentEditText;
    private Button postButton;
    private ListView commentListView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private int bookId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        bookId = getIntent().getIntExtra("bookId", 0);
        username = getIntent().getStringExtra("username");

        commentEditText = findViewById(R.id.commentEditText);
        postButton = findViewById(R.id.postButton);
        commentListView = findViewById(R.id.commentListView);

        commentList = DatabaseHelper.getInstance(getApplicationContext()).getComments(bookId);
        commentAdapter = new CommentAdapter(this, commentList);
        commentListView.setAdapter(commentAdapter);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = commentEditText.getText().toString();

                if (content.isEmpty()) {
                    Toast.makeText(CommentActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 发布评论
                if (DatabaseHelper.getInstance(getApplicationContext()).postComment(username, bookId, content)) {
                    commentList.add(new Comment(username, content, System.currentTimeMillis()));
                    commentAdapter.notifyDataSetChanged();
                    commentEditText.setText("");
                    Toast.makeText(CommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}