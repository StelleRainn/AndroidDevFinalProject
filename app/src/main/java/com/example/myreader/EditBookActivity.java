package com.example.myreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class EditBookActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText authorEditText;
    private EditText priceEditText;
    private TextView filePathTextView;
    private ImageView coverImageView;
    private Button selectCoverButton;
    private Button updateButton;
    private Button cancelButton;
    private int bookId;
    private String username;
    private Uri coverUri;
    private String destinationCoverPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        nameEditText = findViewById(R.id.nameEditText);
        authorEditText = findViewById(R.id.authorEditText);
        priceEditText = findViewById(R.id.priceEditText);
        filePathTextView = findViewById(R.id.filePathTextView);
        coverImageView = findViewById(R.id.coverImageView);
        selectCoverButton = findViewById(R.id.selectCoverButton);
        updateButton = findViewById(R.id.updateButton);
        cancelButton = findViewById(R.id.cancelButton);

        bookId = getIntent().getIntExtra("bookId", 0);
        username = getIntent().getStringExtra("username");

        Book book = DatabaseHelper.getInstance(getApplicationContext()).getBookById(bookId);
        if (book != null) {
            nameEditText.setText(book.getName());
            authorEditText.setText(book.getAuthor());
            priceEditText.setText(String.valueOf(book.getPrice()));
            filePathTextView.setText(book.getFilePath());
            if (book.getCoverPath() != null && !book.getCoverPath().isEmpty()) {
                coverImageView.setImageURI(android.net.Uri.parse(book.getCoverPath()));
                coverImageView.setVisibility(View.VISIBLE);
            } else {
                coverImageView.setVisibility(View.GONE);
            }
        }

        selectCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String author = authorEditText.getText().toString();
                String priceStr = priceEditText.getText().toString();

                if (name.isEmpty() || author.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(EditBookActivity.this, "所有字段不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(EditBookActivity.this, "价格格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (coverUri != null) {
                    destinationCoverPath = saveFileToBookDirectory(coverUri);
                    if (destinationCoverPath == null) {
                        Toast.makeText(EditBookActivity.this, "封面保存失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    destinationCoverPath = book != null ? book.getCoverPath() : null;
                }

                if (DatabaseHelper.getInstance(getApplicationContext()).updateBook(bookId, name, author, price, destinationCoverPath)) {
                    Toast.makeText(EditBookActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditBookActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            coverUri = data.getData();
            coverImageView.setImageURI(coverUri);
            coverImageView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "选择了封面: " + coverUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        }
    }

    private String saveFileToBookDirectory(Uri fileUri) {
        try {
            File bookDir = getBookDirectory();
            if (!bookDir.exists()) {
                if (!bookDir.mkdirs()) {
                    throw new Exception("无法创建目录: " + bookDir.getAbsolutePath());
                }
            }

            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                throw new Exception("无法获取输入流");
            }

            String fileName = System.currentTimeMillis() + "_" + fileUri.getLastPathSegment().replace(":", "_") + ".jpg";

            File destinationFile = new File(bookDir, fileName);

            OutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return destinationFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File getBookDirectory() {
        return new File(getExternalFilesDir(null), "book");
    }
}