package com.debarunlahiri.burnab.messenger.Group;

public class Group {

    private String group_id = null;
    private String group_profile_image = null;
    private String group_cover_image = null;
    private String group_name = null;
    private String group_admin_user_id = null;
    private String group_desc = null;

    public Group() {

    }

    public Group(String group_id, String group_profile_image, String group_cover_image, String group_name, String group_admin_user_id, String group_desc) {
        this.group_id = group_id;
        this.group_profile_image = group_profile_image;
        this.group_cover_image = group_cover_image;
        this.group_name = group_name;
        this.group_admin_user_id = group_admin_user_id;
        this.group_desc = group_desc;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_profile_image() {
        return group_profile_image;
    }

    public void setGroup_profile_image(String group_profile_image) {
        this.group_profile_image = group_profile_image;
    }

    public String getGroup_cover_image() {
        return group_cover_image;
    }

    public void setGroup_cover_image(String group_cover_image) {
        this.group_cover_image = group_cover_image;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_admin_user_id() {
        return group_admin_user_id;
    }

    public void setGroup_admin_user_id(String group_admin_user_id) {
        this.group_admin_user_id = group_admin_user_id;
    }

    public String getGroup_desc() {
        return group_desc;
    }

    public void setGroup_desc(String group_desc) {
        this.group_desc = group_desc;
    }
}
