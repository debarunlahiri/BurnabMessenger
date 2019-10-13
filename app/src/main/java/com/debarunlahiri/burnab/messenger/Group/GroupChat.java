package com.debarunlahiri.burnab.messenger.Group;

public class GroupChat {

    private String chat_id = null;
    private String group_id = null;
    private String formatted_date = null;
    private String message = null;
    private long timestamp;
    private String user_id = null;

    public GroupChat() {

    }

    public GroupChat(String user_id, String chat_id, String group_id, String formatted_date, String message, long timestamp) {
        this.user_id = user_id;
        this.chat_id = chat_id;
        this.group_id = group_id;
        this.formatted_date = formatted_date;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getFormatted_date() {
        return formatted_date;
    }

    public void setFormatted_date(String formatted_date) {
        this.formatted_date = formatted_date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
