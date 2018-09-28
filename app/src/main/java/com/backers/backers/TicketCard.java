package com.backers.backers;

/**
 * Created by so on 2017-08-01.
 */

public class TicketCard extends Card {
    public int type=Card.TYPE_TICKET;



    public int id;
    public String title;
    public String detail;
    public String status;


    @Override
    int getType() {
        return type;
    }
}
