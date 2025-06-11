package com.example.myreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyPublishedBooksAdapter extends ArrayAdapter<Book> {
    private List<Book> books;
    private String username;
    private Context context;

    public MyPublishedBooksAdapter(Context context, List<Book> books, String username) {
        super(context, 0, books);
        this.books = books;
        this.username = username;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_item_my_published, parent, false);
        }

        ImageView coverImageView = convertView.findViewById(R.id.coverImageView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView authorTextView = convertView.findViewById(R.id.authorTextView);
        TextView priceTextView = convertView.findViewById(R.id.priceTextView);
        Button editButton = convertView.findViewById(R.id.editButton);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        final Book book = getItem(position);

        nameTextView.setText(book.getName());
        authorTextView.setText(book.getAuthor());
        priceTextView.setText(book.getPrice() == 0 ? "免费" : "¥" + book.getPrice());

        if (book.getCoverPath() != null && !book.getCoverPath().isEmpty()) {
            coverImageView.setImageURI(android.net.Uri.parse(book.getCoverPath()));
            coverImageView.setVisibility(View.VISIBLE);
        } else {
            coverImageView.setVisibility(View.GONE);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditBookActivity.class);
                intent.putExtra("bookId", book.getId());
                intent.putExtra("username", username);
                context.startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                builder.setTitle("确认删除");
                builder.setMessage("确定要删除《" + book.getName() + "》吗？");
                builder.setPositiveButton("确定", (dialog, which) -> {
                    if (DatabaseHelper.getInstance(context).deleteBook(book.getId())) {
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        books.remove(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });

        return convertView;
    }
}