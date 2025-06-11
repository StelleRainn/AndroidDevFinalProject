package com.example.myreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {
    public CommentAdapter(Context context, List<Comment> comments) {
        super(context, 0, comments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
        }

        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView contentTextView = convertView.findViewById(R.id.contentTextView);
        TextView timeTextView = convertView.findViewById(R.id.timeTextView);

        Comment comment = getItem(position);

        usernameTextView.setText(comment.getUsername());
        contentTextView.setText(comment.getContent());
        timeTextView.setText(comment.getCommentTime());

        return convertView;
    }
}