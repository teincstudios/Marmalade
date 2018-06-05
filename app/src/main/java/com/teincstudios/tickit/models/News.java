package com.teincstudios.tickit.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kwesi manny on 2/10/2018.
 */

public class News {
    @SerializedName("posttitle")
    @Expose
    private String posttitle;

    @SerializedName("postdesc")
    @Expose
    private String postdesc;

    @SerializedName("postauthor")
    @Expose
    private String postauthor;

    @SerializedName("postparagragh")
    @Expose
    private String postparagragh;

    @SerializedName("postImage")
    @Expose
    private Integer postImage;

    public News(String s) {
        this.posttitle = s;

    }

    public Integer getPostImage() {
        return postImage;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public String getPostdesc() {
        return postdesc;
    }

    public String getPostauthor() {
        return postauthor;
    }

    public String getPostparagragh() {
        return postparagragh;
    }

}
