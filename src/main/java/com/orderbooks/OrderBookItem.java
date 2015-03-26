package com.orderbooks;

import java.util.List;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 9:51 AM
 */
public abstract class OrderBookItem {
    private long timeOfOrderBookItem;

    protected OrderBookItem(long timeOfOrderBookItem) {
        this.timeOfOrderBookItem = timeOfOrderBookItem;
    }

    /**
     * search PriceLevels of this OrderItem, and return min PriceLevel(compared only by Ask Price)
     * according to volume and moreThan parameter.
     *
     * @param volume   value of volume to compare
     * @param moreThan
     * @return if moreThan=true 0 than function return PriceLevel with AskVolume >= volume, else - function return PriceLevel with AskVolume <= volume. If nothing found - return null
     */
    public abstract PriceLevel getMinPriceLevelByAskVolume(long volume, boolean moreThan);

    /**
     * search PriceLevels of this OrderItem, and return max PriceLevel(compared only by Bid Price)
     * according to volume and moreThan parameter.
     *
     * @param volume   value of volume to compare
     * @param moreThan
     * @return if moreThan 0 than function return PriceLevel with BidVolume >= volume, else - function return PriceLevel with BidVolume <= volume. If nothing found - return null
     */
    public abstract PriceLevel getMaxPriceLevelByBidVolume(long volume, boolean moreThan);

    public abstract List<PriceLevel> getPriceLevels();

    /**
     * return time of order book item
     * @return
     */
    public long getTimeOfOrderBookItem() {
        return timeOfOrderBookItem;
    }
}
