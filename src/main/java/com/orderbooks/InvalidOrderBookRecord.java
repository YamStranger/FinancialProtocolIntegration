package com.orderbooks;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:09 AM
 */
public class InvalidOrderBookRecord extends RuntimeException {
    public InvalidOrderBookRecord(Throwable throwable) {
        super(throwable);
    }

    public InvalidOrderBookRecord(String string) {
        super(string);
    }
}
