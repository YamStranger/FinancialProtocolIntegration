package com.processing.serialization.json;

import com.DateUtils;
import com.google.gson.*;
import com.orders.NewOrderSingle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:02 AM
 */
public class NewOrderSingleSerializer implements JsonSerializer<NewOrderSingle> {
    Logger logger = LoggerFactory.getLogger(NewOrderSingleSerializer.class);

    @Override
    public JsonElement serialize(NewOrderSingle newOrderSingle, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(JsonTags.orderId, newOrderSingle.getUniqueOrderID());
        jsonObject.addProperty(JsonTags.price, newOrderSingle.getPrice());
        jsonObject.addProperty(JsonTags.quantity, newOrderSingle.getQuantity());
        jsonObject.addProperty(JsonTags.type, newOrderSingle.getSide().getKey());
        jsonObject.addProperty(JsonTags.timeStamp, DateUtils.dateToString(newOrderSingle.getSendingTimestampUTC()));
        return jsonObject;
    }
}
