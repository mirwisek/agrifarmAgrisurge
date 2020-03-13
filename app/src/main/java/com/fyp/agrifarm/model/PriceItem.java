package com.fyp.agrifarm.model;

import com.fyp.agrifarm.R;

public class PriceItem {

    public static final int ARROW_UP = R.drawable.ic_arrow_up;
    public static final int ARROW_DOWN = R.drawable.ic_arrow_down;

    public static final int APPLE = R.drawable.sym_apple;
    public static final int BELL_PEPPER = R.drawable.sym_bell_pepper;
    public static final int BET_ROOT = R.drawable.sym_betroot;
    public static final int BRINJAL = R.drawable.sym_brinjal;
    public static final int CARROT = R.drawable.sym_carrot;
    public static final int CHERRY = R.drawable.sym_cherry;
    public static final int GRAPES = R.drawable.sym_grapes;
    public static final int GUAVA = R.drawable.sym_guava;
    public static final int ORANGE = R.drawable.sym_orange;
    public static final int WATER_MELON = R.drawable.sym_wmelon;

    private String name, price;
    private int priceSymbol,pricePace;

    public PriceItem(String name, String price, int priceSymbol, int pricePace) {
        this.name = name;
        this.price = price;
        this.priceSymbol = priceSymbol;
        this.pricePace = pricePace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPriceSymbol() {
        return priceSymbol;
    }

    public void setPriceSymbol(int priceSymbol) {
        this.priceSymbol = priceSymbol;
    }

    public int getPricePace() {
        return pricePace;
    }

    public void setPricePace(int pricePace) {
        this.pricePace = pricePace;
    }
}
