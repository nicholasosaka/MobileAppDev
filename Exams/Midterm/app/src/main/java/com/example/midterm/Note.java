package com.example.midterm;

import java.io.Serializable;

public class Note implements Serializable {
    private String id;
    private String userId;
    private String text;

    public static final String breakStr = "*%*";

    public Note(String id, String userId, String text) {
        this.id = id;
        this.userId = userId;
        this.text = text;
    }

    public String getTitle(){
        int index = text.indexOf(breakStr);
        return text.substring(0, index);
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        int index = text.indexOf(breakStr) + 3;

        return text.substring(index);
    }
}
