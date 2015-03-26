package com;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:18 AM
 */

import com.orderbooks.OrderBookItem;
import com.orderbooks.PriceLevel;
import com.orders.*;

import java.util.concurrent.BlockingQueue;

/**
 * OrdersProcessor responsible for logic of creating orders, depends on incoming OrderBookItems with PriceLevel.
 */
public class OrdersProcessor extends Thread {
    private BlockingQueue<OrderBookItem> orderBookItemsForProcessing;
    private BlockingQueue<OrderMessage> ordersForSending;
    private long processedOrderItemsCount = 0;
    private String NEW_ORDERS_STORAGE = "new_orders.txt";

    public OrdersProcessor(BlockingQueue<OrderBookItem> orderBookItemsForProcessing, BlockingQueue<OrderMessage> ordersForSending) {
        this.orderBookItemsForProcessing = orderBookItemsForProcessing;
        this.ordersForSending = ordersForSending;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                OrderBookItem processingOrderBookItem = orderBookItemsForProcessing.take();
                for (SideEnum sideEnum : SideEnum.values()) {
                    OrderMessage order = processOrderBookItem(processingOrderBookItem, sideEnum);
                    if (order != null) {
                        if (order instanceof NewOrderSingle) {
                            Storage.getInstance().register(NEW_ORDERS_STORAGE, (NewOrderSingle) order);
                        } else {
                            Storage.getInstance().register(NEW_ORDERS_STORAGE, (OrderCancel) order);
                        }
                        ordersForSending.put(order);
                    }
                }
                processedOrderItemsCount++;
            } catch (InterruptedException interruptedException) {
                interrupt();
            }
        }
    }

    private NewOrderSingle lastNewOrderSingleSell = null;
    private NewOrderSingle lastNewOrderSingleBuy = null;

    /**
     * Processing of order book item
     * For Sell operations:
     * if last operation was creation of new single order "Sell" - next operation can be only creation of cancel order for "Sell"
     * if last operation was creation of cancel order for "Sell"- next operation can be only creation of new single order  "Sell"
     * <p/>
     * For Buy operations:
     * if last operation was creation of new single order "Buy"- next operation can be only creation of cancel order  for "Buy"
     * if last operation was creation of cancel order for "Buy"- next operation can be only creation of new single order  "Buy"
     *
     * @param orderBookItem orderBookItem for analyzing
     * @param sideEnumValue what type of operations orders can be created as result
     * @return new OrderMessage or null. Null will be if its is not possible to create order for this orderBookItem
     */
    public OrderMessage processOrderBookItem(OrderBookItem orderBookItem, SideEnum sideEnumValue) {
        OrderMessage orderMessage = null;
        if (SideEnum.Buy.equals(sideEnumValue)) { // only Buy orders can be created of modified
            if (lastNewOrderSingleBuy != null) {
                //can crete only CancelOrder for Buy order
                //check if there is no exists PriceLevel with BidVolume >=200
                // if such PriceLevel does not exist - create close order
                PriceLevel priceLevel = orderBookItem.getMaxPriceLevelByBidVolume(200, true);
                if (priceLevel == null) {
                    long closeOrderTime = DateUtils.conventCurrentToGMT(System.currentTimeMillis());
                    String orderId = String.valueOf(Storage.getInstance().generateOrderId());
                    orderMessage = OrderCreator.createOrderCancel(closeOrderTime, orderId, lastNewOrderSingleBuy.getUniqueOrderID());
                    lastNewOrderSingleBuy = null;//last will be cancel
                }
            } else {
                //can crete only newOrderSingle with Buy operation
                //check if there is exists PriceLevel with BidVolume >=500
                // if such PriceLevel exists - create open order Buy
                // Price to open Buy order - lowest from AskPrices
                PriceLevel priceLevel = orderBookItem.getMaxPriceLevelByBidVolume(500, true);
                if (priceLevel != null) {
                    PriceLevel priceLevelForOpenPosition = orderBookItem.getMinPriceLevelByAskVolume(0, true);
                    long openOrderTime = DateUtils.conventCurrentToGMT(System.currentTimeMillis());
                    String orderId = String.valueOf(Storage.getInstance().generateOrderId());
                    lastNewOrderSingleBuy = OrderCreator.createNewOrderSingle(openOrderTime, orderId, priceLevelForOpenPosition.getAskPrice(), 100, SideEnum.Buy);
                    orderMessage = lastNewOrderSingleBuy;
                }
            }
        } else if (SideEnum.Sell.equals(sideEnumValue)) {
            if (lastNewOrderSingleSell != null) {
                //can crete only CancelOrder for Sell order
                //check if there is no exists PriceLevel with AskVolume >=200
                // if such PriceLevel does not exist - create close order
                PriceLevel priceLevel = orderBookItem.getMinPriceLevelByAskVolume(200, true);
                if (priceLevel == null) {
                    long closeOrderTime = DateUtils.conventCurrentToGMT(System.currentTimeMillis());
                    String orderId = String.valueOf(Storage.getInstance().generateOrderId());
                    orderMessage = OrderCreator.createOrderCancel(closeOrderTime, orderId, lastNewOrderSingleSell.getUniqueOrderID());
                    lastNewOrderSingleSell = null;//last will be cancel
                }
            } else {
                //can crete only newOrderSingle with Sell operation
                //check if there is exists PriceLevel with AskVolume >=500
                // if such PriceLevel exists - create open order Buy
                // Price to open Buy order - lowest from AskPrices
                PriceLevel priceLevel = orderBookItem.getMinPriceLevelByAskVolume(500, true);
                if (priceLevel != null) {
                    PriceLevel priceLevelForOpenPosition = orderBookItem.getMaxPriceLevelByBidVolume(0, true);
                    long openOrderTime = DateUtils.conventCurrentToGMT(System.currentTimeMillis());
                    String orderId = String.valueOf(Storage.getInstance().generateOrderId());
                    lastNewOrderSingleSell = OrderCreator.createNewOrderSingle(openOrderTime, orderId, priceLevelForOpenPosition.getBidPrice(), 100, SideEnum.Sell);
                    orderMessage = lastNewOrderSingleSell;
                }
            }
        }
        return orderMessage;
    }

    public long getProcessedOrderItemsCount() {
        return processedOrderItemsCount;
    }
}

