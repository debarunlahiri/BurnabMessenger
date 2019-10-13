package com.debarunlahiri.burnab.messenger.Inbox;

public class Inbox {

    private String message = null;
    private String sender_user_id = null;
    private String receiver_user_ld = null;
    private long timestamp;
    private boolean has_seen;
    private String formatted_Date = null;
    private String user_key = null;
    private String reply_chat_id = null;

    public Inbox() {

    }

    public Inbox(String message, String sender_user_id, String receiver_user_ld, long timestamp, boolean has_seen, String formatted_Date, String user_key, String reply_chat_id) {
        this.message = message;
        this.sender_user_id = sender_user_id;
        this.receiver_user_ld = receiver_user_ld;
        this.timestamp = timestamp;
        this.has_seen = has_seen;
        this.formatted_Date = formatted_Date;
        this.user_key = user_key;
        this.reply_chat_id = reply_chat_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_user_id() {
        return sender_user_id;
    }

    public void setSender_user_id(String sender_user_id) {
        this.sender_user_id = sender_user_id;
    }

    public String getReceiver_user_ld() {
        return receiver_user_ld;
    }

    public void setReceiver_user_ld(String receiver_user_ld) {
        this.receiver_user_ld = receiver_user_ld;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isHas_seen() {
        return has_seen;
    }

    public void setHas_seen(boolean has_seen) {
        this.has_seen = has_seen;
    }

    public String getFormatted_Date() {
        return formatted_Date;
    }

    public void setFormatted_Date(String formatted_Date) {
        this.formatted_Date = formatted_Date;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getReply_chat_id() {
        return reply_chat_id;
    }

    public void setReply_chat_id(String reply_chat_id) {
        this.reply_chat_id = reply_chat_id;
    }
}
