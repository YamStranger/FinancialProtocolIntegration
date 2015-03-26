package com.orders;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 9:45 AM
 */
public class OrderCreator {
    private OrderCreator() {
    }

    public static NewOrderSingle createNewOrderSingle(long sendingTimestampUTC, String uniqueOrderID, long price, long quantity, SideEnum sideEnum) {
        return new NewOrderSingle(sendingTimestampUTC, uniqueOrderID, price, quantity, sideEnum);
    }

    public static OrderCancel createOrderCancel(long sendingTimestampUTC, String uniqueOrderID, String orderIDofOrderToCancel) {
        return new OrderCancel(sendingTimestampUTC, uniqueOrderID, orderIDofOrderToCancel);
    }
}
