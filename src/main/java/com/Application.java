package com;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 11:21 AM
 */

import com.orderbooks.OrderBookItem;
import com.orders.OrderMessage;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * main application.
 * Starts processing of OrderBook, and Mock receiving server.
 * Preforms all needed operations
 */
public class Application {
    public static void main(String... args) throws Exception {

        LinkedBlockingQueue<OrderBookItem> orderBookItemsForProcessing = new LinkedBlockingQueue<>(10);
        LinkedBlockingQueue<OrderMessage> ordersForSending = new LinkedBlockingQueue<>(10);
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 2000);
        Path workingDir = Paths.get("");
        System.out.println("started processing files at working directory " + workingDir.toAbsolutePath().toString());

        //Thread will read OrderBookItems one by one and send them to processing queue
        OrderBookReader orderBookReader = new OrderBookReader(workingDir, orderBookItemsForProcessing);
        orderBookReader.start();

        //Thread will read OrderBookItems one by one from processing queue, process them.
        //Result of processing - creating some order (newSingleOrder or CancelOrder) will be added to sending queue
        OrdersProcessor ordersProcessor = new OrdersProcessor(orderBookItemsForProcessing, ordersForSending);
        ordersProcessor.start();

        MockServer mockServer = new MockServer(serverAddress);
        mockServer.start();

        //Thread responsible for sending orders to server
        OrderSender orderSender = new OrderSender(ordersForSending, serverAddress);
        orderSender.start();

        ordersProcessor.join();
        orderBookReader.join();
        Thread.currentThread().sleep(3000);
        orderSender.interrupt(); //stop sender (No order items to process, no messages to send)
        mockServer.interrupt();  //stop server
        System.out.println("Total order items processed " + ordersProcessor.getProcessedOrderItemsCount());
        System.out.println("Total orders sended " + orderSender.getOrdersCount());
        Storage.getInstance().close();
    }
}
