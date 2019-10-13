package com.debarunlahiri.burnab.messenger.Search;

public class Search {

    private String profile_image = null;
    private String user_id;
    private String name = null;
    private String username = null;

    Search() {

    }

    public Search(String profile_image, String user_id, String name, String username) {
        this.profile_image = profile_image;
        this.user_id = user_id;
        this.name = name;
        this.username = username;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
