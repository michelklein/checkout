import java.util.HashMap;
import java.util.Map;

public class Checkout {

    private final Map<String, Integer> scannedItems = new HashMap<>();
    private final Map<String, Item> prices;

    public Checkout(Map<String, Item> prices) {
        this.prices = prices;
    }

    void scan(String item) {
        if (item == null || "".equals(item)) {
            return;
        }
        scannedItems.compute(item, (key, value) -> (value == null) ? 1 : value + 1);
    }

    int calculateTotalPrice() {
        return scannedItems.keySet().stream().map(this::calculateTotalPriceForItem).reduce(0, Integer::sum);
    }

    private int calculateTotalPriceForItem(String itemName) {
        var quantity = scannedItems.get(itemName);
        if (quantity == null) {
            throw new IllegalStateException(itemName + " not scanned");
        }
        var item = prices.get(itemName);
        if (item == null) {
            throw new IllegalStateException(itemName + " not in price list");
        }
        var specialPrice = item.specialPrice();
        if (specialPrice != null) {
            return calculateSpecialPrice(quantity, item);
        }
        return quantity * item.price();
    }


    private int calculateSpecialPrice(Integer quantity, Item item) {
        return calculateSpecialPrice(0, quantity, item);
    }

    private int calculateSpecialPrice(Integer totalPrice, Integer quantity, Item item) {
        if (quantity < item.specialPrice().quantity()) {
            return totalPrice + (quantity * item.price());
        }
        if (quantity.equals(item.specialPrice().quantity())) {
            return totalPrice + item.specialPrice().price();
        }
        return calculateSpecialPrice(item.specialPrice().price(), quantity - item.specialPrice().quantity(), item);
    }

}
