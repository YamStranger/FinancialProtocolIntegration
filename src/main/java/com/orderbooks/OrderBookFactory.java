package com.orderbooks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 9:36 AM
 */

/**
 * OrderBookFactory init all OrderBook classes
 */
public class OrderBookFactory {
    Logger logger = LoggerFactory.getLogger(OrderBookFactory.class);

    private OrderBookFactory() {

    }

    public static OrderBook loadOrderBook(Path orderBookFilePath, Path messagesFilePath, long dateInFileMask, int priceLevel) {
        return new FileBasedOrderBook(orderBookFilePath, messagesFilePath, dateInFileMask, priceLevel);
    }

    public static OrderBookItem createOrderBookItem(List<PriceLevel> priceLevels, long timeOfOrderBookItemEvent) {
        return new SimpleOrderBookItem(priceLevels, timeOfOrderBookItemEvent);
    }

    public static PriceLevel createPriceLevel(long askPrice, long askVolume, long bidPrice, long bidVolume) {
        return new PriceLevel(askPrice, askVolume, bidPrice, bidVolume);
    }

}
