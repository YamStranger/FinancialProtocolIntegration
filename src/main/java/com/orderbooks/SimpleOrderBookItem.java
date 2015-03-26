package com.orderbooks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 3:13 PM
 */

/**
 * SimpleOrderBookItem contains sorted priceLevels to choose best for same operation
 */
public class SimpleOrderBookItem extends OrderBookItem {
    Logger logger= LoggerFactory.getLogger(SimpleOrderBookItem.class);
    List<PriceLevel> priceLevelsSortedByAskPrice = new ArrayList<>();  //sorting using  natural order by AskPrice (first - least)
    List<PriceLevel> priceLevelsSortedByBidPrice = new ArrayList<>();  //sorting using opposite for natural order by BidPrice (first - max)

    SimpleOrderBookItem(List<PriceLevel> priceLevels, long timeOfOrderBookItem) {
        super(timeOfOrderBookItem);
        priceLevelsSortedByAskPrice.addAll(priceLevels);
        Collections.sort(priceLevelsSortedByAskPrice, new Comparator<PriceLevel>() {
            @Override
            public int compare(PriceLevel o1, PriceLevel o2) {
                return (int) (o1.getAskPrice() - o2.getAskPrice());
            }
        });
        priceLevelsSortedByBidPrice.addAll(priceLevels);
        Collections.sort(priceLevelsSortedByBidPrice, new Comparator<PriceLevel>() {
            @Override
            public int compare(PriceLevel o1, PriceLevel o2) {
                return (int) (o2.getBidPrice() - o1.getBidPrice());
            }
        });
    }

    /**
     * used sorting with natural order by AskPrice (first - min, last - max)
     * @param volume   value of volume to compare
     * @param moreThan
     * @return
     */
    @Override
    public PriceLevel getMinPriceLevelByAskVolume(long volume, boolean moreThan) {
        for (PriceLevel priceLevel : priceLevelsSortedByAskPrice) {
            if (moreThan && (priceLevel.getAskVolume() >= volume)) {
                return priceLevel;
            } else if ((!moreThan) && (priceLevel.getAskVolume() <= volume)) {
                return priceLevel;
            }
        }
        return null;
    }

    /**
     * used sorting with opposite for natural order by BidPrice (first - max, last - min)
     * @param volume   value of volume to compare
     * @param moreThan
     * @return
     */
    @Override
    public PriceLevel getMaxPriceLevelByBidVolume(long volume, boolean moreThan) {
        for (PriceLevel priceLevel : priceLevelsSortedByBidPrice) {
            if (moreThan && (priceLevel.getBidVolume() >= volume)) {
                return priceLevel;
            } else if ((!moreThan) && (priceLevel.getBidVolume() <= volume)) {
                return priceLevel;
            }
        }
        return null;
    }

    @Override
    public List<PriceLevel> getPriceLevels() {
        return new ArrayList<>(priceLevelsSortedByBidPrice);
    }
}
