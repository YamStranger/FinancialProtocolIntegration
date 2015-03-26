import com.orders.OrderCreator;
import com.orders.OrderMessage;
import com.orders.SideEnum;
import com.processing.serialization.SerializationException;
import com.processing.serialization.Serializer;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: YAPI
 * Date: 3/26/15
 * Time: 2:30 PM
 */
public class SerializerTest {

    @DataProvider(name = "Orders")
    public Iterator<Object[]> createData() {
        List<Object[]> orderMessageList = new ArrayList<>();
        orderMessageList.add(new Object[]{OrderCreator.createNewOrderSingle(System.currentTimeMillis(), "asdf", 1523, 21, SideEnum.Sell)});
        orderMessageList.add(new Object[]{OrderCreator.createNewOrderSingle(System.currentTimeMillis() + 70, "1234", 0, 21, SideEnum.Buy)});
        orderMessageList.add(new Object[]{OrderCreator.createNewOrderSingle(System.currentTimeMillis() - 90, "3122134", 1, 21, SideEnum.Sell)});
        orderMessageList.add(new Object[]{OrderCreator.createNewOrderSingle(System.currentTimeMillis() + 80, "asdf", -1, 21, SideEnum.Buy)});
        return orderMessageList.iterator();
    }


    //This test method declares that its data should be supplied by the Data Provider
    //named "test1"
    @Test(dataProvider = "Orders", groups = {"unit"})
    public void testSerializationProcess(OrderMessage orderMessage) throws SerializationException {
        byte[] forSend = Serializer.getInstance().serialize(orderMessage);
        OrderMessage orderMessageResult = Serializer.getInstance().deserialize(forSend);
        Assert.assertEquals(orderMessage,orderMessageResult);
    }

}
