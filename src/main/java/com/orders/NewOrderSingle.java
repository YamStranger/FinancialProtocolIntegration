package com.orders;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 9:36 AM
 */
public class NewOrderSingle extends OrderMessage {
    private long price;
    private long quantity;
    private SideEnum sideEnum;

    NewOrderSingle(long sendingTimestampUTC, String uniqueOrderID, long price, long quantity, SideEnum sideEnum) {
        super(sendingTimestampUTC,uniqueOrderID);
        this.price = price;
        this.quantity = quantity;
        this.sideEnum = sideEnum;
    }

    public long getPrice() {
        return price;
    }

    void setPrice(long price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public SideEnum getSide() {
        return sideEnum;
    }

    void setSideEnum(SideEnum sideEnum) {
        this.sideEnum = sideEnum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NewOrderSingle that = (NewOrderSingle) o;

        if (price != that.price) return false;
        if (quantity != that.quantity) return false;
        if (sideEnum != that.sideEnum) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (price ^ (price >>> 32));
        result = 31 * result + (int) (quantity ^ (quantity >>> 32));
        result = 31 * result + (sideEnum != null ? sideEnum.hashCode() : 0);
        return result;
    }
}
