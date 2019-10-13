package com.debarunlahiri.burnab.messenger.Utils;

public class Comment {

    private String comment = null;
    private String user_id = null;
    private String formatted_date = null;
    private long timestamp;
    private String comment_id = null;

    private String story_id = null;
    private String story_user_id = null;

    public Comment() {

    }

    public Comment(String comment, String user_id, String formatted_date, long timestamp, String story_id, String story_user_id, String comment_id) {
        this.comment = comment;
        this.user_id = user_id;
        this.formatted_date = formatted_date;
        this.timestamp = timestamp;
        this.story_id = story_id;
        this.story_user_id = story_user_id;
        this.comment_id = comment_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFormatted_date() {
        return formatted_date;
    }

    public void setFormatted_date(String formatted_date) {
        this.formatted_date = formatted_date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public String getStory_user_id() {
        return story_user_id;
    }

    public void setStory_user_id(String story_user_id) {
        this.story_user_id = story_user_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
