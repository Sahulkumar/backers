package com.backers.backers;

/**
 * Created by so on 2017-08-01.
 */

public class ProductCard extends Card {
    public int type=Card.TYPE_PRODUCT;



    public int product_id;
    public String name;
    public String description;
    public String image;
    public int warranty;
    public boolean liked;


    @Override
    int getType() {
        return type;
    }
}
