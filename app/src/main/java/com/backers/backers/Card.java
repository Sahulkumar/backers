package com.backers.backers;

/**
 * Created by so on 2017-08-01.
 */

public abstract class Card {
    public static final int TYPE_TICKET=0;
    public static final int TYPE_PRODUCT=1;
    public static final int NOT_CARD=2;

    abstract int getType();
}
