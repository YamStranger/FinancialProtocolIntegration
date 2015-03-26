package com.processing.serialization.json;

import com.DateUtils;
import com.google.gson.*;
import com.orders.*;

import java.lang.reflect.Type;
import java.text.ParseException;

/**
 * User: YAPI
 * Date: 3/26/15
 * Time: 1:42 PM
 */
public class OrderMessageDeserializer implements JsonDeserializer<OrderMessage> {
    @Override
    public OrderMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!jsonElement.isJsonObject()) {
            throw new IllegalArgumentException();
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonPrimitive cancelOrderIdPrimitive = jsonObject.getAsJsonPrimitive(JsonTags.cancelOrderId);
        if (cancelOrderIdPrimitive == null) {
            //this is newOrderSimple
            return deserializeNewOrderSingle(jsonObject);
        } else {
            //this is OrderCancel
            return deserializeOrderCancel(jsonObject);
        }
    }

    public NewOrderSingle deserializeNewOrderSingle(JsonObject jsonObject) {
        JsonPrimitive jsonPrimitive;
        String orderId;
        if ((jsonPrimitive = jsonObject.getAsJsonPrimitive(JsonTags.orderId)) != null) {
            orderId = jsonPrimitive.getAsString();
        } else {
            throw new JsonParseException("no order id tag");
        }

        String timeStampString;
        if ((jsonPrimitive = jsonObject.getAsJsonPrimitive(JsonTags.timeStamp)) != null) {
            timeStampString = jsonPrimitive.getAsString();
        } else {
            throw new JsonParseException("no time stamp tag");
        }
        long timeStamp = 0;
        try {
            timeStamp = DateUtils.stringToDate(timeStampString);
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }

        long price;
        if ((jsonPrimitive = jsonObject.getAsJsonPrimitive(JsonTags.price)) != null) {
            price = jsonPrimitive.getAsLong();
        } else {
            throw new JsonParseException("no price tag");
        }
        long quantity;
        if ((jsonPrimitive = jsonObject.getAsJsonPrimitive(JsonTags.quantity)) != null) {
            quantity = jsonPrimitive.getAsLong();
        } else {
            throw new JsonParseException("no price tag");
        }
        int type;
        if ((jsonPrimitive = jsonObject.getAsJsonPrimitive(JsonTags.type)) != null) {
            type = jsonPrimitive.getAsInt();
        } else {
            throw new JsonParseException("no type tag");
        }
        SideEnum sideEnum = SideEnum.getByKey(type);

        return OrderCreator.createNewOrderSingle(timeStamp, orderId, price, quantity, sideEnum);
    }

    public OrderCancel deserializeOrderCancel(JsonObject jsonObject) {
        JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(JsonTags.cancelOrderId);
        String cancelOrderId = jsonPrimitive.getAsString();
        String orderId = jsonObject.getAsJsonPrimitive(JsonTags.orderId).getAsString();
        String timeStampString = jsonObject.getAsJsonPrimitive(JsonTags.timeStamp).getAsString();
        long timeStamp = 0;
        try {
            timeStamp = DateUtils.stringToDate(timeStampString);
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
        return OrderCreator.createOrderCancel(timeStamp, orderId, cancelOrderId);
    }


}
