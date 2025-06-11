package com.example.myreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UploadBookActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText authorEditText;
    private EditText priceEditText;
    private Button selectFileButton;
    private Button selectCoverButton;
    private Button uploadButton;
    private String username;
    private Uri fileUri;
    private Uri coverUri;
    private String destinationFilePath;
    private String destinationCoverPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);

        username = getIntent().getStringExtra("username");

        nameEditText = findViewById(R.id.nameEditText);
        authorEditText = findViewById(R.id.authorEditText);
        priceEditText = findViewById(R.id.priceEditText);
        selectFileButton = findViewById(R.id.selectFileButton);
        selectCoverButton = findViewById(R.id.selectCoverButton);
        uploadButton = findViewById(R.id.uploadButton);

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 1);
            }
        });

        selectCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String author = authorEditText.getText().toString();
                String priceStr = priceEditText.getText().toString();

                if (name.isEmpty() || author.isEmpty() || priceStr.isEmpty() || fileUri == null) {
                    Toast.makeText(UploadBookActivity.this, "书名、作者、价格和文件不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(UploadBookActivity.this, "价格格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (fileUri != null) {
                    destinationFilePath = saveFileToBookDirectory(fileUri, "book");
                    if (destinationFilePath == null) {
                        Toast.makeText(UploadBookActivity.this, "文件保存失败", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (coverUri != null) {
                        destinationCoverPath = saveFileToBookDirectory(coverUri, "cover");
                        if (destinationCoverPath == null) {
                            Toast.makeText(UploadBookActivity.this, "封面保存失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (DatabaseHelper.getInstance(getApplicationContext()).uploadBook(username, name, author, price, destinationFilePath, destinationCoverPath)) {
                        Toast.makeText(UploadBookActivity.this, "图书上传成功", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(UploadBookActivity.this, "图书上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 1) {
                fileUri = data.getData();
                Toast.makeText(this, "选择了文件: " + fileUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
            } else if (requestCode == 2) {
                coverUri = data.getData();
                Toast.makeText(this, "选择了封面: " + coverUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveFileToBookDirectory(Uri fileUri, String type) {
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

            String fileExtension = type.equals("book") ? ".pdf" : ".jpg";
            String fileName = System.currentTimeMillis() + "_" + fileUri.getLastPathSegment().replace(":", "_") + fileExtension;

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