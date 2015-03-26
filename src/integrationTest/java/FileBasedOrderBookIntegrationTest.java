import com.DateUtils;
import com.orderbooks.FileBasedOrderBook;
import com.orderbooks.OrderBookItem;
import com.orderbooks.PriceLevel;
import org.testng.Assert;
import org.testng.annotations.*;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: YAPI
 * Date: 3/26/15
 * Time: 9:17 AM
 */
public class FileBasedOrderBookIntegrationTest {
    private FileBasedOrderBook fileBasedOrderBook;

    /**
     * Reaload FileBasedOrderBook
     * @param resourcePath
     * @throws Exception
     */
    @Parameters("resourcePath")
    @BeforeMethod(groups = {"integration"})
    public void init(String resourcePath) throws Exception {
        Path currentDirectory = FileSystems.getDefault().getPath("").toAbsolutePath();
        Path testResources = currentDirectory.resolve(resourcePath);
        fileBasedOrderBook = new FileBasedOrderBook(testResources.resolve("AMZN_2012-06-21_34200000_57600001_orderbook_5.csv"), testResources.resolve("AMZN_2012-06-21_34200000_57600001_message_5.csv"), new SimpleDateFormat("YYYY/MM/dd").parse("2012/06/21").getTime(), 5);
    }

    /**
     * Testing parsing correct values
     */
    @Test(groups = {"integration"})
    public void creatingOrderItemsFromFile() throws ParseException {
        List<OrderBookItem> orderBookItems = new ArrayList<>();
        for (OrderBookItem orderBookItem : fileBasedOrderBook) {
            orderBookItems.add(orderBookItem);
        }
        Assert.assertEquals(7, orderBookItems.size(), "incorrect size of order book items");
        OrderBookItem orderBookItem = orderBookItems.get(1);
        List<PriceLevel> priceLevels = orderBookItem.getPriceLevels();
        Assert.assertEquals(5, priceLevels.size(), "incorrect size of orderBook item's price levels");
        long orderBookGMTTime = orderBookItem.getTimeOfOrderBookItem();
        long orderBookLocalTime = DateUtils.convent(TimeZone.getTimeZone("GMT"), TimeZone.getDefault(), orderBookGMTTime);
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");        //parse date from from name of file
        Date fileDateTime = dateFormat.parse("2012-06-21");
        long orderBookTime = 34200 * 1000 + 18961 + fileDateTime.getTime();
        Assert.assertEquals(orderBookLocalTime, orderBookTime, "incorrect time of event of order book item");
    }

    /**
     * Testing searching
     */
    @Test(groups = {"integration"}, dependsOnMethods = {"creatingOrderItemsFromFile"})
    public void searchingTest() {
        List<OrderBookItem> orderBookItems = new ArrayList<>();
        for (OrderBookItem orderBookItem : fileBasedOrderBook) {
            orderBookItems.add(orderBookItem);
        }
        Assert.assertEquals(7, orderBookItems.size(), "incorrect size of order book items");
        OrderBookItem orderBookItem = orderBookItems.get(1);
        //source line 2 (Order book item 1) of testing file 10	101	10	201	20	102	20	202	30	103	30	203	40	104	40	204	50	105	50	205
        // 10	101	10	201
        PriceLevel minAsk = orderBookItem.getMinPriceLevelByAskVolume(103, false);
        Assert.assertEquals(minAsk.getAskPrice(), 10, "incorrect ask price");
        Assert.assertEquals(minAsk.getAskVolume(), 101, "incorrect ask volume");
        Assert.assertEquals(minAsk.getBidPrice(), 10, "incorrect bid price");
        Assert.assertEquals(minAsk.getBidVolume(), 201, "incorrect bid volume");
        minAsk = orderBookItem.getMinPriceLevelByAskVolume(99, false);
        Assert.assertNull(minAsk, "getFirstPriceLevelByAskVolume error");
        minAsk = orderBookItem.getMinPriceLevelByAskVolume(102, true);
        Assert.assertEquals(minAsk.getAskPrice(), 20, "incorrect search by ask volume");
        PriceLevel maxBid = orderBookItem.getMaxPriceLevelByBidVolume(205, true);
        Assert.assertEquals(maxBid.getAskPrice(), 50, "incorrect ask price");
        Assert.assertEquals(maxBid.getAskVolume(), 105, "incorrect ask volume");
        Assert.assertEquals(maxBid.getBidPrice(), 50, "incorrect bid price");
        Assert.assertEquals(maxBid.getBidVolume(), 205, "incorrect bid volume");
    }

}
