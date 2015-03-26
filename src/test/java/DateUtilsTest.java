import com.DateUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 11:44 PM
 */
public class DateUtilsTest {
    @Test(groups = {"unit"})
    public void conventTest() {
        long kievTime=new Date().getTime();
        long gmtTime = DateUtils.convent(TimeZone.getTimeZone("Europe/Kiev"), TimeZone.getTimeZone("GMT"), kievTime);
        long diff = gmtTime-kievTime;
//        System.out.println("GMT: "+new Date(gmtTime)+", KIEV:"+new Date(kievTime));
        Assert.assertEquals(-120,TimeUnit.MILLISECONDS.toMinutes(diff),"incorrect time conversation using different time zones");
        long newKievTime = DateUtils.convent(TimeZone.getTimeZone("GMT"),TimeZone.getTimeZone("Europe/Kiev"), gmtTime);
        Assert.assertEquals(newKievTime,kievTime,"incorrect round time conversation");
    }
}
