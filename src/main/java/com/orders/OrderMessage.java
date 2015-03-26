package com.orders;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 9:36 AM
 */
public abstract class OrderMessage {
    long sendingTimestampUTC;
    String uniqueOrderID;

    OrderMessage(long sendingTimestampUTC, String uniqueOrderID) {
        this.sendingTimestampUTC = sendingTimestampUTC;
        this.uniqueOrderID = uniqueOrderID;
    }

    public long getSendingTimestampUTC() {
        return sendingTimestampUTC;
    }

    void setSendingTimestampUTC(long sendingTimestampUTC) {
        this.sendingTimestampUTC = sendingTimestampUTC;
    }

    public String getUniqueOrderID() {
        return uniqueOrderID;
    }

    void setUniqueOrderID(String uniqueOrderID) {
        this.uniqueOrderID = uniqueOrderID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderMessage that = (OrderMessage) o;

        if (sendingTimestampUTC != that.sendingTimestampUTC) return false;
        if (uniqueOrderID != null ? !uniqueOrderID.equals(that.uniqueOrderID) : that.uniqueOrderID != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (sendingTimestampUTC ^ (sendingTimestampUTC >>> 32));
        result = 31 * result + (uniqueOrderID != null ? uniqueOrderID.hashCode() : 0);
        return result;
    }
}
