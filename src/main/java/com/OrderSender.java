package com;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:19 AM
 */

import com.orderbooks.OrderBookItem;
import com.orders.OrderMessage;
import com.processing.serialization.SerializationException;
import com.processing.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

/**
 * Thread for processing messages. This class responsible for holding connection and creating it, and for
 * writing Orders to server and receiving answer.
 */
public class OrderSender extends Thread {
    private Logger logger = LoggerFactory.getLogger(OrderSender.class);
    private BlockingQueue<OrderMessage> ordersForSending;
    private InetSocketAddress serverAddress;
    private long ordersCount = 0;

    public OrderSender(BlockingQueue<OrderMessage> ordersForSending, InetSocketAddress serverAddress) {
        this.ordersForSending = ordersForSending;
        this.serverAddress = serverAddress;
    }

    @Override
    public void run() {
        OrderMessage processingOrder = null;
        SocketChannel channel = null;
        while (!isInterrupted()) {
            try {

                if (processingOrder == null) {
                    processingOrder = ordersForSending.take();
                }
                if (channel == null || !channel.isOpen() || !channel.isConnected()) {
                    channel = SocketChannel.open();
                    channel.configureBlocking(false);
                    channel.connect(serverAddress);
                    while (!channel.finishConnect()) {
                        Thread.currentThread().sleep(10);
                    }
                }

                byte[] bytesToSend = Serializer.getInstance().serialize(processingOrder);
                ByteBuffer forSend = ByteBuffer.wrap(bytesToSend);
                channel.write(forSend);
                System.out.println("sended " + processingOrder.getUniqueOrderID());
                forSend.clear();
                processingOrder = null;
            } catch (IOException e) {
                logger.error("not connect ot server", e);
            } catch (InterruptedException ee) {
                interrupt();
            } catch (SerializationException serialException) {
                throw new RuntimeException("Error in communication protocol");
            }
        }
    }

    public long getOrdersCount() {
        return ordersCount;
    }
}
