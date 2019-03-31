package com.shalate.red.shalate.Model;

/**
 * Created by Ahmed on 8/27/2018.
 */

public class ChatModel {
    private long date;
    private String to;
    private String message;
    private String from;
    private String isLocation;
    private String lat;
    private String lon;
    private String voiceRecrod;
    private String postImage;
    private String postId;
    private int type;

    public ChatModel() {
    }

    public ChatModel(long date, String to, String message, String from, int type) {
        this.date = date;
        this.to = to;
        this.message = message;
        this.from = from;
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIsLocation() {
        return isLocation;
    }

    public void setIsLocation(String isLocation) {
        this.isLocation = isLocation;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getVoiceRecrod() {
        return voiceRecrod;
    }

    public void setVoiceRecrod(String voiceRecrod) {
        this.voiceRecrod = voiceRecrod;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
