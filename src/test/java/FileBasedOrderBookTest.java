import com.orderbooks.FileBasedOrderBook;
import com.orderbooks.InvalidOrderBookRecord;
import com.orderbooks.PriceLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 2:17 PM
 */
public class FileBasedOrderBookTest {
    private FileBasedOrderBook fileBasedOrderBook;

    @Parameters("resourcePath")
    @BeforeClass(groups = {"unit"})
    public void init(String resourcePath) throws Exception {
        Path currentDirectory = FileSystems.getDefault().getPath("").toAbsolutePath();
        Path testResources = currentDirectory.resolve(resourcePath);
        fileBasedOrderBook = new FileBasedOrderBook(testResources.resolve("AMZN_2012-06-21_34200000_57600001_orderbook_5.csv"), testResources.resolve("AMZN_2012-06-21_34200000_57600001_message_5.csv"), new SimpleDateFormat("YYYY/MM/dd").parse("2012/06/21").getTime(), 5);
    }

    /**
     * Testing parsing correct values
     */
    @Test(groups = {"unit"})
    public void parsePriceLevelsTestSimpleParse() {
        List<PriceLevel> list = fileBasedOrderBook.parsePriceLevels("2239500,101,2231800,100,2239900,100,2230700,200,2240000,220,2230400,100,2242500,100,2230000,10,2244000,547,2226200,100", 3);
        Assert.assertEquals(3, list.size());
        PriceLevel priceLevel = list.get(0);
        Assert.assertEquals(2239500, priceLevel.getAskPrice(),"incorrect Ask price parsing");
        Assert.assertEquals(101, priceLevel.getAskVolume(),"incorrect Ask volume parsing");
        Assert.assertEquals(2231800, priceLevel.getBidPrice(),"incorrect Bid price parsing");
        Assert.assertEquals(100, priceLevel.getBidVolume(),"incorrect Bid volume parsing");
    }

    /**
     * Testing parsing Unoccupied Price Levels
     */
    @Test(groups = {"unit"})
    public void parsePriceLevelsTestUnoccupiedPriceLevels() {
        List<PriceLevel> list = fileBasedOrderBook.parsePriceLevels("-9999999999,0,2231800,100,2239900,103,2230700,200,2240000,220,2230400,100,2242500,100,2230000,10,2244000,547,2226200,100", 3);
        Assert.assertEquals(2, list.size());
        PriceLevel priceLevel = list.get(0);
        Assert.assertEquals(2239900, priceLevel.getAskPrice(),"incorrect Ask price parsing");
        Assert.assertEquals(103, priceLevel.getAskVolume(),"incorrect Ask volume parsing");
        Assert.assertEquals(2230700, priceLevel.getBidPrice(),"incorrect Bid price parsing");
        Assert.assertEquals(200, priceLevel.getBidVolume(),"incorrect Bid volume parsing");
    }

    /**
     * Testing parsing incorrect values
     */
    @Test(groups = {"unit"}, expectedExceptions = {InvalidOrderBookRecord.class})
    public void parsePriceLevelsTestIncorrectValues() {
        List<PriceLevel> list = fileBasedOrderBook.parsePriceLevels("-2239500,0,2231800,100,2239900,103,2230700,200,2240000,220,2230400,100,2242500,100,2230000,10,2244000,547,2226200,100", 3);
        Assert.assertEquals(3, list.size());
        PriceLevel priceLevel = list.get(0);
        Assert.assertEquals(2239900, priceLevel.getAskPrice(),"incorrect Ask price parsing");
        Assert.assertEquals(103, priceLevel.getAskVolume(),"incorrect Ask volume parsing");
        Assert.assertEquals(2230700, priceLevel.getBidPrice(),"incorrect Bid price parsing");
        Assert.assertEquals(200, priceLevel.getBidVolume(),"incorrect Bid volume parsing");
    }

    @Test(groups = {"unit"})
    public void testParsingDate(){
        Assert.assertEquals(1001, fileBasedOrderBook.parseDateNumber("1.1,1,1,1.2,2"),"incorrect date from messages conventing");
        Assert.assertEquals(1000,fileBasedOrderBook.parseDateNumber("1,1,1,1.2,2"),"incorrect date from messages conventing");

    }
}
