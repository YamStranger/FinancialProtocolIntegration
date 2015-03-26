import com.OrdersProcessor;
import com.orderbooks.OrderBookFactory;
import com.orderbooks.OrderBookItem;
import com.orderbooks.PriceLevel;
import com.orders.OrderMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: YAPI
 * Date: 3/26/15
 * Time: 12:15 PM
 */
public class OrdersProcessorIntegrationTest {
    LinkedBlockingQueue<OrderBookItem> orderBookItemsQueue;
    LinkedBlockingQueue<OrderMessage> ordersForSendingQueue;

    @BeforeMethod(groups = {"integration"})
    public void init() {
        orderBookItemsQueue = new LinkedBlockingQueue<>(100);
        ordersForSendingQueue = new LinkedBlockingQueue<>(100);
        OrdersProcessor ordersProcessor = new OrdersProcessor(orderBookItemsQueue, ordersForSendingQueue);
        ordersProcessor.start();
    }

    @Test(groups = {"integration"})
    public void testProcessingFlowSellOrders() throws InterruptedException {
        List<PriceLevel> priceLevels = new ArrayList<>();
        priceLevels.add(OrderBookFactory.createPriceLevel(1, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(3, 500, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(4, 1, 3, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(5, 1, 7, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(6, 1, 1, 1));
        //result of processing this order must be one newSingeleSEll order
        OrderBookItem orderBookItemForOneSellOrder = OrderBookFactory.createOrderBookItem(priceLevels, System.currentTimeMillis());

        priceLevels.clear();
        priceLevels.add(OrderBookFactory.createPriceLevel(1, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(3, 1, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(4, 1, 3, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(5, 1, 7, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(6, 1, 1, 1));
        //result of processing this order must be one OrderClose order
        OrderBookItem orderBookItemForOneOrderCloseOrder = OrderBookFactory.createOrderBookItem(priceLevels, System.currentTimeMillis());
        orderBookItemsQueue.add(orderBookItemForOneSellOrder);
        orderBookItemsQueue.add(orderBookItemForOneOrderCloseOrder);
        Thread.sleep(1000);
        Assert.assertEquals(2, ordersForSendingQueue.size(), "orders now putted to sending queue");
    }

    @Test(groups = {"integration"})
    public void testProcessingFlowBuyOrders() throws InterruptedException {
        List<PriceLevel> priceLevels = new ArrayList<>();
        priceLevels.add(OrderBookFactory.createPriceLevel(1, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(3, 1, 2, 500));
        priceLevels.add(OrderBookFactory.createPriceLevel(4, 1, 3, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(5, 1, 7, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(6, 1, 1, 1));
        //result of processing this order must be one newSingeleSEll order
        OrderBookItem orderBookItemForOneSellOrder = OrderBookFactory.createOrderBookItem(priceLevels, System.currentTimeMillis());

        priceLevels.clear();
        priceLevels.add(OrderBookFactory.createPriceLevel(1, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(3, 1, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(4, 1, 3, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(5, 1, 7, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(6, 1, 1, 1));
        //result of processing this order must be one OrderClose order
        OrderBookItem orderBookItemForOneOrderCloseOrder = OrderBookFactory.createOrderBookItem(priceLevels, System.currentTimeMillis());
        orderBookItemsQueue.add(orderBookItemForOneSellOrder);
        orderBookItemsQueue.add(orderBookItemForOneOrderCloseOrder);
        Thread.sleep(1000);
        Assert.assertEquals(2, ordersForSendingQueue.size(), "orders now putted to sending queue");
    }
}
