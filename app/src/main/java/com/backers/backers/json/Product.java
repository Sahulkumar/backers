package com.backers.backers.json;

import java.util.ArrayList;

/**
 * Created by so on 2017-08-10.
 */

public class Product {
    public String name;
    public String img;
    public String warranty;
    public String detail;
    public String share;
    public ArrayList<Review> review;
    public ArrayList<Faq> faq;
    public boolean liked;

    public class Review{
        public String img;
        public String name;
        public String review;
        public String date;
    }
    public class Faq{
        public String question;
        public String answer;
    }
}
