package com.example.hotel_management.data.model;

public class Comment {
    private String id;
    private String userName;
    private String userAvatar;
    private float rating;
    private String commentText;
    private String date;

    public Comment(String id, String userName, String userAvatar, float rating, String commentText, String date) {
        this.id = id;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rating = rating;
        this.commentText = commentText;
        this.date = date;
    }

    public String getId() { return id; }
    public String getUserName() { return userName; }
    public String getUserAvatar() { return userAvatar; }
    public float getRating() { return rating; }
    public String getCommentText() { return commentText; }
    public String getDate() { return date; }
}
