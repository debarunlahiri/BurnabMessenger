package com.debarunlahiri.burnab.messenger.Group;

public class GroupPosts {

    private String body;
    private String user_id;
    private String post_id;
    private String group_id;
    private long timestamp;
    private String formatted_date;
    private String post_image = null;
    private String thumb_post_image = null;

    public GroupPosts() {

    }

    public GroupPosts(String body, String user_id, String post_id, String group_id, long timestamp, String formatted_date, String post_image, String thumb_post_image) {
        this.body = body;
        this.user_id = user_id;
        this.post_id = post_id;
        this.group_id = group_id;
        this.timestamp = timestamp;
        this.formatted_date = formatted_date;
        this.post_image = post_image;
        this.thumb_post_image = thumb_post_image;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormatted_date() {
        return formatted_date;
    }

    public void setFormatted_date(String formatted_date) {
        this.formatted_date = formatted_date;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getThumb_post_image() {
        return thumb_post_image;
    }

    public void setThumb_post_image(String thumb_post_image) {
        this.thumb_post_image = thumb_post_image;
    }
}
