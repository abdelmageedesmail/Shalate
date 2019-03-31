package com.shalate.red.shalate.Model;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Ahmed on 8/15/2018.
 */

public class ListModel {
    private String information;
    private String postId;
    private String like;
    private String userID;
    private String comment1;
    private String comment2;
    private String commentName1;
    private String commentName2;
    private String name;
    private String profession;
    private String commentCount;
    private String imageProfile;
    private String likeCount;
    private String videoUrl;
    private String lat;
    private String lng;
    private String created_at;
    private ArrayList<ImageModel> postImages;
    private ArrayList<CommentModel> arrayComments;
    private JSONArray arrayImages;
    private JSONArray comments;
    private ArrayList<String> arrayLikes;


    public ArrayList<ImageModel> getPostImages() {
        return postImages;
    }

    public void setPostImages(ArrayList<ImageModel> postImages) {
        this.postImages = postImages;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getComment1() {
        return comment1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String getCommentName1() {
        return commentName1;
    }

    public void setCommentName1(String commentName1) {
        this.commentName1 = commentName1;
    }

    public String getCommentName2() {
        return commentName2;
    }

    public void setCommentName2(String commentName2) {
        this.commentName2 = commentName2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public ArrayList<CommentModel> getArrayComments() {
        return arrayComments;
    }

    public void setArrayComments(ArrayList<CommentModel> arrayComments) {
        this.arrayComments = arrayComments;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public JSONArray getArrayImages() {
        return arrayImages;
    }

    public void setArrayImages(JSONArray arrayImages) {
        this.arrayImages = arrayImages;
    }

    public JSONArray getComments() {
        return comments;
    }

    public void setComments(JSONArray comments) {
        this.comments = comments;
    }

    public ArrayList<String> getArrayLikes() {
        return arrayLikes;
    }

    public void setArrayLikes(ArrayList<String> arrayLikes) {
        this.arrayLikes = arrayLikes;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
