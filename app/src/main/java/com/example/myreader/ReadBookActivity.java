package com.example.myreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReadBookActivity extends AppCompatActivity {
    private PDFView pdfView;
    private Button commentButton;
    private Button downloadButton;
    private int bookId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        bookId = getIntent().getIntExtra("bookId", 0);
        username = getIntent().getStringExtra("username");

        pdfView = findViewById(R.id.pdfView);
        commentButton = findViewById(R.id.commentButton);
        downloadButton = findViewById(R.id.downloadButton);

        // 异步加载 PDF 文件
        new LoadBookContentTask().execute();

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadBookActivity.this, CommentActivity.class);
                intent.putExtra("bookId", bookId);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookFilePath = DatabaseHelper.getInstance(getApplicationContext()).getBookFilePath(bookId, username);
                if (bookFilePath != null) {
                    Toast.makeText(ReadBookActivity.this, "下载开始", Toast.LENGTH_SHORT).show();
                    new DownloadBookTask().execute(bookFilePath);
                } else {
                    Toast.makeText(ReadBookActivity.this, "无法获取图书文件路径", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class LoadBookContentTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return DatabaseHelper.getInstance(getApplicationContext()).getBookFilePath(bookId, username);
        }

        @Override
        protected void onPostExecute(String filePath) {
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    pdfView.fromFile(file)
                            .enableSwipe(true)
                            .enableDoubletap(true)
                            .defaultPage(0)
                            .load();
                } else {
                    Toast.makeText(ReadBookActivity.this, "PDF 文件不存在", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ReadBookActivity.this, "无法加载 PDF 文件", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadBookTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... filePaths) {
            String sourcePath = filePaths[0];
            File sourceFile = new File(sourcePath);
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }
            String fileName = sourceFile.getName() + ".pdf";
            File targetFile = new File(downloadDir, fileName);
            try {
                InputStream inputStream = new FileInputStream(sourceFile);
                OutputStream outputStream = new FileOutputStream(targetFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
                return targetFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(ReadBookActivity.this, "下载成功，文件保存至: " + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ReadBookActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfView != null) {
            pdfView.recycle();
        }
    }
}