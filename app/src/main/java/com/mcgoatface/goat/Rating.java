package com.mcgoatface.goat;

/**
 * Created by user on 10/01/2018.
 */

public class Rating {
    public User rater;
    public int score;
    public Rating() {

    }

    public Rating( User rater, int score){
        this.rater = rater;
        this.score = score;
    }
}
