package com.backers.backers.json;

import java.util.ArrayList;

/**
 * Created by so on 2017-08-11.
 */

public class Ticket {
    public String title;
    public String product;
    public String description;
    public String date;
    public String status;
    public ArrayList<Dialog> dialog;


    public class Dialog{
        public String userImg;
        public String userName;
        public String date;
        public String reply;
    }
}
