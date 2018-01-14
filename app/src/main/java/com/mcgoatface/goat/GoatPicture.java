package com.mcgoatface.goat;

import java.util.Map;

/**
 * Created by user on 10/01/2018.
 */
public class GoatPicture {

    public String Title;
    public String Uploader;
    public Map<String, Object> ratings;
    public GoatPicture() {
        // Default Constructor apparently needed
    }

    public GoatPicture(String Title, String Uploader, Map<String,Object> ratings) {
        this.Title = Title;
        this.Uploader = Uploader;
        this.ratings = ratings;
    }
}

