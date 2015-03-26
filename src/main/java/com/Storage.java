package com;

import com.orders.NewOrderSingle;
import com.orders.OrderCancel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:40 AM
 */
public class Storage {
    private static Storage storage;
    private Map<String, RandomAccessFile> randomAccessFiles;
    private static String storageDirName="storage";

    private Storage() {
        randomAccessFiles = new HashMap<>();
    }

    public static Storage getInstance() {
        if (storage == null) {
            synchronized (Storage.class) {
                if (storage == null) {
                    storage = new Storage();
                }
            }
        }
        return storage;
    }

    public void register(String storageId, NewOrderSingle newOrderSingle) {
        System.out.println("received "+ newOrderSingle.getUniqueOrderID());
    }

    public void register(String storageId, OrderCancel newOrderCancel) {
        System.out.println("received "+ newOrderCancel.getUniqueOrderID());
    }

    public void register(String storageId, Exception exception) {

    }

    public long generateOrderId() {
        return ThreadLocalRandom.current().nextLong();
    }

    public void close() throws IOException {
        for (RandomAccessFile file : randomAccessFiles.values()) {
            file.close();
        }

    }

}
