import com.OrdersProcessor;
import com.orderbooks.OrderBookFactory;
import com.orderbooks.OrderBookItem;
import com.orderbooks.PriceLevel;
import com.orders.NewOrderSingle;
import com.orders.OrderCancel;
import com.orders.OrderMessage;
import com.orders.SideEnum;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: YAPI
 * Date: 3/26/15
 * Time: 12:23 PM
 */
public class OrdersProcessorTest {
    private LinkedBlockingQueue<OrderBookItem> orderBookItemsQueue;
    private LinkedBlockingQueue<OrderMessage> ordersForSendingQueue;
    private OrdersProcessor ordersProcessor;

    @BeforeMethod(groups = {"unit"})
    public void init() {
        orderBookItemsQueue = new LinkedBlockingQueue<>(100);
        ordersForSendingQueue = new LinkedBlockingQueue<>(100);
        ordersProcessor = new OrdersProcessor(orderBookItemsQueue, ordersForSendingQueue);
    }

    @Test(groups = {"unit"})
    public void testProcessingFlowWithNoOrders() {
        List<PriceLevel> priceLevels = new ArrayList<>();
        priceLevels.add(OrderBookFactory.createPriceLevel(1, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(3, 1, 3, 1));
        OrderBookItem orderBookItem = OrderBookFactory.createOrderBookItem(priceLevels, System.currentTimeMillis());
        //expected no orders, because there is no price level with volume > 500;
        OrderMessage orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Buy);
        Assert.assertNull(orderMessage);
        orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Sell);
        Assert.assertNull(orderMessage);
    }

    @Test(groups = {"unit"})
    public void testProcessingFlowWithNewSELLOrder() {
        List<PriceLevel> priceLevels = new ArrayList<>();
        priceLevels.add(OrderBookFactory.createPriceLevel(1, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 501, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(3, 1, 3, 1));
        OrderBookItem orderBookItem = OrderBookFactory.createOrderBookItem(priceLevels, System.currentTimeMillis());
        //expected one order SELL(open price 3), because there is price level with Ask volume > 500;
        OrderMessage orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Buy);
        Assert.assertNull(orderMessage);
        orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Sell);
        Assert.assertNotNull(orderMessage);
        Assert.assertTrue(orderMessage instanceof NewOrderSingle);
        NewOrderSingle orderSingle = (NewOrderSingle) orderMessage;
        Assert.assertEquals(3, orderSingle.getPrice());
        Assert.assertEquals(SideEnum.Sell, orderSingle.getSide());

        // expected one close order SELL, because there is no price level with Ask volume > 200;
        priceLevels.clear();
        priceLevels.add(OrderBookFactory.createPriceLevel(1, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(3, 1, 3, 1));
        orderBookItem = OrderBookFactory.createOrderBookItem(priceLevels, System.currentTimeMillis());
        orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Buy);
        Assert.assertNull(orderMessage);
        orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Sell);
        Assert.assertNotNull(orderMessage);
        Assert.assertTrue(orderMessage instanceof OrderCancel);
        OrderCancel orderCancel = (OrderCancel) orderMessage;
        Assert.assertEquals(orderSingle.getUniqueOrderID(),orderCancel.getOrderIDofOrderToCancel());
    }

    @Test(groups = {"unit"})
    public void testProcessingFlowWithNewBUYOrder() {
        List<PriceLevel> priceLevels = new ArrayList<>();
        priceLevels.add(OrderBookFactory.createPriceLevel(1, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 2, 501));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(3, 1, 3, 1));
        OrderBookItem orderBookItem = OrderBookFactory.createOrderBookItem(priceLevels, System.currentTimeMillis());
        //expected one order BUY(open price 1), because there is price level with Bid volume > 500;
        OrderMessage orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Sell);
        Assert.assertNull(orderMessage);
        orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Buy);
        Assert.assertNotNull(orderMessage);
        Assert.assertTrue(orderMessage instanceof NewOrderSingle);
        NewOrderSingle orderSingle = (NewOrderSingle) orderMessage;
        Assert.assertEquals(1, orderSingle.getPrice());
        Assert.assertEquals(SideEnum.Buy, orderSingle.getSide());

        // expected one close order BUY, because there is no price level with Bid volume > 200;
        priceLevels.clear();
        priceLevels.add(OrderBookFactory.createPriceLevel(1, 1, 1, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(2, 1, 2, 1));
        priceLevels.add(OrderBookFactory.createPriceLevel(3, 1, 3, 1));
        orderBookItem = OrderBookFactory.createOrderBookItem(priceLevels, System.currentTimeMillis());
        orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Sell);
        Assert.assertNull(orderMessage);
        orderMessage = ordersProcessor.processOrderBookItem(orderBookItem, SideEnum.Buy);
        Assert.assertNotNull(orderMessage);
        Assert.assertTrue(orderMessage instanceof OrderCancel);
        OrderCancel orderCancel = (OrderCancel) orderMessage;
        Assert.assertEquals(orderSingle.getUniqueOrderID(),orderCancel.getOrderIDofOrderToCancel());
    }

}
