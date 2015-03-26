package com.processing.serialization.json;

import com.DateUtils;
import com.google.gson.*;
import com.orders.OrderCancel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:01 AM
 */
public class OrderCancelSerializer implements JsonSerializer<OrderCancel> {
    Logger logger = LoggerFactory.getLogger(OrderCancelSerializer.class);

    @Override
    public JsonElement serialize(OrderCancel orderCancel, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(JsonTags.orderId, orderCancel.getUniqueOrderID());
        jsonObject.addProperty(JsonTags.cancelOrderId, orderCancel.getOrderIDofOrderToCancel());
        jsonObject.addProperty(JsonTags.timeStamp, DateUtils.dateToString(orderCancel.getSendingTimestampUTC()));
        return jsonObject;
    }
}
