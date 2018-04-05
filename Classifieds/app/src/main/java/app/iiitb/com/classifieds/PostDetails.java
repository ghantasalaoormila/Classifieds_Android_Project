package app.iiitb.com.classifieds;

import android.media.Image;

import java.io.File;
import java.util.Scanner;

/**
 * Created by URMILA on 16-Jul-17.
 */

public class PostDetails {
    private String title;
    private String description;
    private String location;
    private String mobile;
    private String id;
    private String postedBy;
    private String postedOn;
    private String category;
    private Boolean featured;
    private String img;

    public String getImg() {
        return img;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public PostDetails(String title, String description,String cat, String location, String mobile, String id,String postedBy, String postedOn, boolean feature, String image) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.mobile = mobile;
        this.id = id;
        this.postedBy = postedBy;
        this.postedOn = postedOn;
        this.category = cat;
        featured = feature;
        img = image;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getMobile() {
        return mobile;
    }

    public String getId() {
        return id;
    }
}
