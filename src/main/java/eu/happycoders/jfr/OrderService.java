package eu.happycoders.jfr;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

class OrderService {

  static final int PRODUCT_COUNT = 1_000_000;
  private static final int BLOCKED_PRODUCT_COUNT = 200_000;

  private final List<String> blockedProducts = loadBlockedProducts();
  private final Map<Long, String> receiptCache = new ConcurrentHashMap<>();
  private final InvoiceNumberGenerator invoiceNumbers = new InvoiceNumberGenerator();
  private final ShippingClient shippingClient;
  private final AtomicLong processedOrders = new AtomicLong();

  OrderService(URI shippingServiceUri) {
    this.shippingClient = new ShippingClient(shippingServiceUri);
  }

  void processUntil(Instant end, OrderGenerator generator) {
    while (Instant.now().isBefore(end)) {
      try {
        process(generator.next());
      } catch (InterruptedException _) {
        Thread.currentThread().interrupt();
        return;
      }
    }
  }

  void process(Order order) throws InterruptedException {
    OrderEvent event = new OrderEvent();
    event.orderId = order.id();
    event.begin();

    int quantity = parseQuantity(order.quantity());
    if (!isBlocked(order.productId())) {
      BigDecimal shippingCost = shippingClient.shippingCost(order);
      long invoiceNumber = invoiceNumbers.next();
      String receipt = formatReceipt(order, quantity, shippingCost, invoiceNumber);
      receiptCache.put(order.id(), receipt);
    }

    event.commit();
    processedOrders.incrementAndGet();
  }

  private static int parseQuantity(String quantity) {
    try {
      return Integer.parseInt(quantity);
    } catch (NumberFormatException _) {
      return 1;
    }
  }

  private boolean isBlocked(String productId) {
    return blockedProducts.contains(productId);
  }

  private static String formatReceipt(
      Order order, int quantity, BigDecimal shippingCost, long invoiceNumber) {
    String receipt = "Invoice " + invoiceNumber + ", quantity " + quantity + "\n";
    for (String line : order.lines()) {
      receipt += line + "\n";
    }
    receipt += "Shipping: " + shippingCost + " EUR\n";
    return receipt;
  }

  private static List<String> loadBlockedProducts() {
    List<String> products = new ArrayList<>(BLOCKED_PRODUCT_COUNT);
    for (int i = 0; i < BLOCKED_PRODUCT_COUNT; i++) {
      products.add("P-" + (PRODUCT_COUNT + i));
    }
    return products;
  }

  long processedOrders() {
    return processedOrders.get();
  }
}
