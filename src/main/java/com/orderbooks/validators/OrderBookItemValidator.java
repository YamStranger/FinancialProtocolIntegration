package com.orderbooks.validators;

import com.orderbooks.OrderBookItem;
import com.orderbooks.PriceLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:10 AM
 */

/**
 * OrderBookItemValidator contains functions for validation of orderBookItems and their representation
 */
public class OrderBookItemValidator {
    Logger logger= LoggerFactory.getLogger(OrderBookItemValidator.class);
    private OrderBookItemValidator() {
    }

    public static boolean isCorrect(OrderBookItem orderBookItem) {
        return true;
    }

    public static boolean isCorrect(PriceLevel priceLevel) {
        boolean isCorrect=true;
        if(isCorrect && !(priceLevel.getAskPrice()>0)){
            isCorrect=false;
        }
        if(isCorrect && !(priceLevel.getBidPrice()>0)){
            isCorrect=false;
        }
        if(isCorrect && !(priceLevel.getBidVolume()>=0)){
            isCorrect=false;
        }
        if(isCorrect && !(priceLevel.getAskVolume()>=0)){
            isCorrect=false;
        }

        return isCorrect;
    }
}
