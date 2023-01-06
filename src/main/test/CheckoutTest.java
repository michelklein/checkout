import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckoutTest {

    private static final Map<String, Item> PRICES = new HashMap<>();

    static {
        PRICES.put("A", new Item("A", 50, new SpecialPrice(3, 130)));
        PRICES.put("B", new Item("B", 30, new SpecialPrice(2, 45)));
        PRICES.put("C", new Item("C", 20, null));
        PRICES.put("D", new Item("D", 15, null));
    }


    @ParameterizedTest
    @NullAndEmptySource
    void testCalculateTotalPriceGivenEmptyOrNullItemReturnZero(String item) {
        Checkout checkout = new Checkout(PRICES);
        checkout.scan(item);
        int totalPrice = checkout.calculateTotalPrice();
        assertEquals(0, totalPrice);
    }

    @ParameterizedTest
    @CsvSource(value = {"A:50", "B:30", "C:20", "D:15"}, delimiter = ':')
    void testCalculateTotalPriceGivenSingleItemScannedReturnItemPrice(String item, String expectedPrice) {
        Checkout checkout = new Checkout(PRICES);
        checkout.scan(item);
        int totalPrice = checkout.calculateTotalPrice();
        assertEquals(Integer.valueOf(expectedPrice).intValue(), totalPrice);
    }

    @ParameterizedTest
    @CsvSource(value = {"A,B:80", "C,D,B,A:115", "A,A:100", "A,A,A:130", "A,A,A,A:180", "A,A,A,A,A:230", "A,A,A,A,A,A:260", "A,A,A,B:160", "A,A,A,B,B:175", "A,A,A,B,B,D:190", "D,A,B,A,B,A:190"}, delimiter = ':')
    void testCalculateTotalPriceGivenMultipleItemsScannedReturnAccumulatedPrice(String items, String expectedPrice) {
        Checkout checkout = new Checkout(PRICES);
        Arrays.stream(items.split(",")).forEach(checkout::scan);
        int totalPrice = checkout.calculateTotalPrice();
        assertEquals(Integer.valueOf(expectedPrice).intValue(), totalPrice);
    }

    @Test
    void testCalculateTotalPriceGivenIncrementalItemScanReturnUpdatedPrice() {
        Checkout checkout = new Checkout(PRICES);
        checkout.scan("A");
        assertEquals(50, checkout.calculateTotalPrice());
        checkout.scan("B");
        assertEquals(80, checkout.calculateTotalPrice());
        checkout.scan("A");
        assertEquals(130, checkout.calculateTotalPrice());
        checkout.scan("A");
        assertEquals(160, checkout.calculateTotalPrice());
        checkout.scan("B");
        assertEquals(175, checkout.calculateTotalPrice());
    }
}
