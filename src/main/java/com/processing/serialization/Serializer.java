package com.processing.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.orders.NewOrderSingle;
import com.orders.OrderCancel;
import com.orders.OrderMessage;
import com.processing.networking.Converter;
import com.processing.serialization.json.NewOrderSingleSerializer;
import com.processing.serialization.json.OrderCancelSerializer;
import com.processing.serialization.json.OrderMessageDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 9:56 AM
 */

/**
 * Serializer contains functions to prepare String representation of orderMessage,
 * and for converting String to orderMessage.
 */
public class Serializer {
    Logger logger = LoggerFactory.getLogger(Serializer.class);
    private static Serializer serializer;
    private static Gson gson;

    private Serializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(NewOrderSingle.class, new NewOrderSingleSerializer());
        gsonBuilder.registerTypeAdapter(OrderMessage.class, new OrderMessageDeserializer());
        gsonBuilder.registerTypeAdapter(OrderCancel.class, new OrderCancelSerializer());
        gson = gsonBuilder.create();
    }

    public static Serializer getInstance() {
        if (serializer == null) {
            synchronized (Serializer.class) {
                if (serializer == null) {
                    serializer = new Serializer();
                }
            }
        }
        return serializer;
    }

    public byte[] serialize(OrderMessage orderMessage) throws SerializationException {
        return Converter.writeString(gson.toJson(orderMessage));
    }

    public OrderMessage deserialize(byte[] orderMessageBytes) throws SerializationException {
        String jsonString = Converter.readString(orderMessageBytes);
        try {
            return gson.fromJson(jsonString, OrderMessage.class);
        } catch (JsonParseException jsonException) {
            logger.error("during parsing message", jsonException);
            throw new SerializationException(jsonException);
        }
    }

}
