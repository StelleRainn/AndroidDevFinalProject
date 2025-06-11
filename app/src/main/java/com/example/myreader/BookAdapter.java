package com.example.myreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    private List<Book> bookList;
    private String username;

    public BookAdapter(Context context, List<Book> bookList, String username) {
        super(context, 0, bookList);
        this.bookList = bookList;
        this.username = username;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
        }

        ImageView coverImageView = convertView.findViewById(R.id.coverImageView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView authorTextView = convertView.findViewById(R.id.authorTextView);
        TextView priceTextView = convertView.findViewById(R.id.priceTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusTextView);

        Book book = getItem(position);

        nameTextView.setText(book.getName());
        authorTextView.setText(book.getAuthor());
        priceTextView.setText(book.getPrice() == 0 ? "免费" : "¥" + book.getPrice());

        if (book.isFree()) {
            statusTextView.setText("免费");
        } else if (DatabaseHelper.getInstance(getContext()).hasBoughtBook(username, book.getId())) {
            statusTextView.setText("已购买");
        } else {
            statusTextView.setText("付费");
        }

        if (book.getCoverPath() != null && !book.getCoverPath().isEmpty()) {
            coverImageView.setImageURI(android.net.Uri.parse(book.getCoverPath()));
            coverImageView.setVisibility(View.VISIBLE);
        } else {
            coverImageView.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void updateBooks(List<Book> newBooks) {
        clear();
        addAll(newBooks);
        notifyDataSetChanged();
    }
}