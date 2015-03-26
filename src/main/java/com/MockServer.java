package com;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:19 AM
 */

import com.orders.NewOrderSingle;
import com.orders.OrderCancel;
import com.orders.OrderMessage;
import com.processing.networking.Converter;
import com.processing.serialization.Serializer;

import javax.swing.plaf.basic.BasicTextAreaUI;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Server, for emulation purpose.
 * It will accept connection, receive message and send it back
 */
public class MockServer extends Thread {
    private InetSocketAddress inetSocketAddress;
    private Map<SocketChannel, ByteBuffer> buffersBySocketChannels;
    private Map<SocketChannel, Integer> buffersExpectedSize;


    public MockServer(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
        buffersBySocketChannels = new HashMap<>();
        buffersExpectedSize = new HashMap<>();
    }

    @Override
    public void run() {
        Selector selector = null;
        ServerSocketChannel server = null;
        try {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.socket().bind(inetSocketAddress);
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (!isInterrupted()) {
                selector.select();
                for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    SelectionKey key = i.next();
                    i.remove();
                    if (key.isConnectable()) {
                        ((SocketChannel) key.channel()).finishConnect();
                    }
                    if (key.isAcceptable()) {
                        // accept connection
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.socket().setKeepAlive(true);
                        client.register(selector, SelectionKey.OP_READ);
                        buffersBySocketChannels.put(client, ByteBuffer.allocate(1000));
                    }
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = buffersBySocketChannels.get(client);
                        while (client.read(byteBuffer) > 0) ;
                        byteBuffer.flip();
                        byte[] readedBytes = new byte[byteBuffer.limit()];
                        byteBuffer.get(readedBytes, 0, readedBytes.length);
                        OrderMessage orderMessage = Serializer.getInstance().deserialize(readedBytes);
                        if (orderMessage instanceof NewOrderSingle) {
                            Storage.getInstance().register("recieved_orders.txt", (NewOrderSingle) orderMessage);
                        } else {
                            Storage.getInstance().register("recieved_orders.txt", (OrderCancel) orderMessage);
                        }
                        buffersBySocketChannels.remove(client);
                        byteBuffer.clear();
                        client.close();

                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("Server failure: " + e.getMessage());
        } finally {
            try {
                selector.close();
                server.socket().close();
                server.close();
            } catch (Exception e) {
                // do nothing - server failed
            }
        }
    }
}
