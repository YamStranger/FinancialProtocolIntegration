package com.orderbooks;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:13 AM
 */
public class PriceLevel {
    private long askPrice;
    private long askVolume;
    private long bidPrice;
    private long bidVolume;

    PriceLevel(long askPrice, long askVolume, long bidPrice, long bidVolume) {
        this.askPrice = askPrice;
        this.askVolume = askVolume;
        this.bidPrice = bidPrice;
        this.bidVolume = bidVolume;
    }

    public long getAskPrice() {
        return askPrice;
    }

    void setAskPrice(long askPrice) {
        this.askPrice = askPrice;
    }

    public long getAskVolume() {
        return askVolume;
    }

    void setAskVolume(long askVolume) {
        this.askVolume = askVolume;
    }

    public long getBidPrice() {
        return bidPrice;
    }

    void setBidPrice(long bidPrice) {
        this.bidPrice = bidPrice;
    }

    public long getBidVolume() {
        return bidVolume;
    }

    void setBidVolume(long bidVolume) {
        this.bidVolume = bidVolume;
    }
}
