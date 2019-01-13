package br.com.chatlaguage.vinicius.chatlanguage;

public class User {

    private String uuid;
    private String username;
    private String profileUrl;

    public User() {

    }


    public User(String uuid, String username, String profileUrl) {
        this.uuid = uuid;
        this.username = username;
        this.profileUrl = profileUrl;
    }


    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

}
