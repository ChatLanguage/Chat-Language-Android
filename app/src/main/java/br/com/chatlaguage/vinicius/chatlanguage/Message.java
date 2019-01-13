package br.com.chatlaguage.vinicius.chatlanguage;

public class Message {
    private String text;
    private long timestamp;
    private String fromId;
    private String toId;


    public void setText(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    };
    public long getTimestamp(){
        return timestamp;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public String getFromId() {
        return fromId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }
    public String getToId() {
        return toId;
    }



}
