package com.example.myreader;

public class Comment {
    private int id;
    private String username;
    private String content;
    private String commentTime;

    public Comment() {
    }

    public Comment(String username, String content, long time) {
        this.username = username;
        this.content = content;
        this.commentTime = String.valueOf(time);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}