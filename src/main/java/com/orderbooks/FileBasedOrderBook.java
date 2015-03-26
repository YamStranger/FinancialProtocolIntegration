package com.orderbooks;

import com.DateUtils;
import com.orderbooks.validators.OrderBookItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 9:52 AM
 */

/**
 * FileBasedOrderBook represent orderBook file as Iterable collection of OrderBookItes's
 * To get access to all OrderBookItems you need to use Iterator<OrderBookItem>.
 * When you call Iterator.hasNext - it will return true only if file contains one more representation of OrderBookItem
 * When you call Iterator.next - new line from file is read, parsed and convented to orderBookItem.
 */
public class FileBasedOrderBook implements OrderBook {
    Logger logger= LoggerFactory.getLogger(FileBasedOrderBook.class);
    Iterator<OrderBookItem> orderBookItemIterator;

    public FileBasedOrderBook(Path orderBookFilePath, Path messagesFilePath, long dateInFileMask, int maxPriceLevel) throws InvalidOrderBookRecord {
        orderBookItemIterator = new OrderBookItemIterator(orderBookFilePath, messagesFilePath, dateInFileMask, maxPriceLevel);
    }


    @Override
    public Iterator<OrderBookItem> iterator() {
        return orderBookItemIterator;
    }

    /**
     * Creating PriceLevel according string format
     * 1.) Ask Price 1: 	Level 1 Ask Price 	(Best Ask)
     * 2.) Ask Size 1: 	Level 1 Ask Volume 	(Best Ask Volume)
     * 3.) Bid Price 1: 	Level 1 Bid Price 	(Best Bid)
     * 4.) Bid Size 1: 	Level 1 Bid Volume 	(Best Bid Volume)
     * 5.) Ask Price 2: 	Level 2 Ask Price 	(2nd Best Ask)
     * ....
     * values separated non-digits values (,-)
     * There ara some validations during parsing string @See OrderBookItemValidator
     *
     * @param orderBookLine
     * @param maxPriceLevel
     * @return
     * @throws InvalidOrderBookRecord if price level incorrect or it is some parsing troubles
     */
    public List<PriceLevel> parsePriceLevels(String orderBookLine, int maxPriceLevel) {
        List<PriceLevel> priceLevels = new ArrayList<>(maxPriceLevel);
        String[] separatedData = orderBookLine.split("[^\\d-]+");
        int levelCount = 0;
        try {
            while (levelCount < maxPriceLevel) {
                long ask = Long.parseLong(separatedData[levelCount * 4]);
                long askVolume = Long.parseLong(separatedData[levelCount * 4 + 1]);
                long bid = Long.parseLong(separatedData[levelCount * 4 + 2]);
                long bidVolume = Long.parseLong(separatedData[levelCount * 4 + 3]);
                if (Math.abs(ask) != 9999999999L && Math.abs(bid) != 9999999999L) {
                    PriceLevel priceLevel = new PriceLevel(ask, askVolume, bid, bidVolume);
                    if (OrderBookItemValidator.isCorrect(priceLevel)) {
                        priceLevels.add(priceLevel);
                    } else {
                        throw new InvalidOrderBookRecord("maxPriceLevel[" + levelCount + "]" + "incorrect");
                    }
                }
                levelCount++;
            }
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidOrderBookRecord(numberFormatException);
        }
        return priceLevels;
    }

    /**
     * parse date from string, and convert from local Locale to UTC
     * Format of string: 34200.01746,5,0,1,2238200,-1
     * Seconds after midnight with decimal
     * precision of at least milliseconds
     * and up to nanoseconds depending on
     * the requested period
     *
     * @param sourceLine
     * @return milliseconds after midnight
     * @throws InvalidOrderBookRecord if messages contains invalid format
     */
    public long parseDateNumber(String sourceLine) {
        Pattern pattern = Pattern.compile("[\\d\\.]+");
        Matcher matcher = pattern.matcher(sourceLine);

        if (matcher.find()) {
            try {
                String value = matcher.group();
                String parts[] = value.split("\\D");
                long resultTime = (Long.parseLong(parts[0]) * 1000L) + (parts.length > 1 ? Long.parseLong(parts[1]) : 0);
                return resultTime;
            } catch (NumberFormatException e) {
                throw new InvalidOrderBookRecord("invalid date in messages file");
            }
        } else {
            throw new InvalidOrderBookRecord("invalid date in messages file");
        }
    }

    /**
     * Iterator for iterating throw orderBookItems in file
     */
    private class OrderBookItemIterator implements Iterator<OrderBookItem> {
        private Scanner orderBookFileScanner;
        private Scanner messagesFileScanner;
        private int maxPriceLevel;
        private long dateInFileMask;


        /**
         * Created OrderBookItemIterator from file by Path.
         * If Path invalid, or structure of files inappropriate InvalidOrderBookRecord will be thrown
         *
         * @param orderBookFilePath
         * @param messagesFilePath
         * @throws InvalidOrderBookRecord if orderBook file constance invalid data
         */
        private OrderBookItemIterator(Path orderBookFilePath, Path messagesFilePath, long dateInFileMask, int maxPriceLevel) throws InvalidOrderBookRecord {
            try {
                this.dateInFileMask = dateInFileMask;
                this.maxPriceLevel = maxPriceLevel;
                orderBookFileScanner = new Scanner(orderBookFilePath);
                messagesFileScanner = new Scanner(messagesFilePath);
            } catch (Exception e) {
                if (orderBookFileScanner != null) {
                    orderBookFileScanner.close();
                }
                if (messagesFileScanner != null) {
                    messagesFileScanner.close();
                }
                throw new InvalidOrderBookRecord(e);
            }
        }

        /**
         * check If there is next OrderBookItem representation in file
         *
         * @return true if there is new OrderBookItem
         * @see java.util.Iterator
         */
        @Override
        public boolean hasNext() {
            return orderBookFileScanner.hasNext() && messagesFileScanner.hasNext();
        }

        /**
         * Returns the next orderBookItem form file. During reading it, if format is inappropriate - exception will be thrown
         *
         * @return the next element in the iteration
         * @throws java.util.NoSuchElementException if the iteration has no more elements
         * @throws InvalidOrderBookRecord           if next OrderBook invalid
         * @see java.util.Iterator
         */
        @Override
        public OrderBookItem next() throws InvalidOrderBookRecord {
            String orderBookLine = orderBookFileScanner.nextLine();
            String messagesLine = messagesFileScanner.nextLine();
            if (orderBookLine == null || messagesLine == null) {
                throw new NoSuchElementException();
            }
            List<PriceLevel> priceLevels = parsePriceLevels(orderBookLine, maxPriceLevel);
            long timeInLocalTimeZone = parseDateNumber(messagesLine) + dateInFileMask;
            return OrderBookFactory.createOrderBookItem(priceLevels, DateUtils.conventCurrentToGMT(timeInLocalTimeZone));
        }

        /**
         * this function cant be performed on OrderBookItemIterator
         * remove operation can not be permitted;
         *
         * @see java.util.Iterator
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
