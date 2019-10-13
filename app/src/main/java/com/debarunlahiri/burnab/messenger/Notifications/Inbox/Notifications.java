package com.debarunlahiri.burnab.messenger.Notifications.Inbox;

public class Notifications {

    private String user_id = null;

    Notifications() {

    }

    public Notifications(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
