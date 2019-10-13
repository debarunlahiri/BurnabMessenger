package com.debarunlahiri.burnab.messenger.Stories;

public class Stories {

    private String user_id = null;
    private String story_caption = null;
    private String formatted_date = null;
    private long timestamp;
    private String story_id = null;
    private String story_image = null;

    public Stories() {

    }

    public Stories(String user_id, String story_caption, String formatted_date, long timestamp, String story_id, String story_image) {
        this.user_id = user_id;
        this.story_caption = story_caption;
        this.formatted_date = formatted_date;
        this.timestamp = timestamp;
        this.story_id = story_id;
        this.story_image = story_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStory_caption() {
        return story_caption;
    }

    public void setStory_caption(String story_caption) {
        this.story_caption = story_caption;
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

    public String getStory_image() {
        return story_image;
    }

    public void setStory_image(String story_image) {
        this.story_image = story_image;
    }
}
