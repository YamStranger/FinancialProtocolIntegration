package com.orders;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 9:36 AM
 */
public class OrderCancel extends OrderMessage {
    String orderIDofOrderToCancel;

    OrderCancel(long sendingTimestampUTC, String uniqueOrderID, String orderIDofOrderToCancel) {
        super(sendingTimestampUTC, uniqueOrderID);
        this.orderIDofOrderToCancel = orderIDofOrderToCancel;
    }

    public String getOrderIDofOrderToCancel() {
        return orderIDofOrderToCancel;
    }

    void setOrderIDofOrderToCancel(String orderIDofOrderToCancel) {
        this.orderIDofOrderToCancel = orderIDofOrderToCancel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OrderCancel that = (OrderCancel) o;

        if (orderIDofOrderToCancel != null ? !orderIDofOrderToCancel.equals(that.orderIDofOrderToCancel) : that.orderIDofOrderToCancel != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (orderIDofOrderToCancel != null ? orderIDofOrderToCancel.hashCode() : 0);
        return result;
    }
}
