import com.processing.networking.Converter;
import com.sun.swing.internal.plaf.metal.resources.metal;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * User: YAPI
 * Date: 3/26/15
 * Time: 2:39 PM
 */
public class ConverterTest {
    @Test(groups = {"unit"})
    public void conventingTest() {
        String sourceMessage = "message";
        byte[] bytes = Converter.writeString(sourceMessage);
        String resultMessage = Converter.readString(bytes);
        Assert.assertEquals(sourceMessage, resultMessage);
    }
}
