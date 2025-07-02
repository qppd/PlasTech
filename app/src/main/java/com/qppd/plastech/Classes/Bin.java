package com.qppd.plastech.Classes;

public class Bin {

    private int bottle_large;
    private int bottle_small;
    private int bin_level;
    private int total_rewards;
    private int total_weight;
    private int coin_stock;

    public Bin(int bottle_large, int bottle_small, int bin_level, int total_rewards, int total_weight, int coin_stock) {
        this.bottle_large = bottle_large;
        this.bottle_small = bottle_small;
        this.bin_level = bin_level;
        this.total_rewards = total_rewards;
        this.total_weight = total_weight;
        this.coin_stock = coin_stock;
    }

    public Bin(){

    }

    public int getBottle_large() {
        return bottle_large;
    }

    public void setBottle_large(int bottle_large) {
        this.bottle_large = bottle_large;
    }

    public int getBottle_small() {
        return bottle_small;
    }

    public void setBottle_small(int bottle_small) {
        this.bottle_small = bottle_small;
    }

    public int getBin_level() {
        return bin_level;
    }

    public void setBin_level(int bin_level) {
        this.bin_level = bin_level;
    }

    public int getTotal_rewards() {
        return total_rewards;
    }

    public void setTotal_rewards(int total_rewards) {
        this.total_rewards = total_rewards;
    }

    public int getTotal_weight() {
        return total_weight;
    }

    public void setTotal_weight(int total_weight) {
        this.total_weight = total_weight;
    }

    public int getCoin_stock() {
        return coin_stock;
    }

    public void setCoin_stock(int coin_stock) {
        this.coin_stock = coin_stock;
    }
}
