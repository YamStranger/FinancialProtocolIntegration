package com;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:18 AM
 */

import com.orderbooks.InvalidOrderBookRecord;
import com.orderbooks.OrderBookFactory;
import com.orderbooks.OrderBookItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OrderBookReader contains functional for creating and manage OrderBook representation from Files, and
 * inti processing one by one OrderBookItems.
 * 1) looking for files with orderbooks by mask
 * 2) if orderbook file is found - messages file mast exists too.
 * 3) if orderbook and messages files exists - start processing this orderbook
 * 4) for current orderbook will be created orderBookItem for every row(step by step, not all), and init processing of them.
 * 5) if during processing orderbook of messages file will be found some error(incorrect format, or invalid value) - processing of this orderbook stops
 * 6) after processing current orderbook will be started processing next orderbook (sorted by start date)
 */
public class OrderBookReader extends Thread {
    private Logger logger = LoggerFactory.getLogger(OrderBookReader.class);
    private Path directoryToProcess;
    private BlockingQueue<OrderBookItem> orderBookItemsProcessingQueue;
    private static String orderBookKeyName = "_orderbook_";
    private static String messagesKeyName = "_message_";
    private static String fileMaskDateFormat = "YYYY-MM-dd";
    private static String csvFiles = "*{_orderbook_}*.csv";//TICKER_Year-Month-Day_StartTime_EndTime_orderbook_LEVEL.csv
    private static String orderBookFiles = "([^\\W_]+_)([\\d]+-[\\d]+-[\\d]+)(_[\\d]+_[\\d]+)(" + orderBookKeyName + ")(\\d+)(\\.csv)";

    public OrderBookReader(Path directoryToProcess, BlockingQueue<OrderBookItem> orderBookItemsProcessingQueue) {
        this.directoryToProcess = directoryToProcess;
        this.orderBookItemsProcessingQueue = orderBookItemsProcessingQueue;
    }

    @Override
    public void run() {
        Map<Path, Path> orderBooks = new HashMap<>(); //orderBookPath:messagesPath
        try {
            Pattern orderBookPattern = Pattern.compile(orderBookFiles);
            StringBuilder messagesNameBuilder = new StringBuilder();
            for (Path csvPath : Files.newDirectoryStream(directoryToProcess, csvFiles)) { //take all csv files form working directory
                Matcher orderBookNameMatcher = orderBookPattern.matcher(csvPath.getFileName().toString());
                if (orderBookNameMatcher.matches()) { //check if file with orderbook valid by file name mask
                    messagesNameBuilder.append(orderBookNameMatcher.group(1)).append(orderBookNameMatcher.group(2)).append(orderBookNameMatcher.group(3));
                    messagesNameBuilder.append(messagesKeyName).append(orderBookNameMatcher.group(5)).append(orderBookNameMatcher.group(6));
                    String correspondingMessagesFile = messagesNameBuilder.toString();
                    Path messagesFilePath = FileSystems.getDefault().getPath(correspondingMessagesFile);
                    if (Files.exists(messagesFilePath)) { //check if exists file with messages for file with orderbook
                        try {
                            Integer maxPriceLevel = Integer.parseInt(orderBookNameMatcher.group(5)); //parse maxPriceLevel from name of file
                            DateFormat dateFormat = new SimpleDateFormat(fileMaskDateFormat);        //parse date from from name of file
                            Date localDate = dateFormat.parse(orderBookNameMatcher.group(2));
                            logger.trace("start processing files " + messagesFilePath + " " + csvPath);
                            initProcessingOfOrderBook(csvPath, messagesFilePath, localDate.getTime(), maxPriceLevel);  //init processing of this orderbook file
                        } catch (NumberFormatException | ParseException | InvalidOrderBookRecord exception) {
                            logger.error("OrderBook " + csvPath + " contains some error or invalid record ", exception);
                            Storage.getInstance().register(csvPath.toString().concat("_result.txt"), exception);
                        }
                    }
                }
            }
        } catch (IOException ioException) {
            logger.error("Exception during processing directory", ioException);
        } catch (InterruptedException interruptedException) {
            interrupt();
        }
    }

    /**
     * init processing orderBook, loaded from file.
     * Function read orderBookItem and sends them to queue.
     * If queue is full - function will wait until some free space become available
     *
     * @param orderBookFilePath file with OrderBook data
     * @param messagesFilePath  file with messages for this orderBook
     * @param priceLevel        max price level in file
     * @param dateInFileMask    date in file mask, by local time
     * @throws com.orderbooks.InvalidOrderBookRecord if some invalid record will be found
     * @see com.orderbooks.OrderBookFactory
     */
    private void initProcessingOfOrderBook(Path orderBookFilePath, Path messagesFilePath, long dateInFileMask, int priceLevel) throws InvalidOrderBookRecord, InterruptedException {
        for (OrderBookItem orderBookItem : OrderBookFactory.loadOrderBook(orderBookFilePath, messagesFilePath, dateInFileMask, priceLevel)) {
            orderBookItemsProcessingQueue.put(orderBookItem);
        }
    }
}
